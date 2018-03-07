package com.cool.repository;

import com.cool.util.KeyValuePair;

import java.util.Map;

/**
 * Created by codelover on 17/4/1.
 */
public interface WhereHqlGetter {
    /**
     * 生成查询的hql语句
     *
     * @param entityName 要查询的实体名称
     * @param modelName  实体在查询中的别名
     * @param enableSort 启用排序
     * @return
     */
    KeyValuePair<String, Map<String, Object>> getWhereHql(String entityName, String modelName, boolean enableSort);

    /**
     * 生成查询条数的语句
     * @param entityName
     * @param modelName
     * @return
     */
    KeyValuePair<String, Map<String, Object>> getCountHql(String entityName, String modelName);
}
