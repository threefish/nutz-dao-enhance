package org.nutz.dao.spring.boot.scanner;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.enhance.annotation.Dao;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.enhance.method.holder.AutoCreateTableHolder;
import org.nutz.dao.spring.boot.factory.DaoProxyFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Slf4j
public class ClassPathDaoScanner extends ClassPathBeanDefinitionScanner {

    @Setter
    private String dataSource;


    public ClassPathDaoScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        final RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(DaoFactory.DEFAUALT_DAO_FACTORY_BEAN_NAME);
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder definitionHolder : beanDefinitions) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) definitionHolder.getBeanDefinition();
            String beanClassName = beanDefinition.getBeanClassName();
            beanDefinition.setBeanClass(DaoProxyFactory.class);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(dataSource);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(runtimeBeanReference);
            AutoCreateTableHolder.addDataSourceEntityClassMapping(dataSource, beanClassName);
            if (log.isDebugEnabled()) {
                log.debug("自动生成'{}'代理类 beanName:{}", beanClassName, definitionHolder.getBeanName());
            }
        }
        return beanDefinitions;
    }

    /**
     * @param beanDefinition
     * @return beanDefinition 是接口 && 独立的类，则返回true
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(Dao.class));
        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }
}
