package org.nutz.dao.enhance.annotation;

import java.lang.annotation.*;
import java.sql.Types;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/15
 * 调用存储过程
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallStoredProcedure {

    String value();

    /**
     * 出参订阅
     */
    Out[] out() default {};

    /**
     * 定义Out参数
     */
    @interface Out {

        String name();

        int jdbcType() default Types.VARCHAR;

    }


}
