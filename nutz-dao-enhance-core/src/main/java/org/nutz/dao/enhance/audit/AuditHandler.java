package org.nutz.dao.enhance.audit;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/18
 */
public interface AuditHandler<T> {

    /**
     * 持久化前
     *
     * @param object
     * @param entity
     */
    void prePersist(T object, Entity<?> entity);

    /**
     * 更新前
     *
     * @param object
     * @param entity
     */
    void preUpdate(T object, Entity<?> entity);

    /**
     * 给字段设置值
     *
     * @param object
     * @param entity
     * @param field
     * @param value
     */
    default void setField(T object, Entity<?> entity, String field, Object value) {
        MappingField mf = entity.getField(field);
        if (mf != null) {
            mf.setValue(object, value);
        }
    }
}
