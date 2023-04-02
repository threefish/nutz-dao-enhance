package org.nutz.dao.enhance.method.holder;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.annotation.IgnoreAutoDDL;
import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Slf4j
public class AutoCreateTableHolder {

    /**
     * 需要自动建表的数据源名称对应实体类映射
     */
    private static transient HashMap<String, Set<String>> DATA_SOURCE_ENTITY_PACKAGE = new HashMap<>();

    /**
     * 批量建表,优先建立带@ManyMany的表
     *
     * @param dao   Dao实例
     * @param list  需要自动创建的表
     * @param force 如果表存在,是否先删后建
     */
    private static void createTables(final Dao dao, List<Class<?>> list, boolean force) {
        Collections.sort(list, (prev, next) -> {
            int links_prev = dao.getEntity(prev).getLinkFields(null).size();
            int links_next = dao.getEntity(next).getLinkFields(null).size();
            if (links_prev == links_next) {
                return 0;
            }
            return links_prev > links_next ? 1 : -1;
        });
        ArrayList<Exception> es = new ArrayList<>();
        for (Class<?> klass : list) {
            try {
                dao.create(klass, force);
            } catch (Exception e) {
                es.add(new RuntimeException("class=" + klass.getName(), e));
            }
        }
        if (es.size() > 0) {
            for (Exception exception : es) {
                log.debug(exception.getMessage(), exception);
            }
            throw (RuntimeException) es.get(0);
        }
    }

    /**
     * 自动建表
     */
    public static void autoGenerateDdl(EnhanceCoreFactory enhanceCoreFactory, DaoProperties properties) {
        Daos.CHECK_COLUMN_NAME_KEYWORD = properties.isCheckColumnNameKeyword();
        Daos.FORCE_WRAP_COLUMN_NAME = properties.isForceWrapColumnName();
        Daos.FORCE_UPPER_COLUMN_NAME = properties.isForceUpperColumnName();
        Daos.FORCE_HUMP_COLUMN_NAME = properties.isForceHumpColumnName();
        Daos.DEFAULT_VARCHAR_WIDTH = properties.getDefaultVarcharWidth();
        if (!properties.isEnableDdl()) {
            return;
        }
        DATA_SOURCE_ENTITY_PACKAGE.forEach((dataSource, classes) -> {
            final Dao dao = enhanceCoreFactory.getDao(dataSource);
            classes.stream().filter(Strings::isNotBlank).distinct().forEach(packageName -> {
                List<Class<?>> list = new ArrayList<Class<?>>();
                for (Class<?> klass : Scans.me().scanPackage(packageName)) {
                    if (Mirror.me(klass).getAnnotation(Table.class) != null && Mirror.me(klass).getAnnotation(IgnoreAutoDDL.class) == null) {
                        list.add(klass);
                    }
                }
                createTables(dao, list, false);
                list.forEach(clazz -> Daos.migration(dao, clazz, properties.isMigrationAdd(), properties.isMigrationDel(), properties.isMigrationCheckIndex(), null));
            });
        });
        DATA_SOURCE_ENTITY_PACKAGE.clear();
    }


    public static void addDataSourceEntityPackages(String dataSource, Set<String> basePackages) {
        DATA_SOURCE_ENTITY_PACKAGE.put(dataSource, basePackages);
    }
}
