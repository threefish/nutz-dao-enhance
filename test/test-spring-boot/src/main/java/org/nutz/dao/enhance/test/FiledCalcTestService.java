package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.test.entity.UserDO;
import org.springframework.stereotype.Component;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/19
 */
@Component("filedCalcTestService")
public class FiledCalcTestService {

    public UserDO query(UserDO userDO){
        return new UserDO();
    }
}
