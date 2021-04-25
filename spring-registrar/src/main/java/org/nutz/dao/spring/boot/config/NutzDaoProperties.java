package org.nutz.dao.spring.boot.config;

import org.nutz.dao.enhance.config.DaoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@ConfigurationProperties(prefix = "nutz.dao")
@Configuration
public class NutzDaoProperties extends DaoProperties {
}
