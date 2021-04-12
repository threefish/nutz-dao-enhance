package org.nutz.spring.boot.dao.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@ConfigurationProperties(prefix = "nutz.dao")
@Configuration
@Data
public class NutzDaoProperties {
    /**
     * 启用ddl功能
     */
    boolean enableDdl = false;
    /**
     * 是否允许添加字段
     */
    boolean migrationAdd = true;
    /**
     * 是否允许删除字段
     */
    boolean migrationDel = true;
    /**
     * 是否检查索引
     */
    boolean migrationCheckIndex = true;


}
