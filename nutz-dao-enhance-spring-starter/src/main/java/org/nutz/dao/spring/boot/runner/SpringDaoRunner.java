/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2021/1/12 下午9:09
 */

package org.nutz.dao.spring.boot.runner;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2021/1/12
 * 将事务交给spring管理
 */
public class SpringDaoRunner extends NutDaoRunner {

    @SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
    @Override
    public void _run(DataSource dataSource, ConnCallback callback) {
        Connection con = DataSourceUtils.getConnection(dataSource);
        try {
            callback.invoke(con);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }
}
