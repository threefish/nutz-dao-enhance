package org.nutz.spring.boot.dao.spring.mapper;

import org.nutz.spring.boot.dao.spring.binding.MapperProxyFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
public class ClassPathMapperScanner<T> extends ClassPathBeanDefinitionScanner {

    @Setter
    private Class<T> mapperInterface;

    /**
     * 使用Spring Bean机制自动创建
     */
    @Setter
    private String daoFactoryBeanName;

    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder definitionHolder : beanDefinitions) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) definitionHolder.getBeanDefinition();
            String beanClassName = beanDefinition.getBeanClassName();
            // 必须在这里加入泛型限定，要不然在spring下会有循环引用的问题
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            // 供spring实例化代理对象使用
            beanDefinition.setBeanClass(MapperProxyFactory.class);
            beanDefinition.getPropertyValues().add("mapperInterface", beanClassName);
            beanDefinition.getPropertyValues().add("daoFactory", new RuntimeBeanReference(this.daoFactoryBeanName));
            // 设置Mapper按照接口组装
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            if (logger.isDebugEnabled()) {
                logger.debug("已开启Dao自动按照类型注入 '" + definitionHolder.getBeanName() + "'.");
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
        boolean acceptAllInterfaces = true;
        // override AssignableTypeFilter to ignore matches on the actual marker interface
        if (this.mapperInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.mapperInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }
}
