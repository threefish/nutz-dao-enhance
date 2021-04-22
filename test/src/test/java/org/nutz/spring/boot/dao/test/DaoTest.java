package org.nutz.spring.boot.dao.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.spring.boot.dao.annotation.DaoScan;
import org.nutz.spring.boot.dao.pagination.PageData;
import org.nutz.spring.boot.dao.test.entity.UserDO;
import org.nutz.spring.boot.dao.test.mapper.UserMapper;
import org.nutz.spring.boot.yaml.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@PropertySource(value = {"classpath:application.yml"}, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DaoTest.class)
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@DaoScan("org.nutz.spring.boot.dao.test.mapper")
@Transactional
public class DaoTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void testCustomizeSql() {
        List<UserDO> list = userMapper.listUser(Cnd.where("id", "=", 8));
        PageData<UserDO> pageData = userMapper.listUserPage(new Pager(1, 10));
        UserDO uset = userMapper.fetchEntityOne(8);
        Map map = userMapper.fetchMapOne(8);
        Record record = userMapper.fetchRecordOne(8);
        UserDO usetDO = userMapper.fetchOne();
        userMapper.insertVoid("王五", 100, "张三");
        int insertId = userMapper.insert("王五", 100, "张三");
        int updateCount = userMapper.updateAgeById(50, insertId);
        int delectCount = userMapper.delectById(insertId);
        System.out.println(delectCount);
    }

    @Test
    public void testBaseMapper() {
        UserDO insert = userMapper.insert(UserDO.builder().age(15).realName("测试11").build());
        UserDO fetch = userMapper.fetch(insert.getId());
        int updateCount = userMapper.delete(insert.getId());
        System.out.println(updateCount);
    }


    @Test
    public void testHql() {
        final UserDO userDO = UserDO.builder().age(15).realName("测试11").build();
        userMapper.insert(userDO);
        UserDO fetch = userMapper.queryByHql("测试11");
        UserDO fetch2 = userMapper.queryByHql2(userDO);
        System.out.println(fetch2);
    }

    @Test
    public void testCnd() {
        UserDO fetch2 = userMapper.fetch(Cnd.where(UserDO::getId, "=", 1));
        System.out.println(fetch2);

    }

}
