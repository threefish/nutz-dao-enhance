package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/12/12
 * 忽略自动建表
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAutoDDL {

}
