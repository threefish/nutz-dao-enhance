package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/22
 */
@SuppressWarnings("all")
public class LambdaUpdate<T> extends LambdaCondition<LambdaUpdate<T>, T> {

    private final ProviderContext providerContext;


    private Map<String, Object> data;

    public LambdaUpdate(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        super(Cnd.NEW(), providerContext, notNull, notEmpty);
        this.providerContext = providerContext;
    }

    public LambdaUpdate<T> set(PFun<T, ?> name, Object value) {
        set(LambdaQuery.resolve(name), value);
        return this;
    }

    public LambdaUpdate<T> set(String name, Object value) {
        if (data == null) {
            data = new HashMap();
        }
        data.put(name, value);
        return this;
    }

    public LambdaUpdate<T> setEx(String name, Object value) {
        if (Cnd._ex(value)) {
            return this;
        }
        return set(name, value);
    }

    public LambdaUpdate<T> setEx(PFun<T, ?> name, Object value) {
        if (Cnd._ex(value)) {
            return this;
        }
        return set(name, value);
    }

    public LambdaUpdate<T> ignoreNull() {
        this.ignoreNull = true;
        return this;
    }

    private void convertData() {
        if (this.ignoreNull) {
            this.data.entrySet().removeIf(entry -> Objects.isNull(entry.getValue()));
        }
    }

    public int update() {
        convertData();
        if (data == null || data.isEmpty()) {
            throw new UnsupportedOperationException("必须通过 set 方法设置更新的列和值");
        }
        return _invoke(() -> providerContext.dao.update(providerContext.entity, Chain.from(data), cnd));
    }

    public void insert() {
        convertData();
        if (data == null || data.isEmpty()) {
            throw new UnsupportedOperationException("必须通过 set 方法设置更新的列和值");
        }
        _invoke(() -> {
            providerContext.dao.insert(providerContext.entityClass, Chain.from(data));
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
