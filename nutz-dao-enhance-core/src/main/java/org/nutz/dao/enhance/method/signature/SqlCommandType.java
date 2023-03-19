/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午11:27
 */
package org.nutz.dao.enhance.method.signature;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/12/12
 */
public enum SqlCommandType {
    /**
     * 新增
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查询
     */
    SELECT,
    /**
     * 调用存储过程
     */
    CALL_STORED_PROCEDURE
}
