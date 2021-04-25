package org.nutz.dao.spring.boot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/1/12
 */
@Configuration
@ComponentScan("org.nutz.dao.spring.boot")
public class NutzDaoAutoConfig {
}
