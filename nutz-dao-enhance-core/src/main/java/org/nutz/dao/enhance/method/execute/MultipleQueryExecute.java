package org.nutz.dao.enhance.method.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.sql.Sql;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 黄川 2020/12/15
 * 多记录查询
 */
public class MultipleQueryExecute extends AbstractExecute {

    public MultipleQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        sql.setEntity(dao.getEntity(this.methodSignature.getEntityClass()));
        sql.setCallback(methodSignature.getSqlCallback());
        dao.execute(sql);
        List<?> listData = sql.getList(methodSignature.getEntityClass());
        if (Set.class.isAssignableFrom(methodSignature.getReturnType())) {
            return this.returnIsOptionalVal(listData.stream().collect(Collectors.toSet()));
        }
        return this.returnIsOptionalVal(listData);
    }
}
