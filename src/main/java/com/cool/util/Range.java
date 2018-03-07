package com.cool.util;

/**
 * Created by codelover on 17/4/11.
 */
public interface Range<L,R> {
    L getLeft();
    void setLeft(L left);
    R getRight();
    void setRight(R right);
    static <L,R> Range<L,R> getInstance(L left, R right){
        return new RangeImpl<L, R>(left,right);
    }
    static <L,R> Range<L,R> getInstance(){
        return new RangeImpl<L, R>();
    }
}
