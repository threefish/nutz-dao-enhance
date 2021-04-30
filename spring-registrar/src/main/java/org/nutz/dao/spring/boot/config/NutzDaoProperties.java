package org.nutz.dao.spring.boot.config;

import org.nutz.dao.enhance.config.DaoEnhanceConstant;
import org.nutz.dao.enhance.config.DaoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@ConfigurationProperties(prefix = DaoEnhanceConstant.PROPERTIES_PREFIX)
@Configuration
public class NutzDaoProperties extends DaoProperties {
}
