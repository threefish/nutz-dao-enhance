package org.nutz.dao.enhance.method;


import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.annotation.*;
import org.nutz.dao.enhance.el.AuditingEntityRunMethod;
import org.nutz.dao.enhance.el.IdentifierGeneratorRunMethod;
import org.nutz.dao.enhance.enhance.EnhanceNutDaoElPojoInterceptor;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.method.execute.*;
import org.nutz.dao.enhance.method.holder.EntityClassInfoHolder;
import org.nutz.dao.enhance.method.parser.ConditionMapping;
import org.nutz.dao.enhance.method.parser.SimpleSqlParser;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.enhance.util.MethodSignatureUtil;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.interceptor.PojoInterceptor;
import org.nutz.dao.interceptor.impl.DefaultPojoInterceptor;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/7/31
 */
@Slf4j
public class DaoMethodInvoke {

    /**
     * 方法信息
     */
    private final MethodSignature methodSignature;
    private final DaoFactory daoFactory;
    private final Class<?> entityClass;
    private Entity<?> entity;
    /**
     * 源sql信息
     */
    private String sourceSql;
    private String countSourceSql;
    /**
     * 动态条件信息，若入参为空或者null或个数为0则不参与sql执行
     */
    private List<ConditionMapping> conditions = Collections.EMPTY_LIST;
    private List<ConditionMapping> countConditions = Collections.EMPTY_LIST;


    /**
     * 这里对mapper进行解析，每个mapper只会解析1次
     *
     * @param mapperInterface
     * @param method
     */
    public DaoMethodInvoke(DaoFactory daoFactory, String dataSource, Class<?> mapperInterface, Method method) {
        this.daoFactory = daoFactory;
        this.methodSignature = new MethodSignature(mapperInterface, method);
        this.entityClass = this.methodSignature.getEntityClass();
        final Dao dao = daoFactory.getDao(dataSource);
        if (Objects.isNull(dao)) {
            throw new RuntimeException(String.format("dataSource '%s' is null.", dataSource));
        }
        this.initEntityInfo(dao);

    }

    private void initEntityInfo(Dao dao) {
        this.entity = Objects.isNull(this.entityClass) ? null : dao.getEntity(this.entityClass);
        boolean cacheEntityEquals = this.entity == EntityClassInfoHolder.getEntity(this.entityClass);
        boolean nonNull = Objects.nonNull(this.entityClass) && Objects.nonNull(this.entity);
        if (nonNull && !cacheEntityEquals) {
            EntityClassInfoHolder.setEntity(this.entityClass, this.entity, dao.getEntityHolder());
            PojoInterceptor interceptor = this.entity.getInterceptor();
            if (interceptor instanceof DefaultPojoInterceptor) {
                DefaultPojoInterceptor defaultPojoInterceptor = ((DefaultPojoInterceptor) interceptor);
                List<Field> declaredFields = MethodSignatureUtil.getAllFields(this.entityClass);
                for (Field declaredField : declaredFields) {
                    MappingField mf = this.entity.getField(declaredField.getName());
                    CreatedBy createdBy = declaredField.getAnnotation(CreatedBy.class);
                    CreatedDate createdDate = declaredField.getAnnotation(CreatedDate.class);
                    LastModifiedBy lastModifiedBy = declaredField.getAnnotation(LastModifiedBy.class);
                    LastModifiedDate lastModifiedDate = declaredField.getAnnotation(LastModifiedDate.class);
                    AutoID autoID = declaredField.getAnnotation(AutoID.class);
                    if (Objects.nonNull(autoID)) {
                        defaultPojoInterceptor.getList().add(new EnhanceNutDaoElPojoInterceptor(mf, IdentifierGeneratorRunMethod.FUN_NAME, "prevInsert", autoID.nullEffective()));
                    } else if (Objects.nonNull(createdBy)) {
                        defaultPojoInterceptor.getList().add(new EnhanceNutDaoElPojoInterceptor(mf, AuditingEntityRunMethod.FUN_NAME, "prevInsert", createdBy.nullEffective()));
                    } else if (Objects.nonNull(lastModifiedBy)) {
                        defaultPojoInterceptor.getList().add(new EnhanceNutDaoElPojoInterceptor(mf, AuditingEntityRunMethod.FUN_NAME, "prevUpdate", lastModifiedBy.nullEffective()));
                    } else if (Objects.nonNull(createdDate)) {
                        defaultPojoInterceptor.getList().add(new EnhanceNutDaoElPojoInterceptor(mf, "now()", "prevInsert", createdDate.nullEffective()));
                    } else if (Objects.nonNull(lastModifiedDate)) {
                        defaultPojoInterceptor.getList().add(new EnhanceNutDaoElPojoInterceptor(mf, "now()", "prevUpdate", lastModifiedDate.nullEffective()));
                    }
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("'{}' PojoInterceptor is not DefaultPojoInterceptor,Will affect audit functionality!!!", this.entityClass);
                }
            }
        }
    }


