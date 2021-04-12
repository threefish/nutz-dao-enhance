/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午11:19
 */

package org.nutz.spring.boot.dao.spring.binding.method;

import lombok.Getter;
import org.nutz.dao.Sqls;
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
    /***
     * 是自定义sql
     */
    private boolean customizeSql;
    /**
     * 是否返回多条记录
     */
    private final boolean multipleRecords;
    /**
     * sql模板语句
     */
    private String sqlTemplate;
    /**
     * 是否分页查询
     */
    private final boolean paginationQuery;
    /**
     * 参数名列表
     */
    private final HashMap<Integer, String> parameterNames = new HashMap<>();
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
    /**
     * Cnd 参数位置，-1 代表没有
     */
    private final int conditionParameterInedx;

    private final String methodName;

    public MethodSignature(Class<?> mapperInterface, Method method) {
        String name = String.format("%s.%s", mapperInterface.getName(), MethodSignatureUtil.getMethodName(method));
        this.methodName = method.getName();
        this.initCustomizeSql(name, method);
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
        if (resolvedReturnType instanceof Class<?>) {
            this.returnType = (Class<?>) resolvedReturnType;
        } else if (resolvedReturnType instanceof ParameterizedType) {
            this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
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
        // 先获取方法上的注解返回类型
        this.returnEntityClass = MethodSignatureUtil.getMethodEntityType(method);
        if (Objects.isNull(this.returnEntityClass)) {
            // 到类上获取泛型实体类型
            this.returnEntityClass = MethodSignatureUtil.getClassEntityType(mapperInterface);
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
        // 未找到
        if (this.returnType == this.returnEntityClass) {
            // 是返回的单独实体类
            this.sqlCallback = Sqls.callback.entity();
            return;
        }
        this.sqlCallback = SqlCallbackUtil.getSqlCallback(this.returnType);
        if (Objects.isNull(this.sqlCallback)) {
            if (ValueTypeUtil.isCollection(this.returnType) && Objects.nonNull(this.returnEntityClass)) {
                // 返回类型是list，且返回的是实体对象
                this.sqlCallback = Sqls.callback.entities();
                return;
            }
        }
        List<String> methodNames = Arrays.asList("getEntity", "getDao", "getEntityClass");
        if (this.returnType != void.class) {
            // 有返回值且方法名不是内部的
            if (!methodNames.contains(this.methodName)) {
                Assert.notNull(this.sqlCallback, String.format("方法[%s]不支持的返回类型[%s]", this.methodName, this.returnType));
            }
        }
    }


}
