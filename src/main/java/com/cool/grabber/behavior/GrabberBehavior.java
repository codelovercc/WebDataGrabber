package com.cool.grabber.behavior;

import com.cool.grabber.exception.GrabberException;

import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 数据抓取行为接口
 *
 * @param <T> 抓取后的结果对象
 */
public interface GrabberBehavior<T> {
    /**
     * 进行抓取操作
     *
     * @param grabLink 抓取的页面
     * @param referer  请求时的引用页
     * @return 返回抓取到的集合
     */
    List<T> doGrab(String grabLink, String referer) throws GrabberException;
}
