package org.nutz.dao.enhance.registrar.factory;

import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.method.DaoMethodInvoker;
import org.nutz.dao.enhance.method.proxy.DaoProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@SuppressWarnings("all")
public class DaoProxyFactory {

    private static final Map<Method, DaoMethodInvoker> METHOD_CACHE = new ConcurrentHashMap<>();

    public static <T> T getObject(Class<T> mapperInterface, EnhanceCoreFactory enhanceCoreFactory, String dataSource) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                new DaoProxy<>(enhanceCoreFactory, dataSource, mapperInterface, METHOD_CACHE)
        );
    }

}
