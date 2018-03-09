package com.cool.grabber.behavior.impl;

import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.exception.GrabberInvalidDataException;
import com.cool.models.AmazonSimpleProduct;
import com.cool.util.Range;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 销售排行页面抓取列表商品
 */
@Component
public class AmazonBestSellersListGrabberBehavior extends AbstractGrabberBehavior<AmazonSimpleProduct> {

    @Override
    protected List<AmazonSimpleProduct> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        List<AmazonSimpleProduct> list = new ArrayList<>(20);
        Document document = Jsoup.parse(data, fromLink);
        String kind = findKindTree(document);
        Elements clothingElements = document.select("div.zg_itemImmersion");
        for (Element e :
                clothingElements) {
            int rank = Integer.parseInt(e.select(".zg_rankDiv .zg_rankNumber").text().replace(".", ""));//text() like "1."
            Element itemWrapper = e.select(".zg_itemWrapper").first();
            String detailUrl = itemWrapper.select(".a-link-normal").attr("href");
            Elements img = itemWrapper.select(".a-link-normal div.a-spacing-mini img");
            String thumbnailUrl = img.attr("src");
            String name = img.attr("alt");//alt attr is the title
            //find starts
            String startsTitle = e.select(".p13n-asin div.a-icon-row .a-link-normal:first").attr("title");//title attr like "4.3 out of 5 stars"
            startsTitle = startsTitle.split(" ")[0];
            float starts = Float.parseFloat(startsTitle);
            int countOfReviews = Integer.parseInt(e.select(".p13n-asin div.a-icon-row .a-size-small").text().replace(",", ""));//text() like "2,080"
            //find price
            String strPrice = e.select(".p13n-asin .a-row .p13n-sc-price").text();//text() like "$18.98 - $20.98" or "$13.99"
            Range<Double, Double> priceRange = Range.getInstance();
            if (strPrice.contains("-")) {
                String[] prices = strPrice.trim().replace("$", "").split("-");
                priceRange.setLeft(Double.parseDouble(prices[0]));
                priceRange.setRight(Double.parseDouble(prices[1]));
            } else {
                double p = Double.parseDouble(strPrice.trim().replace("$", ""));
                priceRange.setLeft(p);
                priceRange.setRight(p);
            }
            AmazonSimpleProduct product = new AmazonSimpleProduct(rank, detailUrl, thumbnailUrl, name,
                    starts, countOfReviews, priceRange, kind, fromLink);
            list.add(product);
        }
        return list;
    }

    private String findKindTree(Document document) throws GrabberInvalidDataException {
        Element element = document.getElementById("zg_browseRoot");
        if (element == null) {
            throw new GrabberInvalidDataException("获取类型列表失败");
        }
        String kind = "";
        Element browseUpElement;
        while ((browseUpElement = element.select("li.zg_browseUp:first").first()) != null) {
            kind += browseUpElement.select("a").text() + "|";
        }
        kind += element.select(".zg_selected").text();
        return kind;
    }
}
