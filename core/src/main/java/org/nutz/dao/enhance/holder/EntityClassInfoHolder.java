package org.nutz.dao.enhance.holder;

import org.nutz.dao.entity.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class EntityClassInfoHolder {
    /**
     * 完整类名 -->>  Entity信息
     */
    private static final HashMap<Class<?>, Entity<?>> ENTITY_HASH_MAP = new HashMap<>();
    /**
     * 类名 -->> 完整类名
     */
    private static final HashMap<String, Class<?>> STRING_CLASS_HASH_MAP = new LinkedHashMap<>();

    /**
     * 放入
     *
     * @param klass
     * @param entity
     */
    public static void setEntity(Class<?> klass, Entity<?> entity) {
        STRING_CLASS_HASH_MAP.put(klass.getSimpleName(), klass);
        ENTITY_HASH_MAP.put(klass, entity);
    }

    /**
     * 获取
     *
     * @param klass
     * @return
     */
    public static Entity<?> getEntity(Class<?> klass) {
        return ENTITY_HASH_MAP.get(klass);
    }

    /**
     * 获取
     *
     * @param name
     * @return
     */
    public static Entity<?> getEntity(String name) {
        Class<?> klass = getClass(name);
        return Objects.isNull(klass) ? null : ENTITY_HASH_MAP.get(klass);
    }

    /**
     * 获取类
     *
     * @param name
     * @return
     */
    public static Class<?> getClass(String name) {
        return STRING_CLASS_HASH_MAP.get(name);
    }


}
