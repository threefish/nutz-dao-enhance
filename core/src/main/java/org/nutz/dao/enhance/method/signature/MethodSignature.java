/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午11:19
 */

package org.nutz.dao.enhance.method.signature;

import lombok.Getter;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.annotation.Delete;
import org.nutz.dao.enhance.annotation.Insert;
import org.nutz.dao.enhance.annotation.Query;
import org.nutz.dao.enhance.annotation.Update;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.enhance.util.MethodSignatureUtil;
import org.nutz.dao.enhance.util.SqlCallbackUtil;
import org.nutz.dao.enhance.util.TypeParameterResolver;
import org.nutz.dao.enhance.util.ValueTypeUtil;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Strings;

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
     * 是否返回多条记录
     */
    private final boolean multipleRecords;
    /**
     * Cnd 参数位置，-1 代表没有
     */
    private final int conditionParameterInedx;
    /**
     * 方法名
     */
    private final String methodName;
    /**
     * 参数名列表
     */
    private final HashMap<Integer, String> parameterNames = new HashMap<>();

    /**
     * 无返回值的
     */
    private boolean returnsVoid;
    /**
     * 可选返回
     */
    private boolean returnsOptional;
    /**
     * 是否分页查询
     */
    private boolean paginationQuery;
    /**
     * 返回类型
     */
    private Class<?> returnType;
    /***
     * 是自定义sql
     */
    private boolean customizeSql;
    /**
     * sql模板语句
     */
    private String sqlTemplate;
    /**
     * 当前类或者当前方法标注的实体类
     */
    private Class<?> entityClass;
    /**
     * 返回类型的泛型
     */
    private Class<?> returnGenericType;
    /**
     * sql回调类
     */
    private SqlCallback sqlCallback;
    /**
     * 是否是更新语句
     */
    private SqlCommandType sqlCommandType;

    public MethodSignature(Class<?> mapperInterface, Method method) {
        // 代码顺序不能调整
        final String name = String.format("%s.%s", mapperInterface.getName(), MethodSignatureUtil.getMethodName(method));
        this.methodName = method.getName();
        // 获取条件参数位置
        this.conditionParameterInedx = MethodSignatureUtil.getConditionParameterInedx(method.getParameterTypes());
        // 类上直接获取实体类型
        this.entityClass = MethodSignatureUtil.getClassEntityType(mapperInterface);
        this.initParameterName(method);
        this.initCustomizeSql(name, method);
        this.initReturnType(mapperInterface, method);
        this.initPager(method, name);
        this.initEntityClass(method);
        this.initSqlCallback();
        // 是否是返回多条记录
        this.multipleRecords = ValueTypeUtil.isCollection(this.returnType);
    }

    /**
     * 方法上标了 @Entity 注解，优先使用 @Entity 注解
     *
     * @param method
     */
    private void initEntityClass(Method method) {
        final Class methodEntityType = MethodSignatureUtil.getMethodEntityType(method);
        if (Objects.nonNull(methodEntityType)) {
            this.entityClass = methodEntityType;
        }
    }

    /**
     * 设置分页信息
     *
     * @param method
     */
    private void initPager(Method method, String name) {
        this.paginationQuery = PageRecord.class.equals(this.returnType);
        if (this.paginationQuery) {
            if (!MethodSignatureUtil.firstParameterIsPaginationInfo(method)) {
                throw new RuntimeException(String.format("[%s]的返回值是分页类型，第一个参数必须是 Pager", name));
            }
        }
    }

    /**
     * 设置参数名
     *
     * @param method
     */
    private void initParameterName(Method method) {
        // 设置参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            parameterNames.put(i, MethodSignatureUtil.getParamName(parameters[i]));
        }
    }

    /**
     * 设置返回类型
     *
     * @param mapperInterface
     * @param method
     */
    private void initReturnType(Class<?> mapperInterface, Method method) {
        this.returnType = method.getReturnType();
        this.returnsOptional = Optional.class.equals(method.getReturnType());
        this.returnsVoid = void.class.equals(method.getReturnType());
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
        if (this.returnsOptional && !(resolvedReturnType instanceof ParameterizedType)) {
            throw new RuntimeException("是可选返回值，必须要有泛型");
        }
        if (this.returnsOptional) {
            final ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            // 返回类型就是第一个泛型
            final Type actualTypeFirst = MethodSignatureUtil.getActualTypeFirst(parameterizedType.getActualTypeArguments());
            this.setReturnTypeAndReturnGenericType(actualTypeFirst);
            return;
        }
        this.setReturnTypeAndReturnGenericType(resolvedReturnType);
    }

    /**
     * 通过泛型获取返回类型和返回类型泛型
     */
    private void setReturnTypeAndReturnGenericType(Type actualTypeFirst) {
        if (actualTypeFirst instanceof Class) {
            // 第一个泛型类是class，很好，直接设置即可
            this.returnType = (Class) actualTypeFirst;
        } else if (actualTypeFirst instanceof ParameterizedType) {
            // 第一个泛型类还是带泛型的，很好，直接设置原始类型
            final ParameterizedType actualTypeFirstParameterizedType = (ParameterizedType) actualTypeFirst;
            this.returnType = (Class<?>) actualTypeFirstParameterizedType.getRawType();
            if (ValueTypeUtil.isCollection(this.returnType)) {
                // 如果是集合，获取下泛型
                this.returnGenericType = MethodSignatureUtil.getActualTypeClassFirst(actualTypeFirstParameterizedType.getActualTypeArguments());
            }
        }
    }

    /**
     * 设置自定义sql
     *
     * @param name
     * @param method
     */
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
            if (Strings.isBlank(this.sqlTemplate)) {
                throw new RuntimeException(String.format("自定义sql不能为空", name));
            }
        }
    }


    /**
     * 设置sql回调类型
     */
    private void initSqlCallback() {
        if (this.returnType == this.entityClass) {
            // 是返回的单独实体类
            this.sqlCallback = Sqls.callback.entity();
            return;
        }
        this.sqlCallback = SqlCallbackUtil.getCommonSqlCallback(this.returnType);
        if (Objects.isNull(this.sqlCallback)) {
            // 设置集合类型的回调
            if (ValueTypeUtil.isCollection(this.returnType)) {
                this.setCollectionSqlCallback();
            } else if (ValueTypeUtil.isArray(this.returnType)) {
                throw new RuntimeException("不支持设置List数组类型的回调！");
            }
        }
        List<String> methodNames = Arrays.asList("getEntity", "getDao", "getEntityClass");
        if (this.returnType != void.class) {
            // 有返回值且方法名不是内部的
            if (!methodNames.contains(this.methodName)) {
                if (Objects.isNull(this.sqlCallback)) {
                    throw new RuntimeException(String.format("方法[%s]无法获取设置Callback!!!请发ISSUSE", this.methodName));
                }
            }
        }
    }

    /**
     * 设置集合类型的回调
     */
    private void setCollectionSqlCallback() {
        // 在返回集合的时候有几种情况，1 有泛型返回类型（具体在细分） 2无泛型返回类型（默认为实体类）
        if (Objects.nonNull(this.returnGenericType)) {
            // 有泛型返回类型
            if (this.entityClass == this.returnGenericType) {
                // 返回的是实体类
                this.sqlCallback = Sqls.callback.entities();
                return;
            }
            if (Map.class.isAssignableFrom(this.returnGenericType)) {
                // 返回的是map
                this.sqlCallback = Sqls.callback.maps();
                return;
            }
            final SqlCallback collectionSqlCallback = SqlCallbackUtil.getCollectionSqlCallback(this.returnGenericType);
            if (Objects.nonNull(collectionSqlCallback)) {
                return;
            }
            // collectionSqlCallback 还是null，那就是未识别的类型
            throw new RuntimeException("未识别的返回类型，请提交ISSUSE");
        }
        if (Objects.nonNull(this.entityClass)) {
            // 没有泛型，那就返回实体类型吧
            this.sqlCallback = Sqls.callback.entities();
            return;
        }
    }


}
