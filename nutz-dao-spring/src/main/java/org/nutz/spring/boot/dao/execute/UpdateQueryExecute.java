package org.nutz.spring.boot.dao.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.spring.boot.dao.spring.binding.method.MethodSignature;

/**
 * @author 黄川 2020/12/15
 * 单记录查询
 */
public class UpdateQueryExecute extends AbstractExecute {

    public UpdateQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        sql.setEntity(dao.getEntity(this.methodSignature.getReturnEntityClass()));
        sql.setCallback(methodSignature.getSqlCallback());
        dao.execute(sql);
        return sql.getUpdateCount();
    }
}
