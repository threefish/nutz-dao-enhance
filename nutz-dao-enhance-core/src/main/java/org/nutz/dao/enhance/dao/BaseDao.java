package org.nutz.dao.enhance.dao;

import org.nutz.dao.Condition;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.enhance.annotation.CustomProvider;
import org.nutz.dao.enhance.dao.lambda.LambdaQuery;
import org.nutz.dao.enhance.dao.lambda.LambdaUpdate;
import org.nutz.dao.enhance.method.provider.BaseDaoProvider;
import org.nutz.dao.enhance.method.provider.LambdaQueryProvider;
import org.nutz.dao.enhance.method.provider.LambdaUpdateProvider;
import org.nutz.dao.enhance.method.provider.ProviderContext;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Each;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 */
@SuppressWarnings("all")
public interface BaseDao<T> {
    /**
     * 将一个对象插入到一个数据源。
     * 声明了 '@Id'的字段会在插入数据库时被忽略，因为数据库会自动为其设值。如果想手动设置，请设置 '@Id(auto=false)'
     * 插入之前，会检查声明了 '@Default(@SQL("SELECT ..."))' 的字段，预先执行 SQL 为字段设置。
     * 插入之后，会检查声明了 '@Next(@SQL("SELECT ..."))' 的字段，通过执行 SQL 将值取回
     * 如果你的字段仅仅声明了 '@Id(auto=true)'，没有声明 '@Next'，则认为你还是想取回插入后最新的 ID 值，因为 自动为你添加类似 @Next(@SQL("SELECT MAX(id) FROM tableName")) 的设置
     *
     * @param t
     * @return
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insert(T t);

    @CustomProvider(type = BaseDaoProvider.class)
    T save(T t);

    /**
     * 以特殊规则执行insert
     *
     * @param t              实例对象
     * @param ignoreNull     忽略空值
     * @param ignoreZero     忽略0值
     * @param ignoreBlankStr 忽略空白字符串
     * @return 传入的实例变量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insert(T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr);

    @CustomProvider(type = BaseDaoProvider.class)
    T save(T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr);

    /**
     * 将对象插入数据库同时，也将符合一个正则表达式的所有关联字段关联的对象统统插入相应的数据库
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insertWith(T obj, String regex);

    /**
     * 根据一个正则表达式，仅将对象所有的关联字段插入到数据库中，并不包括对象本身
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insertLinks(T obj, String regex);


    /**
     * 将对象的一个或者多个，多对多的关联信息，插入数据表
     *
     * @param obj   对象
     * @param regex 正则表达式，描述了那种多对多关联字段将被执行该操作
     * @return 对象自身
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insertRelation(T obj, String regex);


    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     *
     * @param t 对象
     * @return 原对象
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insertOrUpdate(T t);

    @CustomProvider(type = BaseDaoProvider.class)
    T saveOrUpdate(T t);

    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     *
     * @param t                 对象
     * @param insertFieldFilter 插入时的字段过滤, 可以是null
     * @param updateFieldFilter 更新时的字段过滤,可以是null
     * @return 原对象
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T insertOrUpdate(T t, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter);

    /**
     * 乐观锁, 以特定字段的值作为限制条件,更新对象,并自增该字段.
     * <p/>
     * 执行的sql如下:
     * <p/>
     * <code>update t_user set age=30, city="广州", version=version+1 where name="wendal" and version=124;</code>
     *
     * @param obj         需要更新的对象, 必须带@Id/@Name/@Pk中的其中一种.
     * @param fieldFilter 需要过滤的属性. 若设置了哪些字段不更新,那务必确保过滤掉fieldName的字段
     * @param fieldName   参考字段的Java属性名.默认是"version",可以是任意数值字段
     * @return 若更新成功, 返回值大于0, 否则小于等于0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int updateAndIncrIfMatch(T obj, FieldFilter fieldFilter, String fieldName);

    /**
     * 基于版本的更新，版本不一样无法更新到数据
     *
     * @param obj 需要更新的对象, 必须有version属性
     * @return 若更新成功, 大于0, 否则小于0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int updateWithVersion(T obj);

    /**
     * 基于版本的更新，版本不一样无法更新到数据
     *
     * @param obj         需要更新的对象, 必须有version属性
     * @param fieldFilter 需要过滤的字段设置
     * @return 若更新成功, 大于0, 否则小于0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int updateWithVersion(T obj, FieldFilter fieldFilter);

    /**
     * 更新一个对象。对象必须有 '@Id' 或者 '@Name' 或者 '@PK' 声明。
     * <p>
     * 并且调用这个函数前， 主键的值必须保证是有效，否则会更新失败
     * <p>
     * 这个对象所有的字段都会被更新，即，所有的没有被设值的字段，都会被置成 NULL，如果遇到 NOT NULL 约束，则会引发异常。
     * 如果想有选择的更新个别字段，请使用 org.nutz.dao.FieldFilter
     * <p>
     * 如果仅仅想忽略所有的 null 字段，请使用 updateIgnoreNull 方法更新对象
     *
     * @param obj
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     * @see FieldFilter
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj);


    /**
     * 更新对象一部分字段
     *
     * @param obj     对象
     * @param actived 正则表达式描述要被更新的字段
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj, String actived);


    /**
     * 更新对象一部分字段
     *
     * @param obj     对象
     * @param actived 正则表达式描述要被更新的字段
     * @return 返回实际被更新的记录条数，一般的情况下，如果更新成功，返回 1，否则，返回 0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj, String actived, String locked, boolean ignoreNull);

    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj, FieldFilter fieldFilter);

    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj, FieldFilter fieldFilter, Condition cnd);

    @CustomProvider(type = BaseDaoProvider.class)
    int update(T obj, Condition cnd);


    /**
     * 更新一个对象，并且忽略所有 null 字段。
     * <p>
     * 注意: 基本数据类型都是不可能为null的,这些字段肯定会更新
     *
     * @param obj 要被更新的对象
     * @return 返回实际被更新的记录条数，一般的情况下，如果是单一Pojo,更新成功，返回 1，否则，返回 0
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int updateIgnoreNull(T obj);


    /**
     * 将对象更新的同时，也将符合一个正则表达式的所有关联字段关联的对象统统更新
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T updateWith(T obj, String regex);


    /**
     * 根据一个正则表达式，仅更新对象所有的关联字段，并不包括对象本身
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T updateLinks(T obj, String regex);


    @CustomProvider(type = BaseDaoProvider.class)
    List<T> list(Condition cnd, Pager pager, FieldMatcher matcher);

    @CustomProvider(type = BaseDaoProvider.class)
    List<T> list(Condition cnd, Pager pager, String regex);

    /**
     * 查询一组对象。你可以为这次查询设定条件，并且只获取一部分对象（翻页）
     *
     * @param cnd   WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param pager 翻页信息。如果为 null，则一次全部返回. 不会使用cnd中的pager!!!
     * @return 对象列表
     */
    @CustomProvider(type = BaseDaoProvider.class)
    List<T> list(Condition cnd, Pager pager);

