package org.nutz.dao.spring.boot.ddl;

import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.util.Daos;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 自动建表
 */
@Component
public class DefaualtAutoGenerate implements ApplicationContextAware, CommandLineRunner {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        DaoProperties nutzDaoProperties = applicationContext.getBean(DaoProperties.class);
        Daos.CHECK_COLUMN_NAME_KEYWORD = nutzDaoProperties.isCheckColumnNameKeyword();
        Daos.FORCE_WRAP_COLUMN_NAME = nutzDaoProperties.isForceWrapColumnName();
        Daos.FORCE_UPPER_COLUMN_NAME = nutzDaoProperties.isForceUpperColumnName();
        Daos.FORCE_HUMP_COLUMN_NAME = nutzDaoProperties.isForceHumpColumnName();
        Daos.DEFAULT_VARCHAR_WIDTH = nutzDaoProperties.getDefaultVarcharWidth();
        if (!nutzDaoProperties.isEnableDdl()) {
            return;
        }
        DaoFactory daoFactory = applicationContext.getBean(DaoFactory.class);
        final HashMap<String, String> entityPackages = nutzDaoProperties.getEntityPackages();
        entityPackages.forEach((dataSource, packageStr) -> {
            Daos.createTablesInPackage(daoFactory.getDao(dataSource), packageStr, false);
            Daos.migration(daoFactory.getDao(dataSource), packageStr,
                    nutzDaoProperties.isMigrationAdd(), nutzDaoProperties.isMigrationDel(),
                    nutzDaoProperties.isMigrationCheckIndex());
        });
    }


}
