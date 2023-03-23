package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.enhance.dao.lambda.LambdaUpdate;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/22
 */
@SuppressWarnings("all")
public class LambdaUpdateProvider {

    public static <T> LambdaUpdate<T> lambdaUpdate(ProviderContext providerContext) {
        return new LambdaUpdate<T>(providerContext);
    }

}