    /**
     * 查询一组对象。你可以为这次查询设定条件
     *
     * @param cnd WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序<br>
     *            只有在调用这个函数的时候， cnd.limit 才会生效
     * @return 对象列表
     */
    @CustomProvider(type = BaseDaoProvider.class)
    List<T> list(Condition cnd);


    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     * <p>
     * 对象类型
     *
     * @param cnd      WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param pager    翻页信息。如果为 null，则一次全部返回
     * @param callback 处理回调
     * @return 一共迭代的数量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int each(Condition cnd, Pager pager, Each<T> callback);

    /**
     * 对一组对象进行迭代，这个接口函数非常适用于很大的数据量的集合，因为你不可能把他们都读到内存里
     *
     * @param cnd      WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序
     * @param callback 处理回调
     * @return 一共迭代的数量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int each(Condition cnd, Each<T> callback);


    /**
     * 根据对象 ID 删除一个对象。它只会删除这个对象，关联对象不会被删除。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * <p>
     * 如果你设定了外键约束，没有正确的清除关联对象会导致这个操作失败
     *
     * @param id 对象 ID
     * @return 影响的行数
     * @see org.nutz.dao.entity.annotation.Id
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int delete(long id);


    /**
     * 根据对象 Name 删除一个对象。它只会删除这个对象，关联对象不会被删除。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p>
     * 如果你设定了外键约束，没有正确的清除关联对象会导致这个操作失败
     * <p>
     * 对象类型
     *
     * @param name 对象 Name
     * @return 影响的行数
     * @see org.nutz.dao.entity.annotation.Name
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int delete(String name);

    /**
     * 自动判断如何删除一个对象。
     * <p>
     * 如果声明了 '@Id' 则相当于 delete(Class<T>,long)<br>
     * 如果声明了 '@Name'，则相当于 delete(Class<T>,String)<br>
     * 如果声明了 '@PK'，则 deletex(Class<T>,Object ...)<br>
     * 如果没声明任何上面三个注解，则会抛出一个运行时异常
     * </p>
     *
     * @param obj 要被删除的对象
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int delete(T obj);


    /**
     * 根据复合主键，删除一个对象。该对象必须声明 '@PK'，并且，给定的参数顺序 必须同 '@PK' 中声明的顺序一致，否则会产生不可预知的错误。
     *
     * @param pks 复合主键需要的参数，必须同 '@PK'中声明的顺序一致
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int deletex(Object... pks);


    /**
     * 将对象删除的同时，也将符合一个正则表达式的所有关联字段关联的对象统统删除
     * <p>
     * <b style=color:red>注意：</b>
     * <p>
     * Java 对象的字段会被保留，这里的删除，将只会删除数据库中的记录
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被删除
     * @return 被影响的记录行数
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int deleteWith(T obj, String regex);


    /**
     * 根据一个正则表达式，仅删除对象所有的关联字段，并不包括对象本身。
     * <p>
     * <b style=color:red>注意：</b>
     * <p>
     * Java 对象的字段会被保留，这里的删除，将只会删除数据库中的记录
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被删除
     * @return 被影响的记录行数
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int deleteLinks(T obj, String regex);


    /**
     * 根据对象 ID 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     *
     * @param id 对象 ID
     * @see org.nutz.dao.entity.annotation.Id
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetch(long id);


    /**
     * 根据对象 Name 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p>
     * 对象类型
     *
     * @param name 对象 Name
     * @return 对象本身
     * @see org.nutz.dao.entity.annotation.Name
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetch(String name);

    /**
     * 根据 WHERE 条件获取一个对象。如果有多个对象符合条件，将只获取 ResultSet 第一个记录
     *
     * @param cnd WHERE 条件
     * @return 对象本身
     * @see Condition
     * @see org.nutz.dao.entity.annotation.Name
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetch(Condition cnd);

    /**
     * 根据复合主键，获取一个对象。该对象必须声明 '@PK'，并且，给定的参数顺序 必须同 '@PK' 中声明的顺序一致，否则会产生不可预知的错误。
     *
     * @param pks 复合主键需要的参数，必须同 '@PK'中声明的顺序一致
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchx(Object... pks);


    /**
     * 根据一个正则表达式，获取对象所有的关联字段
     *
     * @param obj   数据对象,不可以是Class啊!!!传对象或集合啊!!!
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被查询
     * @return 更新后的数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchLinks(T obj, String regex);

    @CustomProvider(type = BaseDaoProvider.class)
    T fetchLinks(T obj);


    /**
     * 根据一个正则表达式，获取对象所有的关联字段, 并按Condition进行数据过滤排序
     * <p/>
     * <b>严重提醒,当使用Condition进行数据过滤排序时,应当使regex只匹配特定的映射字段</b>
     *
     * @param obj   数据对象,可以是普通对象或集合,但不是类
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被查询
     * @param cnd   关联字段的过滤(排序,条件语句,分页等)
     * @return 传入的数据对象
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchLinks(T obj, String regex, Condition cnd);

    @CustomProvider(type = BaseDaoProvider.class)
    T fetchLinks(ProviderContext providerContext, T obj, Condition cnd);

    /**
     * 根据一个 WHERE 条件，清除一组对象。只包括对象本身，不包括关联字段
     *
     * @param cnd 查询条件，如果为 null，则全部清除
     * @return 影响的行数
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int clear(Condition cnd);


    /**
     * 清除对象所有的记录
     * <p>
     * 对象类型
     *
     * @return 影响的行数
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int clear();


    /**
     * 根据正则表达式，清除对象的关联。
     * <p>
     * 对于 '@One' 和 '@Many'，对应的记录将会删除<br>
     * 而 '@ManyMay' 对应的字段，只会清除关联表中的记录
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被清除
     * @return 数据对象本身
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T clearLinks(T obj, String regex);


    /**
     * 根据条件，计算某个对象在数据库中有多少条记录
     *
     * @param cnd WHERE 条件
     * @return 数量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int count(Condition cnd);

    /**
     * 计算某个对象在数据库中有多少条记录
     *
     * @return 数量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int count();


    /**
     * 获取某个对象，最大的 ID 值。这个对象必须声明了 '@Id'
     *
     * @return 最大 ID 值
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int getMaxId();


    /**
     * 根据查询条件获取一个对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件,必须带表名!!!
     * @return 实体对象, 符合regex的关联属性也会取出
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchByJoin(String regex, Condition cnd);

    /**
     * 根据对象 ID 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param id    对象id
     * @return 实体
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchByJoin(String regex, long id);


    /**
     * 根据对象 NAME 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param name  对象name
     * @return 实体
     */
    @CustomProvider(type = BaseDaoProvider.class)
    T fetchByJoin(String regex, String name);

