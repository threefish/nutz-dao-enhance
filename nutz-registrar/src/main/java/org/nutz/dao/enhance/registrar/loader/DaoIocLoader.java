package org.nutz.dao.enhance.registrar.loader;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.enhance.annotation.Dao;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.registrar.annotation.DaoScan;
import org.nutz.dao.enhance.registrar.annotation.DaoScans;
import org.nutz.dao.enhance.registrar.factory.DaoProxyFactory;
import org.nutz.ioc.*;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Slf4j
public class DaoIocLoader implements IocLoader {

    public static final String LOADER_NAME = "*org.nutz.dao.enhance.registrar.loader.DaoIocLoader";

    protected Map<String, List<String>> dataSourcePackagesMapping = new HashMap<>();
    private HashMap<String, IocObject> map = new HashMap<String, IocObject>();

    public DaoIocLoader() {
    }

    /**
     * 传入要扫描的主模块包名
     *
     * @param packages
     */
    public DaoIocLoader(String... packages) {
        for (String pkg : packages) {
            log.info(" > scan '{}'", pkg);
            for (Class<?> classZ : Scans.me().scanPackage(pkg)) {
                addDaoPackages(classZ);
            }
        }
        addDaoClass();
        if (map.isEmpty()) {
            log.warn("NONE @DaoScan  @DaoScans found!! Check your ioc configure!! packages={}", Arrays.toString(packages));
        }
    }


    public void addDaoClass() {
        dataSourcePackagesMapping.forEach((dataSourceName, packages) -> {
            log.info(" > scan '{}'", packages);
            for (String pkg : packages) {
                for (Class<?> classZ : Scans.me().scanPackage(pkg)) {
                    addClass(dataSourceName, classZ);
                }
            }
        });
    }

    public void addClass(String dataSourceName, Class<?> classZ) {
        if (classZ.isMemberClass()
                || classZ.isEnum()
                || classZ.isAnnotation()
                || classZ.isAnonymousClass()) {
            return;
        }
        final Dao annotation = classZ.getAnnotation(Dao.class);
        if (classZ.isInterface() && Objects.nonNull(annotation)) {
            String beanName = annotation.name();
            if (Strings.isBlank(beanName)) {
                // 否则采用 @InjectName
                InjectName innm = classZ.getAnnotation(InjectName.class);
                if (null != innm && !Strings.isBlank(innm.value())) {
                    beanName = innm.value();
                } else {
                    beanName = Strings.lowerFirst(classZ.getSimpleName());
                }
            }
            // 重名了, 需要用户用@IocBean(name="xxxx") 区分一下
            if (map.containsKey(beanName)) {
                throw new IocException(beanName,
                        "Duplicate beanName=%s, by %s !!  Have been define by %s !!",
                        beanName,
                        classZ.getName(),
                        map.get(beanName).getType().getName());
            }
            IocObject iocObject = new IocObject();
            iocObject.setType(classZ);
            iocObject.addArg(new IocValue(IocValue.TYPE_NORMAL, classZ));
            iocObject.addArg(Iocs.convert(DaoFactory.defaualtDaoFactoryBeanName, true));
            iocObject.addArg(new IocValue(IocValue.TYPE_NORMAL, dataSourceName));
            iocObject.setFactory(DaoProxyFactory.class.getName() + "#getObject");
            map.put(beanName, iocObject);
            log.info("   > add '{}' - {}", beanName, classZ.getName());
        }
    }

    public void addDaoPackages(Class<?> classZ) {
        if (classZ.isInterface()
                || classZ.isMemberClass()
                || classZ.isEnum()
                || classZ.isAnnotation()
                || classZ.isAnonymousClass()) {
            return;
        }
        int modify = classZ.getModifiers();
        if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify))) {
            return;
        }
        DaoScans daoScans = classZ.getAnnotation(DaoScans.class);
        if (Objects.nonNull(daoScans)) {
            Arrays.stream(daoScans.value()).forEach(this::addToMapping);
        }
        DaoScan daoScan = classZ.getAnnotation(DaoScan.class);
        if (Objects.nonNull(daoScan)) {
            addToMapping(daoScan);
        }
    }

    private void addToMapping(DaoScan daoScan) {
        final List<String> list = dataSourcePackagesMapping.getOrDefault(daoScan.dataSource(), new ArrayList<>());
        Arrays.stream(daoScan.value()).forEach(list::add);
        Arrays.stream(daoScan.basePackages()).forEach(list::add);
        Arrays.stream(daoScan.basePackageClasses()).forEach(aClass -> list.add(aClass.getName()));
        dataSourcePackagesMapping.put(daoScan.dataSource(), list);
    }

    @Override
    public String[] getName() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        if (has(name)) {
            return map.get(name);
        }
        throw new ObjectLoadException("Object '" + name + "' without define! Pls check your ioc configure");
    }

    @Override
    public boolean has(String name) {
        return map.containsKey(name);
    }
}
