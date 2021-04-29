package org.nutz.dao.enhance.registrar.util;

import org.nutz.dao.enhance.config.DaoEnhanceConstant;
import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.ioc.impl.PropertiesProxy;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class DaoPropertiesUtil {

    public static DaoProperties toDaoProperties(PropertiesProxy propertiesProxy) {
        DaoProperties daoProperties = new DaoProperties();
        daoProperties.setEnableDdl(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "enableDdl"));
        daoProperties.setMigrationAdd(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "migrationAdd"));
        daoProperties.setMigrationDel(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "migrationDel"));
        daoProperties.setMigrationCheckIndex(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "migrationCheckIndex"));
        daoProperties.setCheckColumnNameKeyword(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "checkColumnNameKeyword"));
        daoProperties.setForceWrapColumnName(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "forceWrapColumnName"));
        daoProperties.setForceUpperColumnName(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "forceUpperColumnName"));
        daoProperties.setForceHumpColumnName(propertiesProxy.getBoolean(DaoEnhanceConstant.PROPERTIES_PREFIX + "forceHumpColumnName"));
        daoProperties.setDefaultVarcharWidth(propertiesProxy.getInt(DaoEnhanceConstant.PROPERTIES_PREFIX + "defaultVarcharWidth"));
        if(daoProperties.getDefaultVarcharWidth()<=0){
            throw new RuntimeException(String.format("defaultVarcharWidth = [%s] 参数不正确!!",daoProperties.getDefaultVarcharWidth()));
        }
        return daoProperties;
    }
}
