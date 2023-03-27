package org.nutz.dao.enhance.registrar.loader;

import org.nutz.boot.AppContext;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.ioc.IocLoader;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2021/6/14
 */
public class NutzBootDaoIocLoader implements IocLoaderProvider {

    @Override
    public IocLoader getIocLoader() {
        AppContext defaultContext = AppContext.getDefault();
        return new DaoIocLoader(defaultContext.getPackage());
    }
}
