package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/22
 */
public class LambdaUpdate<T> {

    private final ProviderContext providerContext;
    private final Cnd condition;

    private Chain chain;

    public LambdaUpdate(ProviderContext providerContext) {
        this.providerContext = providerContext;
        this.condition = Cnd.NEW();
    }

    public LambdaUpdate<T> set(PFun<T, ?> name, Object value) {
        if (chain == null) {
            chain = Chain.make(LambdaQuery.resolve(name), value);
        } else {
            chain.add(LambdaQuery.resolve(name), value);
        }
        return this;
    }

    public LambdaUpdate<T> exp(PFun<T, ?> name, String op, Object value) {
        condition.and(name, op, value);
        return this;
    }

    public int update() {
        return providerContext.dao.update(providerContext.entity, chain, condition);
    }


}
