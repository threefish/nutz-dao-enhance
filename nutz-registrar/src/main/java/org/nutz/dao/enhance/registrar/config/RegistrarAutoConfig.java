package org.nutz.dao.enhance.registrar.config;

import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.registrar.factory.DefaualtNutDaoFactory;
import org.nutz.dao.enhance.registrar.util.DaoPropertiesUtil;
import org.nutz.dao.util.Daos;
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

    public void init() {
        this.daoProperties = DaoPropertiesUtil.toDaoProperties(propertiesProxy);
        Daos.CHECK_COLUMN_NAME_KEYWORD = this.daoProperties.isCheckColumnNameKeyword();
        Daos.FORCE_WRAP_COLUMN_NAME = this.daoProperties.isForceWrapColumnName();
        Daos.FORCE_UPPER_COLUMN_NAME = this.daoProperties.isForceUpperColumnName();
        Daos.FORCE_HUMP_COLUMN_NAME = this.daoProperties.isForceHumpColumnName();
        Daos.DEFAULT_VARCHAR_WIDTH = this.daoProperties.getDefaultVarcharWidth();
    }

    @IocBean
    public DaoProperties daoProperties() {
        return this.daoProperties;
    }

    @IocBean(name = DaoFactory.defaualtDaoFactoryBeanName, create = "init")
    public DaoFactory daoFactory() {
        return new DefaualtNutDaoFactory(this.ioc, this.daoProperties);
    }

}
