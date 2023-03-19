package org.nutz.dao.enhance.method.provider;

import org.nutz.dao.Condition;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/13
 */
@SuppressWarnings("all")
public class BaseDaoProvider {

    public static <T> T insert(ProviderContext providerContext, T obj) {
        return providerContext.dao.insert(obj);
    }

    public static <T> T insert(ProviderContext providerContext, T obj, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr) {
        return providerContext.dao.insert(obj, ignoreNull, ignoreZero, ignoreBlankStr);
    }


    public static <T> T insertWith(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.insert(obj, regex);
    }


    public static <T> T insertLinks(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.insertLinks(obj, regex);
    }


    public static <T> T insertRelation(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.insertRelation(obj, regex);
    }


    public static <T> T insertOrUpdate(ProviderContext providerContext, T obj) {
        return providerContext.dao.insertOrUpdate(obj);
    }


    public static <T> T insertOrUpdate(ProviderContext providerContext, T obj, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter) {
        return providerContext.dao.insertOrUpdate(obj, insertFieldFilter, updateFieldFilter);
    }


    public static <T> int updateAndIncrIfMatch(ProviderContext providerContext, T obj, FieldFilter fieldFilter, String fieldName) {
        return providerContext.dao.updateAndIncrIfMatch(obj, fieldFilter, fieldName);
    }


    public static <T> int updateWithVersion(ProviderContext providerContext, T obj) {
        return providerContext.dao.updateWithVersion(obj);
    }


    public static <T> int updateWithVersion(ProviderContext providerContext, T obj, FieldFilter fieldFilter) {
        return providerContext.dao.updateWithVersion(obj, fieldFilter);
    }


    public static <T> int update(ProviderContext providerContext, T obj) {
        return providerContext.dao.update(obj);
    }


    public static <T> int update(ProviderContext providerContext, T obj, String actived) {
        return providerContext.dao.update(obj, actived);
    }


    public static <T> int update(ProviderContext providerContext, T obj, String actived, String locked, boolean ignoreNull) {
        return providerContext.dao.update(obj, actived, locked, ignoreNull);
    }


    public static <T> int update(ProviderContext providerContext, T obj, FieldFilter fieldFilter) {
        return providerContext.dao.update(obj, fieldFilter);
    }


    public static <T> int update(ProviderContext providerContext, T obj, FieldFilter fieldFilter, Condition cnd) {
        return providerContext.dao.update(obj, fieldFilter, cnd);
    }


    public static <T> int update(ProviderContext providerContext, T obj, Condition cnd) {
        return providerContext.dao.update(obj, cnd);
    }


    public static <T> int updateIgnoreNull(ProviderContext providerContext, T obj) {
        return providerContext.dao.update(obj);
    }


    public static <T> T updateWith(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.updateWith(obj, regex);
    }


    public static <T> T updateLinks(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.updateLinks(obj, regex);
    }

    public static int each(ProviderContext providerContext, Condition cnd, Pager pager, Each callback) {
        return providerContext.dao.each(providerContext.entityClass, cnd, pager, callback);
    }

    public static int each(ProviderContext providerContext, Condition cnd, Each callback) {
        return providerContext.dao.each(providerContext.entityClass, cnd, callback);
    }

    public static int delete(ProviderContext providerContext, long id) {
        return providerContext.dao.delete(providerContext.entityClass, id);
    }

    public static int delete(ProviderContext providerContext, String name) {
        return providerContext.dao.delete(providerContext.entityClass, name);
    }

    public static <T> int delete(ProviderContext providerContext, T obj) {
        return providerContext.dao.delete(obj);
    }

    public static int deletex(ProviderContext providerContext, Object... pks) {
        return providerContext.dao.deletex(providerContext.entityClass, pks);
    }

    public static <T> int deleteWith(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.deleteWith(obj, regex);
    }

    public static <T> int deleteLinks(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.deleteLinks(obj, regex);
    }

    public static <T> T fetch(ProviderContext<T> providerContext, long id) {
        return providerContext.dao.fetch(providerContext.entityClass, id);
    }

