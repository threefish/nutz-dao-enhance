package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/9/13
 * 需要实现 org.nutz.dao.enhance.audit.AuditingEntity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface CreatedBy {
    /**
     * nullEffective=true时上面的赋值规则要起效必须是在[当前字段==null]时才能生效
     */
    boolean nullEffective() default true;
}
