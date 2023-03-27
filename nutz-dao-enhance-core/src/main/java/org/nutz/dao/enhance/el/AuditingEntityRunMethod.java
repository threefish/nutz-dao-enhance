package org.nutz.dao.enhance.el;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.enhance.audit.AuditingEntity;
import org.nutz.el.opt.RunMethod;

import java.util.List;
import java.util.Optional;


/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/18
 * 审计实体运行方法
 */
@Slf4j
public class AuditingEntityRunMethod implements RunMethod {

    public static final String NAME = "currentAudit";
    public static final String FUN_NAME = NAME + "()";

    private final AuditingEntity auditingEntity;

    public AuditingEntityRunMethod(AuditingEntity auditingEntity) {
        this.auditingEntity = auditingEntity;
        if (log.isInfoEnabled()) {
            log.info("Init AuditingEntityRunMethod:'{}'", auditingEntity);
        }
        if (this.auditingEntity == null) {
            if (log.isWarnEnabled()) {
                log.warn("auditingEntity is null,Unable to record audit information!!!");
            }
        }
    }

    @Override
    public Object run(List<Object> fetchParam) {
        if (auditingEntity == null) {
            return null;
        }
        Optional currentAuditor = this.auditingEntity.getCurrentAuditor();
        if (currentAuditor.isPresent()) {
            return currentAuditor.get();
        }
        return null;
    }

    @Override
    public String fetchSelf() {
        return NAME;
    }
}
