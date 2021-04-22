/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午11:19
 */

package org.nutz.spring.boot.dao.spring.binding.method;

import lombok.Getter;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.spring.boot.dao.annotation.Delete;
import org.nutz.spring.boot.dao.annotation.Insert;
import org.nutz.spring.boot.dao.annotation.Query;
import org.nutz.spring.boot.dao.annotation.Update;
import org.nutz.spring.boot.dao.pagination.PageData;
import org.nutz.spring.boot.dao.reflection.TypeParameterResolver;
import org.nutz.spring.boot.dao.util.MethodSignatureUtil;
import org.nutz.spring.boot.dao.util.SqlCallbackUtil;
import org.nutz.spring.boot.dao.util.ValueTypeUtil;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/12/12
 */
@Getter
public class MethodSignature {
    /**
     * 无返回值的
     */
    private final boolean returnsVoid;
    /**
     * 可选返回
     */
    private final boolean returnsOptional;
    /**
     * 返回类型
     */
    private final Class<?> returnType;
    /**
     * 是否返回多条记录
     */
    private final boolean multipleRecords;
    /**
     * 是否分页查询
     */
    private final boolean paginationQuery;
    /**
     * 参数名列表
     */
    private final HashMap<Integer, String> parameterNames = new HashMap<>();
    /**
     * Cnd 参数位置，-1 代表没有
     */
    private final int conditionParameterInedx;
    private final String methodName;
    /**
     * 返回类型的泛型
     */
    private Class<?> returnGenericType;
    /***
     * 是自定义sql
     */
    private boolean customizeSql;
    /**
     * sql模板语句
     */
    private String sqlTemplate;
    /**
     * 返回实体类
     */
    private Class<?> returnEntityClass;
    /**
     * sql回调类
     */
    private SqlCallback sqlCallback;
    /**
     * 是否是更新语句
     */
    private SqlCommandType sqlCommandType;

