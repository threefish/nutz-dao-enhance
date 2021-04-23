package org.nutz.spring.boot.dao.util;

import org.nutz.dao.Condition;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Lang;
import org.nutz.spring.boot.dao.annotation.Entity;
import org.nutz.spring.boot.dao.execute.BaseDao;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author 黄川 2020/12/16
 */
public class MethodSignatureUtil {
    /**
     * 内置方法无需获取@Sql
     */
    public final static List<String> BUILT_IN_METHOD = new ArrayList<String>() {
        {
            this.addValueBygetDeclaredMethodVal(BaseDao.class);
        }

        /**
         * 根据传入的类，获取全部方法并添加到List中
         *
         * @param clazz
         */
        private void addValueBygetDeclaredMethodVal(Class<?> clazz) {
            Method[] methods = clazz.getMethods();
            Arrays.stream(methods).forEach(method -> this.add(getMethodName(method)));
        }
    };
    /**
     * 内置的crud baseMapper
     */
    private static final List<Class<?>> BASE_MAPPER_CLASS = Arrays.asList(
            BaseDao.class
    );

    /**
     * 获取方法名
     */
    public static String getMethodName(Method method) {
        String name = method.getName();
        Parameter[] parameters = method.getParameters();
        StringJoiner joiner = new StringJoiner(",");
        for (Parameter parameter : parameters) {
            joiner.add(parameter.getType().toString());
        }
        return String.format("%s(%s)", name, joiner);
    }

    /**
     * 是否是继承了baseMapper
     *
     * @param method
     * @return
     */
    public static boolean isExtendsBaseMapper(Method method) {
        // 获取父类
        Class<?> declaringClass = method.getDeclaringClass();
        if (Objects.isNull(declaringClass)) {
            return false;
        }
        return BASE_MAPPER_CLASS.contains(method.getDeclaringClass());
    }

    /**
     * 是否需要获取sql注解
     *
     * @param method
     * @return
     */
    public static boolean isNeedSqlAnnotation(Method method) {
        // 继承了 baseMapper
        if (isExtendsBaseMapper(method)) {
            // 是否是内置方法，内置方法无需读取sql注解
            return !BUILT_IN_METHOD.contains(getMethodName(method));
        }
        // 没有继承baseMapper 必须要获取sql注解
        return true;
    }

    /**
     * 第一个参数应该是否是分页信息
     *
     * @param method
     * @return
     */
    public static boolean firstParameterIsPaginationInfo(Method method) {
        Parameter[] parameters = method.getParameters();
        if (Lang.isEmpty(parameters)) {
            return false;
        }
        return Pager.class.equals(parameters[0].getType());
    }


    /**
     * 获取实体class
     *
     * @param clazz
     * @return
     */
    public static Class getClassEntityType(Class<?> clazz) {
        Class cl = null;
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (Lang.isNotEmpty(genericInterfaces)) {
            Type genericInterface = genericInterfaces[0];
            if (genericInterface instanceof ParameterizedTypeImpl) {
                ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericInterface;
                cl = getActualTypeClass(parameterizedType.getActualTypeArguments());
            }
        }
        if (Objects.isNull(cl)) {
            Entity annotation = clazz.getAnnotation(Entity.class);
            if (Objects.nonNull(annotation)) {
                cl = annotation.value();
            }
        }
        return cl;
    }

    /**
     * 获取第一个泛型类
     *
     * @param actualTypeArguments
     * @return
     */
    public static Class getActualTypeClass(Type[] actualTypeArguments) {
        if (Lang.isNotEmpty(actualTypeArguments)) {
            Type actualTypeArgument = actualTypeArguments[0];
            return (Class) actualTypeArgument;
        }
        return null;
    }

    /**
     * 获取实体class
     *
     * @param method
     * @return
     */
    public static Class getMethodEntityType(Method method) {
        Entity annotation = method.getAnnotation(Entity.class);
        if (Objects.nonNull(annotation)) {
            return annotation.value();
        }
        return null;
    }

    /**
     * 获取 Condition 条件参数
     * @param parameterTypes
     * @return
     */
    public static int getConditionParameterInedx(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (Condition.class.equals(parameterType)) {
                return i;
            }
        }
        return -1;
    }
}
