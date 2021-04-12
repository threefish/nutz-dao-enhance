package org.nutz.spring.boot.dao.execute;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.spring.boot.dao.pagination.PageData;
import org.nutz.spring.boot.dao.spring.binding.method.MethodSignature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 2020/12/15
 * 分页查询
 */
public class PaginationQueryExecute extends AbstractExecute {

    /**
     * 分页数据信息
     */
    private final Pager pager;

    public PaginationQueryExecute(Dao dao, MethodSignature methodSignature, Object[] args) {
        super(dao, methodSignature, args);
        // 第一位必须是分页对象
        this.pager = (Pager) args[0];
    }

    /**
     * 执行
     *
     * @return
     */
    @Override
    public Object invoke() {
        PageData pageData = new PageData();
        pageData.setPager(this.pager);
        List listData = new ArrayList();
        Sql sql = Sqls.create(methodSignature.getSql()).setParams(this.params);
        this.setCondition(sql);
        pageData.setTotal(Daos.queryCount(dao, sql));
        if (pageData.getTotal() > 0) {
            sql.setPager(this.pager);
            sql.setEntity(dao.getEntity(this.methodSignature.getReturnEntityClass()));
            sql.setCallback(methodSignature.getSqlCallback());
            dao.execute(sql);
            listData = sql.getList(methodSignature.getReturnEntityClass());
        }
        pageData.setRecords(listData);
        return pageData;
    }


}
