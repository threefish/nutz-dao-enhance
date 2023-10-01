package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.util.cri.IsNull;
import org.nutz.dao.util.cri.NoParamsSqlExpression;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.dao.util.lambda.LambdaQuery;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/23
 */
@SuppressWarnings("all")
public abstract class LambdaCondition<Children extends LambdaCondition, T> {

    protected final Children thisType = (Children) this;

    protected final QueryCondition cnd;

    protected final ProviderContext providerContext;
    /**
     * 入参条件不允许null值
     */
    protected final boolean notNull;
    /**
     * 入参条件不允许空集合
     */
    protected final boolean notEmpty;
    /**
     * 使用lambda更新、插入时是否忽略null值
     */
    protected boolean ignoreNull;
    /**
     * 激活的字段
     */
    private List<String> activeds;
    /**
     * 排除的字段
     */
    private List<String> excludes;

    private boolean orStatus = false;

    public LambdaCondition(QueryCondition cnd, ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        this.cnd = cnd;
        this.providerContext = providerContext;
        this.notNull = notNull;
        this.notEmpty = notEmpty;
    }

    /**
     * 执行
     *
     * @param moleculeSupplier
     * @param <T>
     * @return
     */
    protected <T> T _invoke(MoleculeSupplier<T> moleculeSupplier) {
        if (Lang.isEmpty(activeds) && Lang.isEmpty(excludes)) {
            return moleculeSupplier.call();
        }
        FieldFilterMolecule<T> fieldFilterMolecule = new FieldFilterMolecule(() -> moleculeSupplier.call());
        String actived = Lang.isEmpty(activeds) ? null : String.format("^%s$", String.join("|", activeds));
        String locked = Lang.isEmpty(excludes) ? null : String.format("^%s$", String.join("|", excludes));
        FieldFilter.create(providerContext.entityClass, actived, locked, this.ignoreNull).run(fieldFilterMolecule);
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
        this.activeds = this.activeds == null ? new ArrayList<>() : this.activeds;
        for (PFun<T, ?> name : names) {
            this.activeds.add(LambdaQuery.resolve(name));
        }
        return this.thisType;
    }

    /**
     * 激活的字段
     *
     * @param names
     * @return
     */
    @SafeVarargs
    public final Children activeds(String... names) {
        if (names == null || names.length == 0) {
            return this.thisType;
        }
        this.activeds = this.activeds == null ? new ArrayList<>() : this.activeds;
        for (String name : names) {
            this.activeds.add(name);
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
        this.excludes = this.excludes == null ? new ArrayList<>() : this.excludes;
        for (PFun<T, ?> name : names) {
            this.excludes.add(LambdaQuery.resolve(name));
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
    public final Children excludes(String... names) {
        if (names == null || names.length == 0) {
            return this.thisType;
        }
        this.excludes = this.excludes == null ? new ArrayList<>() : this.excludes;
        for (String name : names) {
            this.excludes.add(name);
        }
        return this.thisType;
    }

    public Children eq(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    private void addConditionItem(PFun<T, ?> name, String op, Object value) {
        if (this.orStatus) {
            this.cnd.or(name, op, value);
        } else {
            this.cnd.and(name, op, value);
        }
        this.orStatus = false;
    }

    private void addConditionItemAndSetNot(PFun<T, ?> name, String op, Object value) {
        if (this.orStatus) {
            this.cnd.orNot(name, op, value);
        } else {
            this.cnd.andNot(name, op, value);
        }
        this.orStatus = false;
    }

    private void addConditionItemAndSetNot(String name, String op, Object value) {
        if (this.orStatus) {
            this.cnd.orNot(name, op, value);
        } else {
            this.cnd.andNot(name, op, value);
        }
        this.orStatus = false;
    }

    private void addConditionItem(String name, String op, Object value) {
        if (this.orStatus) {
            this.cnd.or(name, op, value);
        } else {
            this.cnd.and(name, op, value);
        }
        this.orStatus = false;
    }


    private void addConditionItemNotOp(NoParamsSqlExpression noParamsSqlExpression) {
        if (this.orStatus) {
            this.cnd.or(noParamsSqlExpression);
        } else {
            this.cnd.and(noParamsSqlExpression);
        }
        this.orStatus = false;
    }

    public Children eq(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    public Children eq(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    public Children eq(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    public Children ne(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "!=", value);
        return this.thisType;
    }

    public Children ne(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    public Children ne(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "=", value);
        return this.thisType;
    }

    public Children ne(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "!=", value);
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
        this.addConditionItem(name, ">", value);
        return this.thisType;
    }

    public Children gt(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, ">", value);
        return this.thisType;
    }

    public Children gt(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, ">", value);
        return this.thisType;
    }

    public Children gt(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, ">", value);
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
        this.addConditionItem(name, ">=", value);
        return this.thisType;
    }

    public Children gte(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, ">=", value);

        return this.thisType;
    }

    public Children gte(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, ">=", value);

        return this.thisType;
    }

