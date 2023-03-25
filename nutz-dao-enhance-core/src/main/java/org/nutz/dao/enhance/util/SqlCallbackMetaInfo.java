package org.nutz.dao.enhance.util;

import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class SqlCallbackMetaInfo {
    /**
     * 通用的
     */
    public static final HashMap<Class<?>, SqlCallback> COMMON_SQL_CALLBACK_HASH_MAP = new HashMap() {
        {
            put(int.class, Sqls.callback.integer());
            put(Integer.class, Sqls.callback.integer());
            put(Long.class, Sqls.callback.longValue());
            put(long.class, Sqls.callback.longValue());
            put(float.class, Sqls.callback.floatValue());
            put(Float.class, Sqls.callback.floatValue());
            put(Double.class, Sqls.callback.doubleValue());
            put(double.class, Sqls.callback.doubleValue());
            put(java.sql.Timestamp.class, Sqls.callback.timestamp());
            put(java.sql.Date.class, Sqls.callback.timestamp());
            put(java.util.Date.class, Sqls.callback.timestamp());
            put(String.class, Sqls.callback.str());
            put(Map.class, Sqls.callback.map());
            put(boolean.class, Sqls.callback.bool());
            put(Boolean.class, Sqls.callback.bool());
            put(Blob.class, Sqls.callback.blob());
            put(Record.class, Sqls.callback.record());
            put(NutMap.class, Sqls.callback.map());
            put(PageRecord.class, Sqls.callback.entities());

        }
    };

    /**
     * 对应的list返回类型
     */
    public static final HashMap<Class<?>, SqlCallback> LIST_SQL_CALLBACK_HASH_MAP = new HashMap() {
        {
            put(Integer.class, Sqls.callback.ints());
            put(int.class, Sqls.callback.ints());
            put(Long.class, Sqls.callback.longs());
            put(long.class, Sqls.callback.longs());
            put(String.class, Sqls.callback.strList());
            put(Boolean.class, Sqls.callback.bools());
            put(Record.class, Sqls.callback.records());
            put(Map.class, Sqls.callback.maps());

            put(String[].class, Sqls.callback.strs());
            put(int[].class, Sqls.callback.ints());
            put(Integer[].class, Sqls.callback.ints());
            put(long[].class, Sqls.callback.longs());
            put(Long[].class, Sqls.callback.longs());
            put(boolean[].class, Sqls.callback.bools());
            put(Boolean[].class, Sqls.callback.bools());
        }
    };


    /**
     * 获取callback类型
     *
     * @param clazz
     * @return
     */
    public static SqlCallback getCommonSqlCallback(Class<?> clazz) {
        SqlCallback sqlCallback = COMMON_SQL_CALLBACK_HASH_MAP.get(clazz);
        if (sqlCallback != null) {
            return sqlCallback;
        }
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            if (componentType == Integer.class || componentType == int.class) {
                return Sqls.callback.ints();
            }
            if (componentType == String.class) {
                return Sqls.callback.strs();
            }
            if (componentType == Long.class || componentType == long.class) {
                return Sqls.callback.longs();
            }
            if (componentType == Float.class || componentType == float.class) {
                return Sqls.callback.longs();
            }
            return new QueryObjectArrayCallback();
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new QueryObjectCollectionCallback();
        }
        return null;
    }

    /**
     * 获取callback类型
     *
     * @param clazz
     * @return
     */
    public static SqlCallback getCollectionSqlCallback(Class<?> clazz) {
        return LIST_SQL_CALLBACK_HASH_MAP.get(clazz);
    }

    public static class QueryObjectArrayCallback implements SqlCallback {

        @Override
        public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
            ArrayList<Object> list = new ArrayList(20);
            while (rs.next()) {
                list.add(rs.getObject(1));
            }
            return list.toArray();
        }

    }

    public static class QueryObjectCollectionCallback implements SqlCallback {

        @Override
        public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
            ArrayList<Object> list = new ArrayList(20);
            while (rs.next()) {
                list.add(rs.getObject(1));
            }
            return list;
        }

    }

}
