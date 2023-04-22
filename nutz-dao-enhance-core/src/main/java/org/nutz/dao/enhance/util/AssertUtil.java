package org.nutz.dao.enhance.util;

import org.nutz.lang.Strings;

import java.util.Collection;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/1
 */
public class AssertUtil {

    public static void isNotTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cant't be null");
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void notBlank(String str, String message) {
        if (Strings.isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(String str) {
        if (Strings.isBlank(str)) {
            throw new IllegalArgumentException("string cant't be empty");
        }
    }
}
