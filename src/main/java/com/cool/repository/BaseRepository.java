package com.cool.repository;

import org.hibernate.CacheMode;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by codelover on 17/3/22.
 *
 * @param <T> T is Entity
 */
public interface BaseRepository<T> {
    T findById(Serializable id);

    /**
     *
     * @param id
     * @param cacheMode 二级缓存模式
     * @return
     */
    T findById(Serializable id, CacheMode cacheMode);

    /**
     * 热切的查找一个实体，会先flush Session
     * @param id
     * @return
     */
    T findByIdEager(Serializable id);

    // 保存实体
    Serializable save(T entity);

    // 更新实体
    void update(T entity);

    // 删除实体
    T delete(T entity);

    /**
     * 用hql语句删除
     * @param id 请转换成long类型，不然会报 java.lang.Integer cannot be cast to java.lang.Long
     * @return
     */
    int delete(Serializable id);

    long findAllCount();

    List<T> findAll();

    /**
     * 按给定的实体查询出个数
     *
     * @param entity
     * @return
     */
    long findCount(T entity);

    /**
     * 按实体进行搜索分页
     *
     * @param entity
     * @param currentPage
     * @param pageSize
     * @param orderBy
     * @return
     */
    List<T> findByPaged(T entity, int currentPage, int pageSize, String orderBy);

    /**
     * 按生成的hql进行分页
     *
     * @param whereHqlGetter
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<T> findByPaged(WhereHqlGetter whereHqlGetter, int currentPage, int pageSize);

    /**
     * 按生成的hql查询出记录个数
     *
     * @param whereHqlGetter
     * @return
     */
    long findCount(WhereHqlGetter whereHqlGetter);

    Collection<Serializable> saveAll(Collection<T> entities);
}