    public Children gte(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, ">=", value);

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
        this.addConditionItem(name, "<", value);

        return this.thisType;
    }

    public Children lt(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "<", value);

        return this.thisType;
    }

    public Children lt(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "<", value);

        return this.thisType;
    }

    public Children lt(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "<", value);

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
        this.addConditionItem(name, "<=", value);

        return this.thisType;
    }

    public Children lte(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "<=", value);

        return this.thisType;
    }

    public Children lte(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "<=", value);

        return this.thisType;
    }

    public Children lte(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "<=", value);

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
        this.addConditionItem(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children between(String name, Object val1, Object val2) {
        checkValueForNull(name, val1, val2);
        this.addConditionItem(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children between(boolean condition, PFun<T, ?> name, Object val1, Object val2) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        this.addConditionItem(name, "between", new Object[]{val1, val2});

        return this.thisType;
    }

    public Children between(boolean condition, String name, Object val1, Object val2) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        this.addConditionItem(name, "between", new Object[]{val1, val2});

        return this.thisType;
    }

    public Children notBetween(PFun<T, ?> name, Object val1, Object val2) {
        checkValueForNull(name, val1, val2);
        this.addConditionItemAndSetNot(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children notBetween(String name, Object val1, Object val2) {
        checkValueForNull(name, val1, val2);
        this.addConditionItemAndSetNot(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children notBetween(boolean condition, PFun<T, ?> name, Object val1, Object val2) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        this.addConditionItemAndSetNot(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children notBetween(boolean condition, String name, Object val1, Object val2) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, val1, val2);
        this.addConditionItemAndSetNot(name, "between", new Object[]{val1, val2});
        return this.thisType;
    }

    public Children like(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children like(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children like(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children like(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s%%", value));

        return this.thisType;
    }

    public Children notLike(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItemAndSetNot(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children notLike(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItemAndSetNot(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children notLike(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItemAndSetNot(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children notLike(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItemAndSetNot(name, "like", String.format("%%%s%%", value));
        return this.thisType;
    }

    public Children likeLeft(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s", value));
        return this.thisType;
    }

    public Children likeLeft(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s", value));
        return this.thisType;
    }

    public Children likeLeft(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s", value));
        return this.thisType;
    }

    public Children likeLeft(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%%%s", value));
        return this.thisType;
    }

    public Children likeRight(PFun<T, ?> name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%s%%", value));
        return this.thisType;
    }

    public Children likeRight(String name, Object value) {
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%s%%", value));
        return this.thisType;
    }

    public Children likeRight(boolean condition, PFun<T, ?> name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%s%%", value));
        return this.thisType;
    }

    public Children likeRight(boolean condition, String name, Object value) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkValueForNull(name, value);
        this.addConditionItem(name, "like", String.format("%s%%", value));
        return this.thisType;
    }

    public Children isNull(PFun<T, ?> name) {
        this.addConditionItemNotOp(new IsNull(name));
        return this.thisType;
    }

    public Children isNull(String name) {
        this.addConditionItemNotOp(new IsNull(name));
        return this.thisType;
    }

    public Children isNull(boolean condition, PFun<T, ?> name) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        this.addConditionItemNotOp(new IsNull(name));
        return this.thisType;
    }

    public Children isNull(boolean condition, String name) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        this.addConditionItemNotOp(new IsNull(name));
        return this.thisType;
    }

    public Children isNotNull(PFun<T, ?> name) {
        IsNull isNull = new IsNull(name);
        isNull.setNot(true);
        this.addConditionItemNotOp(isNull);
        return this.thisType;
    }

    public Children isNotNull(String name) {
        IsNull isNull = new IsNull(name);
        isNull.setNot(true);
        this.addConditionItemNotOp(isNull);
        return this.thisType;
    }

    public Children isNotNull(boolean condition, PFun<T, ?> name) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        IsNull isNull = new IsNull(name);
        isNull.setNot(true);
        this.addConditionItemNotOp(isNull);
        return this.thisType;
    }

    public Children isNotNull(boolean condition, String name) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        IsNull isNull = new IsNull(name);
        isNull.setNot(true);
        this.addConditionItemNotOp(isNull);
        return this.thisType;
    }

    public Children in(PFun<T, ?> name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "in", coll);
        return this.thisType;
    }

    public Children in(String name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "in", coll);
        return this.thisType;
    }

    public Children in(boolean condition, PFun<T, ?> name, Collection<?> coll) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "in", coll);
        return this.thisType;
    }

    public Children in(boolean condition, String name, Collection<?> coll) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "in", coll);
        return this.thisType;
    }

    public Children notIn(PFun<T, ?> name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "not in", coll);
        return this.thisType;
    }

    public Children notIn(String name, Collection<?> coll) {
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "not in", coll);
        return this.thisType;
    }

    public Children notIn(boolean condition, PFun<T, ?> name, Collection<?> coll) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "not in", coll);
        return this.thisType;
    }

    public Children notIn(boolean condition, String name, Collection<?> coll) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        checkCollectionValueForEmpty(name, coll);
        this.addConditionItem(name, "not in", coll);
        return this.thisType;
    }


    public Children or() {
        this.orStatus = true;
        return this.thisType;
    }


    /**
     * OR链接
     *
     * @param wapperFunction
     * @return
     */
    public final Children or(Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>> wapperFunction) {
        if (wapperFunction != null) {
            SqlExpressionGroup sqlExpressionGroup = wapperFunction.apply(new LambdaConditionWapper<T>(QueryCondition.NEW(), this.providerContext, this.notNull, this.notEmpty)).getSqlExpressionGroup();
            List<SqlExpression> exps = sqlExpressionGroup.getExps();
            if (exps != null && !exps.isEmpty()) {
                this.cnd.or(sqlExpressionGroup);
            }
        }
        return this.thisType;
    }

    /**
     * OR链接
     *
     * @param wapperFunction
     * @return
     */
    public final Children or(boolean condition, Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>> wapperFunction) {
        if (!condition) {
            this.orStatus = false;
            return this.thisType;
        }
        return this.or(wapperFunction);
    }

    /**
     * 检查集合值是否为空
     *
     * @param name
     * @param coll
     */
    protected void checkCollectionValueForEmpty(PFun name, Collection<?> coll) {
        checkCollectionValueForEmpty(LambdaQuery.resolve(name), coll);
    }

    /**
     * 检查集合值是否为空
     *
     * @param name
     * @param coll
     */
    protected void checkCollectionValueForEmpty(String name, Collection<?> coll) {
        if (this.notEmpty) {
            if (coll == null || coll.isEmpty()) {
                throw new IllegalArgumentException(String.format("Value for [%s] cannot be empty", name));
            }
        }
    }

    /**
     * 如果字段值是null将报错，阻止sql提交
     */
    @SafeVarargs
    protected final void checkValueForNull(PFun name, Object... values) {
        checkValueForNull(LambdaQuery.resolve(name), values);
    }

    /**
     * 如果字段值是null将报错，阻止sql提交
     */
    @SafeVarargs
    protected final void checkValueForNull(String name, Object... values) {
        if (this.notNull) {
            if (values == null) {
                throw new IllegalArgumentException(String.format("Value for [%s] cannot be null", name));
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null) {
                    throw new IllegalArgumentException(String.format("Value for [%s] cannot be null,index [%s]", name, i + 1));
                }
            }
        }

    }

}
