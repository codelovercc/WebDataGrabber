package com.cool.grabber.behavior.amazon.impl;

import com.cool.grabber.Grabber;
import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.behavior.GrabberBehavior;
import com.cool.grabber.behavior.amazon.AbstractAmazonGrabberBehavior;
import com.cool.grabber.exception.GrabberException;
import com.cool.grabber.exception.GrabberInvalidDataException;
import com.cool.models.AmazonClothingProduct;
import com.cool.util.KeyValuePair;
import com.cool.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by codelover on 18/3/7.
 */
@Component
public class AmazonClothingProductInfoGrabberBehavior extends AbstractAmazonGrabberBehavior<AmazonClothingProduct> {

    @Autowired
    private GrabberBehavior<String> amazonClothingColorInfoOfSizeGrabberBehavior;

    @Override
    protected List<AmazonClothingProduct> getModels(String data, String fromLink) throws GrabberInvalidDataException {
        Document document = Jsoup.parse(data, fromLink);
        Element addToCartForm = document.getElementById("addToCart");
        if (addToCartForm == null) {
            throw new GrabberInvalidDataException("没有找到关键数据的form表单，请确定页面是否已经更改或该页面是否为商品详情页");
        }
        AmazonClothingProduct product = new AmazonClothingProduct();
        //set the merchant ID
        product.setMerchantID(addToCartForm.getElementById("merchantID").val());
        Element brand = document.getElementById("brand");
        if (brand == null) {
            brand = document.getElementById("bylineInfo");
            if(brand == null) {
                throw new GrabberInvalidDataException("没有找到商家名");
            }
        }
        String tmpStr = brand.attr("href");//like /ODODOS/b/ref=w_bl_sl_ap_ap_web_15982972011?ie=UTF8&node=15982972011&field-lbr_brands_browse-bin=ODODOS
        tmpStr = tmpStr.substring(1);//remove the first letter "/"
        tmpStr = tmpStr.split("/")[0].replace('-',' ');
        if(tmpStr.equals("s")){
            tmpStr = brand.text();
        }
        //set the merchant name
        product.setMerchant(tmpStr);
        //set the product name
        product.setName(document.getElementById("productTitle").text().trim());
        String startsTitle = document.select("#acrPopover:eq(0)").attr("title");//text() like "4.3 out of 5 stars"
        //set the starts
        product.setStarts(getStarts(startsTitle));
        //set the count of reviews
        product.setCountOfReviews(parseNumber(document.getElementById("acrCustomerReviewText").text().split(" ")[0]).intValue());//text() like "2,085 customer reviews"
        //set the count of answered questions
        product.setCountOfAnsweredQuestions(parseNumber(document.select("#askATFLink .a-size-base").text().split(" ")[0]).intValue());//text() like "143 answered questions"
        product.setRangeOfPrice(parsePrice(document.getElementById("priceblock_ourprice").text()));
        tmpStr = document.getElementById("fitRecommendationsSupportingStatement").select("b:eq(0)").text().replace("%", "");
        //set the expected percent
        product.setExpected(parseNumber(tmpStr).floatValue());
        List<String> sizeList = getAvailableSizeList(document);
        // TODO: 18/3/9 有一种情况是只有一种尺寸，比如说是均码，那么上面尺寸列表将会是空的，如果是均码的情况下，那么只需要获取均码的名字和当前页所有颜色就好了
        Map<String, List<String>> sizeAndColor = getSizeAndColors(fromLink, sizeList);
        //set the size and color
        product.setSizeAndColor(sizeAndColor);
        //set the feature bullets
        product.setFeatureBullets(document.getElementById("feature-bullets").text());
        //region get the first available date and asin
        String availableDate = null, asin = null;
        Elements detailBullets = document.select("#detailBullets_feature_div li>.a-list-item");
        for (Element e :
                detailBullets) {
            if (e.children().size() > 0 && e.children().hasText()) {
                if (e.text().contains("Date first available")) {
                    //first available date
                    availableDate = e.children().select(":not(.a-text-bold)").text();
                } else if (e.text().contains("ASIN:")) {
                    asin = e.select(":not(.a-text-bold):eq(0)").text().split(":")[1].trim();
                }
            }
        }
        if (asin == null || availableDate == null) {
            throw new GrabberInvalidDataException("没有找到ASIN或者没有找到首次上架时间");
        }
        //endregion
        //set the asin
        product.setAsin(asin);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        try {
            //set the first available date
            product.setFirstAvailable(dateFormat.parse(availableDate));
        } catch (ParseException e) {
            throw new GrabberInvalidDataException("转换首次上架时间的为日期失败");
        }
        String kind = document.select("#wayfinding-breadcrumbs_feature_div>ul").text();
        kind = kind.replace(" › ", "|");
        //set the kind
        product.setKind(kind);
        //region get rank infomation
        Element saleRank = document.getElementById("SalesRank");
        String rankOfFirstKind = saleRank.text();
        List<KeyValuePair<Integer, String>> rankInKindList = new ArrayList<>(5);
        /**
         * find all matches like below in single line
         * Amazon Best Sellers Rank: #1 in Clothing, Shoes & Jewelry (See Top 100 in Clothing, Shoes & Jewelry)
         * #1 in Clothing, Shoes & Jewelry > Women > Clothing > Active > Active Pants
         * #1 in Sports & Outdoors > Sports & Fitness > Exercise & Fitness > Yoga > Clothing > Women > Pants
         * #1 in Sports & Outdoors > Sports & Fitness > Clothing > Women > Pants
         */
        String[] ranksInKind = rankOfFirstKind.split("#");
        for (String oneRankInKind :
                ranksInKind) {
            Matcher matcher = Pattern.compile("(^\\d+[\\w\\s\\W]+$)")
                    .matcher(oneRankInKind);
            if(matcher.find()){
                String m = matcher.group();
                int i = m.indexOf('(');
                if (i != -1) {
                    m = m.substring(0, i);
                }
                String[] rk = m.split("\\bin\\b");
                KeyValuePair<Integer, String> rankInKind = KeyValuePair.getInstance(
                        Integer.parseInt(rk[0].trim()),
                        rk[1].trim().replace(" > ", "|"));
                rankInKindList.add(rankInKind);
            }
        }
        if(rankInKindList.isEmpty()){
            throw new GrabberInvalidDataException("没有找到详细的排名信息");
        }
        //endregion
        //set the rank
        product.setRankInKind(rankInKindList);
        product.setRankOfFirstKind(rankInKindList.get(0).getKey());
        if(rankInKindList.size()>=2){
            product.setRankOfSecondKind(rankInKindList.get(1).getKey());
        }
        //set the product link
        product.setProductLink(fromLink);
        String thumbnail = document.getElementById("landingImage").attr("abs:src");
        if(thumbnail.isEmpty()){
            thumbnail = document.getElementById("landingImage").attr("data-old-hires");
        }
        //set the product landing image
        product.setProductThumbnail(thumbnail);
        return Collections.singletonList(product);
    }

