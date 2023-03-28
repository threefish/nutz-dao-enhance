package org.nutz.dao.enhance.dao.lambda;


import org.nutz.dao.Condition;
import org.nutz.dao.sql.GroupBy;
import org.nutz.lang.Each;

import java.util.List;

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

}
