package org.nutz.dao.enhance.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.util.ValueTypeUtil;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 黄川 2020/12/15
 * 自定义插入
 */
public class InsertQueryExecute extends AbstractExecute {

    public InsertQueryExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
    }

    @Override
    public Object invoke() {
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        if (this.methodSignature.getReturnType() == void.class) {
            dao.execute(sql);
            return this.returnIsOptionalVal(sql.getUpdateCount());
        } else {
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
            return this.returnIsOptionalVal(idGeneratedKey.get());
        }
    }
}
