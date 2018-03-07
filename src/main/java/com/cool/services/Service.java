package com.cool.services;

import org.hibernate.CacheMode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by codelover on 17/4/10.
 * 所有服务类的基接口
 * @param <T> 使用的模型对象
 */
public interface Service<T> {
    List<T> findAll();
    T findById(Serializable id);
    T findById(Serializable id, CacheMode cacheMode);
    T findByIdEager(Serializable id);
    T delete(T entity);
    int delete(Serializable id);
    Serializable save(T entity);
    void update(T entity);

}
