package org.nutz.spring.boot.test;


import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.spring.boot.annotation.DaoScan;
import org.nutz.spring.boot.MainApplication;
import org.nutz.spring.boot.test.dao.UserDao;
import org.nutz.spring.boot.test.entity.UserDO;
import org.nutz.spring.boot.yaml.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DaoTest {

    @Autowired
    private UserDao userMapper;

    private UserDO u1 = null;
    private UserDO u2 = null;
    private UserDO u3 = null;

    @Before
    public void before() {
        u1 = userMapper.insert(UserDO.builder().age(15).realName("测试1").build());
        u2 = userMapper.insert(UserDO.builder().age(16).realName("测试2").build());
        u3 = userMapper.insert(UserDO.builder().age(17).realName("测试3").build());
    }

    @Test
    public void test_condition() {
        List<UserDO> list = userMapper.listUser(Cnd.where(UserDO::getAge, "=", 15));
        assert list.size() == 1;
    }


    @Test
    public void list_optional_user_condition() {
        Optional<List<UserDO>> optional = userMapper.listOptionalUser(Cnd.where(UserDO::getAge, "=", 15));
        assert optional.isPresent() && optional.get().size() == 1;
    }


    @Test
    public void test_list_map() {
        List<Map> maps = userMapper.listMap();
        assert maps.size() == 3;

    }

    @Test
    public void test_list_pagedata() {
        PageRecord<UserDO> pageRecord = userMapper.listUserPage(new Pager(1, 10));
        assert pageRecord.getTotal() == 3;
        assert pageRecord.getRecords().size() == 3;
    }

    @Test
    public void test_query_user_by_id() {
        UserDO uset = userMapper.queryUserById(u1.getId());
        assert uset != null;
    }

    @Test
    public void test_query_optional_user_by_id() {
        final Optional<UserDO> userDO = userMapper.queryOptionalUserById(u1.getId());
        assert userDO != null && userDO.isPresent();
    }

    @Test
    public void test_query_map_by_id() {
        Map map = userMapper.queryMapById(u1.getId());
        assert map != null;

    }

    @Test
    public void test_query_record_by_id() {
        Record record = userMapper.queryRecordById(u1.getId());
        assert record != null;
    }

    @Test
    public void test_query_all_and_get_first() {
        UserDO usetDO = userMapper.queryAllAndGetFirst();
        assert usetDO != null;
    }

    @Test
    public void test_insert_void() {
        userMapper.insertVoid("王五", 100, "张三");
    }

    @Test
    public void test_insert_and_incr_pk_id() {
        int insertId = userMapper.insert("王五", 100, "张三");
        assert insertId > 0;

    }

    @Test
    public void test_update_age_by_id() {
        int insertId = userMapper.insert("王五", 100, "张三");
        int updateCount = userMapper.updateAgeById(50, insertId);
        assert updateCount == 1;
    }

    @Test
    public void test_delect_by_id() {
        int insertId = userMapper.insert("王五", 100, "张三");
        int delectCount = userMapper.delectById(insertId);
        assert delectCount == 1;
    }


    @Test
    public void test_insert_entity() {
        UserDO insert = userMapper.insert(UserDO.builder().age(15).realName("测试11").build());
        assert insert.getId() > 0;
    }

    @Test
    public void test_fetch_by_id() {
        UserDO insert = userMapper.insert(UserDO.builder().age(15).realName("测试11").build());
        UserDO fetch = userMapper.fetch(insert.getId());
        assert fetch.getId() > 0;
    }

    @Test
    public void test_delete_by_id() {
        UserDO insert = userMapper.insert(UserDO.builder().age(15).realName("测试11").build());
        int updateCount = userMapper.delete(insert.getId());
        assert updateCount == 1;
    }


    @Test
    public void test_cnd_hql() {
        final UserDO userDO = UserDO.builder().age(15).realName("测试11").build();
        userMapper.insert(userDO);
        UserDO fetch = userMapper.queryByCndHql("测试11");
        assert fetch != null;
    }

    @Test
    public void test_cnd_hql_vo() {
        final UserDO userDO = UserDO.builder().age(15).realName(u1.getRealName()).build();
        UserDO fetch2 = userMapper.queryByVoHql(userDO);
        assert fetch2 != null;
    }


    @Test
    public void test_query_realnames() {
        String[] strings = userMapper.queryRealNames();
        assert Arrays.equals(strings, new String[]{u1.getRealName(), u2.getRealName(), u3.getRealName()});
    }

    @Test
    public void test_query_optional_realnames() {
        final Optional<String[]> strings = userMapper.queryOptionalRealNames();
        assert strings.isPresent() && Arrays.equals(strings.get(), new String[]{u1.getRealName(), u2.getRealName(), u3.getRealName()});
    }

    @Test
    public void test_query_int_ids() {
        int[] ints = userMapper.queryIntIds();
        assert Arrays.equals(ints, new int[]{u1.getId(), u2.getId(), u3.getId()});
    }

    @Test
    public void test_query_integer_ids() {
        Integer[] ints = userMapper.queryIntegerIds();
        assert Arrays.equals(ints, new Integer[]{u1.getId(), u2.getId(), u3.getId()});
    }

}
