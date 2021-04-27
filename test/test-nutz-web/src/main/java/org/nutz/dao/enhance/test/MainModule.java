package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.registrar.annotation.DaoScan;
import org.nutz.dao.enhance.registrar.loader.DaoIocLoader;
import org.nutz.mvc.annotation.IocBy;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBy(args = {
        "*js", "ioc/",
        // 注解扫描
        "*anno", MainModule.BASEPACKAGE, "org.nutz.dao.enhance.registrar",
        "*tx",
        // 加载DaoIocLoader
        DaoIocLoader.LOADER_NAME, MainModule.BASEPACKAGE,
})
@DaoScan(basePackages = MainModule.BASEPACKAGE)
public class MainModule {

    public static final String BASEPACKAGE = "org.nutz.dao.enhance.test";

}
