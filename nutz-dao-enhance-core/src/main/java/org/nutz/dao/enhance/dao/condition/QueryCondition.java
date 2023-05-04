package org.nutz.dao.enhance.dao.condition;

import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.dao.join.JoinType;
import org.nutz.dao.enhance.dao.join.QueryJoin;
import org.nutz.dao.enhance.dao.join.SelectAsColumn;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/3
 */
public class QueryCondition extends Cnd {


    private List<QueryJoin> queryJoins = new ArrayList<>();
    private List<SelectAsColumn> selectAsColumns = new ArrayList<>();

    public QueryCondition() {
        this.cri = new SimpleCriteria();
    }

    /**
     * @return 一个 Cnd 的实例
     */
    public static QueryCondition NEW() {
        return new QueryCondition();
    }

    public List<QueryJoin> getQueryJoins() {
        return queryJoins;
    }

    public List<SelectAsColumn> getSelectAsColumns() {
        return selectAsColumns;
    }

    public boolean hasJoin() {
        return Lang.isNotEmpty(queryJoins);
    }

    public void leftJoin(Class<?> clazz, String mainName, String leftName) {
        queryJoins.add(QueryJoin.of(JoinType.LEFT, clazz, mainName, leftName));
    }

    public void rightJoin(Class<?> clazz, String mainName, String leftName) {
        queryJoins.add(QueryJoin.of(JoinType.RIGHT, clazz, mainName, leftName));
    }

    public void innerJoin(Class<?> clazz, String mainName, String leftName) {
        queryJoins.add(QueryJoin.of(JoinType.INNER, clazz, mainName, leftName));
    }

    public void selectAs(String mainFieldName, String entityFieldName) {
        selectAsColumns.add(SelectAsColumn.of(mainFieldName, entityFieldName));
    }
}
