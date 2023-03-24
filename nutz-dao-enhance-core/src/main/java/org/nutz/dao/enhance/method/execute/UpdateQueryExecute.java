package org.nutz.dao.enhance.method.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.util.Collection;

/**
 * @author 黄川 2020/12/15
 * 更新
 */
@SuppressWarnings("all")
public class UpdateQueryExecute extends AbstractExecute {

    public UpdateQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql);
        this.setCondition(sql);
        sql.setEntity(dao.getEntity(this.methodSignature.getEntityClass()));
        sql.setCallback(methodSignature.getSqlCallback());
        if (methodSignature.isLoop()) {
            Collection<Object> collections = ((Collection) this.params.get(methodSignature.getLoopForField()));
            if (Lang.isNotEmpty(collections)) {
                for (Object collection : collections) {
                    sql.setParams(new NutMap(this.params).setv(methodSignature.getLoopForField(), collection));
                    sql.addBatch();
                }
                dao.execute(sql);
            }
        } else {
            sql.setParams(params);
            dao.execute(sql);
        }
        return this.returnIsOptionalVal(sql.getUpdateCount());
    }
}
