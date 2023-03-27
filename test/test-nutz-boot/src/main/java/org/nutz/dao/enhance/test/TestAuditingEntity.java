package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.audit.AuditingEntity;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/18
 */
@IocBean
public class TestAuditingEntity implements AuditingEntity {
    @Override
    public Optional getCurrentAuditor() {
        return Optional.of("nutz-test");
    }
}
