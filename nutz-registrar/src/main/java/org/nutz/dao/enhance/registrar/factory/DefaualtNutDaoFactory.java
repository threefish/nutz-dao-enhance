package org.nutz.dao.enhance.registrar.factory;

import org.nutz.dao.Dao;
import org.nutz.dao.enhance.config.DataSourceConstant;
import org.nutz.dao.enhance.factory.DaoFactory;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author 黄川 2020/12/16
 * 默认实现
 */
public class DefaualtNutDaoFactory implements DaoFactory {
    /**
     * 数据源缓存
     */
    private final HashMap<String, Dao> daoCatche = new HashMap<>();

    private final Ioc ioc;

    public DefaualtNutDaoFactory(Ioc ioc) {
        this.ioc = ioc;
    }

    @Override
    public Dao getDao() {
        return this.getDao(DataSourceConstant.DEFAUALT_DATASOURCE_KEY);
    }

    @Override
    public Dao getDao(String dataSource) {
        return daoCatche.get(dataSource);
    }

    public void init() {
        // 默认数据源
        final DataSource dataSource = this.ioc.get(DataSource.class);
        daoCatche.put(DataSourceConstant.DEFAUALT_DATASOURCE_KEY, new NutDao(dataSource));
        // 多数据源
        final String[] names = this.ioc.getNamesByType(DataSource.class);
        Arrays.stream(names).filter(dsName -> !daoCatche.containsKey(dsName)).forEach(dsName ->
                daoCatche.put(dsName, new NutDao(this.ioc.get(DataSource.class)))
        );
    }

}
