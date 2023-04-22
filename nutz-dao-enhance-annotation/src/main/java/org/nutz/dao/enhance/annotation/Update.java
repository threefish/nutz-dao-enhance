package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/12/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Update {

    String value();

    /**
     * 根据指定字段批量执行SQL语句
     */
    String loopFor() default "";
}
