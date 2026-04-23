package org.nutz.dao.enhance.dao.lambda;


import org.nutz.dao.Condition;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.GroupBy;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Each;

import java.util.List;
import java.util.function.Function;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2022/12/28
 */
@SuppressWarnings("all")
public class LambdaQueryGroupBy<T> {

    private final LambdaQuery lambdaQuery;
    private final GroupBy groupBy;

    public LambdaQueryGroupBy(LambdaQuery lambdaQuery, org.nutz.dao.sql.GroupBy groupBy) {
        this.lambdaQuery = lambdaQuery;
        this.groupBy = groupBy;
    }

    public LambdaQueryGroupBy<T> having(Condition cnd) {
        this.groupBy.having(cnd);
        return this;
    }

    /**
     * having 条件（Lambda 风格）
     */
    public LambdaQueryGroupBy<T> having(Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>> wapperFunction) {
        if (wapperFunction != null) {
            ProviderContext providerContext = this.lambdaQuery.providerContext;
            SqlExpressionGroup sqlExpressionGroup = wapperFunction.apply(
                    new LambdaConditionWapper<>(QueryCondition.NEW(), providerContext, this.lambdaQuery.notNull, this.lambdaQuery.notEmpty)
            ).getSqlExpressionGroup();
            List<SqlExpression> exps = sqlExpressionGroup.getExps();
            if (exps != null && !exps.isEmpty()) {
                QueryCondition havingCnd = QueryCondition.NEW();
                for (SqlExpression exp : exps) {
                    havingCnd.getCri().where().and(exp);
                }
                this.groupBy.having(havingCnd);
            }
        }
        return this;
    }

    /**
     * having 条件（带条件的 Lambda 风格）
     */
    public LambdaQueryGroupBy<T> having(boolean condition, Function<LambdaConditionWapper<T>, LambdaConditionWapper<T>> wapperFunction) {
        return condition ? having(wapperFunction) : this;
    }

    /**
     * 查询
     */
    public List<T> list() {
        return (List<T>) this.lambdaQuery._invoke(() -> lambdaQuery.baseDao.list(groupBy));
    }

    /**
     * 查询
     */
    public void eachRow(Each<T> each) {
        this.lambdaQuery._invoke(() -> this.lambdaQuery.baseDao.each(groupBy, each));
    }

    /**
     * 统计数量
     */
    public int count() {
        Object result = this.lambdaQuery._invoke(() -> this.lambdaQuery.baseDao.count(groupBy));
        return result instanceof Integer ? (Integer) result : 0;
    }

    /**
     * 分页查询
     */
    @SuppressWarnings("unchecked")
    public PageRecord<T> listPage(int pageNumber, int pageSize) {
        return (PageRecord<T>) this.lambdaQuery._invoke(() -> this.lambdaQuery.baseDao.listPage(groupBy, pageNumber, pageSize));
    }

    /**
     * 分页查询
     */
    @SuppressWarnings("unchecked")
    public PageRecord<T> listPage(Pager pager) {
        return (PageRecord<T>) this.lambdaQuery._invoke(() -> this.lambdaQuery.baseDao.listPage(groupBy, pager));
    }

}
