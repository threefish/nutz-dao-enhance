package org.nutz.spring.boot.dao.factory;

import lombok.RequiredArgsConstructor;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.spring.boot.dao.config.DataSourceConstant;
import org.nutz.spring.boot.dao.spring.run.SpringDaoRunner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component
@ConditionalOnBean(DaoFactory.class)
@RequiredArgsConstructor
public class DefaualtNutDaoFactory implements DaoFactory, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    /**
     * 数据源缓存
     */
    private HashMap<String, Dao> daoHashMap = new HashMap<>();
    /**
     * 默认数据源
     */
    private Dao defaualtDao;

    @Override
    public Dao getDao() {
        if (daoHashMap.isEmpty()) {
            throw new RuntimeException("数据源为空");
        } else {
            return defaualtDao;
        }
    }

    @Override
    public Dao getDao(String dataSource) {
        return daoHashMap.get(dataSource);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final String[] names = this.applicationContext.getBeanNamesForType(DataSource.class);
        if (names.length == 1) {
            // 单数据源
            this.defaualtDao = this.createDao(this.applicationContext.getBean(DataSource.class));
            daoHashMap.put(DataSourceConstant.DEFAUALT_DATASOURCE_KEY, this.defaualtDao);
        } else {
            // 多数据源
            Arrays.stream(names).forEach(name -> {
                final DataSource bean = this.applicationContext.getBean(name, DataSource.class);
                final Dao dao = this.createDao(bean);
                if (DataSourceConstant.DEFAUALT_DATASOURCE_KEY.equals(name)) {
                    this.defaualtDao = dao;
                }
                daoHashMap.put(name, dao);
            });
        }
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
