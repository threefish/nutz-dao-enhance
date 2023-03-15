package org.nutz.dao.enhance.execute;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.method.signature.OutParam;
import org.nutz.dao.entity.Record;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 2020/12/15
 * 存储过程
 */
@Slf4j
public class CallFunctionExecute extends AbstractExecute {

    public CallFunctionExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
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
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        NutMap data = NutMap.NEW();
        dao.run(connection -> {
            String originalSql = sql.toPreparedStatement();
            ValueAdaptor[] adaptors = sql.getAdaptors();
            Object[][] paramMatrix = sql.getParamMatrix();
            CallableStatement callableStatement = connection.prepareCall(originalSql);
            for (int i = 0; i < paramMatrix[0].length; i++) {
                adaptors[i].set(callableStatement, paramMatrix[0][i], i + 1);
            }
            for (OutParam storedProcedureOutParameter : this.methodSignature.getStoredProcedureOutParameters()) {
                callableStatement.registerOutParameter(storedProcedureOutParameter.index, storedProcedureOutParameter.type);
            }
            callableStatement.execute();
            for (OutParam storedProcedureOutParameter : this.methodSignature.getStoredProcedureOutParameters()) {
                data.put(storedProcedureOutParameter.name, callableStatement.getObject(storedProcedureOutParameter.index));
            }
        });
        return Lang.map2Object(data, this.methodSignature.getReturnType());
    }

    /**
     * 集合数据
     *
     * @return
     */
    private List collectionDataQuery() {
        List dataList = new ArrayList<>();
        dao.run(connection -> {
            CallableStatement callableStatement = connection.prepareCall(this.executeSql);
//            callableStatement.setString(1, "1");
//            callableStatement.registerOutParameter(2, Types.REF_CURSOR);
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();
            if (resultSet != null) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                ResultSetLooping ing = new ResultSetLooping() {
                    @Override
                    protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
                        NutMap re = new NutMap();
                        Record.create(re, rs, metaData);
                        list.add(re);
                        return true;
                    }
                };
                ing.doLoop(resultSet, new SqlContext());
                for (Object row : ing.getList()) {
                    dataList.add(Lang.map2Object((NutMap) row, this.methodSignature.getReturnGenericType()));
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("'{}' 未返回 resultSet ", this.executeSql);
                }
            }
        });
        return dataList;
    }
}
