package com.cool.models;

import com.cool.util.Range;

/**
 * Created by codelover on 18/3/7.
 * 用来表示在排名列表中的一个商品的简略信息
 */
public class SimpleProduct {
    /**
     * 在本类中的排名，最好的排名为1
     */
    private int rank;
    /**
     * 详情页的链接
     */
    private String detailUrl;
    /**
     * 排名列表中的标题图片链接
     */
    private String thumbnailUrl;
    /**
     * 商品的标题
     */
    private String name;
    /**
     * 商品的星级
     */
    private float starts;
    /**
     * 商品的评价数
     */
    private int reviewsCount;
    /**
     * 商品的售价区间
     */
    private Range<Double, Double> priceRange;
    /**
     * 商品的类型，截取自销售排名的当前类型
     */
    private String kind;

    public SimpleProduct() {
    }

    /**
     * @param rank         在本类中的排名，最好的排名为1
     * @param detailUrl    详情页的链接
     * @param thumbnailUrl 排名列表中的标题图片链接
     * @param name         商品的标题
     * @param starts       商品的星级
     * @param reviewsCount 商品的评价数
     * @param priceRange   商品的售价区间
     * @param kind         商品的类型，截取自销售排名的当前类型
     */
    public SimpleProduct(int rank, String detailUrl, String thumbnailUrl, String name, float starts, int reviewsCount, Range<Double, Double> priceRange, String kind) {
        this.rank = rank;
        this.detailUrl = detailUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.starts = starts;
        this.reviewsCount = reviewsCount;
        this.priceRange = priceRange;
        this.kind = kind;
    }

    /**
     * 在本类中的排名，最好的排名为1
     *
     * @return
     */
    public int getRank() {
        return rank;
    }

    /**
     * 详情页的链接
     *
     * @return
     */
    public String getDetailUrl() {
        return detailUrl;
    }

    /**
     * 商品的标题
     *
     * @return
     */

    public String getName() {
        return name;
    }

    /**
     * 商品的星级
     *
     * @return
     */
    public float getStarts() {
        return starts;
    }

    /**
     * 商品的评价数
     *
     * @return
     */
    public int getReviewsCount() {
        return reviewsCount;
    }

    /**
     * 排名列表中的标题图片链接
     *
     * @return
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * 商品的类型，截取自销售排名的当前类型
     *
     * @return
     */
    public String getKind() {
        return kind;
    }

    /**
     * 商品的售价区间
     *
     * @return
     */
    public Range<Double, Double> getPriceRange() {
        return priceRange;
    }
}
