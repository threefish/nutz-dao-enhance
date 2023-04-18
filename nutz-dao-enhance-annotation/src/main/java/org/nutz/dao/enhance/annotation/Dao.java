package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/7/30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dao {
    /**
     * bean名称
     */
    String name() default "";
}
