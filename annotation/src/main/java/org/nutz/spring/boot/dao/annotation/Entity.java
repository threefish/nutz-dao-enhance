package org.nutz.spring.boot.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/12/12
 * 指定返回的实体类型，默认读取注解优先，若没有注解，则默认通过反射去class类上获取实体类泛型
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    Class<?> value();
}
