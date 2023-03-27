/*
 *  Copyright © 2020 - 2020 黄川 Rights Reserved.
 *  版权声明：黄川保留所有权利。
 *  免责声明：本规范是初步的，随时可能更改，恕不另行通知。黄川对此处包含的任何错误不承担任何责任。
 *  最后修改时间：2020/12/12 下午11:19
 */

package org.nutz.dao.enhance.method.signature;

import lombok.Getter;
import org.nutz.dao.Sqls;
import org.nutz.dao.enhance.annotation.*;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.enhance.util.*;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.lang.annotation.Annotation;
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
     * 存储过程出参列表参数名列表
     */
    private final List<OutParam> storedProcedureOutParameters = new ArrayList<>();
    /**
     * 自定义提供者
     */
    private final boolean customProvider;
    /**
     * 是存储过程
     */
    private boolean storedProcedure;
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
    /***
     * 是循环
     */
    private boolean loop;
    /**
     * 循环参数字段
     */
    private String loopForField;
    /**
     * 插入时返回自增ID
     */
    private boolean returnGeneratedKeys;
    /**
     * sql模板语句
     */
    private String sqlTemplate;
    /**
     * sql模板语句
     */
    private String countSqlTemplate;
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
    /**
     * 自定义提供者类型
     */
    private Class<?> customProviderType;
    /**
     * 自定义提供者执行方法
     */
    private Method customProviderMethod;


    public MethodSignature(Class<?> mapperInterface, Method method) {
        // 代码顺序不能调整
        final String name = String.format("%s.%s", mapperInterface.getName(), MethodSignatureUtil.getMethodName(method));
        this.methodName = method.getName();
        // 获取条件参数位置
        this.conditionParameterInedx = MethodSignatureUtil.getConditionParameterInedx(method.getParameterTypes());
        // 类上直接获取实体类型
        this.entityClass = MethodSignatureUtil.getClassEntityType(mapperInterface);
        this.initCallFunction(method);
        this.initParameterName(method);
        this.initCustomizeSql(name, method);
        this.initReturnType(mapperInterface, method);
        // 是否是返回多条记录
        this.multipleRecords = ValueTypeUtil.isCollection(this.returnType);
        final CustomProvider customProvider = method.getAnnotation(CustomProvider.class);
        this.customProvider = Objects.nonNull(customProvider);
        if (this.customProvider) {
            // 自定义提供者
            this.initCustomProvider(customProvider, method);
        } else {
            this.initPager(method, name);
            this.initEntityClass(method);
            this.initSqlCallback();
        }
    }

    private void initCallFunction(Method method) {
        CallStoredProcedure annotation = method.getAnnotation(CallStoredProcedure.class);
        this.storedProcedure = Objects.nonNull(annotation);
        if (this.storedProcedure) {
            CallStoredProcedure.Out[] outs = annotation.out();
            if (Lang.isNotEmpty(outs)) {
                for (CallStoredProcedure.Out out : outs) {
                    this.storedProcedureOutParameters.add(OutParam.of(out.name(), out.jdbcType()));
                }
            }
        }
    }

    /**
     * 初始化自定义提供者信息
     *
     * @param customProvider
     */
    private void initCustomProvider(CustomProvider customProvider, Method method) {
        String methodName = customProvider.methodName();
        if (Strings.isBlank(customProvider.methodName())) {
            methodName = method.getName();
        }
        this.customProviderType = customProvider.type();
        this.customProviderMethod = MethodSignatureUtil.getCustomProviderTypeMethod(method, customProvider.type(), methodName, this.returnType);
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
     * 设置自定义sql相关信息
     *
     * @param name
     * @param method
     */
    private void initCustomizeSql(String name, Method method) {
        this.customizeSql = MethodSignatureUtil.isNeedSqlAnnotation(method);
        if (this.customizeSql) {
            // 需要Sql注解，则必须要获取自定义sql
            Annotation[] annotations = method.getAnnotations();
            NutMap attrMap = new NutMap();
            for (Annotation annotation : annotations) {
                attrMap = AnnotationUtil.getAttrMap(annotation);
                if (Query.class.isAssignableFrom(annotation.getClass())) {
                    this.sqlCommandType = SqlCommandType.SELECT;
                    break;
                } else if (Update.class.isAssignableFrom(annotation.getClass())) {
                    this.sqlCommandType = SqlCommandType.UPDATE;
                    break;
                } else if (Insert.class.isAssignableFrom(annotation.getClass())) {
                    this.sqlCommandType = SqlCommandType.INSERT;
                    break;
                } else if (Delete.class.isAssignableFrom(annotation.getClass())) {
                    this.sqlCommandType = SqlCommandType.DELETE;
                    break;
                } else if (CallStoredProcedure.class.isAssignableFrom(annotation.getClass())) {
                    this.sqlCommandType = SqlCommandType.CALL_STORED_PROCEDURE;
                    break;
                } else {
                    throw new RuntimeException(String.format("[%s] 缺失 QuerySql、UpdateSql、InsertSql、CallStoredProcedure 等任意注解", name));
                }
            }
            this.sqlTemplate = attrMap.getString("value");
            this.countSqlTemplate = attrMap.getString("countSql");
            this.loopForField = attrMap.getString("loopFor");
            this.returnGeneratedKeys = attrMap.getBoolean("returnGeneratedKeys");
            if (Strings.isNotBlank(this.loopForField)) {
                this.loop = true;
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
        if (this.returnType == void.class || this.storedProcedure) {
            // 是存储过程，存储过程是自定义实现的，无需设置callback
            return;
        }
        if (this.returnType == this.entityClass) {
            // 是返回的单独实体类
            this.sqlCallback = Sqls.callback.entity();
            return;
        }

        if (Collection.class.isAssignableFrom(this.returnType)) {
            // 在返回集合的时候有几种情况，1 有泛型返回类型（具体在细分） 2无泛型返回类型（默认为实体类）
            if (Objects.nonNull(this.returnGenericType)) {
                // 有泛型返回类型
                if (this.entityClass == this.returnGenericType) {
                    // 返回的是实体类
                    this.sqlCallback = Sqls.callback.entities();
                    return;
                }
            }
        }
        this.sqlCallback = SqlCallbackMetaInfo.getCommonSqlCallback(this.returnType);
        if (Objects.isNull(this.sqlCallback)) {
            throw new RuntimeException(String.format("方法[%s]无法获取设置Callback!!!请发ISSUSE", this.methodName));
        }
    }

}
