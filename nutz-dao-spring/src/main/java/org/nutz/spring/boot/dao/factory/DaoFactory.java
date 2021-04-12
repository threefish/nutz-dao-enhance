/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午7:31
 */

package org.nutz.spring.boot.dao.factory;

import org.nutz.dao.Dao;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/12/12
 */
public interface DaoFactory {

    /**
     * 获取dao
     *
     * @return
     */
    Dao getDao();

    /**
     * 获取dao
     *
     * @return
     */
    Dao getDao(String dataSource);

}
