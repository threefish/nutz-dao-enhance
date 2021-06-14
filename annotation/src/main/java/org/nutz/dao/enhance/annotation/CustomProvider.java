package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/6/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomProvider {

    /**
     * 指定实现 SQL 查询的静态类
     */
    Class<?> type();

    /**
     * 指定实现 SQL 查询的静态类中的具体方法名
     * @return
     */
    String methodName();

}