    /**
     * 执行
     *
     * @param proxy
     * @param dataSource
     * @param methodTraget
     * @param args
     * @return
     */
    public Object execute(Object proxy, String dataSource, Method methodTraget, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Stopwatch stopWatch = new Stopwatch();
        try {
            stopWatch.start();
            Dao dao = daoFactory.getDao(dataSource);
            if (this.methodSignature.isCustomizeSql()) {
                this.parseAndTranslationSql();
                // 是自定义sql，且有自定义提供方法处理
                if (this.methodSignature.isCustomProvider()) {
                    return this.invokeCustomProvider(proxy, dao, args, this.entity);
                }
                return this.getCustomizeSqlExecute(dao, args).invoke();
            } else if (this.methodSignature.isCustomProvider()) {
                // 有自定义提供方法处理,但不是自定义sql
                return this.invokeCustomProvider(proxy, dao, args, this.entity);
            }
            throw new UnsupportedOperationException(String.format("方法 %s 未提供实现方法！", methodTraget.toGenericString()));
        } finally {
            stopWatch.stop();
            if (log.isDebugEnabled()) {
                log.debug("执行耗时:{}ms", stopWatch.getDuration());
            }
        }
    }

    /**
     * 执行自定义处理器方法
     *
     * @param dao
     * @param args
     * @return
     */
    private Object invokeCustomProvider(Object proxy, Dao dao, Object[] args, Entity entity) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String executeSql = replaceConditionSql(this.sourceSql, args);
        ProviderContext providerContext = ProviderContext.of(dao, this.methodSignature, executeSql, args, this.methodSignature.getEntityClass(), entity, proxy);
        Object[] parameterObject = Objects.nonNull(args) ? new Object[args.length + 1] : new Object[1];
        parameterObject[0] = providerContext;
        if (Objects.nonNull(args)) {
            for (int i = 0; i < args.length; i++) {
                parameterObject[i + 1] = args[i];
            }
        }
        Object targetObject = this.methodSignature.getCustomProviderType().getDeclaredConstructor().newInstance();
        return this.methodSignature.getCustomProviderMethod().invoke(targetObject, parameterObject);
    }

    /**
     * 解析SQL，将sql中的实体类和字段解析为对应数据库字段
     */
    private void parseAndTranslationSql() {
        if (Strings.isBlank(this.sourceSql) && Strings.isNotBlank(this.methodSignature.getSqlTemplate())) {
            SimpleSqlParser simpleSqlParserHelper = new SimpleSqlParser(this.methodSignature.getSqlTemplate()).parse();
            this.sourceSql = simpleSqlParserHelper.getSql();
            this.conditions = simpleSqlParserHelper.getConditions();
        }
        if (Strings.isBlank(this.countSourceSql) && Strings.isNotBlank(this.methodSignature.getCountSqlTemplate())) {
            SimpleSqlParser countSqlParserHelper = new SimpleSqlParser(this.methodSignature.getCountSqlTemplate()).parse();
            this.countSourceSql = countSqlParserHelper.getSql();
            this.countConditions = countSqlParserHelper.getConditions();
        }
    }

    /**
     * 替换条件sql
     *
     * @param args
     * @return
     */
    private String replaceConditionSql(String sourceSql, Object[] args) {
        if (Strings.isBlank(sourceSql)) {
            return sourceSql;
        }
        String sql = sourceSql;
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
        String executeSql = replaceConditionSql(this.sourceSql, args);
        String countExecuteSql = replaceConditionSql(this.countSourceSql, args);
        if (log.isDebugEnabled()) {
            log.debug("执行SQL:{}", executeSql);
        }
        Execute execute = null;
        switch (this.methodSignature.getSqlCommandType()) {
            case SELECT:
                if (this.methodSignature.isPaginationQuery()) {
                    // 是分页查询
                    execute = new PaginationQueryExecute(dao, executeSql, countExecuteSql, this.methodSignature, args);
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
            case CALL_STORED_PROCEDURE:
                execute = new CallStoredProcedureExecute(dao, executeSql, this.methodSignature, args);
                break;
            default:
        }
        if (Objects.isNull(execute)) {
            throw new RuntimeException("不支持的方法");
        }
        return execute;
    }

}
