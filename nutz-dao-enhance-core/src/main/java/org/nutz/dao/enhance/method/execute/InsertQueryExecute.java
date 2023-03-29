package org.nutz.dao.enhance.method.execute;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.util.ValueTypeUtil;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 黄川 2020/12/15
 * 自定义插入
 */
@Slf4j
@SuppressWarnings("all")
public class InsertQueryExecute extends AbstractExecute {

    public InsertQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql);
        this.setCondition(sql);
        if (methodSignature.isLoop()) {
            Collection<Object> collections = ((Collection) this.params.get(methodSignature.getLoopForField()));
            if (Lang.isEmpty(collections)) {
                return this.returnIsOptionalVal(0);
            }
            boolean returnMultipleRecords = this.methodSignature.isMultipleRecords();
            if (this.methodSignature.isReturnGeneratedKeys() && returnMultipleRecords) {
                List list = new ArrayList();
                for (Object collection : collections) {
                    NutMap paramMap = new NutMap(this.params).setv(methodSignature.getLoopForField(), collection);
                    list.add(invokeAndGet(sql, paramMap));
                }
                return this.returnIsOptionalVal(list);
            }
            for (Object collection : collections) {
                sql.setParams(new NutMap(this.params).setv(methodSignature.getLoopForField(), collection));
                sql.addBatch();
            }
            dao.execute(sql);
            return this.returnIsOptionalVal(sql.getUpdateCount());
        }
        if (this.methodSignature.isReturnGeneratedKeys()) {
            return this.returnIsOptionalVal(invokeAndGet(sql, this.params));
        }
        sql.setParams(params);
        dao.execute(sql);
        return this.returnIsOptionalVal(sql.getUpdateCount());
    }


    private Object invokeAndGet(Sql sql, Map<String, Object> params) {
        sql.setParams(params);
        String originalSql = sql.toPreparedStatement();
        ValueAdaptor[] adaptors = sql.getAdaptors();
        Object[][] paramMatrix = sql.getParamMatrix();
        AtomicReference<Object> idGeneratedKey = new AtomicReference<>();
        dao.run(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(originalSql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            try {
                for (int i = 0; i < paramMatrix[0].length; i++) {
                    adaptors[i].set(preparedStatement, paramMatrix[0][i], i + 1);
                }
                preparedStatement.executeUpdate();
                rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    if (ValueTypeUtil.isNumber(this.methodSignature.getReturnType())) {
                        idGeneratedKey.set(rs.getInt(1));
                    } else {
                        idGeneratedKey.set(rs.getString(1));
                    }
                }
            } finally {
                Daos.safeClose(preparedStatement, rs);
            }
        });
        return idGeneratedKey.get();
    }
}
