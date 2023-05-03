package org.nutz.dao.enhance.enhance;

import lombok.SneakyThrows;
import org.nutz.dao.Condition;
import org.nutz.dao.enhance.dao.condition.QueryCondition;
import org.nutz.dao.enhance.dao.join.QueryJoin;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.pojo.PojoQueryEntityCallback;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;
import org.nutz.dao.util.lambda.LambdaQuery;

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

    @SneakyThrows
    @Override
    public <T> List<T> query(Entity<T> entity, Condition cnd, Pager pager) {
        if (cnd instanceof QueryCondition) {
            QueryCondition queryCondition = ((QueryCondition) cnd);
            if (queryCondition.hasJoin()) {
                Pojo pojo = pojoMaker.makeQuery(entity);
                for (QueryJoin queryJoin : queryCondition.getQueryJoins()) {
                    Entity joinEntity = holder.getEntity(queryJoin.getClazz());
                    String mainFieldName = queryJoin.getMainFiled();
                    String joinFieldName = queryJoin.getJoinField();
                    pojo.append(Pojos.Items.wrapf("%s JOIN %s ON %s=%s", queryJoin.getType().getValue(), joinEntity.getTableName(), mainFieldName, joinFieldName));
                }
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


}
