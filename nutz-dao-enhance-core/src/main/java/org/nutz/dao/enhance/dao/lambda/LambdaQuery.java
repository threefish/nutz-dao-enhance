package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.enhance.dao.BaseDao;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.enhance.util.LambdaQueryUtil;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Each;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2022/12/28
 */
@SuppressWarnings("all")
public class LambdaQuery<T> extends LambdaCondition<LambdaQuery<T>, T> {

    protected final BaseDao<T> baseDao;

    public LambdaQuery(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        super(QueryCondition.NEW(), providerContext, notNull, notEmpty);
        this.baseDao = ((BaseDao) providerContext.proxy);
    }

    @SafeVarargs
    public final LambdaQuery<T> select(PFun<T, ?>... names) {
        return activeds(names);
    }

    public final <JOIN> LambdaQuery<T> selectAs(PFun<T, ?> mainName,  PFun<JOIN, ?> joinName) {
        cnd.selectAs(org.nutz.dao.util.lambda.LambdaQuery.resolve(mainName),getEntityFieldName(LambdaQueryUtil.resolve(joinName), joinName));
        return this;
    }

    public LambdaQuery<T> where(PFun<T, ?> name, String op, Object value) {
        this.and(name, op, value);
        return this;
    }

    public LambdaQuery<T> whereEx(PFun<T, ?> name, String op, Object value) {
        this.andEX(name, op, value);
        return this;
    }

    public LambdaQuery<T> and(PFun<T, ?> name, String op, Object value) {
        cnd.and(name, op, value);
        return this;
    }

    public LambdaQuery<T> or(PFun<T, ?> name, String op, Object value) {
        cnd.or(name, op, value);
        return this;
    }

    public LambdaQuery<T> andEX(PFun<T, ?> name, String op, Object value) {
        cnd.andEX(name, op, value);
        return this;
    }

    public LambdaQuery<T> orEx(PFun<T, ?> name, String op, Object value) {
        cnd.orEX(name, op, value);
        return this;
    }

    public LambdaQuery<T> asc(PFun<T, ?> name) {
        cnd.asc(name);
        return this;
    }

    public LambdaQuery<T> asc(String name) {
        cnd.asc(name);
        return this;
    }

    public LambdaQuery<T> desc(PFun<T, ?> name) {
        cnd.desc(name);
        return this;
    }

    public LambdaQuery<T> desc(String name) {
        cnd.desc(name);
        return this;
    }

    public LambdaQueryGroupBy<T> groupBy(PFun<T, ?>... names) {
        return new LambdaQueryGroupBy(this, cnd.groupBy(names));
    }

    public LambdaQueryGroupBy<T> groupBy(String... names) {
        return new LambdaQueryGroupBy(this, cnd.groupBy(names));
    }

    public LambdaQuery<T> orderBy(PFun<T, ?> name, String dir) {
        cnd.orderBy(name, dir);
        return this;
    }

    public LambdaQuery<T> orderBy(String name, String dir) {
        cnd.orderBy(name, dir);
        return this;
    }

    public LambdaQuery<T> orderByAsc(PFun<T, ?> name) {
        cnd.orderBy(name, "asc");
        return this;
    }

    public LambdaQuery<T> orderByAsc(String name) {
        cnd.orderBy(name, "asc");
        return this;
    }

    public LambdaQuery<T> orderByDesc(PFun<T, ?> name) {
        cnd.orderBy(name, "desc");
        return this;
    }

    public LambdaQuery<T> orderByDesc(String name) {
        cnd.orderBy(name, "desc");
        return this;
    }

    public LambdaQuery<T> limit(int pageNumber, int pageSize) {
        cnd.limit(pageNumber, pageSize);
        return this;
    }

    public <JOIN> LambdaQuery<T> leftJoin(Class<?> clazz, PFun<T, ?> mainName, PFun<JOIN, ?> leftName) {
        cnd.leftJoin(clazz, getEntityFieldName(providerContext.entityClass, mainName), getEntityFieldName(clazz, leftName));
        return this;
    }

    public <JOIN> LambdaQuery<T> rightJoin(Class<?> clazz, PFun<T, ?> mainName, PFun<JOIN, ?> leftName) {
        cnd.rightJoin(clazz, getEntityFieldName(providerContext.entityClass, mainName), getEntityFieldName(clazz, leftName));
        return this;
    }

    public <JOIN> LambdaQuery<T> innerJoin(Class<?> clazz, PFun<T, ?> mainName, PFun<JOIN, ?> leftName) {
        cnd.innerJoin(clazz, getEntityFieldName(providerContext.entityClass, mainName), getEntityFieldName(clazz, leftName));
        return this;
    }

