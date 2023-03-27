package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.dao.BaseDao;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Each;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2022/12/28
 */
@SuppressWarnings("all")
public class LambdaQuery<T> extends LambdaCondition<LambdaQuery<T>, T> {

    protected final BaseDao<T> baseDao;

    public LambdaQuery(ProviderContext providerContext, boolean notNull, boolean notEmpty) {
        super(Cnd.NEW(), providerContext, notNull, notEmpty);
        this.baseDao = ((BaseDao) providerContext.proxy);
    }

    @SafeVarargs
    public final LambdaQuery<T> select(PFun<T, ?>... names) {
        return activeds(names);
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

    public LambdaQuery<T> desc(PFun<T, ?> name) {
        cnd.desc(name);
        return this;
    }

    public LambdaQueryGroupBy<T> groupBy(PFun<T, ?>... names) {
        return new LambdaQueryGroupBy(this, cnd.groupBy(names));
    }

    public LambdaQuery<T> orderBy(PFun<T, ?> name, String dir) {
        cnd.orderBy(name, dir);
        return this;
    }

    public LambdaQuery<T> orderByAsc(PFun<T, ?> name) {
        cnd.orderBy(name, "asc");
        return this;
    }

    public LambdaQuery<T> orderByDesc(PFun<T, ?> name) {
        cnd.orderBy(name, "desc");
        return this;
    }

    public LambdaQuery<T> limit(int pageNumber, int pageSize) {
        cnd.limit(pageNumber, pageSize);
        return this;
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
