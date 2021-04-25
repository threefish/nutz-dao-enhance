package org.nutz.dao.enhance.util;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.SqlCallback;

import java.sql.Blob;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class SqlCallbackUtil {
    /**
     * 通用的
     */
    private static final HashMap<Class<?>, SqlCallback> COMMON_SQL_CALLBACK_HASH_MAP = new HashMap() {
        {
            put(Integer.class, Sqls.callback.integer());
            put(int.class, Sqls.callback.integer());
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
            put(String[].class, Sqls.callback.strs());
            put(int[].class, Sqls.callback.strs());
            put(Integer[].class, Sqls.callback.ints());
            put(long[].class, Sqls.callback.longs());
            put(Long[].class, Sqls.callback.longs());
            put(Map.class, Sqls.callback.map());
            put(HashMap.class, Sqls.callback.map());
            put(LinkedHashMap.class, Sqls.callback.map());
            put(Boolean.class, Sqls.callback.bool());
            put(Blob.class, Sqls.callback.blob());
            put(Record.class, Sqls.callback.record());
        }
    };

    /**
     * 对应的list返回类型
     */
    private static final HashMap<Class<?>, SqlCallback> LIST_SQL_CALLBACK_HASH_MAP = new HashMap() {
        {
            put(Integer.class, Sqls.callback.ints());
            put(int.class, Sqls.callback.ints());
            put(Long.class, Sqls.callback.longs());
            put(long.class, Sqls.callback.longs());
            put(String.class, Sqls.callback.strList());
            put(String[].class, Sqls.callback.strs());
            put(int[].class, Sqls.callback.ints());
            put(Integer[].class, Sqls.callback.ints());
            put(long[].class, Sqls.callback.longs());
            put(Long[].class, Sqls.callback.longs());
            put(Boolean.class, Sqls.callback.bools());
            put(Record.class, Sqls.callback.records());
        }
    };


    /**
     * 获取callback类型
     *
     * @param clazz
     * @return
     */
    public static SqlCallback getCommonSqlCallback(Class<?> clazz) {
        return COMMON_SQL_CALLBACK_HASH_MAP.get(clazz);
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

}
