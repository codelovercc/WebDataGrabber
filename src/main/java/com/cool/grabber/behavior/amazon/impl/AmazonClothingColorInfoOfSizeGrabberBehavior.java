package com.cool.grabber.behavior.amazon.impl;

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
 * Created by codelover on 18/3/9.
 * 用于抓取衣服尺寸对应的可用的颜色
 */
@Component
public class AmazonClothingColorInfoOfSizeGrabberBehavior extends AbstractAmazonGrabberBehavior<String> {
    @Override
    protected List<String> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        Document document = Jsoup.parse(data, fromLink);
        Elements colorElements = document.select("#variation_color_name ul.a-unordered-list li:not(.swatchUnavailable)");
        List<String> colorList = new ArrayList<>(20);
        for (Element e :
                colorElements) {
            //e is LI tag
            String colorCode = e.attr("data-defaultasin");
            Element img = e.select("img.imgSwatch").first();
            colorCode += "|" + img.attr("alt") + "|" + img.attr("abs:src");
            colorList.add(colorCode);
        }
        return colorList;
    }
}
