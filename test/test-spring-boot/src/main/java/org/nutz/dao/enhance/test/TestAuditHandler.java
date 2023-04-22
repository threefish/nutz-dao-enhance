package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.audit.AuditHandler;
import org.nutz.dao.entity.Entity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/18
 */
@Component
public class TestAuditHandler implements AuditHandler {

    @Override
    public void prePersist(Object object, Entity entity) {
        setField(object, entity, "gmtCreate", LocalDateTime.now());
        setField(object, entity, "createBy", "spring-test");
    }

    @Override
    public void preUpdate(Object object, Entity entity) {

    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("spring-test");
    }
}