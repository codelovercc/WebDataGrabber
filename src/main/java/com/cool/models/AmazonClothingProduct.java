package com.cool.models;

import com.cool.util.Range;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codelover on 18/3/7.
 * 衣服类的商品信息
 */
public class AmazonClothingProduct extends AbstractAmazonProduct {
    /**
     *商家ID
     */
    private String merchantID;
    /**
     * 商家名
     */
    private String merchant;
    /**
     * 商品名，商品标题
     */
    private String name;
    /**
     * 商品星级
     */
    private float starts;
    /**
     * 商品浏览数
     */
    private int countOfReviews;
    /**
     * 问答数
     */
    private int countOfAnsweredQuestions;
    /**
     * 商品价格区间
     */
    private Range<Double, Double> rangeOfPrice;

    /**
     * 预期度，百分比数值，如果值为82则表示82%
     */
    private float expected;

    /**
     * key为尺寸，value为这个尺寸下的颜色集合
     * key 取值为 0|B0783QXV1G|One Size (Size 0-12) 的格式，也就是 显示顺序|尺寸编号|尺寸名
     * value 颜色一个项取值为 B074FLC91K|Light Gray|https://images-na.ssl-images-amazon.com/images/I/31A8HirzKxL._SX38_SY50_CR,0,0,38,50_.jpg 的格式，
     * 也就是 颜色码|颜色名|颜色图片链接
     */
    private Map<String, List<String>> sizeAndColor;
    /**
     * 商品说明
     */
    private String featureBullets;
    /**
     * 商品编码
     */
    private String asin;
    /**
     * 首次上架日期
     */
    private Date firstAvailable;
    /**
     * 商品类型 格式 父类*|当前类 例: Clothing,Shoes & Jewelry|Women|Clothing|Dresses
     */
    private String kind;

    /**
     * 大类中的排名
     */
    private int rankOfFirstKind;
    /**
     * 本类中的排名
     */
    private int rankOfSecondKind;

    /**
     * 商品链接
     */
    private String productLink;

    /**
     * 商品缩略图
     */
    private String productThumbnail;

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStarts() {
        return starts;
    }

    public void setStarts(float starts) {
        this.starts = starts;
    }

    public int getCountOfReviews() {
        return countOfReviews;
    }

    public void setCountOfReviews(int countOfReviews) {
        this.countOfReviews = countOfReviews;
    }

    public int getCountOfAnsweredQuestions() {
        return countOfAnsweredQuestions;
    }

    public void setCountOfAnsweredQuestions(int countOfAnsweredQuestions) {
        this.countOfAnsweredQuestions = countOfAnsweredQuestions;
    }

    public Range<Double, Double> getRangeOfPrice() {
        return rangeOfPrice;
    }

    public void setRangeOfPrice(Range<Double, Double> rangeOfPrice) {
        this.rangeOfPrice = rangeOfPrice;
    }

    public float getExpected() {
        return expected;
    }

    public void setExpected(float expected) {
        this.expected = expected;
    }

    public Map<String, List<String>> getSizeAndColor() {
        return sizeAndColor;
    }

    public void setSizeAndColor(Map<String, List<String>> sizeAndColor) {
        this.sizeAndColor = sizeAndColor;
    }

    public String getFeatureBullets() {
        return featureBullets;
    }

    public void setFeatureBullets(String featureBullets) {
        this.featureBullets = featureBullets;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public Date getFirstAvailable() {
        return firstAvailable;
    }

    public void setFirstAvailable(Date firstAvailable) {
        this.firstAvailable = firstAvailable;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getRankOfFirstKind() {
        return rankOfFirstKind;
    }

    public void setRankOfFirstKind(int rankOfFirstKind) {
        this.rankOfFirstKind = rankOfFirstKind;
    }

    public int getRankOfSecondKind() {
        return rankOfSecondKind;
    }

    public void setRankOfSecondKind(int rankOfSecondKind) {
        this.rankOfSecondKind = rankOfSecondKind;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public String getProductThumbnail() {
        return productThumbnail;
    }

    public void setProductThumbnail(String productThumbnail) {
        this.productThumbnail = productThumbnail;
    }
}
