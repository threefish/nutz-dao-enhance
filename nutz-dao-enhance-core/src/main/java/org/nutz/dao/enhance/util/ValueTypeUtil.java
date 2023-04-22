/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2021/1/3 下午6:07
 */

package org.nutz.dao.enhance.util;

import org.nutz.dao.enhance.pagination.PageRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2021/1/3
 */
public class ValueTypeUtil {

    private static final HashSet<Class<?>> NUMBER_HASH_MAP = new HashSet<>(Arrays.asList(
            int.class, Integer.class, long.class, Long.class, Float.class, float.class, Short.class, short.class
    ));


    /**
     * 是多记录查询,或者分页
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || PageRecord.class == clazz;
    }

    /**
     * 是多记录查询,或者分页
     *
     * @param clazz
     * @return
     */
    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
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
