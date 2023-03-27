package org.nutz.dao.enhance.dao.lambda;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/24
 */
public class LambdaConditionWapper<T> extends LambdaCondition<LambdaConditionWapper<T>, T> {

    protected LambdaConditionWapper(Cnd cnd, boolean notNull, boolean notEmpty) {
        super(cnd, notNull, notEmpty);
    }

    public SqlExpressionGroup getSqlExpressionGroup() {
        return this.cnd.getCri().where();
    }
}
