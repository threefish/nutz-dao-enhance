package org.nutz.dao.enhance.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.enhance.method.signature.MethodSignature;

/**
 * @author 黄川 2020/12/15
 * 单记录查询
 */
public class SingleQueryExecute extends AbstractExecute {

    public SingleQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        sql.setEntity(dao.getEntity(this.methodSignature.getEntityClass()));
        sql.setCallback(methodSignature.getSqlCallback());
        dao.execute(sql);
        return this.returnIsOptionalVal(sql.getObject(methodSignature.getReturnType()));

    }
}
