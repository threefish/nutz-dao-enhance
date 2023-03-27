package org.nutz.dao.enhance.audit;

import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/18
 */
public interface AuditingEntity<T> {
    /**
     * @return 获取当前审计员
     */
    Optional<T> getCurrentAuditor();
}
