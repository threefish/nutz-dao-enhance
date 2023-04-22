package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 字段计算功能，可按分组进行计算
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface FieldCalculation {

    /**
     * EL 表达式
     * 例如: $userService.findById($me.id)
     * 例如: $me.age + $me.name
     */
    String expression();

    /**
     * 顺序 越小越先执行
     */
    int order() default 0;

    /**
     * 分组计算
     *
     * @return
     */
    String[] groups() default {};

}
