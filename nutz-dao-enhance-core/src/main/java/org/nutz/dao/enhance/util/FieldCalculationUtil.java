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

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/22
 */
public class FieldCalculationUtil {

    private static EnhanceCoreFactory enhanceCoreFactory;

    public static void setEnhanceCoreFactory(EnhanceCoreFactory enhanceCoreFactory) {
        FieldCalculationUtil.enhanceCoreFactory = enhanceCoreFactory;
    }

    /**
     * 按组进行字段计算
     *
     * @param t
     * @param <T>
     */
    public static <T> void fieldCalculation(T t) {
        fieldCalculation(t, FieldCalculationHolder.DEFAULT_GROUP);
    }

    /**
     * 按组进行字段计算
     *
     * @param t
     * @param group
     * @param <T>
     */
    public static <T> void fieldCalculation(T t, String group) {
        Map<String, List<FieldCalculationInfo>> listMap = FieldCalculationHolder.getOrCreate(t.getClass());
        if (Lang.isNotEmpty(listMap) && Strings.isNotBlank(group)) {
            List<FieldCalculationInfo> fieldCalculationInfos = listMap.get(group);
            for (FieldCalculationInfo fieldCalculationInfo : fieldCalculationInfos) {
                Context context = Lang.context();
                context.set("$me", t);
                if (Strings.isNotBlank(fieldCalculationInfo.getBeanName())) {
                    Object bean = enhanceCoreFactory.getBean(fieldCalculationInfo.getBeanName());
                    context.set(fieldCalculationInfo.getBeanName(), bean);
                }
                Object value = El.eval(context, fieldCalculationInfo.getExpression());
                Mirror.me(t).setValue(t, fieldCalculationInfo.getFieldName(), value);
            }
        }
    }
}
