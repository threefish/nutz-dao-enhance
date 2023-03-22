package org.nutz.dao.enhance.dao.lambda;


import org.nutz.dao.Condition;
import org.nutz.dao.enhance.dao.BaseDao;
import org.nutz.dao.sql.GroupBy;
import org.nutz.lang.Each;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2022/12/28
 */
public class LambdaQueryGroupBy<T> {

    private final BaseDao baseDao;
    private final GroupBy groupBy;

    public LambdaQueryGroupBy(BaseDao baseDao, org.nutz.dao.sql.GroupBy groupBy) {
        this.baseDao = baseDao;
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
        return this.baseDao.query(groupBy);
    }

    /**
     * 查询
     */
    public void eachRow(Each<T> each) {
        this.baseDao.each(groupBy, each);
    }

}
