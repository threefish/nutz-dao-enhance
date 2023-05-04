package org.nutz.dao.enhance.enhance;

import lombok.SneakyThrows;
import org.nutz.dao.Condition;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.dao.join.QueryJoin;
import org.nutz.dao.enhance.dao.join.SelectAsColumn;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.pojo.PojoFetchIntCallback;
import org.nutz.dao.impl.sql.pojo.PojoQueryEntityCallback;
import org.nutz.dao.impl.sql.pojo.QueryEntityFieldsAndSelectAsPItem;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.GroupBy;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Pojos;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/3
 */
public class EnhanceNutDao extends NutDao {

    public EnhanceNutDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public int count(Entity<?> en, Condition cnd) {
        return _count(en, en.getViewName(), cnd);
    }

    /**
     *
     */
    private int _count(Entity<?> en, String tableName, Condition cnd) {
        // 如果有条件的话
        if (null != cnd) {
            Pojo pojo = pojoMaker.makeFunc(tableName, "COUNT", "*");
            pojo.setEntity(en);
            if (cnd instanceof QueryCondition) {
                // 此条件内不能debug，否则不会按照预期执行，奇葩
                QueryCondition queryCondition = ((QueryCondition) cnd);
                appendJoin(pojo, queryCondition);
                // 高级条件接口，直接得到 WHERE 子句
                if (cnd instanceof Criteria) {
                    if (cnd instanceof SimpleCriteria) {
                        String beforeWhere = ((SimpleCriteria) cnd).getBeforeWhere();
                        if (!Strings.isBlank(beforeWhere)) {
                            pojo.append(Pojos.Items.wrap(beforeWhere));
                        }
                    }
                    pojo.append(((Criteria) cnd).where());
                    // MySQL/PgSQL/SqlServer 与 Oracle/H2的结果会不一样,奇葩啊
                    GroupBy gb = ((Criteria) cnd).getGroupBy();
                    pojo.append(gb);
                }
                // 否则暴力获取 WHERE 子句
                else {
                    String str = Pojos.formatCondition(en, cnd);
                    if (!Strings.isBlank(str)) {
                        String[] ss = str.toUpperCase().split("ORDER BY");
                        pojo.append(Pojos.Items.wrap(str.substring(0, ss[0].length())));
                    }
                }

            }
            // 设置回调，并执行 SQL
            pojo.setAfter(new PojoFetchIntCallback());
            _exec(pojo);
            return pojo.getInt();
        }
        // 没有条件，直接生成表达式
        return func(tableName, "COUNT", "*");
    }

    @SneakyThrows
    @Override
    public <T> List<T> query(Entity<T> entity, Condition cnd, Pager pager) {
        if (cnd instanceof QueryCondition) {
            QueryCondition queryCondition = ((QueryCondition) cnd);
            if (queryCondition.hasJoin()) {
                Pojo pojo = makeQuery(entity, queryCondition.getSelectAsColumns());
                appendJoin(pojo, queryCondition);
                pojo.append(Pojos.Items.cnd(cnd))
                        .addParamsBy("*")
                        .setPager(pager)
                        .setAfter(new PojoQueryEntityCallback());
                expert.formatQuery(pojo);
                _exec(pojo);
                return pojo.getList(entity.getType());
            }
        }
        return super.query(entity, cnd, pager);
    }

    private void appendJoin(Pojo pojo, QueryCondition queryCondition) {
        for (QueryJoin queryJoin : queryCondition.getQueryJoins()) {
            Entity joinEntity = holder.getEntity(queryJoin.getClazz());
            String mainFieldName = queryJoin.getMainFiled();
            String joinFieldName = queryJoin.getJoinField();
            pojo.append(Pojos.Items.wrapf("%s JOIN %s ON %s=%s", queryJoin.getType().getValue(), joinEntity.getTableName(), mainFieldName, joinFieldName));
        }
    }

    private Pojo makeQuery(Entity<?> en, List<SelectAsColumn> selectAsColumns) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.SELECT);
        pojo.setEntity(en);
        pojo.append(new QueryEntityFieldsAndSelectAsPItem(selectAsColumns));
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityViewName());
        return pojo;
    }

}
