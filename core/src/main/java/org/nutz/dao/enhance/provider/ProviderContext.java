package org.nutz.dao.enhance.provider;

import lombok.Data;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.method.signature.MethodSignature;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/6/14
 */
@Data
public class ProviderContext {

    Dao dao;

    MethodSignature methodSignature;

    String executeSql;


}
