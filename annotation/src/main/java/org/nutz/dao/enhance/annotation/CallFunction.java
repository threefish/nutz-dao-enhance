package org.nutz.dao.enhance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CallFunction {

    String value();


    Out[] out() default {};

    /**
     * 定义Out参数
     */
    @interface Out {

        int index();

        String name();

        int type() default Types.VARCHAR;

    }


}
