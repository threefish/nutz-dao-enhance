package org.nutz.demo.test;

import org.nutz.dao.enhance.registrar.annotation.DaoScan;
import org.nutz.dao.enhance.registrar.loader.DaoIocLoader;
import org.nutz.mvc.annotation.IocBy;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBy(args = {
        "*js", "ioc/",
        // 注解扫描
        "*anno", "org.nutz.demo", "org.nutz.dao.enhance.registrar",
        "*tx",
        // 加载DaoIocLoader
        DaoIocLoader.LOADER_NAME, "org.nutz.demo",
})
@DaoScan(basePackages = "org.nutz.demo.test.dao")
public class MainModule {
}
