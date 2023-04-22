package org.nutz.dao.enhance.method.provider;

import lombok.AllArgsConstructor;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.method.signature.MethodSignature;
import org.nutz.dao.entity.Entity;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2021/6/14
 */
@AllArgsConstructor(staticName = "of")
public class ProviderContext<T> {

    public final EnhanceCoreFactory enhanceCoreFactory;

    public final Dao dao;

    public final MethodSignature methodSignature;

    public final String executeSql;

    /**
     * 参数
     */
    public final Object[] args;

    public final Class<T> entityClass;

    public final Entity entity;
    /**
     * 当前代理类
     */
    public final Object proxy;


}