    private Map<String, List<String>> getSizeAndColors(String fromLink, List<String> sizeList) throws GrabberInvalidDataException {
        Map<String, List<String>> sizeAndColor = new HashMap<>(20);
        for (String size :
                sizeList) {
            String sizeCode = size.split("\\|")[1];
            //fromLink like this https://www.amazon.com/ODODOS-Control-Workout-Running-Leggings/dp/B071SDN5H8/ref=zg_bs_1040660_1?_encoding=UTF8&refRID=AT29K55QMQJ0AZEDPGTX&th=1&psc=1
            //and colorLink like this https://www.amazon.com/ODODOS-Control-Workout-Running-Leggings/dp/{sizeCode}/ref=zg_bs_1040660_1?_encoding=UTF8&refRID=AT29K55QMQJ0AZEDPGTX&th=1&psc=1
            //so we replace the {sizeCode} into the link then we can grab color info
            String colorLink = fromLink.replaceAll("/dp/[A-Z0-9]+/ref", "/dp/" + sizeCode + "/ref");
            try {
                List<String> colors = amazonClothingColorInfoOfSizeGrabberBehavior.doGrab(colorLink, fromLink);
                sizeAndColor.put(size, colors);
            } catch (GrabberException e) {
                throw new GrabberInvalidDataException("抓取尺寸对应的颜色出错", e);
            }
        }
        return sizeAndColor;
    }

    private List<String> getAvailableSizeList(Document document) throws GrabberInvalidDataException {
        Elements sizeElements = document.select("select#native_dropdown_selected_size_name option.dropdownAvailable");
        List<String> sizeList = new ArrayList<>(5);
        for (Element e :
                sizeElements) {
            //e is OPTION tag
            String sizeStr = e.val();//like "0,B071XSRRGB"
            sizeStr = sizeStr.replace(',', '|');
            sizeStr += "|" + e.text();//sizeStr format to 显示顺序|尺寸编号|尺寸名
            sizeList.add(sizeStr);
        }
        return sizeList;
    }
}
