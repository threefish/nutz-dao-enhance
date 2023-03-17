package org.nutz.dao.enhance.execute;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.method.signature.OutParam;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

/**
 * @author 黄川 2020/12/15
 * 存储过程
 */
@Slf4j
public class CallStoredProcedureExecute extends AbstractExecute {

    public CallStoredProcedureExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        boolean multipleRecords = this.methodSignature.isMultipleRecords();
        if (multipleRecords) {
            return this.returnIsOptionalVal(collectionDataQuery());
        } else {
            return this.returnIsOptionalVal(lineDataQuery());
        }
    }

    /**
     * 输出是out参数
     *
     * @return
     */
    private Object lineDataQuery() {
        Sql sql = getSql();
        dao.execute(sql);
        if (this.methodSignature.isReturnsVoid()) {
            return null;
        }
        return Lang.map2Object(sql.getOutParams(), this.methodSignature.getReturnType());
    }


    /**
     * 集合数据
     *
     * @return
     */
    private Object collectionDataQuery() {
        Sql sql = getSql();
        Class<?> returnGenericType = this.methodSignature.getReturnGenericType() == null ? NutMap.class : this.methodSignature.getReturnGenericType();
        sql.setEntity(dao.getEntity(returnGenericType));
        for (OutParam storedProcedureOutParameter : this.methodSignature.getStoredProcedureOutParameters()) {
            sql.params().set(String.format("OUT%s", storedProcedureOutParameter.name), storedProcedureOutParameter.type);
        }
        sql.setCallback(Sqls.callback.entities());
        dao.execute(sql);
        if (this.methodSignature.isReturnsVoid()) {
            return null;
        }
        return sql.getList(this.methodSignature.getReturnGenericType());
    }

    private Sql getSql() {
        String realSqlString = executeSql;
        for (OutParam storedProcedureOutParameter : this.methodSignature.getStoredProcedureOutParameters()) {
            realSqlString = realSqlString.replaceAll(String.format("@%s", storedProcedureOutParameter.name), String.format("@OUT%s", storedProcedureOutParameter.name));
        }
        Sql sql = Sqls.create(realSqlString).setParams(this.params);
        this.setCondition(sql);
        for (OutParam storedProcedureOutParameter : this.methodSignature.getStoredProcedureOutParameters()) {
            sql.params().set(String.format("OUT%s", storedProcedureOutParameter.name), storedProcedureOutParameter.type);
        }
        return sql;
    }

}
