package com.cool.grabber;

import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 抓取操作的接口
 * @param <T> 抓取后返回的数据
 */
public interface Grabber<T> {
    /**
     * 进行抓取操作
     * @return 返回结果集合
     */
    List<T> get();
}
