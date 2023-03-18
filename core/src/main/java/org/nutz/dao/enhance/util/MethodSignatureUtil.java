package org.nutz.dao.enhance.util;

import org.nutz.dao.Condition;
import org.nutz.dao.enhance.annotation.Entity;
import org.nutz.dao.enhance.annotation.Param;
import org.nutz.dao.enhance.dao.BaseDao;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Lang;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 黄川 2020/12/16
 */
public class MethodSignatureUtil {
    /**
     * 内置方法
     */
    public static final List<String> BUILT_IN_METHOD = new ArrayList<String>() {
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
     * 获取参数名
     */
    public static String getParamName(Parameter parameter) {
        Param param = parameter.getAnnotation(Param.class);
        return Objects.nonNull(param) ? param.value() : parameter.getName();
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
    public static Class<?> getClassEntityType(Class<?> clazz) {
        Class cl = null;
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (Lang.isNotEmpty(genericInterfaces)) {
            Type genericInterface = genericInterfaces[0];
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                cl = getActualTypeClassFirst(parameterizedType.getActualTypeArguments());
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
    public static Type getActualTypeFirst(Type[] actualTypeArguments) {
        if (Lang.isNotEmpty(actualTypeArguments)) {
            Type actualTypeArgument = actualTypeArguments[0];
            return actualTypeArgument;
        }
        return null;
    }

    /**
     * 如果泛型是class就直接返回否则返回null
     *
     * @param actualType
     * @return
     */
    public static Class getActualTypeClass(Type actualType) {
        if (Objects.nonNull(actualType) && actualType instanceof Class) {
            return (Class) actualType;
        }
        return null;
    }

    /**
     * 如果泛型是class就直接返回否则返回null
     *
     * @param actualTypeArguments
     * @return
     */
    public static Class getActualTypeClassFirst(Type[] actualTypeArguments) {
        return getActualTypeClass(getActualTypeFirst(actualTypeArguments));
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
     *
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

    /**
     * 获取自定义提供者的方法
     *
     * @param providerType
     * @param methodName
     * @param returnType
     * @return
     */
    public static Method getCustomProviderTypeMethod(Method orginMethod, Class<?> providerType, String methodName, Class<?> returnType) {
        List<Method> sameNameMethods = Arrays.stream(providerType.getMethods()).filter(m -> m.getName().equals(methodName)).collect(Collectors.toList());
        if (sameNameMethods.isEmpty()) {
            throw new RuntimeException("Cannot resolve the provider method because '" + methodName + "' not found in CustomProvider '" + providerType.getName() + "'.");
        }
        List<Method> targetMethods = sameNameMethods.stream()
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(porxyMethod -> {
                    boolean returnTypeEq = orginMethod.getReturnType().isAssignableFrom(porxyMethod.getReturnType());
                    Class<?>[] orginMethodParameterTypes = orginMethod.getParameterTypes();
                    Class<?>[] porxyMethodParameterTypes = porxyMethod.getParameterTypes();
                    if (returnTypeEq
                            && porxyMethodParameterTypes.length > 0
                            && orginMethodParameterTypes.length == porxyMethodParameterTypes.length - 1
                            && porxyMethodParameterTypes[0].isAssignableFrom(ProviderContext.class)
                    ) {
                        for (int i = 1; i < porxyMethodParameterTypes.length; i++) {
                            Class<?> porxyMethodParameterType = porxyMethodParameterTypes[i];
                            if (!porxyMethodParameterType.isAssignableFrom(orginMethodParameterTypes[i - 1])) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        if (targetMethods.size() == 1) {
            return targetMethods.get(0);
        }
        if (targetMethods.isEmpty()) {
            throw new RuntimeException("Cannot resolve the provider method because '"
                    + methodName + "' does not return the CharSequence or its subclass in CustomProvider '"
                    + providerType.getName() + "'.");
        } else {
            throw new RuntimeException("Cannot resolve the provider method because '"
                    + methodName + "' is found multiple in CustomProvider '" + providerType.getName() + "'.");
        }
    }

    /**
     * 获取全部字段
     *
     * @param entityClass
     * @return
     */
    public static List<Field> getAllFields(Class<?> entityClass) {
        Class cls = entityClass;
        List<Field> fieldList = new ArrayList<>();
        while (cls != null && cls != Object.class) {
            Field[] fields = cls.getDeclaredFields();
            for (Field fd : fields) {
                fieldList.add(fd);
                if (!fd.isAccessible()) {
                    fd.setAccessible(true);
                }
            }
            cls = cls.getSuperclass();
        }
        return fieldList;
    }

}
