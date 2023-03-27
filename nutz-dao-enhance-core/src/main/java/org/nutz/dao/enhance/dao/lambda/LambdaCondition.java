package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.cri.IsNull;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/23
 */
@SuppressWarnings("all")
public abstract class LambdaCondition<Children extends LambdaCondition, T> {

    protected final Children thisType = (Children) this;

    protected final Cnd cnd;

    protected final ProviderContext providerContext;

    protected final boolean notNull;

    protected final boolean notEmpty;
    /**
     * 激活的字段
     */
    private List<String> activeds;
    /**
     * 排除的字段
     */
    private List<String> excludes;


    public LambdaCondition(Cnd cnd, ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        this.cnd = cnd;
        this.providerContext = providerContext;
        this.notNull = notNull;
        this.notEmpty = notEmpty;
    }

    protected <T> T _invoke(MoleculeSupplier<T> supplier) {
        FieldFilterMolecule<T> fieldFilterMolecule = new FieldFilterMolecule(() -> supplier.call());
        FieldFilter.create(providerContext.entityClass, "^id|name$").run(fieldFilterMolecule);
        return fieldFilterMolecule.getObj();
    }

    /**
     * 激活的字段
     *
     * @param names
     * @return
     */
    @SafeVarargs
    public final Children activeds(PFun<T, ?>... names) {
        if (names == null || names.length == 0) {
            return this.thisType;
        }
        this.activeds = new ArrayList<>();
        for (PFun<T, ?> name : names) {
            this.activeds.add(LambdaQuery.resolve(name));
        }
        return this.thisType;
    }

    /**
     * 排除的字段
     *
     * @param names
     * @return
     */
    @SafeVarargs
    public final Children excludes(PFun<T, ?>... names) {
        if (names == null || names.length == 0) {
            return this.thisType;
        }
        this.activeds = new ArrayList<>();
        for (PFun<T, ?> name : names) {
            this.activeds.add(LambdaQuery.resolve(name));
        }
        return this.thisType;
    }


    public Children eq(PFun<T, ?> name, Object value) {
        cnd.and(name, "=", value);
        checkValueForNull(name, value);
        return this.thisType;
    }

