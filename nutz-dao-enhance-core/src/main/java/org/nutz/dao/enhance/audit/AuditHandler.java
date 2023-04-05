package org.nutz.dao.enhance.audit;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;

import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/18
 */
public interface AuditHandler {

    /**
     * 持久化前
     *
     * @param object
     * @param entity
     */
    void prePersist(Object object, Entity<?> entity);

    /**
     * 更新前
     *
     * @param object
     * @param entity
     */
    void preUpdate(Object object, Entity<?> entity);

    /**
     * 获取当前审计员信息，如用户ID 用户名等
     *
     * 搭配：@CreatedBy @LastModifiedBy
     */
    <T> Optional<T> getCurrentAuditor();

    /**
     * 给字段设置值
     *
     * @param object
     * @param entity
     * @param field
     * @param value
     */
    default void setField(Object object, Entity<?> entity, String field, Object value) {
        MappingField mf = entity.getField(field);
        if (mf != null) {
            mf.setValue(object, value);
        }
    }
}
