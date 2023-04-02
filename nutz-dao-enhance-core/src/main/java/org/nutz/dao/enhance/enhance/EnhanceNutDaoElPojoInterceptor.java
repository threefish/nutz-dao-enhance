package org.nutz.dao.enhance.enhance;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.interceptor.impl.SimpleElPojoInterceptor;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/18
 */
public class EnhanceNutDaoElPojoInterceptor extends SimpleElPojoInterceptor {

    public EnhanceNutDaoElPojoInterceptor(MappingField mf, String str, String event, boolean nullEffective) {
        super(mf, str, event, nullEffective);
    }

    @Override
    public void onEvent(Object obj, Entity<?> en, String event, Object... args) {
        if (event.equals(this.event)) {
            if (this.nullEffective) {
                if (null != mf.getValue(obj)) {
                    return;
                }
            }
            Context context = Lang.context();
            context.set("field", mf.getColumnName());
            context.set("view", mf.getEntity());
            context.set("$me", obj);
            Object elVal = el.eval(context);
            if (elVal != null) {
                mf.setValue(obj, elVal);
            }
        }
    }

}
