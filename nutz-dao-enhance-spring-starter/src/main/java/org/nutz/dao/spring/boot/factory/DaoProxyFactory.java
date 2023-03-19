package org.nutz.dao.spring.boot.factory;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.method.DaoMethodInvoker;
import org.nutz.dao.enhance.method.proxy.DaoProxy;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 */
@Data
@RequiredArgsConstructor
public class DaoProxyFactory<T> implements FactoryBean<T> {

    private final Map<Method, DaoMethodInvoker> methodCache = new ConcurrentHashMap<>();
    private final Class<T> mapperInterface;
    private final DaoFactory daoFactory;
    private final String dataSource;

    @SuppressWarnings("unchecked")
    protected T newInstance(DaoProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy
        );
    }

    public T newInstance(DaoFactory daoFactory) {
        return newInstance(new DaoProxy<>(daoFactory, dataSource, mapperInterface, methodCache));
    }


    @Override
    public T getObject() {
        return newInstance(this.daoFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
