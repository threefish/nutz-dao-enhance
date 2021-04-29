package org.nutz.dao.enhance.holder;

import org.nutz.dao.enhance.annotation.AutoCreateTable;

import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class AutoCreateTableHolder {

    /**
     * 需要自动建表的数据源名称对应实体类映射,建完表就会清空
     */
    private static HashMap<String, Set<Class<?>>> DATA_SOURCE_ENTITY_CLASS_MAPPING = new HashMap<>();

    public static void addDataSourceEntityClassMapping(String dataSourceName, Class<?> classZ) {
        final AutoCreateTable annotation = classZ.getAnnotation(AutoCreateTable.class);
        if (Objects.nonNull(annotation)) {
            final Set<Class<?>> entityClassSets = DATA_SOURCE_ENTITY_CLASS_MAPPING.getOrDefault(dataSourceName, new HashSet<>());
            Arrays.stream(annotation.value()).forEach(entityClassSets::add);
            DATA_SOURCE_ENTITY_CLASS_MAPPING.put(dataSourceName, entityClassSets);
        }
    }

    public static HashMap<String, Set<Class<?>>> getDataSourceEntityClassMapping() {
        return DATA_SOURCE_ENTITY_CLASS_MAPPING;
    }

    /**
     * 销毁
     */
    public static void destroy() {
        DATA_SOURCE_ENTITY_CLASS_MAPPING.clear();
    }
}
