package com.cool.util;

/**
 * Created by codelover on 17/4/1.
 * 表示一个范围的类
 */
public class RangeImpl<L,R> implements Range<L,R> {
    private L left;
    private R right;

    public RangeImpl() {
    }

    public RangeImpl(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
