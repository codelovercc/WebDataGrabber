package com.cool.services;

import com.cool.repository.BaseRepository;
import com.cool.repository.WhereHqlGetter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by codelover on 17/10/21.
 * 抽象的分页服务其类，提供最基础的分页实现,并继承了基础的服务类实现
 * @param <T> 查询实体
 */
@Transactional
public abstract class AbstractPagedService<T> extends AbstractService<T> implements PagedService<T> {

    private final BaseRepository<T> baseRepository;

    protected AbstractPagedService(BaseRepository<T> baseRepository) {
        super(baseRepository);
        this.baseRepository = baseRepository;
    }

    @Override
    public long findCount(T entity) {
        return baseRepository.findCount(entity);
    }

    @Override
    public long findCount(WhereHqlGetter whereHqlGetter) {
        return baseRepository.findCount(whereHqlGetter);
    }

    @Override
    public List<T> findByPaged(T entity, int currentPage, int pageSize, String orderBy) {
        return baseRepository.findByPaged(entity, currentPage, pageSize, orderBy);
    }

    @Override
    public List<T> findByPaged(WhereHqlGetter whereHqlGetter, int currentPage, int pageSize) {
        return baseRepository.findByPaged(whereHqlGetter, currentPage, pageSize);
    }
}
