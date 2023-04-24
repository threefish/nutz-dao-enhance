package org.nutz.dao.enhance.registrar.factory;

import lombok.SneakyThrows;
import org.nutz.dao.Dao;
import org.nutz.dao.enhance.audit.AuditHandler;
import org.nutz.dao.enhance.config.DaoEnhanceConstant;
import org.nutz.dao.enhance.config.DaoProperties;
import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.method.holder.AutoCreateTableHolder;
import org.nutz.dao.enhance.util.FieldCalcUtil;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄川 2020/12/16
 * 默认实现
 */
@SuppressWarnings("all")
public class DefaualtNutEnhanceCoreFactory implements EnhanceCoreFactory {
    /**
     * 数据源缓存
     */
    private final Map<String, Dao> daoCache = new HashMap<>();

    private final Ioc ioc;

    private final DaoProperties daoProperties;

    public DefaualtNutEnhanceCoreFactory(Ioc ioc, DaoProperties daoProperties) {
        this.ioc = ioc;
        this.daoProperties = daoProperties;
        FieldCalcUtil.setEnhanceCoreFactory(this);
    }

    @Override
    public Dao getDao() {
        return this.getDao(DaoEnhanceConstant.DEFAUALT_DATASOURCE_KEY);
    }

    @Override
    public Dao getDao(String dataSource) {
        return daoCache.get(dataSource);
    }

    @Override
    public AuditHandler getAuditHandler() {
        return ioc.getByType(AuditHandler.class);
    }


    @SneakyThrows
    @Override
    public Object getBean(String beanName) {
        Class<?> type = this.ioc.getType(beanName);
        return this.ioc.get(type, beanName);
    }


    /**
     * 初始化
     */
    public void init() {
        // 默认数据源
        final DataSource dataSource = this.ioc.get(DataSource.class);
        daoCache.put(DaoEnhanceConstant.DEFAUALT_DATASOURCE_KEY, new NutDao(dataSource));
        // 多数据源
        final String[] names = this.ioc.getNamesByType(DataSource.class);
        Arrays.stream(names).filter(dsName -> !daoCache.containsKey(dsName)).forEach(dsName ->
                daoCache.put(dsName, new NutDao(this.ioc.get(DataSource.class)))
        );
        // 自动建表
        AutoCreateTableHolder.autoGenerateDdl(this, daoProperties);
    }

}
