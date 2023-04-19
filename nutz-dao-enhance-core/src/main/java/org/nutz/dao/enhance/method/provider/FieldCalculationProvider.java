package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.enhance.method.fieldcalculation.FieldCalculationInfo;
import org.nutz.dao.enhance.method.holder.FieldCalculationHolder;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/22
 */
@SuppressWarnings("all")
public class FieldCalculationProvider {

    /**
     * 按组进行字段计算
     *
     * @param providerContext
     * @param t
     * @param group
     * @param <T>
     */
    public static <T> void fieldCalculation(ProviderContext providerContext, T t, String group) {
        Map<String, List<FieldCalculationInfo>> listMap = FieldCalculationHolder.getOrCreate(t.getClass());
        if (Lang.isNotEmpty(listMap) && Strings.isNotBlank(group)) {
            List<FieldCalculationInfo> fieldCalculationInfos = listMap.get(group);
            for (FieldCalculationInfo fieldCalculationInfo : fieldCalculationInfos) {
                System.out.println("执行" + fieldCalculationInfo.getOrder() + fieldCalculationInfo.getExpression());
            }
        }
    }

    /**
     * 按组进行字段计算
     *
     * @param providerContext
     * @param collection
     * @param group
     * @param <T>
     */
    public static <T> void fieldCalculation(ProviderContext providerContext, Collection<T> collection, String group) {
        if (collection != null && !collection.isEmpty()) {
            collection.forEach(row -> {
                FieldCalculationProvider.fieldCalculation(providerContext, row, group);
            });
        }
    }

    /**
     * 按组进行字段计算
     *
     * @param providerContext
     * @param t
     * @param <T>
     */
    public static <T> void fieldCalculation(ProviderContext providerContext, T t) {
        FieldCalculationProvider.fieldCalculation(providerContext, t, FieldCalculationHolder.defaultGroup);
    }

    /**
     * 字段计算
     *
     * @param providerContext
     * @param collection
     * @param <T>
     */
    public static <T> void fieldCalculation(ProviderContext providerContext, Collection<T> collection) {
        FieldCalculationProvider.fieldCalculation(providerContext, collection, FieldCalculationHolder.defaultGroup);
    }

}
