package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.enhance.dao.lambda.LambdaQuery;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/22
 */
@SuppressWarnings("all")
public class LambdaQueryProvider {

    public static <T> LambdaQuery<T> lambdaQuery(ProviderContext providerContext) {
        return new LambdaQuery<>(providerContext, true, true);
    }

    public static <T> LambdaQuery<T> lambdaQuery(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        return new LambdaQuery<>(providerContext, notNull, notEmpty);
    }

}
