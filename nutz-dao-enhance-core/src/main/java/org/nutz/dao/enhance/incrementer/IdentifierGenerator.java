package org.nutz.dao.enhance.incrementer;

import org.nutz.lang.random.R;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/19
 */
public interface IdentifierGenerator {

    /**
     * 生成Id,或根据对象信息生成ID
     *
     * @param entity 实体
     * @return id
     */
    Number nextId(Object entity);

    /**
     * 生成uuid
     *
     * @param entity 实体
     * @return uuid
     */
    default String nextUUID(Object entity) {
        return R.UU32();
    }
}
