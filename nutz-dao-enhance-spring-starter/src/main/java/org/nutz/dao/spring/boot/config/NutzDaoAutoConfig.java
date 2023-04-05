package org.nutz.dao.spring.boot.config;

import org.nutz.dao.enhance.audit.AuditHandler;
import org.nutz.dao.enhance.el.AuditingEntityRunMethod;
import org.nutz.dao.enhance.el.IdentifierGeneratorRunMethod;
import org.nutz.dao.enhance.el.NowDateRunMethod;
import org.nutz.dao.enhance.incrementer.IdentifierGenerator;
import org.nutz.el.opt.custom.CustomMake;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2021/1/12
 */
@Configuration
@SuppressWarnings("all")
@ComponentScan("org.nutz.dao.spring.boot")
public class NutzDaoAutoConfig implements InitializingBean {

    @Autowired(required = false)
    private AuditHandler auditHandler;

    @Autowired(required = false)
    private IdentifierGenerator identifierGenerator;

    @Override
    public void afterPropertiesSet() {
        CustomMake.me().register(AuditingEntityRunMethod.NAME, new AuditingEntityRunMethod(auditHandler));
        CustomMake.me().register(IdentifierGeneratorRunMethod.NAME, new IdentifierGeneratorRunMethod(identifierGenerator));
        CustomMake.me().register(NowDateRunMethod.NAME, new NowDateRunMethod());
    }
}
