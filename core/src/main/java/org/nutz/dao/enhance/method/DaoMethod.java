package org.nutz.dao.enhance.method;


import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.execute.*;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.holder.EntityClassInfoHolder;
import org.nutz.dao.enhance.method.parser.ConditionMapping;
import org.nutz.dao.enhance.method.parser.SimpleSqlParser;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.entity.Entity;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Slf4j
public class DaoMethod {

    /**
     * 方法信息
     */
    private final MethodSignature methodSignature;
    private final DaoFactory daoFactory;
    private final Entity<?> entity;
    private final Class<?> entityClass;
    /**
     * 源sql信息
     */
    private String sourceSql;
    /**
     * 动态条件信息，若入参为空或者null或个数为0则不参与sql执行
     */
    private List<ConditionMapping> conditions = Collections.EMPTY_LIST;

    /**
     * 这里对mapper进行解析，每个mapper只会解析1次
     *
     * @param mapperInterface
     * @param method
     */
    public DaoMethod(DaoFactory daoFactory, String dataSource, Class<?> mapperInterface, Method method) {
        this.daoFactory = daoFactory;
        this.methodSignature = new MethodSignature(mapperInterface, method);
        this.entityClass = this.methodSignature.getEntityClass();
        final Dao dao = daoFactory.getDao(dataSource);
        if (Objects.isNull(dao)) {
            throw new RuntimeException(String.format("'%s' dao is null", dataSource));
        }
        this.entity = Objects.isNull(this.entityClass) ? null : dao.getEntity(this.entityClass);
        if (Objects.nonNull(this.entity)) {
            EntityClassInfoHolder.setEntity(this.entityClass, this.entity);
        }
    }

    /**
     * 执行
     *
     * @param dataSource
     * @param methodTraget
     * @param args
     * @return
     */
    public Object execute(String dataSource, Method methodTraget, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Stopwatch stopWatch = new Stopwatch();
        try {
            stopWatch.start();
            Dao dao = daoFactory.getDao(dataSource);
            if (this.methodSignature.isCustomizeSql()) {
                this.parseAndTranslationSql();
                return this.getCustomizeSqlExecute(dao, args).invoke();
            }
            // 每次都new一个对象是方便动态传递dao进去，实现多数据源动态切换
            BaseDao baseMapper = new BaseDaoImpl(dao, this.methodSignature.getEntityClass(), this.entity);
            return methodTraget.invoke(baseMapper, args);
        } finally {
            stopWatch.stop();
            log.debug("SQL执行耗时:{}ms", stopWatch.getDuration());
        }
    }

    /**
     * 解析SQL，将sql中的实体类和字段解析为对应数据库字段
     */
    private void parseAndTranslationSql() {
        if (Strings.isBlank(this.sourceSql)) {
            SimpleSqlParser simpleSqlParserHelper = new SimpleSqlParser(this.methodSignature.getSqlTemplate());
            simpleSqlParserHelper.parse();
            this.sourceSql = simpleSqlParserHelper.getSql();
            this.conditions = simpleSqlParserHelper.getConditions();
        }
    }

    /**
     * 替换条件sql
     *
     * @param args
     * @return
     */
    private String replaceConditionSql(Object[] args) {
        String sql = this.sourceSql;
        final Context context = Lang.context();
        if (Lang.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                context.set(methodSignature.getParameterNames().get(i), args[i]);
            }
        }
        for (ConditionMapping condition : this.conditions) {
            final Set<String> conditionParameter = condition.getConditionParameter();
            // 有参数的情况下，默认加入条件，在看是否满足条件
            boolean status = conditionParameter.size() > 0;
            for (String param : conditionParameter) {
                Object val = El.eval(context, param);
                if (Objects.isNull(val)) {
                    // 值是Null，放弃加入sql
                    status = false;
                    continue;
                }
                if (val instanceof String && Strings.isBlank((String) val)) {
                    // 值是字符串，且没有长度,则放弃加入sql
                    status = false;
                    continue;
                }
                if (Collection.class.isAssignableFrom(val.getClass()) && ((Collection) val).isEmpty()) {
                    // 是空集合
                    status = false;
                    continue;
                }
                if (val.getClass().isArray() && ((Object[]) val).length == 0) {
                    // 是空数组
                    status = false;
                    continue;
                }
            }
            if (status) {
                // 有值，可以加入sql条件中
                sql = sql.replace(condition.getKey(), condition.getConditionSql());
            } else {
                sql = sql.replace(condition.getKey(), " ");
            }
        }
        return sql;
    }

    /**
     * 是自定义sql
     *
     * @param dao
     * @param args
     * @return
     */
    private Execute getCustomizeSqlExecute(Dao dao, Object[] args) {
        String executeSql = replaceConditionSql(args);
        log.debug("执行SQL:{}", executeSql);
        Execute execute = null;
        switch (this.methodSignature.getSqlCommandType()) {
            case SELECT:
                if (this.methodSignature.isPaginationQuery()) {
                    // 是分页查询
                    execute = new PaginationQueryExecute(dao, executeSql, this.methodSignature, args);
                } else if (this.methodSignature.isMultipleRecords()) {
                    // 返回多条记录的情况下
                    execute = new MultipleQueryExecute(dao, executeSql, this.methodSignature, args);
                } else {
                    // 返回单条记录，获取未知的情况
                    execute = new SingleQueryExecute(dao, executeSql, this.methodSignature, args);
                }
                break;
            case UPDATE:
            case DELETE:
                execute = new UpdateQueryExecute(dao, executeSql, this.methodSignature, args);
                break;
            case INSERT:
                execute = new InsertQueryExecute(dao, executeSql, this.methodSignature, args);
                break;
            default:
        }
        if (Objects.isNull(execute)) {
            throw new RuntimeException("不支持的方法");
        }
        return execute;
    }

}
