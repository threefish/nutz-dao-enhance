package org.nutz.dao.spring.boot.factory;

import lombok.RequiredArgsConstructor;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.config.DaoEnhanceConstant;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.spring.boot.runner.SpringDaoRunner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author 黄川 2020/12/16
 * 默认实现
 */
@Component(value = DaoFactory.defaualtDaoFactoryBeanName)
@ConditionalOnBean(DaoFactory.class)
@RequiredArgsConstructor
public class DefaualtNutDaoFactory implements DaoFactory, InitializingBean, ApplicationContextAware {

    /**
     * 数据源缓存
     */
    private final HashMap<String, Dao> daoHashMap = new HashMap<>();
    private ApplicationContext applicationContext;


    @Override
    public Dao getDao() {
        return this.getDao(DaoEnhanceConstant.DEFAUALT_DATASOURCE_KEY);
    }

    @Override
    public Dao getDao(String dataSource) {
        return daoHashMap.get(dataSource);
    }

    @Override
    public void afterPropertiesSet() {
        // 默认数据源
        daoHashMap.put(DaoEnhanceConstant.DEFAUALT_DATASOURCE_KEY, this.createDao(this.applicationContext.getBean(DataSource.class)));
        // 多数据源
        final String[] names = this.applicationContext.getBeanNamesForType(DataSource.class);
        Arrays.stream(names).filter(dataSource -> !daoHashMap.containsKey(dataSource)).forEach(name ->
                daoHashMap.put(name, this.createDao(this.applicationContext.getBean(name, DataSource.class)))
        );
    }

    private Dao createDao(DataSource dataSource) {
        NutDao nutDao = new NutDao(dataSource);
        // 将事务交给spring管理
        nutDao.setRunner(new SpringDaoRunner());
        return nutDao;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
