package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/12/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Insert {
    String value();

    /**
     * 根据指定字段批量执行SQL语句
     */
    String loopFor() default "";

    /**
     * 返回自增ID
     * 采用 loop 时请用list接收
     */
    boolean returnGeneratedKeys() default false;
}
