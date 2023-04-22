package org.nutz.dao.enhance.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/22
 */
public class PatternUtil {

    private static final Pattern BEAN_NAME_PATTERN = Pattern.compile("\\$ioc:(.*?)\\.");

    /**
     * 按表达式查找Bean名称
     *
     * @param expression
     * @return
     */
    public static String findBeanNameByExpression(String expression) {
        Matcher matcher = BEAN_NAME_PATTERN.matcher(expression);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
