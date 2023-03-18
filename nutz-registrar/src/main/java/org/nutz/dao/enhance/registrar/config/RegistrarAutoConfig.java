package org.nutz.dao.enhance.registrar.config;

import org.nutz.dao.enhance.audit.AuditingEntity;
import org.nutz.dao.enhance.audit.AuditingEntityRunMethod;
import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.registrar.factory.DefaualtNutDaoFactory;
import org.nutz.dao.enhance.registrar.util.DaoPropertiesUtil;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBean(create = "init")
public class RegistrarAutoConfig {

    @Inject
    PropertiesProxy propertiesProxy;

    @Inject("refer:$ioc")
    private Ioc ioc;

    private DaoProperties daoProperties;

    @Inject(optional = true)
    AuditingEntity auditingEntity;

    public void init() {
        this.daoProperties = DaoPropertiesUtil.toDaoProperties(propertiesProxy);
        CustomMake.me().register(AuditingEntityRunMethod.NAME, new AuditingEntityRunMethod(auditingEntity));
    }

    @IocBean
    public DaoProperties daoProperties() {
        return this.daoProperties;
    }

    @IocBean(name = DaoFactory.DEFAUALT_DAO_FACTORY_BEAN_NAME, create = "init")
    public DaoFactory daoFactory() {
        return new DefaualtNutDaoFactory(this.ioc, this.daoProperties);
    }

}
