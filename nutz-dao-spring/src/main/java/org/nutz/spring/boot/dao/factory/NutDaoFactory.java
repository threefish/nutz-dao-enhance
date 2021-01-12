package org.nutz.spring.boot.dao.factory;

import org.nutz.spring.boot.dao.spring.run.SpringDaoRunner;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author 黄川 2020/12/16
 * 默认实现
 */
@Component("daoFactory")
@ConditionalOnBean(name = "daoFactory")
public class NutDaoFactory implements DaoFactory {

    @Autowired
    private Dao dao;

    @Bean
    public Dao createDao(@Autowired DataSource dataSource) {
        NutDao nutDao = new NutDao(dataSource);
        // 将事务交给spring管理
        nutDao.setRunner(new SpringDaoRunner());
        return nutDao;
    }

    @Override
    public Dao getDao() {
        return this.dao;
    }

    @Override
    public Dao getDao(String name) {
        return getDao();
    }
}
