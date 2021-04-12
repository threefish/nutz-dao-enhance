package org.nutz.spring.boot.dao.spring.binding;


import org.nutz.dao.Dao;
import org.nutz.spring.boot.dao.execute.*;
import org.nutz.spring.boot.dao.factory.DaoFactory;
import org.nutz.spring.boot.dao.spring.binding.method.MethodSignature;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
public class MapperMethod {

    private static final HashMap<String, Method> CACHE = new HashMap<>();

    static {
        Method[] methods = BaseMapper.class.getMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            CACHE.put(method.toGenericString(), method);
        }
    }

    /**
     * 方法信息
     */
    private final MethodSignature methodSignature;
    private final DaoFactory daoFactory;
    private final String methodGenericString;

    /**
     * 这里对mapper进行解析，每个mapper只会解析1次
     *
     * @param mapperInterface
     * @param method
     */
    public MapperMethod(DaoFactory daoFactory, Class<?> mapperInterface, Method method) {
        this.daoFactory = daoFactory;
        this.methodGenericString = method.toGenericString();
        this.methodSignature = new MethodSignature(mapperInterface, method);
    }


    public Object execute(String dataSource, Object[] args) {
        Dao dao = daoFactory.getDao(dataSource);
        if (this.methodSignature.isCustomizeSql()) {
            return this.getCustomizeSqlExecute(dao, args).invoke();
        }
        BaseMapper baseMapper = new BaseMapperExecute(dao, this.methodSignature.getReturnEntityClass());
        Method method = CACHE.get(this.methodGenericString);
        try {
            return method.invoke(baseMapper, args);
        } catch (Exception e) {
            throw new RuntimeException("BaseMapper执行出错", e);
        }
    }

    /**
     * 是自定义sql
     *
     * @param dao
     * @param args
     * @return
     */
    private Execute getCustomizeSqlExecute(Dao dao, Object[] args) {
        Execute execute = null;
        switch (this.methodSignature.getSqlCommandType()) {
            case SELECT:
                if (this.methodSignature.isPaginationQuery()) {
                    // 是分页查询
                    execute = new PaginationQueryExecute(dao, this.methodSignature, args);
                } else if (this.methodSignature.isMultipleRecords()) {
                    // 返回多条记录的情况下
                    execute = new MultipleQueryExecute(dao, this.methodSignature, args);
                } else {
                    // 返回单条记录，获取未知的情况
                    execute = new SingleQueryExecute(dao, this.methodSignature, args);
                }
                break;
            case UPDATE:
            case DELETE:
                execute = new UpdateQueryExecute(dao, this.methodSignature, args);
                break;
            case INSERT:
                execute = new InsertQueryExecute(dao, this.methodSignature, args);
                break;
            default:
        }
        Assert.notNull(execute, "不支持的方法");
        return execute;
    }

}
