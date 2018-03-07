package com.cool.grabber.impl;

import com.cool.grabber.Grabber;
import com.cool.models.SimpleProduct;

import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 用来抓取销售排名页面中的列表商品简略信息
 */
public class BestSellersListGrabber implements Grabber<SimpleProduct> {

    @Override
    public List<SimpleProduct> get() {
        return null;
    }
}
