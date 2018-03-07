package com.cool.util;

/**
 * Created by codelover on 17/7/26.
 * 定义了回调方法，实现此类，将支持某个操作后的回调
 *
 * @param <S> 调用加调的对象
 * @param <P> 回调方法的参数
 *           @param <E> 额外的参数，调用方将会将这个参数原样传给{@link #callBack(Object, Object, Object)}
 */
public interface CallBack<S, P,E> {
    /**
     * 某个操作执行完后，将调用这个方法进行回调
     *
     * @param src  调用方法的对象
     * @param data 某个操作后的结果
     */
    void callBack(S src, P data);

    /**
     * 某个操作执行完后，将调用这个方法进行回调
     * @param src 调用方法的对象
     * @param data 某个操作后的结果
     * @param extend 额外的数据，将原样传给回调
     */
    default void callBack(S src, P data, E extend){};
}
