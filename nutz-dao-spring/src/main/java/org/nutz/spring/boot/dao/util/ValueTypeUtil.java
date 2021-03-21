/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2021/1/3 下午6:07
 */

package org.nutz.spring.boot.dao.util;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.SqlCallback;

import java.sql.Blob;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/1/3
 */
public class ValueTypeUtil {

    private static final HashMap<Class<?>, SqlCallback> SQL_CALLBACK_HASH_MAP = new HashMap() {
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


    private static final HashSet<Class<?>> NUMBER_HASH_MAP = new HashSet<>(Arrays.asList(
            int.class, Integer.class, long.class, Long.class, Float.class, float.class, Short.class, short.class
    ));


    /**
     * 是多记录查询
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 获取callback类型
     *
     * @param clazz
     * @return
     */
    public static SqlCallback getSqlCallback(Class<?> clazz) {
        return SQL_CALLBACK_HASH_MAP.get(clazz);
    }


    /**
     * 是数字类型
     *
     * @param clazz
     * @return
     */
    public static boolean isNumber(Class<?> clazz) {
        return NUMBER_HASH_MAP.contains(clazz);
    }
}
