package org.nutz.dao.enhance.test.provider;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.provider.ProviderContext;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/6/14
 */
public class TestProvider {

    /**
     * 通过自定义扩展，实现插入并返回自增ID
     *
     * @param providerContext
     * @param name
     * @param age
     * @param create
     * @return
     */
    public static int insertWithCustomprovider(ProviderContext providerContext, String name, int age, String create) {
        String executeSql = providerContext.getExecuteSql();
        Dao dao = providerContext.getDao();
        Map<String, Object> params = new HashMap<>(3);
        params.put("name", name);
        params.put("age", age);
        params.put("create", create);
        Sql sql = Sqls.create(executeSql).setParams(params);
        String originalSql = sql.toPreparedStatement();
        ValueAdaptor[] adaptors = sql.getAdaptors();
        Object[][] paramMatrix = sql.getParamMatrix();
        AtomicReference<Integer> idGeneratedKey = new AtomicReference<>();
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
                    idGeneratedKey.set(rs.getInt(1));
                }
            } finally {
                Daos.safeClose(preparedStatement, rs);
            }
        });
        return idGeneratedKey.get();
    }

}
