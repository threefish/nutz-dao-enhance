# nutz-dao-spring-boot-starter


通过动态代理实现，不需要实现类就可以操作数据库，看起来有点像mybaits

#### 测试类
```java

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
@MapperScan("org.nutz.spring.boot.dao.test.mapper")
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
        UserDO insert = userMapper.insert(UserDO.builder().age(15).name("测试11").build());
        UserDO fetch = userMapper.fetch(insert.getId());
        int updateCount = userMapper.delete(insert.getId());
        System.out.println(updateCount);
    }

}

```

####  实体类 entity
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
public class UserDO {
    @Id
    Integer id;
    @Column
    String name;
    @Column
    Integer age;
}
```

#### 数据库操作对象Mapper
```java
package org.nutz.spring.boot.dao.test.mapper;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.spring.boot.dao.annotation.*;
import org.nutz.spring.boot.dao.execute.BaseMapper;
import org.nutz.spring.boot.dao.pagination.PageData;
import org.nutz.spring.boot.dao.test.entity.UserDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/30
 */
@Mapper
@Component
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 返回当前实体类
     *
     * @param id
     * @return
     */
    @QuerySql("select * from user where id=@id")
    UserDO fetchEntityOne(int id);

    /**
     * 根据 condition 条件返回
     *
     * @param condition
     * @return
     */
    @QuerySql("select * from user $condition")
    List<UserDO> listUser(Condition condition);

    /**
     * 查询返回一个map
     *
     * @param id
     * @return
     */
    @QuerySql("select * from user where id=@id")
    Map fetchMapOne(int id);

    /**
     * 查询返回一个 Record
     *
     * @param id
     * @return
     */
    @QuerySql("select * from user where id=@id")
    Record fetchRecordOne(int id);

    /**
     * 只会返回第一条记录，且不会像mybaits那样会提示too many result
     *
     * @return
     */
    @QuerySql("select * from user")
    UserDO fetchOne();

    /**
     * 分页查询
     *
     * @param pager
     * @return
     */
    @QuerySql("select * from user")
    PageData listUserPage(Pager pager);

    /**
     * 插入获取自增ID
     *
     * @param name
     * @param age
     * @param create
     * @return
     */
    @InsertSql("INSERT INTO user(`name`, `age`,`gmt_create`,`create_by`) VALUES (@name,@age, now(),@create)")
    int insert(String name, int age, String create);

    /**
     * 插入
     *
     * @param name
     * @param age
     * @param create
     */
    @InsertSql("INSERT INTO user(`name`, `age`,`gmt_create`,`create_by`) VALUES (@name,@age, now(),@create)")
    void insertVoid(String name, int age, String create);

    /**
     * 更新数据
     *
     * @param age
     * @param id
     * @return
     */
    @UpdateSql("UPDATE user SET age = @age WHERE id = @id")
    int updateAgeById(int age, int id);

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    @DelectSql("DELETE FROM user WHERE id=@id")
    int delectById(int id);

}

```

