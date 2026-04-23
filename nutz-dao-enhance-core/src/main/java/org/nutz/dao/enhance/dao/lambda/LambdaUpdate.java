package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    /**
     * 原子递增/递减操作，值为原生SQL表达式
     */
    private Map<String, String> specials;

    public LambdaUpdate(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        super(QueryCondition.NEW(), providerContext, notNull, notEmpty);
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

    public LambdaUpdate<T> set(boolean condition, PFun<T, ?> name, Object value) {
        return condition ? set(name, value) : this;
    }

    public LambdaUpdate<T> set(boolean condition, String name, Object value) {
        return condition ? set(name, value) : this;
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

    public LambdaUpdate<T> setEx(boolean condition, PFun<T, ?> name, Object value) {
        return condition ? setEx(name, value) : this;
    }

    public LambdaUpdate<T> setEx(boolean condition, String name, Object value) {
        return condition ? setEx(name, value) : this;
    }

    /**
     * 原子递增
     */
    public LambdaUpdate<T> increment(PFun<T, ?> name, Number value) {
        return increment(LambdaQuery.resolve(name), value);
    }

    /**
     * 原子递增
     */
    public LambdaUpdate<T> increment(String name, Number value) {
        if (specials == null) {
            specials = new HashMap<>();
        }
        String columnName = providerContext.dao.getEntity(providerContext.entityClass).getField(name).getColumnName();
        specials.put(name, columnName + " + " + value);
        return this;
    }

    /**
     * 原子递减
     */
    public LambdaUpdate<T> decrement(PFun<T, ?> name, Number value) {
        return decrement(LambdaQuery.resolve(name), value);
    }

    /**
     * 原子递减
     */
    public LambdaUpdate<T> decrement(String name, Number value) {
        if (specials == null) {
            specials = new HashMap<>();
        }
        String columnName = providerContext.dao.getEntity(providerContext.entityClass).getField(name).getColumnName();
        specials.put(name, columnName + " - " + value);
        return this;
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
        boolean hasData = data != null && !data.isEmpty();
        boolean hasSpecials = specials != null && !specials.isEmpty();
        if (!hasData && !hasSpecials) {
            throw new UnsupportedOperationException("必须通过 set 方法设置更新的列和值");
        }
        return _invoke(() -> {
            Chain chain;
            if (hasData) {
                chain = Chain.from(data);
                if (hasSpecials) {
                    for (Map.Entry<String, String> entry : specials.entrySet()) {
                        chain.addSpecial(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                Iterator<Map.Entry<String, String>> it = specials.entrySet().iterator();
                Map.Entry<String, String> first = it.next();
                chain = Chain.makeSpecial(first.getKey(), first.getValue());
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    chain.addSpecial(entry.getKey(), entry.getValue());
                }
            }
            return providerContext.dao.update(providerContext.entity, chain, cnd);
        });
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
        List<SqlExpression> exps = this.cnd.getCri().where().getExps();
        if (exps == null || exps.isEmpty()) {
            throw new UnsupportedOperationException("删除时请传入条件，避免全表删除!!!");
        }
        return _invoke(() -> this.providerContext.dao.clear(providerContext.entity, this.cnd));
    }

}
