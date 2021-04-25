package org.nutz.spring.boot.dao.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@ConfigurationProperties(prefix = "nutz.dao")
@Configuration
public class NutzDaoProperties extends DaoProperties{
}
