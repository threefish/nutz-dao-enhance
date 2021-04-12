package org.nutz.spring.boot.dao.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

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
    boolean enableDdl = true;
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
    /**
     * key 数据源
     * value 自动创建表扫描实体包,逗号隔开
     */
    HashMap<String, String> entityPackages;
    /**
     * 是否检查字段为数据库的关键字
     */
    boolean checkColumnNameKeyword = false;
    /**
     * 是否把字段名用字符包裹来进行关键字逃逸
     */
    boolean forceWrapColumnName = false;
    /**
     * 是否把字段名给变成大写
     */
    boolean forceUpperColumnName = false;
    /**
     * 强制驼峰列名
     */
    boolean forceHumpColumnName = true;
    /**
     * varchar 字段的默认字段长度
     */
    int defaultVarcharWidth = 128;

}
