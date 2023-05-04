package org.nutz.dao.enhance.util;

import lombok.SneakyThrows;
import org.nutz.dao.util.lambda.PFun;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/4/21
 */
public class LambdaQueryUtil {

    /**
     * 字段映射
     */
    private static final Map<String, Class> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();


    /**
     * @param <T>    类型，被调用的 Function 对象的目标类型
     * @param lambda 需要解析的 lambda 对象
     * @return 返回解析后的字段名称
     */
    public static <T> Class resolve(PFun<T, ?> lambda) {
        Class<?> clazz = lambda.getClass();
        String className = clazz.getName();
        return Optional.ofNullable(COLUMN_CACHE_MAP.get(className)).orElseGet(() -> getOriginLambdaClass(className, lambda));
    }


    /**
     * 获取字段名称
     *
     * @param className
     * @param lambda
     * @param <T>
     * @return
     */
    private static <T> Class getOriginLambdaClass(String className, PFun<T, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
            return COLUMN_CACHE_MAP.computeIfAbsent(className, s -> getClassByClassName(serializedLambda.getImplClass()));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("不可能发生的异常！！！");
        }
    }

    /**
     * 获取lambda表单是原始类
     *
     * @return
     */
    @SneakyThrows
    @SuppressWarnings("all")
    public static Class getClassByClassName(String className) {
        return Class.forName(className.replaceAll("/", "."));
    }
}
