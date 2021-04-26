package org.nutz.dao.enhance.registrar.config;

import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.registrar.factory.DefaualtNutDaoFactory;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBean(create = "init")
public class RegistrarAutoConfig {
    //TODO 修改加载方式
    private DaoProperties daoProperties = new DaoProperties();

    @Inject("refer:$ioc")
    private Ioc ioc;

    public void init() {
        Daos.CHECK_COLUMN_NAME_KEYWORD = daoProperties.isCheckColumnNameKeyword();
        Daos.FORCE_WRAP_COLUMN_NAME = daoProperties.isForceWrapColumnName();
        Daos.FORCE_UPPER_COLUMN_NAME = daoProperties.isForceUpperColumnName();
        Daos.FORCE_HUMP_COLUMN_NAME = daoProperties.isForceHumpColumnName();
        Daos.DEFAULT_VARCHAR_WIDTH = daoProperties.getDefaultVarcharWidth();
    }

    @IocBean(name = DaoFactory.defaualtDaoFactoryBeanName, create = "init")
    public DaoFactory daoFactory(){
       return new DefaualtNutDaoFactory(ioc);
    }
}
