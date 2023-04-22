package org.nutz.dao.enhance.method.holder;

import org.nutz.dao.enhance.annotation.FieldCalculation;
import org.nutz.dao.enhance.method.fieldcalculation.FieldCalculationInfo;
import org.nutz.dao.enhance.util.AssertUtil;
import org.nutz.dao.enhance.util.MethodSignatureUtil;
import org.nutz.dao.enhance.util.PatternUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/19
 */
public class FieldCalculationHolder {

    public static final String DEFAULT_GROUP = "defaultGroup";

    private static final Map<Class<?>, Map<String, List<FieldCalculationInfo>>> CLASS_MAP_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    public static Map<String, List<FieldCalculationInfo>> getOrCreate(Class<?> clazz) {
        Map<String, List<FieldCalculationInfo>> stringListMap = CLASS_MAP_CONCURRENT_HASH_MAP.get(clazz);
        if (Objects.isNull(stringListMap)) {
            List<Field> declaredFields = MethodSignatureUtil.getAllFields(clazz);
            Map<String, List<FieldCalculationInfo>> groupsMap = new ConcurrentHashMap<>();
            for (Field declaredField : declaredFields) {
                FieldCalculation fieldCalculation = MethodSignatureUtil.getAnnotation(declaredField, FieldCalculation.class);
                if (Objects.nonNull(fieldCalculation)) {
                    String beanName = PatternUtil.findBeanNameByExpression(fieldCalculation.expression());
                    String expression = fieldCalculation.expression();
                    AssertUtil.notBlank(expression);
                    expression = expression.replaceAll("\\$ioc:", "");
                    List<String> groups = Arrays.asList(fieldCalculation.groups());
                    if (groups.size() > 0) {
                        for (String group : groups) {
                            groupsMap.computeIfAbsent(group, k -> new ArrayList<>()).add(FieldCalculationInfo.of(declaredField.getName(), beanName, expression, fieldCalculation.order(), group));
                        }
                    } else {
                        groupsMap.computeIfAbsent(DEFAULT_GROUP, k -> new ArrayList<>()).add(FieldCalculationInfo.of(declaredField.getName(), beanName, expression, fieldCalculation.order(), DEFAULT_GROUP));
                    }
                }
            }
            // 排序
            groupsMap.values().forEach(list -> Collections.sort(list, Comparator.comparingInt(FieldCalculationInfo::getOrder)));
            CLASS_MAP_CONCURRENT_HASH_MAP.put(clazz, groupsMap);
        }
        return CLASS_MAP_CONCURRENT_HASH_MAP.get(clazz);
    }

    private static void add(Class<?> clzz, String g, FieldCalculationInfo fieldCalculationInfo) {
        Map<String, List<FieldCalculationInfo>> stringListMap = CLASS_MAP_CONCURRENT_HASH_MAP.computeIfAbsent(clzz, k -> new ConcurrentHashMap<>(6));
        stringListMap.computeIfAbsent(g, k -> new ArrayList<>()).add(fieldCalculationInfo);
    }

    private static void add(Class<?> clzz, FieldCalculationInfo fieldCalculationInfo) {
        FieldCalculationHolder.add(clzz, DEFAULT_GROUP, fieldCalculationInfo);
    }

}
