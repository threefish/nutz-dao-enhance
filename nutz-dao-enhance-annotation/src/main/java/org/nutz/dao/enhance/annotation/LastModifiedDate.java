package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/9/13
 * java.time.LocalDateTime
 * java.time.LocalDate
 * java.time.LocalTime
 * java.util.Date
 * java.sql.Date
 * java.sql.Timestamp
 * java.lang.Long
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface LastModifiedDate {

    /**
     * nullEffective=true时上面的赋值规则要起效必须是在[当前字段==null]时才能生效
     */
    boolean nullEffective() default false;
}
