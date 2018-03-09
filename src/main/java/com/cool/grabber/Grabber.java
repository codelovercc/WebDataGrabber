package com.cool.grabber;

import com.cool.grabber.exception.GrabberException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by codelover on 18/3/7.
 * 抓取操作的接口
 * @param <T> 抓取后返回的数据
 */
public interface Grabber<T> {
    /**
     * 进行抓取操作,单线程进行操作
     * @return 返回结果集合
     */
    List<T> get() throws GrabberException;

    String getStarLink();

    void setStarLink(String starLink);

    String getReferer();

    void setReferer(String referer);

    String getName();

    void setName(String name);
}
