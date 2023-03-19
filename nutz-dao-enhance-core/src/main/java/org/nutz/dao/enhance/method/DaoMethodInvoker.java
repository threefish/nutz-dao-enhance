package org.nutz.dao.enhance.method;

import java.lang.reflect.Method;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 */
public interface DaoMethodInvoker {
    /**
     * 执行
     *
     * @param proxy
     * @param method
     * @param args
     * @param dataSource
     * @throws Throwable
     */
    Object invoke(Object proxy, Method method, Object[] args, String dataSource) throws Throwable;

}
