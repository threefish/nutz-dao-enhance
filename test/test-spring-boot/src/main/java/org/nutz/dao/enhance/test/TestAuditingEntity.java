package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.audit.AuditingEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/18
 */
@Component
public class TestAuditingEntity implements AuditingEntity {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of("spring-test");
    }
}