package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.enhance.method.holder.FieldCalculationHolder;
import org.nutz.dao.enhance.util.FieldCalculationUtil;

import java.util.Collection;

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
        FieldCalculationUtil.fieldCalculation(t, group);
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
        FieldCalculationProvider.fieldCalculation(providerContext, t, FieldCalculationHolder.DEFAULT_GROUP);
    }

    /**
     * 字段计算
     *
     * @param providerContext
     * @param collection
     * @param <T>
     */
    public static <T> void fieldCalculation(ProviderContext providerContext, Collection<T> collection) {
        FieldCalculationProvider.fieldCalculation(providerContext, collection, FieldCalculationHolder.DEFAULT_GROUP);
    }

}
