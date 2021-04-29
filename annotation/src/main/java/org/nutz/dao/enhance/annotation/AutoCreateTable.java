package org.nutz.dao.enhance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/12/12
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCreateTable {

    Class<?>[] value();
}
