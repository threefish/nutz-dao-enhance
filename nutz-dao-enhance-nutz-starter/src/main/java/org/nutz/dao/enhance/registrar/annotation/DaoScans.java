package org.nutz.dao.enhance.registrar.annotation;


import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/7/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DaoScans {
    DaoScan[] value();
}
