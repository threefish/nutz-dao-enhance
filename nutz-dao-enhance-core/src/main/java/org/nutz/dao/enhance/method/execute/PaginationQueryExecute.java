package org.nutz.dao.enhance.method.execute;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 2020/12/15
 * 分页查询
 */
@Slf4j
public class PaginationQueryExecute extends AbstractExecute {

    /**
     * 分页数据信息
     */
    private final Pager pager;

    /**
     * 真正执行的sql
     */
    protected String countExecuteSql;

    public PaginationQueryExecute(Dao dao, String executeSql, String countExecuteSql, MethodSignature methodSignature, Object[] args) {
        super(dao, executeSql, methodSignature, args);
        // 第一位必须是分页对象
        this.pager = (Pager) args[0];
        this.countExecuteSql = countExecuteSql;
    }

    /**
     * 执行
     */
    @Override
    public Object invoke() {
        PageRecord pageRecord = new PageRecord();
        List listData = new ArrayList();
        Sql sql = Sqls.create(executeSql).setParams(this.params);
        this.setCondition(sql);
        pageRecord.setTotal(fetchCount(sql));
        this.pager.setRecordCount(Math.toIntExact(pageRecord.getTotal()));
        if (pageRecord.getTotal() > 0) {
            sql.setPager(this.pager);
            sql.setEntity(dao.getEntity(this.methodSignature.getEntityClass()));
            sql.setCallback(methodSignature.getSqlCallback());
            dao.execute(sql);
            listData = sql.getList(methodSignature.getEntityClass());
        }
        pageRecord.setRecords(listData);
        pageRecord.setPager(this.pager);

        return this.returnIsOptionalVal(pageRecord);
    }

    private long fetchCount(Sql sql) {
        if (Strings.isNotBlank(countExecuteSql)) {
            Sql countSql = Sqls.fetchInt(countExecuteSql).setParams(this.params);
            dao.execute(countSql);
            this.setCondition(countSql);
            return countSql.getLong();
        }
        return Daos.queryCount(dao, sql);
    }

}
