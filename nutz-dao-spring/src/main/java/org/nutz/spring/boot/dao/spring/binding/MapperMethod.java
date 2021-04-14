package org.nutz.spring.boot.dao.spring.binding;


import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.spring.boot.dao.execute.*;
import org.nutz.spring.boot.dao.factory.DaoFactory;
import org.nutz.spring.boot.dao.spring.binding.helper.ConditionMapping;
import org.nutz.spring.boot.dao.spring.binding.helper.SimpleSqlParser;
import org.nutz.spring.boot.dao.spring.binding.method.MethodSignature;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Slf4j
public class MapperMethod {

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
    public MapperMethod(DaoFactory daoFactory, String dataSource, Class<?> mapperInterface, Method method) {
        this.daoFactory = daoFactory;
        this.methodSignature = new MethodSignature(mapperInterface, method);
        this.entityClass = this.methodSignature.getReturnEntityClass();
        this.entity = Objects.isNull(this.entityClass) ? null : daoFactory.getDao(dataSource).getEntity(this.entityClass);
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
    public Object execute(String dataSource, Method methodTraget, Object[] args) {
        Dao dao = daoFactory.getDao(dataSource);
        if (this.methodSignature.isCustomizeSql()) {
            this.parseAndTranslationSql();
            return this.getCustomizeSqlExecute(dao, args).invoke();
        }
        // 每次都new一个对象是方便动态传递dao进去，实现多数据源动态切换
        BaseMapper baseMapper = new BaseMapperExecute(dao, this.methodSignature.getReturnEntityClass(), this.entity);
        try {
            return methodTraget.invoke(baseMapper, args);
        } catch (Exception e) {
            throw new RuntimeException("BaseMapper执行出错", e);
        }
    }

    /**
     * 解析SQL，将sql中的实体类和字段解析为对应数据库字段
     */
    private void parseAndTranslationSql() {
        if (!StringUtils.hasText(this.sourceSql)) {
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
            boolean status = true;
            for (String param : conditionParameter) {
                Object val = El.eval(context, param);
                if (Objects.isNull(val)) {
                    // 值是Null，放弃加入sql
                    status = false;
                    continue;
                }
                if (val instanceof String && !StringUtils.hasLength((String) val)) {
                    // 值是字符串，且没有长度,则放弃加入sql
                    status = false;
                    continue;
                }
                if (Collection.class.isAssignableFrom(val.getClass()) && ((Collection) val).isEmpty()) {
                    // 是空集合
                    status = false;
                    continue;
                }
            }
            if (status) {
                // 有值，可以加入sql条件中
                sql = sql.replace(condition.getKey(), condition.getConditionSql());
            }else{
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
        Assert.notNull(execute, "不支持的方法");
        return execute;
    }

}
