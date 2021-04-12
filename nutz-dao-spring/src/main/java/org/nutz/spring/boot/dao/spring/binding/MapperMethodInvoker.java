package org.nutz.spring.boot.dao.spring.binding;

import org.nutz.spring.boot.dao.factory.DaoFactory;

import java.lang.reflect.Method;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
interface MapperMethodInvoker {
    /**
     * 执行
     *
     * @param proxy
     * @param method
     * @param args
     * @param dataSource
     * @return
     * @throws Throwable
     */
    Object invoke(Object proxy, Method method, Object[] args, String dataSource) throws Throwable;

}
