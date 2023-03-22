package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.enhance.dao.lambda.LambdaQueryWhere;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/22
 */
public class LambdaQueryProvider {

    public static <T> LambdaQueryWhere<T> lambdaQuery(ProviderContext providerContext) {
        return new LambdaQueryWhere<T>(providerContext);
    }

}
