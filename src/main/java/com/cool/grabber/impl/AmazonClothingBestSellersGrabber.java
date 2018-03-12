package com.cool.grabber.impl;

import com.cool.grabber.Grabber;
import com.cool.grabber.behavior.amazon.impl.AmazonBestSellersListGrabberBehavior;
import com.cool.grabber.behavior.amazon.impl.AmazonBestSellersListPageLinkGrabberBehavior;
import com.cool.grabber.behavior.amazon.impl.AmazonClothingProductInfoGrabberBehavior;
import com.cool.grabber.exception.GrabberException;
import com.cool.models.AmazonClothingProduct;
import com.cool.models.AmazonSimpleProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by codelover on 18/3/7.
 * 抓取最佳销售排行的商品
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AmazonClothingBestSellersGrabber implements Grabber<AmazonClothingProduct> {
    private final static Logger logger = LoggerFactory.getLogger(AmazonClothingBestSellersGrabber.class);
    private String starLink;
    private String referer;
    private String name;
    @Value("${com.cool.grabber.max.grab.count}")
    private int maxRetryCount;

    @Autowired
    private AmazonBestSellersListPageLinkGrabberBehavior pageLinkGrabberBehavior;
    @Autowired
    private AmazonBestSellersListGrabberBehavior listGrabberBehavior;
    @Autowired
    private AmazonClothingProductInfoGrabberBehavior productInfoGrabberBehavior;
    @Autowired
    private ThreadPoolTaskScheduler defaultGrabberBehaviorTaskScheduler;

    @Override
    public List<AmazonClothingProduct> get() throws GrabberException {
        Assert.notNull(name, "抓取器的名称不能为空");
        Assert.notNull(starLink, "配置的链接不能为空");
        Assert.notNull(referer, "配置的引用页不能为空");
        //调用抓取排行分页的行为类，获取分页链接
        List<String> pageLinks = pageLinkGrabberBehavior.doGrab(starLink, referer);
        //使用分页链接依次调用排行页抓取简略商品信息
        //如果数据量大可以将这里做成多线程
        List<AmazonSimpleProduct> simpleProducts = new ArrayList<>(100);
        for (String link :
                pageLinks) {
            List<AmazonSimpleProduct> products = listGrabberBehavior.doGrab(link, referer);
            simpleProducts.addAll(products);
        }
        //抓取到所有简略商品信息后，再依次抓取商品详细信息
        //使用spring task & scheduling
        Set<AmazonClothingProduct> clothingProducts = new ConcurrentSkipListSet<>();
        List<Future<List<AmazonClothingProduct>>> futures = startGrabAsy(simpleProducts);
        for (Future<List<AmazonClothingProduct>> f :
                futures) {
            //阻塞等待结果
            try {
                clothingProducts.addAll(f.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.debug("获取商品详细出现异常，将丢弃当前数据", e);
            }
        }
        return new ArrayList<>(clothingProducts);
    }

    /**
     * 发起异步任务进行抓取
     *
     * @param simpleProducts
     * @return
     */
    private List<Future<List<AmazonClothingProduct>>> startGrabAsy(List<AmazonSimpleProduct> simpleProducts) {
        List<Future<List<AmazonClothingProduct>>> futures = new ArrayList<>(100);
        for (AmazonSimpleProduct p :
                simpleProducts) {
            Future<List<AmazonClothingProduct>> future = startProductInfoGrabber(p);
            futures.add(future);
        }
        return futures;
    }

    /**
     * 异步抓取一件商品信息
     *
     * @param p
     * @return
     */
    private Future<List<AmazonClothingProduct>> startProductInfoGrabber(AmazonSimpleProduct p) {
        return defaultGrabberBehaviorTaskScheduler.submit(() -> {
            int grabCount = 0;
            while (++grabCount > 0) {
                try {
                    return productInfoGrabberBehavior.doGrab(p.getDetailUrl(), p.getFromLink());
                } catch (GrabberException e) {
                    if (grabCount > maxRetryCount) {
                        logger.debug("抓取商品: {} 超过最大重试次数: {} ，将跳过", p.getName(), maxRetryCount);
                        break;
                    }
                    logger.debug("抓取商品: " + p.getName() + " 出错,将重新抓取", e);
                }
            }
            return Collections.emptyList();
        });
    }

    @Override
    public String getStarLink() {
        return starLink;
    }

    @Override
    public void setStarLink(String starLink) {
        this.starLink = starLink;
    }

    @Override
    public String getReferer() {
        return referer;
    }

    @Override
    public void setReferer(String referer) {
        this.referer = referer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
