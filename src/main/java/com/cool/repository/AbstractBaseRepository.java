package com.cool.repository;

import com.cool.util.KeyValuePair;
import com.cool.util.StringUtil;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by codelover on 17/3/22.
 * @param <T> 实体
 */
public abstract class AbstractBaseRepository<T> implements BaseRepository<T> {
    private final String entityName;
    private final Class<T> entityClazz;
    public final String ID = "id";

    /**
     *
     * @param claszz T类型的类信息
     */
    public AbstractBaseRepository(Class<T> claszz) {
        this.entityClazz = claszz;
        this.entityName = claszz.getName();
    }

    public String getEntityName() {
        return entityName;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    protected T get(Class<T> clazz, Serializable id) {
        return getCurrentSession().get(clazz, id);
    }

    /**
     * 查找
     * @param clazz
     * @param id
     * @param cacheMode 二级缓存模式
     * @return
     */
    public T get(Class<T> clazz, Serializable id, CacheMode cacheMode) {
        Session session = getCurrentSession();
        session.setCacheMode(cacheMode);
        return session.get(clazz,id);
    }
    protected Query<T> createQuery(String hql) {
        return getCurrentSession().createQuery(hql);
    }

    @Override
    public T findById(Serializable id){
        return this.get(entityClazz, id);
    }

    @Override
    public T findById(Serializable id, CacheMode cacheMode) {
        return this.get(entityClazz,id, cacheMode);
    }

    @Override
    public T findByIdEager(Serializable id){
        Session session = getCurrentSession();
        session.flush();
        return session.get(entityClazz, id);
    }

    /**
     * 保存一个实体，保存成功实体的id自动更新
     * @param entity
     * @return
     */
    @Override
    public Serializable save(T entity) {
        return getCurrentSession().save(entity);
    }

    @Override
    public Collection<Serializable> saveAll(Collection<T> entities) {
        List<Serializable> list = new LinkedList<>();
        for (T e :
                entities) {
            list.add(getCurrentSession().save(e));
        }
        return list;
    }

    @Override
    public void update(T entity) {
        getCurrentSession().update(entity);
    }


    @Override
    public T delete(T entity) {
        getCurrentSession().delete(entity);
        return entity;
    }

    @Override
    public int delete(Serializable id){
        Query query = getCurrentSession().createQuery("delete " + getEntityName() + " as model where model." + ID + "=:id ");
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public void deleteUseSession(Serializable id){
        this.delete(entityClazz, id);
    }

    protected void delete(Class<T> clazz, Serializable id) {
        getCurrentSession().delete(this.get(clazz, id));
    }

    @Override
    public long findAllCount() {
        CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<T> root = query.from(entityClazz);
        query.select(criteriaBuilder.countDistinct(root));
        Long count = getCurrentSession().createQuery(query).uniqueResult();
        return count == null ? 0 : count;
    }


    @Override
    public List<T> findAll(){
        return this.findAll(entityClazz);
    }

    protected List<T> findAll(Class<T> clazz) {
        return this.findAll(clazz, true);
    }

    protected List<T> findAll(Class<T> clazz, boolean cache) {
        CriteriaQuery<T> query = getCurrentSession().getCriteriaBuilder().createQuery(clazz);
        query.select(query.from(clazz));
        Query<T> query1 = getCurrentSession().createQuery(query);
        query1.setCacheable(cache);
        return query1.list();
    }

    protected long findCount(Class<T> clazz) {
        Field[] fields = clazz.getFields();
        String IdName = "id";
        for (Field field : fields) {
            if (field.getAnnotationsByType(Id.class) != null) {
                IdName = field.getName();
                break;
            }
        }
        String hql = "select count(" + IdName + ") from " + clazz.getTypeName();
        Query<Long> query = getCurrentSession().createQuery(hql,Long.class);
        Long count = query.uniqueResult();
        return count == null ? 0 : count;
    }

    protected long findCount(String entityName, String idColumnName) {
        String hql = "select count(" + idColumnName + ") from " + entityName;
        Query<Long> query = getCurrentSession().createQuery(hql,Long.class);
        Long count = query.uniqueResult();
        return count == null ? 0 : count;
    }

    protected List<T> findByProperty(String propertyName, Object value) {
        String hql = "from " + entityName + " as model where model." + propertyName + "= :pname";
        Query<T> query = getCurrentSession().createQuery(hql,entityClazz);
        query.setParameter("pname", value);
        return query.list();
    }

    protected List<T> findByProperty(Field field, Object value) {
        CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClazz);
        Root<T> root = query.from(entityClazz);
        Path fieldPath = root.get(field.getAnnotation(Column.class).name());
        Predicate predicate = criteriaBuilder.equal(fieldPath, value);
        query.where(predicate);
        return getCurrentSession().createQuery(query).list();
    }

    @Override
    public List<T> findByPaged(T entity, int currentPage, int pageSize,String orderBy) {
        Query<T> query = getPagedQuery(entity, currentPage, pageSize, orderBy);
        return query.list();
    }

    @Override
    public List<T> findByPaged(WhereHqlGetter whereHqlGetter, int currentPage, int pageSize) {
        KeyValuePair<String, Map<String, Object>> keyValuePair = whereHqlGetter.getWhereHql(getEntityName(),
                "pageModel", true);
        Query<T> query = setQueryPaged(keyValuePair.getKey(),keyValuePair.getValue(),currentPage,pageSize);
        return query.list();
    }

    @Override
    public long findCount(WhereHqlGetter whereHqlGetter) {
        KeyValuePair<String,Map<String,Object>> keyValuePair = whereHqlGetter.getCountHql(this.getEntityName(),
                "pageModel");
        Query<Long> query = setQueryCount(keyValuePair.getKey(),keyValuePair.getValue());
        Long count = query.uniqueResult();
        return count == null ? 0 : count;
    }

    /**
     * 分页查询，页码从1开始
     *
     * @param hql
     * @param currentPage
     * @param pageSize
     * @return
     */
    protected List<T> findByPaged(String hql, int currentPage, int pageSize) {
        List lst = null;
        try {
            Query query = getCurrentSession().createQuery(hql);
            if (currentPage != 0 && pageSize != 0) {
                query.setFirstResult((currentPage - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            lst = query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lst;
    }

    /**
     * 为给定的模型做为搜索条件，然后返回符合条件的个数。
     * @param entity
     * @return
     */
    @Override
    public long findCount(T entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(").append(ID).append(") ");
        KeyValuePair<String, Map<String, Object>> result = getHql(entity,sb.toString(),null);
        Query<Long> query = getCurrentSession().createQuery(result.getKey(),Long.class);
        Map<String,Object> ps = result.getValue();
        for (String key :
                ps.keySet()) {
            query.setParameter(key,ps.get(key));
        }
        Long count = query.uniqueResult();
        return count == null ? 0 : count;
    }

    /**
     * 设置分页的query
     * @param hql
     * @param ps 查询使用的参数集合
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Query<T> setQueryPaged(String hql, Map<String,Object> ps, int currentPage,int pageSize){
        Query<T> query = setQuery(hql,ps);
        return setPaged(query,currentPage,pageSize);
    }

    /**
     * 设置Query的参数
     * @param hql
     * @param ps 参数
     * @return
     */
    public Query<T> setQuery(String hql, Map<String,Object> ps){
        Query<T> query = getCurrentSession().createQuery(hql, entityClazz);
        return setQueryParams(ps, query);
    }

    protected Query<T> setQueryParams(Map<String, Object> ps, Query<T> query) {
        for (String key :
                ps.keySet()) {
            query.setParameter(key, ps.get(key));
        }
        return query;
    }

    private Query setQueryParamsNotTyped(Map<String, Object> ps, Query query){
        for (String key :
                ps.keySet()) {
            query.setParameter(key, ps.get(key));
        }
        return query;
    }

    public Query setQueryNotTyped(String hql, Map<String,Object> ps){
        Query query = getCurrentSession().createQuery(hql);
        setQueryParamsNotTyped(ps, query);
        return query;
    }

    /**
     * 获取设置好的Query
     * @param hql
     * @param ps
     * @param resultType
     * @param <E>
     * @return
     */
    public <E>Query<E> getQuery(String hql, Map<String, Object> ps, Class<E> resultType){
        Query<E> query = getCurrentSession().createQuery(hql, resultType);
        for (String key :
                ps.keySet()) {
            query.setParameter(key, ps.get(key));
        }
        return query;
    }

    /**
     * 获取设置好的分页Query
     * @param hql 查询语句
     * @param ps 查询的参数集合
     * @param resultType 返回类型
     * @param currentPage 当前页 从1开始
     * @param pageSize 页大小
     * @param <E> 返回类型
     * @return 返回设置好的Query对象
     */
    public <E>Query<E> getQueryPaged(String hql, Map<String, Object> ps, Class<E> resultType, int currentPage, int pageSize){
        Query<E> query = getQuery(hql,ps,resultType);
        return setPaged(query,currentPage,pageSize);
    }

    /**
     * 设置分页数据
     * @param query
     * @param currentPage 从1开始
     * @param pageSize
     * @param <E>
     * @return
     */
    private <E>Query<E> setPaged(Query<E> query, int currentPage, int pageSize){
        if (currentPage > 0 && pageSize > 0) {
            query.setFirstResult((currentPage - 1) * pageSize);
            query.setMaxResults(pageSize);
        }
        return query;
    }

    /**
     * 设置查询记录个数的Query
     * @param hql
     * @param ps
     * @return
     */
    public Query<Long> setQueryCount(String hql, Map<String,Object> ps){
        Query<Long> query = getCurrentSession().createQuery(hql,Long.class);
        for (String key :
                ps.keySet()) {
            query.setParameter(key, ps.get(key));
        }
        return query;
    }

    public <R>Query<R> setQuery(String hql, Map<String, Object> ps, Class<R> clazz){
        Query<R> query = null;
        if(clazz == null){
            query = getCurrentSession().createQuery(hql);
        }else {
            query = getCurrentSession().createQuery(hql, clazz);
        }
        for (String key :
                ps.keySet()) {
            query.setParameter(key, ps.get(key));
        }
        return query;
    }

    /**
     * 按给定的entity来搜索
     *
     * @param entity
     * @return
     */
    protected Query<T> getPagedQuery(T entity, int currentPage, int pageSize, String orderBy) {
        Query<T> query = getPagedQuery(entity, null, orderBy);
        if (currentPage > 0 && pageSize > 0) {
            query.setFirstResult((currentPage - 1) * pageSize);
            query.setMaxResults(pageSize);
        }
        return query;
    }

    /**
     * 获取参数化的hql
     * @param entity
     * @param startHQL
     * @param orderBy
     * @return
     */
    protected Query<T> getPagedQuery(T entity, String startHQL, String orderBy) {
        KeyValuePair<String, Map<String, Object>> result = getHql(entity, startHQL, orderBy);
        String tmp = result.getKey();
        Map<String, Object> ps = result.getValue();
        Query<T> query = getCurrentSession().createQuery(tmp,entityClazz);
        return setQueryParams(ps, query);
    }

    /**
     * 使用entity来生成HQL
     * @param entity
     * @param startHQL
     * @param orderBy
     * @return
     */
    protected KeyValuePair<String, Map<String, Object>> getHql(T entity, String startHQL, String orderBy) {
        StringBuilder hql = new StringBuilder();
        if (StringUtil.isNotEmpty(startHQL)) {
            hql.append(startHQL);
        }
        hql.append(" from ").append(getEntityName()).append(" as model ");
        HashMap<String, Object> ps = new HashMap<>();
        if (entity != null) {
            Field[] fields = entity.getClass().getFields();
            boolean flag = false;
            for (Field f :
                    fields) {
                String columnName = f.getAnnotation(Column.class).name();
                try {
                    Object value = f.get(entity);
                    if (value != null) {
                        if (!flag)
                            hql.append(" where ");
                        hql.append(" model.").append(columnName).append("=:").append(f.getName()).append(flag ? " " : " and ");
                        flag = true;
                        ps.put(f.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        String tmp = StringUtil.isEmpty(orderBy) ? hql.append(" order by id ").toString() : hql.append(" ").append(orderBy).toString();
        return KeyValuePair.getInstance(tmp, ps);
    }

}
