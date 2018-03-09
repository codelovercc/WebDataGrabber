package com.cool.grabber.behavior.impl;

import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.exception.GrabberInvalidDataException;
import com.cool.models.AmazonClothingProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by codelover on 18/3/7.
 */
@Component
public class AmazonClothingProductInfoGrabberBehavior extends AbstractGrabberBehavior<AmazonClothingProduct> {
    @Override
    protected List<AmazonClothingProduct> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        Document document = Jsoup.parse(data, fromLink);

        return null;
    }
}
