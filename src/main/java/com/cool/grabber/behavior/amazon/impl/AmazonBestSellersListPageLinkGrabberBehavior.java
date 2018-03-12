package com.cool.grabber.behavior.amazon.impl;

import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.behavior.amazon.AbstractAmazonGrabberBehavior;
import com.cool.grabber.exception.GrabberInvalidDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 用来抓取排行页面中的分页链接
 */
@Component
public class AmazonBestSellersListPageLinkGrabberBehavior extends AbstractAmazonGrabberBehavior<String> {

    @Override
    protected List<String> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        Document document = Jsoup.parse(data, fromLink);
        Elements elements = document.select("ol.zg_pagination>li>a");
        List<String> list = new ArrayList<>(5);
        for (Element e :
                elements) {
            list.add(e.attr("abs:href"));
        }
        return list;
    }

}
