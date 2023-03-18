package org.nutz.dao.spring.boot.config;

import org.nutz.dao.enhance.audit.AuditingEntity;
import org.nutz.dao.enhance.audit.AuditingEntityRunMethod;
import org.nutz.el.opt.custom.CustomMake;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/1/12
 */
@Configuration
@ComponentScan("org.nutz.dao.spring.boot")
public class NutzDaoAutoConfig implements InitializingBean {

    @Autowired(required = false)
    AuditingEntity auditingEntity;

    @Override
    public void afterPropertiesSet() {
        CustomMake.me().register(AuditingEntityRunMethod.NAME, new AuditingEntityRunMethod(auditingEntity));
    }

}
