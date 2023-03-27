package org.nutz.dao.enhance.util;

import lombok.SneakyThrows;
import org.nutz.lang.util.NutMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2023/3/24
 */
public class AnnotationUtil {

    private static final List<String> EXCLUDE_METHODS = Arrays.asList("equals", "hashCode", "clone", "toString", "getClass");

    @SneakyThrows
    public static NutMap getAttrMap(Annotation annotation) {
        NutMap attr = new NutMap();
        if (annotation == null) {
            return attr;
        }

        Method[] declaredMethods = annotation.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (!EXCLUDE_METHODS.contains(declaredMethod.getName())) {
                attr.put(declaredMethod.getName(), declaredMethod.invoke(annotation));
            }
        }
        return attr;
    }
}
