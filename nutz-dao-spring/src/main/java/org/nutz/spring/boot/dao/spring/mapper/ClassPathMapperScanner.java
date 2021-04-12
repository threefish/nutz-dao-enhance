package org.nutz.spring.boot.dao.spring.mapper;

import lombok.extern.slf4j.Slf4j;
import org.nutz.spring.boot.dao.annotation.Mapper;
import org.nutz.spring.boot.dao.factory.DaoFactory;
import org.nutz.spring.boot.dao.spring.binding.MapperProxyFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Slf4j
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

    private String dataSource;

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        final RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference(DaoFactory.class);
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder definitionHolder : beanDefinitions) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) definitionHolder.getBeanDefinition();
            String beanClassName = beanDefinition.getBeanClassName();
            beanDefinition.setBeanClass(MapperProxyFactory.class);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(dataSource);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(runtimeBeanReference);
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
        addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }
}
