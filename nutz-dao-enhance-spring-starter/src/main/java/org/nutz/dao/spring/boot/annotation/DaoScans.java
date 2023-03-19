package org.nutz.dao.spring.boot.annotation;

import org.nutz.dao.spring.boot.registrar.DaoScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DaoScannerRegistrar.RepeatingRegistrar.class)
public @interface DaoScans {
    DaoScan[] value();
}