    @CustomProvider(type = BaseDaoProvider.class)
    T fetchByJoin(String regex, Condition cnd, Map<String, Condition> cnds);

    /**
     * 根据查询条件获取所有对象.<b>注意: 条件语句需要加上主表名或关联属性的JAVA属性名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件, 主表写表名, 子表写关联属性的JAVA属性名!
     * @return 实体对象的列表, 符合regex的关联属性也会取出
     */
    @CustomProvider(type = BaseDaoProvider.class)
    List<T> queryByJoin(String regex, Condition cnd);

    /**
     * 根据查询条件获取分页对象.<b>注意: 条件语句需要加上主表名或关联属性的JAVA属性名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件, 主表写表名, 子表写关联属性的JAVA属性名!
     * @param pager 分页对象 <b>注意: 分页不要在cnd中传入!</b>
     * @return 实体对象的列表, 符合regex的关联属性也会取出
     */
    @CustomProvider(type = BaseDaoProvider.class)
    List<T> queryByJoin(String regex, Condition cnd, Pager pager);

    @CustomProvider(type = BaseDaoProvider.class)
    List<T> queryByJoin(String regex, Condition cnd, Pager pager, Map<String, Condition> cnds);

    /**
     * 分页查询
     *
     * @param cnd   条件
     * @param pager 分页数
     * @return
     */
    @CustomProvider(type = BaseDaoProvider.class)
    PageRecord listPage(Condition cnd, Pager pager);

