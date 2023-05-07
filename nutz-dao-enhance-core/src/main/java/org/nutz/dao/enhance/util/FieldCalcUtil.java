package org.nutz.dao.enhance.util;

import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.method.fieldcalculation.FieldCalculationInfo;
import org.nutz.dao.enhance.method.holder.FieldCalculationHolder;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/22
 */
public class FieldCalcUtil {

    private static EnhanceCoreFactory enhanceCoreFactory;

    public static void setEnhanceCoreFactory(EnhanceCoreFactory enhanceCoreFactory) {
        FieldCalcUtil.enhanceCoreFactory = enhanceCoreFactory;
    }

    /**
     * 按组进行字段计算
     *
     * @param t
     * @param <T>
     */
    public static <T> void calc(T t) {
        calc(t, FieldCalculationHolder.DEFAULT_GROUP);
    }

    /**
     * 按组进行字段计算
     *
     * @param t
     * @param group
     * @param <T>
     */
    public static <T> void calc(T t, String group) {
        if (t != null) {
            Class<?> clazz = t.getClass();
            if (Iterable.class.isAssignableFrom(clazz)) {
                ((Iterable) t).forEach(c -> calculation(c, group));
            } else {
                calculation(t, group);
            }
        }
    }

    /**
     * 计算
     *
     * @param t
     * @param group
     */
    public static void calculation(Object t, String group) {
        if (t != null) {
            Map<String, List<FieldCalculationInfo>> listMap = FieldCalculationHolder.getOrCreate(t.getClass());
            if (Lang.isNotEmpty(listMap) && Strings.isNotBlank(group)) {
                List<FieldCalculationInfo> fieldCalculationInfos = listMap.get(group);
                for (FieldCalculationInfo fieldCalculationInfo : fieldCalculationInfos) {
                    Context context = Lang.context();
                    context.set("$this", t);
                    if (Strings.isNotBlank(fieldCalculationInfo.getBeanName())) {
                        Object bean = enhanceCoreFactory.getBean(fieldCalculationInfo.getBeanName());
                        context.set(fieldCalculationInfo.getBeanName(), bean);
                    }
                    String conditionExpression = fieldCalculationInfo.getConditionExpression();
                    boolean matchCondition = true;
                    if (Strings.isNotBlank(conditionExpression)) {
                        Object eval = El.eval(context, conditionExpression);
                        matchCondition = "true".equals(String.valueOf(eval));
                    }
                    if (matchCondition) {
                        Object value = El.eval(context, fieldCalculationInfo.getExpression());
                        Object realValue = value;
                        if (value instanceof Optional && fieldCalculationInfo.isIgnoreOptionalWrapper()) {
                            // 需要去除optional包裹
                            Optional optional = ((Optional<?>) value);
                            realValue = optional.isPresent() ? optional.get() : null;
                        }
                        if (realValue != null) {
                            Mirror.me(t).setValue(t, fieldCalculationInfo.getFieldName(), realValue);
                        }
                    }
                }
            }
        }

    }
}
