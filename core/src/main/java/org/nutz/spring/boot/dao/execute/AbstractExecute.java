package org.nutz.spring.boot.dao.execute;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.spring.boot.dao.spring.binding.method.MethodSignature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 黄川 2020/12/16
 */
public abstract class AbstractExecute implements Execute {

    protected Dao dao;

    protected MethodSignature methodSignature;
    /**
     * 参数列表
     */
    protected Map<String, Object> params;
    /**
     * 参数
     */
    protected Object[] args;
    /**
     * 真正执行的sql
     */
    protected String executeSql;


    public AbstractExecute(Dao dao, String executeSql, MethodSignature methodSignature, Object[] args) {
        this.dao = dao;
        this.methodSignature = methodSignature;
        this.args = args;
        this.executeSql = executeSql;
        this.initParams();
    }

    /**
     * 生成参数
     */
    private void initParams() {
        if (Lang.isNotEmpty(args)) {
            this.params = new HashMap<>(args.length);
            for (int i = 0; i < args.length; i++) {
                this.params.put(methodSignature.getParameterNames().get(i), args[i]);
            }
        }
    }

    /**
     * 设置
     *
     * @param sql
     */
    protected void setCondition(Sql sql) {
        if (this.methodSignature.getConditionParameterInedx() > -1) {
            sql.setCondition((Condition) this.args[this.methodSignature.getConditionParameterInedx()]);
        }
    }

    /**
     * 包裹可选返回值
     *
     * @return
     */
    protected Object returnIsOptionalVal(Object val) {
        return this.methodSignature.isReturnsOptional() ? Optional.ofNullable(val) : val;
    }

}
