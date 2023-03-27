package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Strings;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/22
 */
@SuppressWarnings("all")
public class LambdaUpdate<T> extends LambdaCondition<LambdaUpdate<T>, T> {

    private final ProviderContext providerContext;

    private Chain chain;

    public LambdaUpdate(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        super(Cnd.NEW(), providerContext, notNull, notEmpty);
        this.providerContext = providerContext;
    }

    public LambdaUpdate<T> set(PFun<T, ?> name, Object value) {
        if (chain == null) {
            chain = Chain.make(LambdaQuery.resolve(name), value);
        } else {
            chain.add(LambdaQuery.resolve(name), value);
        }
        return this;
    }

    public LambdaUpdate<T> setEx(PFun<T, ?> name, Object value) {
        if (Cnd._ex(value)) {
            return this;
        }
        return set(name, value);
    }

    public int update() {
        if (chain == null || chain.size() == 0) {
            throw new UnsupportedOperationException("必须通过 set 方法设置更新的列和值");
        }
        return _invoke(() -> providerContext.dao.update(providerContext.entity, chain, cnd));
    }

    public void insert() {
        if (chain == null || chain.size() == 0) {
            throw new UnsupportedOperationException("必须通过 set 方法设置更新的列和值");
        }
        _invoke(() -> {
            providerContext.dao.insert(providerContext.entityClass, chain);
            return null;
        });
    }


    public int delete() {
        if (Strings.isBlank(this.cnd.toString())) {
            throw new UnsupportedOperationException("删除时请传入条件，避免全表删除!!!");
        }
        return _invoke(() -> this.providerContext.dao.clear(providerContext.entity, this.cnd));
    }

}