    public static <T> T fetch(ProviderContext<T> providerContext, String name) {
        return providerContext.dao.fetch(providerContext.entityClass, name);
    }

    public static <T> T fetch(ProviderContext<T> providerContext, Condition cnd) {
        return providerContext.dao.fetch(providerContext.entityClass, cnd);
    }

    public static <T> T fetchx(ProviderContext<T> providerContext, Object... pks) {
        return providerContext.dao.fetchx(providerContext.entityClass, pks);
    }

    public static <T> T fetchLinks(ProviderContext<T> providerContext, T obj, String regex) {
        return providerContext.dao.fetchLinks(obj, regex);
    }

    public static <T> T fetchLinks(ProviderContext providerContext, T obj, String regex, Condition cnd) {
        return providerContext.dao.fetchLinks(obj, regex, cnd);
    }

    public static <T> int clear(ProviderContext providerContext, Condition cnd) {
        return providerContext.dao.clear(providerContext.entityClass, cnd);
    }

    public static <T> int clear(ProviderContext providerContext) {
        return providerContext.dao.clear(providerContext.entityClass);
    }

    public static <T> T clearLinks(ProviderContext providerContext, T obj, String regex) {
        return providerContext.dao.clearLinks(obj, regex);
    }

    public static <T> int count(ProviderContext providerContext, Condition cnd) {
        return providerContext.dao.count(providerContext.entityClass, cnd);
    }

    public static <T> int count(ProviderContext providerContext) {
        return providerContext.dao.count(providerContext.entityClass);
    }

    public static <T> int getMaxId(ProviderContext providerContext) {
        return providerContext.dao.getMaxId(providerContext.entityClass);
    }

    public static <T> T fetchByJoin(ProviderContext<T> providerContext, String regex, Condition cnd) {
        return providerContext.dao.fetchByJoin(providerContext.entityClass, regex, cnd);
    }

    public static <T> T fetchByJoin(ProviderContext<T> providerContext, String regex, long id) {
        return providerContext.dao.fetchByJoin(providerContext.entityClass, regex, id);
    }

    public static <T> T fetchByJoin(ProviderContext<T> providerContext, String regex, String name) {
        return providerContext.dao.fetchByJoin(providerContext.entityClass, regex, name);
    }

    public static <T> T fetchByJoin(ProviderContext<T> providerContext, String regex, Condition cnd, Map<String, Condition> cnds) {
        return providerContext.dao.fetchByJoin(providerContext.entityClass, regex, cnd, cnds);
    }

    public static <T> List<T> queryByJoin(ProviderContext providerContext, String regex, Condition cnd) {
        return providerContext.dao.queryByJoin(providerContext.entityClass, regex, cnd);
    }

    public static <T> List<T> queryByJoin(ProviderContext providerContext, String regex, Condition cnd, Pager pager) {
        return providerContext.dao.queryByJoin(providerContext.entityClass, regex, cnd, pager);
    }

    public static <T> List<T> queryByJoin(ProviderContext providerContext, String regex, Condition cnd, Pager pager, Map<String, Condition> cnds) {
        return providerContext.dao.queryByJoin(providerContext.entityClass, regex, cnd, pager, cnds);
    }

    public static <T> int countByJoin(ProviderContext providerContext, String regex, Condition cnd) {
        return providerContext.dao.countByJoin(providerContext.entityClass, regex, cnd);
    }

    public List query(ProviderContext providerContext, Condition cnd, Pager pager, FieldMatcher matcher) {
        return providerContext.dao.query(providerContext.entityClass, cnd, pager, matcher);
    }

    public List query(ProviderContext providerContext, Condition cnd, Pager pager, String regex) {
        return providerContext.dao.query(providerContext.entityClass, cnd, pager, regex);
    }

    public List query(ProviderContext providerContext, Condition cnd, Pager pager) {
        return providerContext.dao.query(providerContext.entityClass, cnd, pager);
    }

    public List query(ProviderContext providerContext, Condition cnd) {
        return providerContext.dao.query(providerContext.entityClass, cnd);
    }

}