    public MethodSignature(Class<?> mapperInterface, Method method) {
        String name = String.format("%s.%s", mapperInterface.getName(), MethodSignatureUtil.getMethodName(method));
        this.methodName = method.getName();
        this.initCustomizeSql(name, method);
        // 类上直接获取实体类型
        this.returnEntityClass = MethodSignatureUtil.getClassEntityType(mapperInterface);
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
        if (resolvedReturnType instanceof Class<?>) {
            this.returnType = (Class<?>) resolvedReturnType;
        } else if (resolvedReturnType instanceof ParameterizedType) {
            // 是泛型
            final ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            this.returnType = (Class<?>) parameterizedType.getRawType();
            if (ValueTypeUtil.isCollection(this.returnType)) {
                // 如果是集合，获取下泛型
                this.returnGenericType = MethodSignatureUtil.getActualTypeClass(parameterizedType.getActualTypeArguments());
            }
        } else {
            this.returnType = method.getReturnType();
        }
        this.returnsVoid = void.class.equals(this.returnType);
        this.returnsOptional = Optional.class.equals(this.returnType);
        this.paginationQuery = PageData.class.equals(this.returnType);
        if (this.paginationQuery) {
            Assert.isTrue(MethodSignatureUtil.firstParameterIsPaginationInfo(method), String.format("[%s]的返回值是分页类型，第一个参数必须是 Pager", name));
        }
        // 设置参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            parameterNames.put(i, parameters[i].getName());
        }
        // 方法上标了 @Entity 注解，优先使用
        final Class methodEntityType = MethodSignatureUtil.getMethodEntityType(method);
        if (Objects.nonNull(methodEntityType)) {
            this.returnEntityClass = methodEntityType;
        }
        this.initSqlCallback();
        // 是否是返回多条记录
        this.multipleRecords = ValueTypeUtil.isCollection(this.returnType);
        this.conditionParameterInedx = MethodSignatureUtil.getConditionParameterInedx(method.getParameterTypes());
    }

    private void initCustomizeSql(String name, Method method) {
        this.customizeSql = MethodSignatureUtil.isNeedSqlAnnotation(method);
        if (this.customizeSql) {
            this.sqlCommandType = SqlCommandType.SELECT;
            // 需要Sql注解，则必须要获取自定义sql
            Query querySql = method.getAnnotation(Query.class);
            Update updateSql = method.getAnnotation(Update.class);
            Insert insertSql = method.getAnnotation(Insert.class);
            Delete delectSql = method.getAnnotation(Delete.class);
            if (Objects.nonNull(querySql)) {
                this.sqlTemplate = querySql.value();
            } else if (Objects.nonNull(updateSql)) {
                this.sqlTemplate = updateSql.value();
                this.sqlCommandType = SqlCommandType.UPDATE;
            } else if (Objects.nonNull(insertSql)) {
                this.sqlTemplate = insertSql.value();
                this.sqlCommandType = SqlCommandType.INSERT;
            } else if (Objects.nonNull(delectSql)) {
                this.sqlTemplate = delectSql.value();
                this.sqlCommandType = SqlCommandType.DELETE;
            } else {
                throw new RuntimeException(String.format("[%s] 缺失 QuerySql、UpdateSql、InsertSql 等任意注解", name));
            }
            Assert.hasText(this.sqlTemplate, String.format("自定义sql不能为空", name));
        }
    }


    /**
     * 设置sql回调类型
     */
    private void initSqlCallback() {
        if (this.returnType == this.returnEntityClass) {
            // 是返回的单独实体类
            this.sqlCallback = Sqls.callback.entity();
            return;
        }
        this.sqlCallback = SqlCallbackUtil.getSqlCallback(this.returnType);
        if (Objects.isNull(this.sqlCallback)) {
            if (ValueTypeUtil.isCollection(this.returnType)) {
                // 在返回集合的时候有几种情况，1 有泛型返回类型（具体在细分） 2无泛型返回类型（默认为实体类）
                if (Objects.nonNull(this.returnGenericType)) {
                    // 有泛型返回类型
                    if (this.returnEntityClass == this.returnGenericType) {
                        // 返回的是实体类
                        this.sqlCallback = Sqls.callback.entities();
                        return;
                    }else if (Record.class == this.returnGenericType) {
                        // 返回的是Records
                        this.sqlCallback = Sqls.callback.records();
                        return;
                    } else if (Map.class.isAssignableFrom(this.returnGenericType)) {
                        // 返回的是map
                        this.sqlCallback = Sqls.callback.maps();
                        return;
                    } else if (String.class == this.returnGenericType) {
                        // 返回的是String
                        this.sqlCallback = Sqls.callback.strList();
                        return;
                    } else if (String[].class == this.returnGenericType) {
                        // 返回的是String
                        this.sqlCallback = Sqls.callback.strs();
                        return;
                    } else if (Integer.class == this.returnGenericType) {
                        // 返回的是Integer
                        this.sqlCallback = Sqls.callback.ints();
                        return;
                    } else if (Long.class == this.returnGenericType) {
                        // 返回的是Integer
                        this.sqlCallback = Sqls.callback.longs();
                        return;
                    } else if (Boolean.class == this.returnGenericType) {
                        // 返回的是Integer
                        this.sqlCallback = Sqls.callback.bools();
                        return;
                    }
                    throw new RuntimeException("未识别的返回类型");
                }
                if (Objects.nonNull(this.returnEntityClass)) {
                    // 没有泛型，那就返回实体类型吧
                    this.sqlCallback = Sqls.callback.entities();
                    return;
                }
            }
        }
        List<String> methodNames = Arrays.asList("getEntity", "getDao", "getEntityClass");
        if (this.returnType != void.class) {
            // 有返回值且方法名不是内部的
            if (!methodNames.contains(this.methodName)) {
                Assert.notNull(this.sqlCallback, String.format("方法[%s]无法获取设置Callback!!!请发ISSUSE", this.methodName));
            }
        }
    }


}
