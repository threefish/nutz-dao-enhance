package org.nutz.dao.enhance.method.holder;

import org.nutz.dao.enhance.annotation.FieldCalculation;
import org.nutz.dao.enhance.method.fieldcalculation.FieldCalculationInfo;
import org.nutz.dao.enhance.util.MethodSignatureUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/19
 */
public class FieldCalculationHolder {

    public static final String defaultGroup = "defaultGroup";

    private static final Map<Class<?>, Map<String, List<FieldCalculationInfo>>> CLASS_MAP_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    public static Map<String, List<FieldCalculationInfo>> getOrCreate(Class<?> clazz) {
        Map<String, List<FieldCalculationInfo>> stringListMap = CLASS_MAP_CONCURRENT_HASH_MAP.get(clazz);
        if (Objects.isNull(stringListMap)) {
            List<Field> declaredFields = MethodSignatureUtil.getAllFields(clazz);
            Map<String, List<FieldCalculationInfo>> groupsMap = new ConcurrentHashMap<>();
            for (Field declaredField : declaredFields) {
                FieldCalculation fieldCalculation = MethodSignatureUtil.getAnnotation(declaredField, FieldCalculation.class);
                if (Objects.nonNull(fieldCalculation)) {
                    List<String> groups = Arrays.asList(fieldCalculation.groups());
                    if (groups.size() > 0) {
                        for (String g : groups) {
                            groupsMap.computeIfAbsent(g, k -> new ArrayList<>()).add(FieldCalculationInfo.of(fieldCalculation.expression(), fieldCalculation.order(), g));
                        }
                    } else {
                        groupsMap.computeIfAbsent(defaultGroup, k -> new ArrayList<>()).add(FieldCalculationInfo.of(fieldCalculation.expression(), fieldCalculation.order(), defaultGroup));
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
        FieldCalculationHolder.add(clzz, defaultGroup, fieldCalculationInfo);
    }

}
