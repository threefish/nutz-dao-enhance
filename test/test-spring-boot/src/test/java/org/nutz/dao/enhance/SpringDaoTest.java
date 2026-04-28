package org.nutz.dao.enhance;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.enhance.test.MainApplication;
import org.nutz.dao.enhance.test.dao.UserDao;
import org.nutz.dao.enhance.test.entity.JobDO;
import org.nutz.dao.enhance.test.entity.UserDO;
import org.nutz.dao.enhance.test.entity.UserVO;
import org.nutz.dao.enhance.util.FieldCalcUtil;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 */
@SuppressWarnings("all")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SpringDaoTest {
    /**
     * 防止IDE报红可以在 UserDao 类上加 Spring 注册注解如  @Repository @Component 等
     */
    @Autowired
    private UserDao userDao;

    private UserDO u1 = null;
    private UserDO u2 = null;
    private UserDO u3 = null;

    @Before
    public void before() {
        userDao.clear();
        u1 = UserDO.builder().age(15).realName("测试1").build();
        u2 = UserDO.builder().age(16).realName("测试2").build();
        u3 = UserDO.builder().age(17).realName("测试3").build();
        List<UserDO> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);
        list.add(u3);
        userDao.saveBatch(list);
    }

    @After
    public void after() {
        userDao.clear();
    }

    @Test
    public void test_auditing() {
        assert Objects.equals(u1.getCreateBy(), "spring-test");
    }

    @Test
    public void test_condition() {
        List<UserDO> list = userDao.listUser(Cnd.where(UserDO::getAge, "=", 15));
        assert list.size() == 1;
    }


    @Test
    public void list_optional_user_condition() {
        Optional<List<UserDO>> optional = userDao.listOptionalUser(Cnd.where(UserDO::getAge, "=", 15));
        assert optional.isPresent() && optional.get().size() == 1;
    }


    @Test
    public void test_list_map() {
        List<Map> maps = userDao.listMap();
        assert maps.size() == 3;

    }

    @Test
    public void test_list_pagedata() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("A");
        PageRecord<UserDO> pageRecord = userDao.listUserPage(new Pager(1, 10));
        assert pageRecord.getTotal() == 3;
        assert pageRecord.getRecords().size() == 3;
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        stopWatch.start("B");
        PageRecord<UserDO> pageRecord2 = userDao.listUserPage(new Pager(1, 10));
        assert pageRecord2.getTotal() == 3;
        assert pageRecord2.getRecords().size() == 3;
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void test_query_user_by_id() {
        UserDO uset = userDao.queryUserById(u1.getId());
        assert uset != null;
    }

    @Test
    public void test_query_optional_user_by_id() {
        final Optional<UserDO> userDO = userDao.queryOptionalUserById(u1.getId());
        assert userDO != null && userDO.isPresent();
    }

    @Test
    public void test_query_map_by_id() {
        Map map = userDao.queryMapById(u1.getId());
        assert map != null;

    }

    @Test
    public void test_query_record_by_id() {
        Record record = userDao.queryRecordById(u1.getId());
        assert record != null;
    }

    @Test
    public void test_query_all_and_get_first() {
        UserDO usetDO = userDao.queryAllAndGetFirst();
        assert usetDO != null;
    }

    @Test
    public void test_insert_void() {
        userDao.insertVoid("王五", 100, "张三");
    }

    @Test
    public void test_insert_and_incr_pk_id() {
        int insertId = userDao.insert("王五", 100, "张三");
        assert insertId > 0;

    }


    @Test
    public void test_update_age_by_id() {
        int insertId = userDao.insert("王五", 100, "张三");
        int updateCount = userDao.updateAgeById(50, insertId);
        assert updateCount == 1;
    }

    @Test
    public void test_delect_by_id() {
        int insertId = userDao.insert("王五", 100, "张三");
        List<UserDO> list = userDao.lambdaQuery().list();
        int delectCount = userDao.delectById(insertId);
        assert delectCount == 1;
    }


    @Test
    public void test_insert_entity() {
        UserDO insert = userDao.insert(UserDO.builder().age(15).realName("测试11").build());
        assert insert.getId() > 0;
    }

    @Test
    public void test_fetch_by_id() {
        UserDO insert = userDao.insert(UserDO.builder().age(15).realName("测试11").build());
        UserDO fetch = userDao.fetch(insert.getId());
        assert fetch.getId() > 0;
    }

    @Test
    public void test_delete_by_id() {
        UserDO insert = userDao.insert(UserDO.builder().age(15).realName("测试11").build());
        int updateCount = userDao.delete(insert.getId());
        assert updateCount == 1;
    }


    @Test
    public void test_cnd_hql() {
        final UserDO userDO = UserDO.builder().age(15).realName("测试11").build();
        userDao.insert(userDO);
        UserDO fetch = userDao.queryByCndHql("测试11");
        assert fetch != null;
    }

    @Test
    public void test_cnd_hql_vo() {
        final UserDO userDO = UserDO.builder().age(15).realName(u1.getRealName()).build();
        UserDO fetch2 = userDao.queryByVoHql(userDO);
        assert fetch2 != null;
    }


    @Test
    public void test_query_realnames() {
        String[] strings = userDao.queryRealNames();
        assert Arrays.equals(strings, new String[]{u1.getRealName(), u2.getRealName(), u3.getRealName()});
    }

    @Test
    public void test_query_optional_realnames() {
        final Optional<String[]> strings = userDao.queryOptionalRealNames();
        assert strings.isPresent() && Arrays.equals(strings.get(), new String[]{u1.getRealName(), u2.getRealName(), u3.getRealName()});
    }

    @Test
    public void test_query_int_ids() {
        int[] ints = userDao.queryIntIds();
        assert Arrays.equals(ints, new int[]{u1.getId(), u2.getId(), u3.getId()});
    }

    @Test
    public void test_query_integer_ids() {
        Integer[] ints = userDao.queryIntegerIds();
        assert Arrays.equals(ints, new Integer[]{u1.getId(), u2.getId(), u3.getId()});
    }

    @Test
    public void test_insert_with_customprovider() {
        int insertId = userDao.insertWithCustomprovider("王五", 100, null);
        assert insertId > 0;
    }

    @Test
    public void test_call_list() {
        Optional<List<UserVO>> maps = userDao.callList();
        assert maps.get().size() == 3;
    }

    @Test
    public void test_call_out() {
        int maxId = userDao.getMaxId();
        Optional<UserVO> data = userDao.callOut(maxId);
        assert u3.getRealName().equals(data.get().getRealName());
    }

    @Test
    public void test_call_lambdaQuery_fetch() {
        UserDO userDO = userDao.lambdaQuery().where(UserDO::getAge, "=", 15).one();
        assert userDO != null;
    }

    @Test
    public void test_call_lambdaQuery_query() {
        List<UserDO> query = userDao.lambdaQuery().list();
        assert query.size() == 3;
    }

    @Test
    public void test_call_lambdaQuery_queryPage() {
        PageRecord<UserDO> userDOPageRecord = userDao.lambdaQuery().limit(1, 10).listPage();
        assert userDOPageRecord.getTotal() == 3;
        PageRecord<UserDO> userDOPageRecord1 = userDao.lambdaQuery().listPage(1, 10);
        assert userDOPageRecord1.getTotal() == 3;
        PageRecord<UserDO> userDOPageRecord2 = userDao.lambdaQuery().listPage(new Pager(1, 10));
        assert userDOPageRecord2.getTotal() == 3;
        PageRecord<UserDO> userDOPageRecord3 = userDao.lambdaQuery().eq(UserDO::getAge, 15).limit(1, 10).listPage();
        assert userDOPageRecord3.getTotal() == 1;
        List<UserDO> userDOPageRecord4 = userDao.lambdaQuery().limit(1, 10).groupBy(UserDO::getId).list();
        assert userDOPageRecord4.size() == 3;
        UserDO fetch = userDao.lambdaQuery().eq(UserDO::getAge, 15).one();
        assert fetch.getAge() == 15;
        List<UserDO> query = userDao.lambdaQuery().eq(UserDO::getAge, 15).list();
        assert query.size() == 1;
        List<UserDO> query1 = userDao.lambdaQuery().likeRight(UserDO::getRealName, "测试").list();
        assert query1.size() == 3;
        UserDO insert = userDao.insert(UserDO.builder().age(16).realName(null).build());
        UserDO nullRealName = userDao.lambdaQuery().isNull(UserDO::getRealName).one();
        assert insert.getId() == nullRealName.getId();
        List<UserDO> query2 = userDao.lambdaQuery().isNotNull(UserDO::getRealName).list();
        assert query2.size() == 3;
        List<UserDO> query3 = userDao.lambdaQuery().isNull(UserDO::getRealName).list();
        assert query3.size() == 1;
        List<UserDO> query4 = userDao.lambdaQuery().isNotNull(UserDO::getRealName).in(UserDO::getAge, Arrays.asList(15, 16)).list();
        assert query4.size() == 2;
        int count = userDao.lambdaQuery().gte(UserDO::getAge, 17).count();
        List<UserDO> query5 = userDao.lambdaQuery().gte(UserDO::getAge, 17).list();
        assert query5.size() == count;
    }

    @Test
    public void test_call_lambda_update() {
        int updateCount = userDao.lambdaUpdate().set(UserDO::getAge, 123).eq(UserDO::getAge, 15).update();
        int update = userDao.lambdaUpdate().set(UserDO::getAge, 15).eq(UserDO::getAge, 123).update();
        assert updateCount == update;
        userDao.lambdaUpdate().set(UserDO::getAge, 150).insert();
        userDao.lambdaUpdate().set(UserDO::getAge, 250).insert();
        int delCount = userDao.lambdaUpdate().gte(UserDO::getAge, 150).delete();
        assert delCount == 2;

    }

    @Test
    public void test_insert_loopfor_age() {
        int count = userDao.insertLoopForAge("1", "张三", Arrays.asList(15, 12, 13, 19));
        assert count == 4;
    }

    @Test
    public void test_insert_loopfor_age_and_returnid() {
        List<Integer> list = userDao.insertLoopForAgeAndReturnId("1", "张三", Arrays.asList(15, 12, 13, 19));
        assert list.size() == 4;
    }

    @Test
    public void test_delect_loopfor_age() {
        int count = userDao.insertLoopForAge("1", "张三", Arrays.asList(150, 120, 130, 190));
        List<NutMap> nutMaps = Arrays.asList(
                NutMap.NEW().setv("test", 150),
                NutMap.NEW().setv("test", 120),
                NutMap.NEW().setv("test", 130),
                NutMap.NEW().setv("test", 190)
        );
        int deleteCount = userDao.deleteLoopForAge(nutMaps);
        assert count == deleteCount;
    }

    @Test
    public void test_call_lambda_query_fields() {
        userDao.lambdaUpdate()
                .set(UserDO::getAge, 123)
                .set(UserDO::getRealName, null)
                .set(UserDO::getCreateBy, "张三")
                .ignoreNull()
                .insert();
        int maxId = userDao.getMaxId();
        UserDO one = userDao.lambdaQuery().select(UserDO::getId).eq(UserDO::getId, maxId).one();
        assert one.getRealName() == null;

        UserDO one1 = userDao.lambdaQuery().excludes(UserDO::getCreateBy).eq(UserDO::getId, maxId).one();
        assert one1.getCreateBy() == null;

        userDao.lambdaUpdate()
                .set(UserDO::getAge, 123)
                .set(UserDO::getRealName, null)
                .ignoreNull()
                .eq(UserDO::getId, maxId)
                .update();

    }

    @Test
    public void test_field_calc() {
        UserDO userDO = userDao.insert(UserDO.builder().age(17).realName("测试3").build());

        FieldCalcUtil.calc(userDO, "test");
        assert userDO.getUserDO() != null;
        assert userDO.getUserDO1() != null;

        FieldCalcUtil.calc(userDO, "test2");
        assert userDO.getTest() == userDO.getId() + userDO.getAge();

        FieldCalcUtil.calc(userDO);
        assert userDO.getUserDO3() != null;


    }

    @Test
    public void test_left_join_query() {
        List<UserDO> list = userDao.lambdaQuery()
                .selectAs(UserDO::getRealName, JobDO::getRealName)
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .like(UserDO.class, JobDO::getRealName, "测试")
                .list();
        assert list.size() == 3;

        PageRecord<UserDO> page = userDao.lambdaQuery()
                .selectAs(UserDO::getRealName, JobDO::getUserId)
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .like(UserDO.class, JobDO::getRealName, "测试")
                .limit(1, 10)
                .listPage();
        assert page.getTotal() == 3;
    }

    @Test
    public void test_or_query() {
        userDao.lambdaQuery().like(UserDO::getRealName, "测试1")
                .or()
                .eq(UserDO::getRealName, "测试2")
                .eq(UserDO::getRealName, "测试3")
                .eq(UserDO::getRealName, "测试4")
                .or(q -> q.eq(false,UserDO::getRealName, "1").or().eq(false,UserDO::getRealName, "2").eq(false,UserDO::getRealName, "3"))
                .groupBy(UserDO::getRealName)
                .list();


    }

    // ===================== 新增功能测试 =====================

    /**
     * 验证 ne 操作符 bug 修复：ne 应生成 != 而不是 =
     */
    @Test
    public void test_ne_bug_fix() {
        List<UserDO> list = userDao.lambdaQuery().ne(UserDO::getAge, 15).list();
        assert list.size() == 2;
        assert list.stream().allMatch(u -> u.getAge() != 15);
    }

    /**
     * 验证 ne(String, Object) 操作符 bug 修复
     */
    @Test
    public void test_ne_string_bug_fix() {
        List<UserDO> list = userDao.lambdaQuery().ne("age", 15).list();
        assert list.size() == 2;
        assert list.stream().allMatch(u -> u.getAge() != 15);
    }

    /**
     * 验证 ne(boolean, PFun, Object) 操作符 bug 修复
     */
    @Test
    public void test_ne_condition_bug_fix() {
        List<UserDO> list = userDao.lambdaQuery().ne(true, UserDO::getAge, 15).list();
        assert list.size() == 2;
        List<UserDO> listAll = userDao.lambdaQuery().ne(false, UserDO::getAge, 15).list();
        assert listAll.size() == 3;
    }

    /**
     * in varargs 便捷方法
     */
    @Test
    public void test_in_varargs() {
        List<UserDO> list = userDao.lambdaQuery().in(UserDO::getAge, 15, 16).list();
        assert list.size() == 2;
    }

    /**
     * in varargs 带 condition
     */
    @Test
    public void test_in_varargs_condition() {
        List<UserDO> listFalse = userDao.lambdaQuery().in(false, UserDO::getAge, 15, 16).list();
        assert listFalse.size() == 3;
        List<UserDO> listTrue = userDao.lambdaQuery().in(true, UserDO::getAge, 15, 16).list();
        assert listTrue.size() == 2;
    }

    /**
     * notIn varargs 便捷方法
     */
    @Test
    public void test_not_in_varargs() {
        List<UserDO> list = userDao.lambdaQuery().notIn(UserDO::getAge, 15, 16).list();
        assert list.size() == 1 && list.get(0).getAge() == 17;
    }

    /**
     * notIn varargs 带 condition
     */
    @Test
    public void test_not_in_varargs_condition() {
        List<UserDO> listFalse = userDao.lambdaQuery().notIn(false, UserDO::getAge, 15, 16).list();
        assert listFalse.size() == 3;
        List<UserDO> listTrue = userDao.lambdaQuery().notIn(true, UserDO::getAge, 15, 16).list();
        assert listTrue.size() == 1;
    }

    /**
     * exists 子查询
     */
    @Test
    public void test_exists_subquery() {
        // 自关联 exists：有 age=15 的用户存在
        List<UserDO> list = userDao.lambdaQuery()
                .exists("select 1 from user as u2 where u2.id = user.id and u2.age = 15")
                .list();
        assert list.size() == 1;
        // 不存在的条件
        List<UserDO> listNone = userDao.lambdaQuery()
                .exists("select 1 from user as u2 where u2.id = user.id and u2.age = 999")
                .list();
        assert listNone.size() == 0;
    }

    /**
     * notExists 子查询
     */
    @Test
    public void test_not_exists_subquery() {
        List<UserDO> list = userDao.lambdaQuery()
                .notExists("select 1 from user as u2 where u2.id = user.id and u2.age = 999")
                .list();
        assert list.size() == 3;
    }

    /**
     * exists 带条件
     */
    @Test
    public void test_exists_condition() {
        // exists(false, ...) 不应用条件
        List<UserDO> listFalse = userDao.lambdaQuery()
                .exists(false, "select 1 from user as u2 where u2.id = user.id and u2.age = 999")
                .list();
        assert listFalse.size() == 3;
        // exists(true, ...) 应用条件
        List<UserDO> listTrue = userDao.lambdaQuery()
                .exists(true, "select 1 from user as u2 where u2.id = user.id and u2.age = 15")
                .list();
        assert listTrue.size() == 1;
    }

    /**
     * 条件 asc 排序
     */
    @Test
    public void test_conditional_asc() {
        List<UserDO> list = userDao.lambdaQuery().asc(true, UserDO::getAge).list();
        assert list.get(0).getAge() == 15;
        List<UserDO> listNoOrder = userDao.lambdaQuery().asc(false, UserDO::getAge).list();
        assert listNoOrder.size() == 3;
    }

    /**
     * 条件 desc 排序
     */
    @Test
    public void test_conditional_desc() {
        List<UserDO> list = userDao.lambdaQuery().desc(true, UserDO::getAge).list();
        assert list.get(0).getAge() == 17;
    }

    /**
     * 条件 limit 分页
     */
    @Test
    public void test_conditional_limit() {
        PageRecord<UserDO> page = userDao.lambdaQuery().limit(true, 1, 2).listPage();
        assert page.getRecords().size() == 2;
        assert page.getTotal() == 3;
    }

    /**
     * select(String...) 便捷方法
     */
    @Test
    public void test_select_string_names() {
        UserDO one = userDao.lambdaQuery().select("id", "age").eq(UserDO::getAge, 15).one();
        assert one != null && one.getAge() == 15;
    }

    /**
     * distinct 去重查询
     */
    @Test
    public void test_distinct_query() {
        List<UserDO> list = userDao.lambdaQuery().distinct().list();
        assert list.size() == 3;
    }

    /**
     * 条件 set 方法
     */
    @Test
    public void test_conditional_set() {
        String newName = "newName";
        int updateCount = userDao.lambdaUpdate()
                .set(newName != null, UserDO::getRealName, newName)
                .set(false, UserDO::getAge, 999)
                .eq(UserDO::getId, u1.getId())
                .update();
        assert updateCount == 1;
        UserDO updated = userDao.fetch(u1.getId());
        assert "newName".equals(updated.getRealName());
        assert updated.getAge() == 15;
    }

    /**
     * 条件 setEx 方法
     */
    @Test
    public void test_conditional_setEx() {
        int updateCount = userDao.lambdaUpdate()
                .setEx(true, UserDO::getRealName, null)
                .setEx(true, UserDO::getAge, 20)
                .eq(UserDO::getId, u1.getId())
                .update();
        UserDO updated = userDao.fetch(u1.getId());
        assert updated.getAge() == 20;
    }

    /**
     * increment 原子递增
     */
    @Test
    public void test_increment() {
        int updateCount = userDao.lambdaUpdate()
                .increment(UserDO::getAge, 1)
                .eq(UserDO::getId, u1.getId())
                .update();
        assert updateCount == 1;
        UserDO updated = userDao.fetch(u1.getId());
        assert updated.getAge() == 16;
    }

    /**
     * decrement 原子递减
     */
    @Test
    public void test_decrement() {
        int updateCount = userDao.lambdaUpdate()
                .decrement(UserDO::getAge, 2)
                .eq(UserDO::getId, u1.getId())
                .update();
        assert updateCount == 1;
        UserDO updated = userDao.fetch(u1.getId());
        assert updated.getAge() == 13;
    }

    /**
     * increment 与 set 混合使用
     */
    @Test
    public void test_increment_with_set() {
        int updateCount = userDao.lambdaUpdate()
                .set(UserDO::getRealName, "递增测试")
                .increment(UserDO::getAge, 5)
                .eq(UserDO::getId, u1.getId())
                .update();
        assert updateCount == 1;
        UserDO updated = userDao.fetch(u1.getId());
        assert "递增测试".equals(updated.getRealName());
        assert updated.getAge() == 20;
    }

    /**
     * JOIN 实体 notLike 方法（job 表无数据时 LEFT JOIN 结果中 job.real_name 为 NULL，
     * NULL NOT LIKE 'x' 不是 TRUE，所以行被排除）
     */
    @Test
    public void test_join_notLike() {
        List<UserDO> list = userDao.lambdaQuery()
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .notLike(JobDO.class, JobDO::getRealName, "不存在")
                .list();
        // job 表无数据，LEFT JOIN 后 job.real_name 为 NULL，NOT LIKE 过滤掉 NULL
        assert list.size() == 0;
    }

    /**
     * JOIN 实体 isNull（job 表无数据时 LEFT JOIN 结果中 job.real_name 为 NULL）
     */
    @Test
    public void test_join_isNull_condition() {
        List<UserDO> list = userDao.lambdaQuery()
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .isNull(JobDO.class, JobDO::getRealName)
                .list();
        // job 表无数据，所有 user 的 job.real_name IS NULL
        assert list.size() == 3;
    }

    /**
     * JOIN 实体 isNotNull
     */
    @Test
    public void test_join_isNotNull_condition() {
        List<UserDO> list = userDao.lambdaQuery()
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .isNotNull(JobDO.class, JobDO::getRealName)
                .list();
        // job 表无数据，无 user 的 job.real_name IS NOT NULL
        assert list.size() == 0;
    }

    /**
     * JOIN 实体 in varargs
     */
    @Test
    public void test_join_in_varargs() {
        // 在 user 表上使用 JOIN 风格的 in varargs
        List<UserDO> list = userDao.lambdaQuery()
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .in(UserDO.class, UserDO::getRealName, "测试1", "测试2")
                .list();
        assert list.size() == 2;
    }

    /**
     * GroupBy having 条件
     */
    @Test
    public void test_groupBy_having() {
        userDao.insert(UserDO.builder().age(15).realName("测试4").build());
        List<UserDO> list = userDao.lambdaQuery()
                .select(UserDO::getAge)
                .groupBy(UserDO::getAge)
                .having(Cnd.where("age", ">", 15))
                .list();
        assert list.size() >= 1;
    }

    /**
     * GroupBy count 统计
     */
    @Test
    public void test_groupBy_count() {
        int count = userDao.lambdaQuery().groupBy(UserDO::getAge).count();
        assert count >= 1;
    }

    /**
     * GroupBy listPage 分页
     */
    @Test
    public void test_groupBy_listPage() {
        PageRecord<UserDO> page = userDao.lambdaQuery()
                .groupBy(UserDO::getAge)
                .listPage(1, 10);
        assert page.getRecords().size() >= 1;
    }

    @Test
    public void test_update_ignore_null() {
        UserDO userDO = userDao.save(UserDO.builder().age(15).realName("测试4").build());
        assert userDO.getGmtCreate() != null;
        userDao.update(UserDO.builder().id(userDO.getId()).age(16).build());
        UserDO fetch = userDao.fetch(userDO.getId());
        assert fetch.getGmtCreate() != null;
    }

    @Test
    public void test_update_with_null() {
        UserDO userDO = userDao.save(UserDO.builder().age(15).realName("测试4").build());
        assert userDO.getRealName() != null;
        userDao.updateWithNull(UserDO.builder().id(userDO.getId()).age(16).build());
        UserDO fetch = userDao.fetch(userDO.getId());
        assert fetch.getAge() == 16;
        assert fetch.getRealName() == null;
    }
}
