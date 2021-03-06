package org.nutz.dao.enhance.registrar.factory;

import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.method.DaoMethodInvoker;
import org.nutz.dao.enhance.method.proxy.DaoProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class DaoProxyFactory {

    private static final Map<Method, DaoMethodInvoker> METHOD_CACHE = new ConcurrentHashMap<>();

    public static <T> T getObject(Class<T> mapperInterface, DaoFactory daoFactory, String dataSource) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                new DaoProxy<>(daoFactory, dataSource, mapperInterface, METHOD_CACHE)
        );
    }

}