    /**
     * 分页查询
     *
     * @param cnd   条件
     * @param pager 分页数
     * @return
     */
    @CustomProvider(type = BaseDaoProvider.class)
    PageRecord listPage(Condition cnd, int pageNumber, int pageSize);

    /**
     * 根据查询条件获取分页对象.<b>注意: 条件语句需要加上主表名或关联属性的JAVA属性名!!!</b>
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件, 主表写表名, 子表写关联属性的JAVA属性名!
     * @return 数量
     */
    @CustomProvider(type = BaseDaoProvider.class)
    int countByJoin(String regex, Condition cnd);

    /**
     * 批量更新
     *
     * @param objList 对象列表
     * @return
     */
    @CustomProvider(type = BaseDaoProvider.class)
    boolean updateBatchByPk(Collection<T> objList);

    /**
     * 批量保存
     *
     * @param objList 对象列表
     * @return
     */
    @CustomProvider(type = BaseDaoProvider.class)
    Collection<T> saveBatch(Collection<T> objList);

    /**
     * 执行链式查询操作
     */
    @CustomProvider(type = LambdaQueryProvider.class)
    LambdaQuery<T> lambdaQuery();

    /**
     * 执行链式查询操作
     *
     * @param notNull  普通条件值不能为null
     * @param notEmpty 集合类型条件值不能为空
     * @return
     */
    @CustomProvider(type = LambdaQueryProvider.class)
    LambdaQuery<T> lambdaQuery(boolean notNull, boolean notEmpty);


    /**
     * 执行链式更新操作
     */
    @CustomProvider(type = LambdaUpdateProvider.class)
    LambdaUpdate<T> lambdaUpdate();

    /**
     * 执行链式更新操作
     *
     * @param notNull  普通条件值不能为null
     * @param notEmpty 集合类型条件值不能为空
     * @return
     */
    @CustomProvider(type = LambdaUpdateProvider.class)
    LambdaUpdate<T> lambdaUpdate(boolean notNull, boolean notEmpty);


}
