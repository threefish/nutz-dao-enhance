package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.dao.BaseDao;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.lambda.PFun;
import org.nutz.lang.Each;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2022/12/28
 */
public class LambdaQueryWhere<T> {

    private final Cnd cnd;

    private final BaseDao baseDao;

    public LambdaQueryWhere(ProviderContext providerContext) {
        this.baseDao = ((BaseDao) providerContext.proxy);
        this.cnd = Cnd.NEW();
    }

    public LambdaQueryWhere<T> where(PFun<T, ?> name, String op, Object value) {
        this.and(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> whereEx(PFun<T, ?> name, String op, Object value) {
        this.andEX(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> and(SqlExpression exp) {
        cnd.and(exp);
        return this;
    }

    public LambdaQueryWhere<T> or(SqlExpression exp) {
        cnd.or(exp);
        return this;
    }


    public LambdaQueryWhere<T> and(PFun<T, ?> name, String op, Object value) {
        cnd.and(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> or(PFun<T, ?> name, String op, Object value) {
        cnd.or(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> andEX(PFun<T, ?> name, String op, Object value) {
        cnd.andEX(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> orEx(PFun<T, ?> name, String op, Object value) {
        cnd.orEX(name, op, value);
        return this;
    }

    public LambdaQueryWhere<T> asc(PFun<T, ?> name) {
        cnd.asc(name);
        return this;
    }

    public LambdaQueryWhere<T> desc(PFun<T, ?> name) {
        cnd.desc(name);
        return this;
    }

    public LambdaQueryGroupBy<T> groupBy(PFun<T, ?>... names) {
        return new LambdaQueryGroupBy(this.baseDao, cnd.groupBy(names));
    }

    public LambdaQueryWhere<T> orderBy(PFun<T, ?> name, String dir) {
        cnd.orderBy(name, dir);
        return this;
    }

    public LambdaQueryWhere<T> orderByAsc(PFun<T, ?> name) {
        cnd.orderBy(name, "asc");
        return this;
    }

    public LambdaQueryWhere<T> orderByDesc(PFun<T, ?> name) {
        cnd.orderBy(name, "desc");
        return this;
    }

    public LambdaQueryWhere<T> limit(int pageNumber, int pageSize) {
        cnd.limit(pageNumber, pageSize);
        return this;
    }


    /**
     * 查询
     */
    public T fetch() {
        return (T) this.baseDao.fetch(cnd);
    }

    /**
     * 查询
     */
    public List<T> query() {
        return this.baseDao.query(cnd);
    }

    /**
     * 分页查询
     */
    public PageRecord<T> queryPage() {
        if (cnd.getPager() == null) {
            throw new IllegalArgumentException("请设置 limit 或 Pager");
        }
        return this.baseDao.queryPage(cnd, cnd.getPager());
    }

    /**
     * 分页查询
     */
    public PageRecord<T> queryPage(Pager pager) {
        return this.baseDao.queryPage(cnd, pager);
    }

    /**
     * 分页查询
     */
    public PageRecord<T> queryPage(int pageNumber, int pageSize) {
        return this.baseDao.queryPage(cnd, pageNumber, pageSize);
    }

    /**
     * 查询
     */
    public void eachRow(Each<T> each) {
        this.baseDao.each(cnd, each);
    }

}