    private <JOIN> String getEntityFieldName(Class<?> clazz, PFun<JOIN, ?> name) {
        Entity<?> entity = this.providerContext.dao.getEntityHolder().getEntity(clazz);
        MappingField field = entity.getField(org.nutz.dao.util.lambda.LambdaQuery.resolve(name));
        return String.format("%s.%s", entity.getTableName(), field.getColumnName());
    }

    public <JOIN> LambdaQuery<T> eq(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.eq(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> eq(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.eq(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> ne(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.ne(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> ne(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.ne(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    /**
     * 大于
     *
     * @param name
     * @param value
     * @return
     */
    public <JOIN> LambdaQuery<T> gt(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.gt(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> gt(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.gt(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    /**
     * 大于等于
     *
     * @param name
     * @param value
     * @return
     */
    public <JOIN> LambdaQuery<T> gte(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.gte(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> gte(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.gte(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    /**
     * 小于
     *
     * @param name
     * @param value
     * @return
     */
    public <JOIN> LambdaQuery<T> lt(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.lt(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> lt(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.lt(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    /**
     * 小于等于
     *
     * @param name
     * @param value
     * @return
     */
    public <JOIN> LambdaQuery<T> lte(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.lte(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> lte(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.lte(condition, getEntityFieldName(clazz, name), value);
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
    public <JOIN> LambdaQuery<T> between(Class<?> clazz, PFun<JOIN, ?> name, Object val1, Object val2) {
        this.between(getEntityFieldName(clazz, name), val1, val2);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> between(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object val1, Object val2) {
        this.between(condition, getEntityFieldName(clazz, name), val1, val2);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> notBetween(Class<?> clazz, PFun<JOIN, ?> name, Object val1, Object val2) {
        this.notBetween(getEntityFieldName(clazz, name), val1, val2);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> notBetween(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object val1, Object val2) {
        this.notBetween(condition, getEntityFieldName(clazz, name), val1, val2);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> like(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.like(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> like(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.like(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> likeLeft(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.likeLeft(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> likeLeft(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.likeLeft(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> likeRight(Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.likeRight(getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> likeRight(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Object value) {
        this.likeRight(condition, getEntityFieldName(clazz, name), value);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> in(Class<?> clazz, PFun<JOIN, ?> name, Collection<?> coll) {
        this.in(getEntityFieldName(clazz, name), coll);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> in(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Collection<?> coll) {
        this.in(condition, getEntityFieldName(clazz, name), coll);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> notIn(Class<?> clazz, PFun<JOIN, ?> name, Collection<?> coll) {
        this.notIn(getEntityFieldName(clazz, name), coll);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> notIn(boolean condition, Class<?> clazz, PFun<JOIN, ?> name, Collection<?> coll) {
        this.notIn(condition, getEntityFieldName(clazz, name), coll);
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> isNull(Class<?> clazz, PFun<JOIN, ?> name) {
        this.isNull(getEntityFieldName(clazz, name));
        return this.thisType;
    }

    public <JOIN> LambdaQuery<T> isNotNull(Class<?> clazz, PFun<JOIN, ?> name) {
        this.isNotNull(getEntityFieldName(clazz, name));
        return this.thisType;
    }

    /**
     * 查询
     */
    public T one() {
        return _invoke(() -> this.baseDao.fetch(cnd));
    }

    /**
     * 查询
     */
    public Optional<T> oneOpt() {
        return Optional.ofNullable(_invoke(() -> this.baseDao.fetch(cnd)));
    }

    /**
     * 查询
     */
    public List<T> list() {
        return _invoke(() -> this.baseDao.list(cnd));
    }

    /**
     * 分页查询
     */
    public PageRecord<T> listPage() {
        if (cnd.getPager() == null) {
            throw new IllegalArgumentException("请设置 limit 或 Pager");
        }
        return _invoke(() -> this.baseDao.listPage(cnd, cnd.getPager()));
    }

    /**
     * 分页查询
     */
    public PageRecord<T> listPage(Pager pager) {
        return _invoke(() -> this.baseDao.listPage(cnd, pager));
    }

    /**
     * 分页查询
     */
    public PageRecord<T> listPage(int pageNumber, int pageSize) {
        return _invoke(() -> this.baseDao.listPage(cnd, pageNumber, pageSize));
    }

    /**
     * 查询
     */
    public void eachRow(Each<T> each) {
        _invoke(() -> this.baseDao.each(cnd, each));
    }


    public int count() {
        return _invoke(() -> this.baseDao.count(cnd));
    }

}
