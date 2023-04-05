package org.nutz.dao.enhance.enhance;

import org.nutz.dao.enhance.audit.AuditHandler;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.interceptor.impl.BasicPojoInterceptor;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/2
 */
@SuppressWarnings("all")
public class AuditingEntityPojoInterceptor extends BasicPojoInterceptor {

    private final AuditHandler auditHandler;


    public AuditingEntityPojoInterceptor(AuditHandler auditHandler) {
        this.auditHandler = auditHandler;
    }

    @Override
    public void onEvent(Object obj, Entity<?> en, String event, Object... args) {
        if ("prevInsert".equals(event)) {
            auditHandler.prePersist(obj, en);
        }
        if ("prevUpdate".equals(event)) {
            auditHandler.preUpdate(obj, en);
        }
    }
}
