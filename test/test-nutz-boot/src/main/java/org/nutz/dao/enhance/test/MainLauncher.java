package org.nutz.dao.enhance.test;

import org.nutz.boot.NbApp;
import org.nutz.dao.enhance.registrar.annotation.DaoScan;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
@DaoScan(basePackageClasses = MainLauncher.class)
public class MainLauncher {

    public static void main(String[] args) throws Exception {
        new NbApp().setArgs(args).setPrintProcDoc(true).run();
    }

}
