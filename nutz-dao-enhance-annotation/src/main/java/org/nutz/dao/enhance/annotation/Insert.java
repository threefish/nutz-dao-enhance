package org.nutz.dao.enhance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/12/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Insert {
    String value();

    /**
     * 根据指定字段批量执行SQL语句
     */
    String loopFor() default "";

    /**
     * 返回自增ID
     * loop 时不生效
     */
    boolean returnGeneratedKeys() default false;
}
