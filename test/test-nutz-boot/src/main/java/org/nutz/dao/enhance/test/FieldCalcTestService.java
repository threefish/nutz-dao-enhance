package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.test.entity.UserDO;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/19
 */
@IocBean(name = "fieldCalcTestService")
public class FieldCalcTestService {

    public UserDO query(UserDO userDO) {
        UserDO userDO1 = new UserDO();
        userDO1.setRealName("fieldCalcTestService#query");
        return userDO1;
    }
}
