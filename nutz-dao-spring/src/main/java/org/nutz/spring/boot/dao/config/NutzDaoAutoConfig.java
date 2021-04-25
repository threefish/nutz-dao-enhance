package org.nutz.spring.boot.dao.config;

import org.nutz.spring.boot.dao.config.properties.DaoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/1/12
 */
@Configuration
@ComponentScan("org.nutz.spring.boot.dao")
public class NutzDaoAutoConfig {
}
