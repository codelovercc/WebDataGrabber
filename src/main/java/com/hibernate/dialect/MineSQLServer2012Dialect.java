package com.hibernate.dialect;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * Created by codelover on 17/10/24.
 * 重写了sql 2012({@link SQLServer2012Dialect}) 的方言，添加了一些函数
 *
 */
public class MineSQLServer2012Dialect extends SQLServer2012Dialect {
    public MineSQLServer2012Dialect() {
        super();
        registerFunction("replace", new StandardSQLFunction("replace"));
        registerFunction("isnull", new StandardSQLFunction("isnull"));
        registerFunction("stuff", new StandardSQLFunction("stuff"));
        registerFunction("GetArrayStringLengthWithoutNullCell", new StandardSQLFunction("dbo.GetArrayStringLengthWithoutNullCell", StandardBasicTypes.INTEGER));
        registerFunction("charindex", new StandardSQLFunction("charindex"));

    }
}
