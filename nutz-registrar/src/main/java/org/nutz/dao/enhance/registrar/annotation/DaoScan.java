package org.nutz.dao.enhance.registrar.annotation;

import org.nutz.dao.enhance.config.DataSourceConstant;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(DaoScans.class)
public @interface DaoScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @DaoScan("org.my.pkg")} instead of {@code @DaoScan(basePackages = "org.my.pkg"})}.
     *
     * @return base package names
     */
    String[] value() default {};

    /**
     * Base packages to scan for MyBatis interfaces. Note that only interfaces with at least one method will be
     * registered; concrete classes will be ignored.
     *
     * @return base package names for scanning mapper interface
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that serves no purpose other than being
     * referenced by this attribute.
     *
     * @return classes that indicate base package for scanning mapper interface
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 数据源,采用默认数据源
     */
    String dataSource() default DataSourceConstant.DEFAUALT_DATASOURCE_KEY;
}
