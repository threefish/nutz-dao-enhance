package org.nutz.spring.boot.dao.spring.binding;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.nutz.spring.boot.dao.factory.DaoFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Data
@RequiredArgsConstructor
public class MapperProxyFactory<T> implements FactoryBean<T> {

    private final Map<Method, MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();
    private final Class<T> mapperInterface;
    private final DaoFactory daoFactory;

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy
        );
    }

    public T newInstance(DaoFactory daoFactory) {
        return newInstance(new MapperProxy<>(daoFactory, mapperInterface, methodCache));
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
