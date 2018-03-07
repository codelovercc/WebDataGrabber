package com.cool.services;

import com.cool.repository.BaseRepository;
import org.hibernate.CacheMode;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Created by codelover on 17/7/13.
 * 抽像的服务基类，实现了{@link Service <T>}接口<br>
 *     如果服务类需要这些基本的服务方法，继承此类
 */
@Transactional
public abstract class AbstractService<T> implements Service<T> {

    private final BaseRepository<T> baseRepository;

    public AbstractService(BaseRepository<T> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public List<T> findAll() {
        return baseRepository.findAll();
    }

    @Override
    public T findById(Serializable id) {
        return baseRepository.findById(id);
    }

    @Override
    public T findById(Serializable id, CacheMode cacheMode) {
        return baseRepository.findById(id, cacheMode);
    }

    @Override
    public T findByIdEager(Serializable id) {
        return baseRepository.findByIdEager(id);
    }

    @Override
    public T delete(T entity) {
        return baseRepository.delete(entity);
    }

    @Override
    public int delete(Serializable id) {
        return baseRepository.delete(id);
    }

    @Override
    public Serializable save(T entity) {
        return baseRepository.save(entity);
    }

    @Override
    public void update(T entity) {
        baseRepository.update(entity);
    }
}
