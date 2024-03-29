package org.nutz.dao.enhance.registrar.config;

import org.nutz.dao.enhance.audit.AuditHandler;
import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.el.AuditingEntityRunMethod;
import org.nutz.dao.enhance.el.IdentifierGeneratorRunMethod;
import org.nutz.dao.enhance.el.NowDateRunMethod;
import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.incrementer.IdentifierGenerator;
import org.nutz.dao.enhance.registrar.factory.DefaualtNutEnhanceCoreFactory;
import org.nutz.dao.enhance.registrar.util.DaoPropertiesUtil;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@SuppressWarnings("all")
@IocBean(create = "init")
public class RegistrarAutoConfig {

    @Inject
    PropertiesProxy propertiesProxy;

    @Inject("refer:$ioc")
    private Ioc ioc;

    private DaoProperties daoProperties;

    @Inject(optional = true)
    private AuditHandler auditHandler;

    @Inject(optional = true)
    private IdentifierGenerator identifierGenerator;


    public void init() {
        this.daoProperties = DaoPropertiesUtil.toDaoProperties(propertiesProxy);
        CustomMake.me().register(AuditingEntityRunMethod.NAME, new AuditingEntityRunMethod(auditHandler));
        CustomMake.me().register(IdentifierGeneratorRunMethod.NAME, new IdentifierGeneratorRunMethod(identifierGenerator));
        CustomMake.me().register(NowDateRunMethod.NAME, new NowDateRunMethod());
    }


    @IocBean
    public DaoProperties daoProperties() {
        return this.daoProperties;
    }

    @IocBean(name = EnhanceCoreFactory.DEFAUALT_DAO_FACTORY_BEAN_NAME, create = "init")
    public EnhanceCoreFactory daoFactory() {
        return new DefaualtNutEnhanceCoreFactory(this.ioc, this.daoProperties);
    }

}
