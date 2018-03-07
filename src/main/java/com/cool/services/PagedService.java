package com.cool.services;

import com.cool.repository.WhereHqlGetter;

import java.util.List;

/**
 * Created by codelover on 17/3/28.
 * 用于分页的Service接口
 * @param <T> 实体
 */
public interface PagedService<T> {

    long findCount(T entity);
    long findCount(WhereHqlGetter whereHqlGetter);
    List<T> findByPaged(T entity, int currentPage, int pageSize, String orderBy);
    List<T> findByPaged(WhereHqlGetter whereHqlGetter, int currentPage, int pageSize);
}