    public Children eq(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, "=", value);
        return this.thisType;
    }

    public Children ne(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, "!=", value);
        return this.thisType;
    }

    public Children ne(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, "!=", value);
        return this.thisType;
    }

    /**
     * 大于
     *
     * @param name
     * @param value
     * @return
     */
    public Children gt(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, ">", value);
        return this.thisType;
    }

    public Children gt(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, ">", value);
        return this.thisType;
    }

    /**
     * 大于等于
     *
     * @param name
     * @param value
     * @return
     */
    public Children gte(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, ">=", value);
        return this.thisType;
    }

    public Children gte(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, ">=", value);
        return this.thisType;
    }

    /**
     * 小于
     *
     * @param name
     * @param value
     * @return
     */
    public Children lt(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, "<", value);
        return this.thisType;
    }

    public Children lt(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, "<", value);
        return this.thisType;
    }

    /**
     * 小于等于
     *
     * @param name
     * @param value
     * @return
     */
    public Children lte(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, "<=", value);
        return this.thisType;
    }

    public Children lte(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, "<=", value);
        return this.thisType;
    }

    /**
     * 之间
     *
     * @param name
     * @param val1
     * @param val2
     * @return
     */
    public Children between(PFun<T, ?> name, Object val1, Object val2) {
        checkValueForNull(name, val1, val2);
        cnd.and(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children between(boolean condition, PFun<T, ?> name, Object val1, Object val2) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        cnd.and(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children notBetween(PFun<T, ?> name, Object val1, Object val2) {
        cnd.andNot(name, "between", new Object[]{val1, val2});
        checkValueForNull(name, val1, val2);
        return this.thisType;
    }

    public Children notBetween(boolean condition, PFun<T, ?> name, Object val1, Object val2) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        cnd.andNot(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children like(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(name, "like", value);
        return this.thisType;
    }

    public Children like(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(name, "like", value);
        return this.thisType;
    }

    public Children notLike(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.andNot(name, "like", value);
        return this.thisType;
    }

    public Children notLike(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.andNot(name, "like", value);
        return this.thisType;
    }

    public Children likeLeft(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(Cnd.exp(name, "like", String.format("%%%s", value)));
        return this.thisType;
    }

    public Children likeLeft(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(Cnd.exp(name, "like", String.format("%%%s", value)));
        return this.thisType;
    }

    public Children likeRight(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        cnd.and(Cnd.exp(name, "like", String.format("%s%%", value)));
        return this.thisType;
    }

    public Children likeRight(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            return this.thisType;
        }
        checkValueForNull(name, value);
        cnd.and(Cnd.exp(name, "like", String.format("%s%%", value)));
        return this.thisType;
    }

    public Children isNull(PFun<T, ?> name) {
        cnd.and(new IsNull(name));
        return this.thisType;
    }

    public Children isNull(boolean condition, PFun<T, ?> name) {
        if (!condition) {
            return this.thisType;
        }
        cnd.and(new IsNull(name));
        return this.thisType;
    }

    public Children isNotNull(PFun<T, ?> name) {
        cnd.and(new IsNull(name).setNot(true));
        return this.thisType;
    }

    public Children isNotNull(boolean condition, PFun<T, ?> name) {
        if (!condition) {
            return this.thisType;
        }
        cnd.and(new IsNull(name).setNot(true));
        return this.thisType;
    }

    public Children in(PFun<T, ?> name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        cnd.and(name, "in", coll);
        return this.thisType;
    }

    public Children in(boolean condition, PFun<T, ?> name, Collection<?> coll) {
        if (!condition) {
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        cnd.and(name, "in", coll);
        return this.thisType;
    }

    public Children notIn(PFun<T, ?> name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        cnd.and(name, "not in", coll);
        return this.thisType;
    }

    public Children notIn(boolean condition, PFun<T, ?> name, Collection<?> coll) {
        if (!condition) {
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        cnd.and(name, "not in", coll);
        return this.thisType;
    }


    public Children and(SqlExpression exp) {
        this.cnd.and(exp);
        return this.thisType;
    }

    public Children and(boolean condition, SqlExpression exp) {
        if (!condition) {
            return this.thisType;
        }
        this.cnd.and(exp);
        return this.thisType;
    }

    public Children or(SqlExpression exp) {
        this.cnd.and(exp);
        return this.thisType;
    }

    public Children or(boolean condition, SqlExpression exp) {
        if (!condition) {
            return this.thisType;
        }
        this.cnd.and(exp);
        return this.thisType;
    }

    /**
     * and链接 组内采用OR
     *
     * @param exps
     * @return
     */
    @SafeVarargs
    public final Children and(Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>>... exps) {
        if (exps != null && exps.length > 0) {
            SqlExpressionGroup sqlExpressionGroup = new SqlExpressionGroup();
            Arrays.stream(exps).forEach(orPart -> sqlExpressionGroup.or(orPart.apply(new LambdaConditionWapper<T>(Cnd.NEW(), this.providerContext, this.notNull, this.notEmpty)).getSqlExpressionGroup()));
            this.cnd.and(sqlExpressionGroup);
        }
        return this.thisType;
    }

    /**
     * OR链接 组内采用OR
     *
     * @param exps
     * @return
     */
    @SafeVarargs
    public final Children or(Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>>... exps) {
        if (exps != null && exps.length > 0) {
            SqlExpressionGroup sqlExpressionGroup = new SqlExpressionGroup();
            Arrays.stream(exps).forEach(orPart -> sqlExpressionGroup.or(orPart.apply(new LambdaConditionWapper<T>(Cnd.NEW(), this.providerContext, this.notNull, this.notEmpty)).getSqlExpressionGroup()));
            this.cnd.or(sqlExpressionGroup);
        }
        return this.thisType;
    }

    /**
     * 检查集合值是否为空
     *
     * @param name
     * @param coll
     */
    private void checkCollectionValueForEmpty(PFun<T, ?> name, Collection<?> coll) {
        if (this.notEmpty) {
            if (coll == null || coll.isEmpty()) {
                throw new IllegalArgumentException(String.format("Value for [%s] cannot be empty", LambdaQuery.resolve(name)));
            }
        }
    }

    /**
     * 如果字段值是null将报错，阻止sql提交
     */
    @SafeVarargs
    private final void checkValueForNull(PFun<T, ?> name, Object... values) {
        if (this.notNull) {
            if (values == null) {
                throw new IllegalArgumentException(String.format("Value for [%s] cannot be null", LambdaQuery.resolve(name)));
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null) {
                    throw new IllegalArgumentException(String.format("Value for [%s] cannot be null,index [%s]", LambdaQuery.resolve(name), i + 1));
                }
            }
        }

    }

}
