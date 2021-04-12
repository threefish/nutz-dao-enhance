package org.nutz.spring.boot.dao.execute;


import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
public class BaseMapperExecute<T> implements BaseMapper<T> {

    private final Dao dao;

    private final Class<T> entityClass;

    private final Entity entity;

    public BaseMapperExecute(Dao dao, Class<T> entityClass) {
        this.dao = dao;
        this.entityClass = entityClass;
        this.entity = dao.getEntity(entityClass);
    }

    @Override
    public T insert(T obj) {
        return this.dao.insert(obj);
    }

    @Override
    public T insertWith(T obj, String regex) {
        return this.dao.insert(obj, regex);
    }

    @Override
    public T insertLinks(T obj, String regex) {
        return this.dao.insertLinks(obj, regex);
    }

    @Override
    public T insertRelation(T obj, String regex) {
        return this.dao.insertRelation(obj, regex);
    }

    @Override
    public T insert(T obj, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr) {
        return this.dao.insert(obj, ignoreNull, ignoreZero, ignoreBlankStr);
    }

    @Override
    public T insertOrUpdate(T obj) {
        return this.dao.insertOrUpdate(obj);
    }

    @Override
    public T insertOrUpdate(T obj, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter) {
        return this.dao.insertOrUpdate(obj, insertFieldFilter, updateFieldFilter);
    }

    @Override
    public int updateAndIncrIfMatch(T obj, FieldFilter fieldFilter, String fieldName) {
        return this.dao.updateAndIncrIfMatch(obj, fieldFilter, fieldName);
    }

    @Override
    public int updateWithVersion(T obj) {
        return this.dao.updateWithVersion(obj);
    }

    @Override
    public int updateWithVersion(T obj, FieldFilter fieldFilter) {
        return this.dao.updateWithVersion(obj, fieldFilter);
    }

    @Override
    public int update(T obj) {
        return this.dao.update(obj);
    }

    @Override
    public int update(T obj, String actived) {
        return this.dao.update(obj, actived);
    }

    @Override
    public int update(T obj, String actived, String locked, boolean ignoreNull) {
        return this.dao.update(obj, actived, locked, ignoreNull);
    }

    @Override
    public int update(T obj, FieldFilter fieldFilter) {
        return this.dao.update(obj, fieldFilter);
    }

    @Override
    public int update(T obj, FieldFilter fieldFilter, Condition cnd) {
        return this.dao.update(obj, fieldFilter, cnd);
    }

    @Override
    public int update(T obj, Condition cnd) {
        return this.dao.update(obj, cnd);
    }

    @Override
    public int updateIgnoreNull(T obj) {
        return this.dao.update(obj);
    }

    @Override
    public T updateWith(T obj, String regex) {
        return this.dao.updateWith(obj, regex);
    }

    @Override
    public T updateLinks(T obj, String regex) {
        return this.dao.updateLinks(obj, regex);
    }

    @Override
    public List query(Condition cnd, Pager pager, FieldMatcher matcher) {
        return this.dao.query(this.entityClass, cnd, pager, matcher);
    }

    @Override
    public List query(Condition cnd, Pager pager, String regex) {
        return this.dao.query(this.entityClass, cnd, pager, regex);
    }

    @Override
    public List query(Condition cnd, Pager pager) {
        return this.dao.query(this.entityClass, cnd, pager);
    }

    @Override
    public List query(Condition cnd) {
        return this.dao.query(this.entityClass, cnd);
    }

    @Override
    public int each(Condition cnd, Pager pager, Each callback) {
        return this.dao.each(this.entityClass, cnd, pager, callback);
    }

    @Override
    public int each(Condition cnd, Each callback) {
        return this.dao.each(this.entityClass, cnd, callback);
    }

    @Override
    public int delete(long id) {
        return this.dao.delete(this.entityClass, id);
    }

    @Override
    public int delete(String name) {
        return this.dao.delete(this.entityClass, name);
    }

    @Override
    public int deletex(Object... pks) {
        return this.dao.deletex(this.entityClass, pks);
    }

    @Override
    public int delete(T obj) {
        return this.dao.delete(obj);
    }

    @Override
    public int deleteWith(T obj, String regex) {
        return this.dao.deleteWith(obj, regex);
    }

    @Override
    public int deleteLinks(T obj, String regex) {
        return this.dao.deleteLinks(obj, regex);
    }

    @Override
    public T fetch(long id) {
        return this.dao.fetch(this.entityClass, id);
    }

    @Override
    public T fetch(String name) {
        return this.dao.fetch(this.entityClass, name);
    }

    @Override
    public T fetchx(Object... pks) {
        return this.dao.fetchx(this.entityClass, pks);
    }

    @Override
    public T fetch(Condition cnd) {
        return this.dao.fetch(this.entityClass, cnd);
    }

    @Override
    public T fetchLinks(T obj, String regex) {
        return this.dao.fetchLinks(obj, regex);
    }

    @Override
    public T fetchLinks(T obj, String regex, Condition cnd) {
        return this.dao.fetchLinks(obj, regex, cnd);
    }

    @Override
    public int clear(Condition cnd) {
        return this.dao.clear(this.entityClass, cnd);
    }

    @Override
    public int clear() {
        return this.dao.clear(this.entityClass);
    }

    @Override
    public T clearLinks(T obj, String regex) {
        return this.dao.clearLinks(obj, regex);
    }

    @Override
    public int count(Condition cnd) {
        return this.dao.count(this.entityClass, cnd);
    }

    @Override
    public int count() {
        return this.dao.count(this.entityClass);
    }

    @Override
    public int getMaxId() {
        return this.dao.getMaxId(this.entityClass);
    }

    @Override
    public T fetchByJoin(String regex, Condition cnd) {
        return this.dao.fetchByJoin(this.entityClass, regex, cnd);
    }

    @Override
    public T fetchByJoin(String regex, long id) {
        return this.dao.fetchByJoin(this.entityClass, regex, id);
    }

    @Override
    public T fetchByJoin(String regex, String name) {
        return this.dao.fetchByJoin(this.entityClass, regex, name);
    }

    @Override
    public List<T> queryByJoin(String regex, Condition cnd) {
        return this.dao.queryByJoin(this.entityClass, regex, cnd);
    }

    @Override
    public T fetchByJoin(String regex, Condition cnd, Map<String, Condition> cnds) {
        return this.dao.fetchByJoin(this.entityClass, regex, cnd, cnds);
    }

    @Override
    public List<T> queryByJoin(String regex, Condition cnd, Pager pager) {
        return this.dao.queryByJoin(this.entityClass, regex, cnd, pager);
    }

    @Override
    public List<T> queryByJoin(String regex, Condition cnd, Pager pager, Map<String, Condition> cnds) {
        return this.dao.queryByJoin(this.entityClass, regex, cnd, pager, cnds);
    }

    @Override
    public int countByJoin(String regex, Condition cnd) {
        return this.dao.countByJoin(this.entityClass, regex, cnd);
    }

    @Override
    public Entity<T> getEntity() {
        return this.entity;
    }

    @Override
    public Dao getDao() {
        return this.dao;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }
}
