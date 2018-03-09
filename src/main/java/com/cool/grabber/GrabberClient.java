package com.cool.grabber;

import com.cool.grabber.exception.GrabberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.*;

/**
 * Created by codelover on 18/3/7.
 * 这里将初始化配置，然后根据配置进行抓取调度<br>
 * 观察此对象，将会在抓取到数据后进行通知并传递抓取的数据
 */
@Component
public class GrabberClient extends Observable implements ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(GrabberClient.class);
    @Value("com.cool.grabbers")
    private List<GrabberConfig> grabberConfigs;
    @Value("${com.cool.grabber.max.grab.count}")
    private int maxRetryCount;
    @Autowired
    private ThreadPoolTaskScheduler defaultGrabberTaskScheduler;

    private List<Grabber<Object>> grabbers;
    private ApplicationContext applicationContext;

    public List<GrabberConfig> getGrabberConfigs() {
        return grabberConfigs;
    }

    public void setGrabberConfigs(List<GrabberConfig> grabberConfigs) {
        this.grabberConfigs = grabberConfigs;
    }

    @PostConstruct
    public void init() {
        grabbers = new ArrayList<>(5);
        for (GrabberConfig g :
                grabberConfigs) {
            try {
                Grabber<Object> grabber = applicationContext.getBean(Class.forName(g.getBeanName()).asSubclass(Grabber.class));
                grabber.setReferer(g.getReferer());
                grabber.setStarLink(g.getStartLink());
                grabber.setName(g.getName());
                grabbers.add(grabber);
            } catch (ClassNotFoundException | ClassCastException e) {
                logger.debug("抓取器: " + g.getName() + " 未能初始化", e);
            } catch (BeansException e) {
                logger.debug("抓取器: " + g.getName() + " bean初始化异常", e);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 简单的流式抓取，如果其中一个抓取器出现问题那么将会跳过抓取
     */
    public void startGrabber() {
        List<Object> list = new ArrayList<>(500);
        for (Grabber g :
                grabbers) {
            try {
                list.addAll(g.get());
            } catch (GrabberException e) {
                logger.debug("抓取器: " + g.getName() + " 出现异常,将跳过抓取", e);
            }
        }
        setChanged();
        notifyObservers(list);
    }

    /**
     * 使用线程来启动抓取器，并在抓取器失败时将重新尝试抓取
     */
    @Async
    public void startGrabberBack() {
        List<Future<List<Object>>> futures = new ArrayList<>(10);
        for (Grabber<Object> g :
                grabbers) {
            Future<List<Object>> future = startGrabberAsy(g);
            futures.add(future);
        }
        List<Object> list = new ArrayList<>(500);
        for (Future<List<Object>> future :
                futures) {
            //阻塞当前线程，得到任务的执行结果
            try {
                list.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.debug("获取结果出现异常，将丢弃当前结果", e);
            }
        }
        setChanged();
        notifyObservers(list);
    }

    private Future<List<Object>> startGrabberAsy(Grabber<Object> g) {
        return defaultGrabberTaskScheduler.submit(() -> {
            int grabCount = 0;
            while (++grabCount > 0) {
                try {
                    return g.get();
                } catch (GrabberException e) {
                    if (grabCount > maxRetryCount) {
                        logger.debug("抓取商品: {} 超过最大重试次数: {} ，将跳过", g.getName(), maxRetryCount);
                        break;
                    }
                    logger.debug("抓取器: " + g.getName() + " 出现异常,将立即重试", e);
                }
            }
            return Collections.emptyList();
        });
    }
}
