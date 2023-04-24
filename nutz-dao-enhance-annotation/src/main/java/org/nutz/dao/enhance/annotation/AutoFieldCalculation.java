package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/4/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface AutoFieldCalculation {

    /**
     * 分组计算
     *
     * @return
     */
    String[] groups() default "default";

}
