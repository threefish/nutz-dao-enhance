package org.nutz.spring.boot.dao.config;

import org.nutz.dao.Sqls;
import org.nutz.spring.boot.dao.enhance.NutSqlEnhance;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/1/12
 */
@Configuration
@ComponentScan("org.nutz.spring.boot.dao")
public class NutzDaoAutoConfig {
    static {
        // 增强sql查询器
        Sqls.setSqlBorning(NutSqlEnhance.class);
    }
}
