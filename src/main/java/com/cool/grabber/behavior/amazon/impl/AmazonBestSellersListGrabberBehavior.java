package com.cool.grabber.behavior.amazon.impl;

import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.behavior.amazon.AbstractAmazonGrabberBehavior;
import com.cool.grabber.exception.GrabberInvalidDataException;
import com.cool.models.AmazonSimpleProduct;
import com.cool.util.Range;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 销售排行页面抓取列表商品
 */
@Component
public class AmazonBestSellersListGrabberBehavior extends AbstractAmazonGrabberBehavior<AmazonSimpleProduct> {

    @Override
    protected List<AmazonSimpleProduct> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        List<AmazonSimpleProduct> list = new ArrayList<>(20);
        Document document = Jsoup.parse(data, fromLink);
        String kind = findKindTree(document);
        Elements clothingElements = document.select("div.zg_itemImmersion");
        for (Element e :
                clothingElements) {
            int rank = Integer.parseInt(e.select(".zg_rankDiv .zg_rankNumber").text().trim().replace(".", ""));//text() like "1."
            Element itemWrapper = e.select(".zg_itemWrapper").first();
            String detailUrl = itemWrapper.select(".a-link-normal").first().attr("abs:href");
            Elements img = itemWrapper.select(".a-link-normal div.a-spacing-mini img");
            String thumbnailUrl = img.attr("abs:src");
            String name = img.attr("alt");//alt attr is the title
            //find starts
            String startsTitle = e.select(".p13n-asin div.a-icon-row .a-link-normal:eq(0)").attr("title");//title attr like "4.3 out of 5 stars"
            float starts = getStarts(startsTitle);
            int countOfReviews = parseNumber(e.select(".p13n-asin div.a-icon-row .a-size-small").text()).intValue();//text() like "2,080"
            //find price
            String strPrice = e.select(".p13n-asin .a-row .p13n-sc-price").text();//text() like "$18.98 - $20.98" or "$13.99"
            Range<Double, Double> priceRange = parsePrice(strPrice);
            //create a amazon simple product
            AmazonSimpleProduct product = new AmazonSimpleProduct(rank, detailUrl, thumbnailUrl, name,
                    starts, countOfReviews, priceRange, kind, fromLink);
            list.add(product);
        }
        return list;
    }

}
