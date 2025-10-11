# 🧩 nutz-dao-enhance

> 🚀 **增强版 NutzDao** —— 无需编写 DAO 实现类，即可完成数据库的基础 CRUD、复杂 SQL 查询、存储过程调用与 Lambda 链式操作。  
> 让 NutzDao 的使用更加优雅、灵活、高效。

---

## 🌟 功能亮点

1. **自动实体绑定**
   - 使用 `@Entity(UserDO.class)` 注解，可添加在 DAO 接口或方法上。
   - 当方法返回实体时，优先采用当前指定的 class。

2. **自动建表可控**
   - 在启用自动建表功能的前提下，为实体类添加 `@IgnoreAutoDDL` 注解可忽略自动建表/更新。

3. **操作人自动填充**
   - 实现 `AuditHandler` 接口，可在插入或更新时自动填充操作人字段（配合 `@CreatedBy`、`@LastModifiedBy` 注解）。
   - 同时支持 `@EntityListener` 注解。

4. **主键自动生成**
   - 实现 `IdentifierGenerator` 接口，为带有 `@AutoID` 的字段自动赋值。
   - 若配合 `@Id` 注解使用，仅在 `@Id(auto = false)` 时生效。

5. **自定义扩展能力**
   - 通过 `@CustomProvider` 扩展基础功能，示例参考 `org.nutz.dao.enhance.dao.BaseDao`。

6. **动态 SQL 条件语法**
   - 使用 `#[]` 语法实现可选条件拼接：
     ```sql
     #[ and u.realName=@name and u.gmtCreate=@gmtCreate ]
     ```
     当 `name` 或 `gmtCreate` 参数不存在时，该语句块将被忽略。

7. **智能实体名与字段映射**
   - 语句中可使用 Java 实体名（如 `UserDO as u`），自动转换为数据库表名（如 `user as u`）。
   - 字段名（如 `u.realName`）将自动转换为下划线格式（如 `u.real_name`）。

8. **批量操作支持**
   - 自定义 SQL 可按参数循环执行，实现批量插入/删除等操作。

9. **查询结果字段计算**
   - 使用 `@FieldCalculation` 注解对查询结果进行计算并赋值（支持分组与表达式）。

10. **Lambda 链式查询与更新**
    - 优雅的链式写法实现 CRUD、分页、连接查询等操作。

> 💡 提示：第 3~4 项功能的底层实现基于 NutzDao 原生 `@PrevInsert` / `@PrevUpdate` 与 EL 表达式，原生能力更强大，可灵活选择。

---

## 📦 Maven 坐标

- [nutz-dao-enhance-spring-starter](https://mvnrepository.com/artifact/org.nutz/nutz-dao-enhance-spring-starter)
- [nutz-dao-enhance-nutz-starter](https://mvnrepository.com/artifact/org.nutz/nutz-dao-enhance-nutz-starter)

---

## 🧠 Lambda 常用操作示例

```java
// 更新操作
userDao.lambdaUpdate().set(UserDO::getAge, 123).eq(UserDO::getAge, 15).update();
userDao.lambdaUpdate().eq(UserDO::getAge, 15).delete();
userDao.lambdaUpdate().set(UserDO::getAge, 250).insert();

// 查询操作
userDao.lambdaQuery().isNotNull(UserDO::getRealName).in(UserDO::getAge, Arrays.asList(15,16)).list();
userDao.lambdaQuery().isNull(UserDO::getRealName).one();
userDao.lambdaQuery().gte(UserDO::getAge, 17).count();
userDao.lambdaQuery().gte(UserDO::getAge, 17)
        .and(c -> c.gte(UserDO::getAge, 15).lte(UserDO::getAge, 40), c -> c.gte(UserDO::getId, 10))
        .list();
```

更多用法可参考测试类：
```java
org.nutz.dao.enhance.SpringDaoTest
```

---

## 🧩 DAO 接口示例

以下为 `UserDao` 的完整示例，展示了 `@Query`、`@Insert`、`@Update`、`@Delete`、`@CallStoredProcedure` 等注解的多种用法：

```java
/**
 * 自动建表，通过泛型 找到 UserDO 再根据 UserDO 信息进行建表。如果不需要自动建表，需要再UserDO上添加 @IgnoreAutoDDL 主键
 * 如果没有定义泛型但是也需要自动建表功能 则需要添加 @Entity(UserDO.class) 注解
 */
@Dao
public interface UserDao extends BaseDao<UserDO> {

   /**
    * gmtCreate 入参不存在，所以当前#[]中的全部条件不生效
    * 输出SQL：select u.id,u.real_name,u.age,u.gmt_create,u.create_by from user as u where 1=1   and u.real_name='测试11'
    *
    * @param name
    * @return
    */
   @Query("select u.* from UserDO as u where 1=1"
           + "#[ and u.realName=#{name} and u.gmtCreate=#{gmtCreate} ] "
           + "#[ and u.realName=#{name} ] ")
   UserDO queryByCndHql(String name);

   /**
    * 查询2
    * 输出SQL： select u.id,u.real_name,u.age,u.gmt_create,u.create_by from user as u where 1=1  and u.real_name='测试11'
    *
    * @param user
    */
   @Query("select u.* from UserDO as u where 1=1 "
           + "#[ and u.realName=#{user.realName} ] ")
   UserDO queryByVoHql(UserDO user);

   /**
    * 返回当前实体类
    *
    * @param id
    * @return
    */
   @Query("select * from user where id=#{id}")
   UserDO queryUserById(int id);

   /**
    * 返回当前实体类
    *
    * @param id
    * @return
    */
   @Query("select * from user where id=#{id}")
   Optional<UserDO> queryOptionalUserById(@Param("id") int id);

   /**
    * 根据 condition 条件返回
    *
    * @param condition
    * @return
    */
   @Query("select * from user $condition")
   @Entity(UserDO.class)
   List<UserDO> listUser(Condition condition);

   /**
    * 根据 condition 条件返回
    *
    * @param condition
    * @return
    */
   @Query("select * from user $condition")
   @Entity(UserDO.class)
   Optional<List<UserDO>> listOptionalUser(Condition condition);

   /**
    * 查询返回一个map
    *
    * @param id
    * @return
    */
   @Query("select * from user where id=#{id}")
   Map queryMapById(int id);

   /**
    * 查询返回一个map
    *
    * @return
    */
   @Query("select * from user")
   List<Map> listMap();

   /**
    * 查询返回一个 Record
    *
    * @param id
    * @return
    */
   @Query("select * from user where id=#{id}")
   Record queryRecordById(int id);

   /**
    * 只会返回第一条记录，且不会像mybaits那样会提示too many result
    *
    * @return
    */
   @Query("select * from user")
   UserDO queryAllAndGetFirst();

   /**
    * 分页查询
    *
    * @param pager
    * @return
    */
   @Query(
           value = "select * from user"
           , countSql = "select count(1) from user"
   )
   PageRecord listUserPage(Pager pager);

   /**
    * 插入获取自增ID
    *
    * @param name
    * @param age
    * @param create
    * @return
    */
   @Insert(value = "INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (#{name},#{age}, now(),#{create})", returnGeneratedKeys = true)
   int insert(String name, int age, String create);


   /**
    * 插入
    *
    * @param name
    * @param age
    * @param create
    */
   @Insert("INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (#{name},#{age}, now(),#{create})")
   void insertVoid(String name, int age, String create);

   /**
    * 更新数据
    *
    * @param age
    * @param id
    * @return
    */
   @Update("UPDATE user SET age = #{age} WHERE id = #{id}")
   int updateAgeById(int age, int id);

   /**
    * 删除数据
    *
    * @param id
    * @return
    */
   @Delete("DELETE FROM user WHERE id=#{id}")
   int delectById(int id);


   /**
    * @return
    */
   @Query("select u.realName from UserDO as u")
   String[] queryRealNames();

   /**
    * @return
    */
   @Query("select u.realName from UserDO as u")
   Optional<String[]> queryOptionalRealNames();

   /**
    * @return
    */
   @Query("select u.id from UserDO as u")
   int[] queryIntIds();

   /**
    * @return
    */
   @Query("select u.id from UserDO as u")
   Integer[] queryIntegerIds();


   /**
    * 自定义提供类处理
    * <p>
    * 通过自定义扩展，实现插入并返回自增ID
    *
    * @param name
    * @param age
    * @param create
    * @return
    */
   @Insert("INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (#{name},#{age}, now(),#{create})")
   @CustomProvider(type = TestProvider.class, methodName = "insertWithCustomprovider")
   int insertWithCustomprovider(String name, int age, String create);

   /**
    * 返回值是列表
    * CREATE  PROCEDURE `callList`()
    * BEGIN
    * SELECT * FROM user;
    * END
    *
    * @return
    */
   @CallStoredProcedure("call callList()")
   Optional<List<UserVO>> callList();

   /**
    * 通过出参返回单行数据
    * CREATE PROCEDURE `callOut`(IN id INT,OUT realName VARCHAR(15),OUT age INT(15))
    * BEGIN
    * SELECT real_name,user.`age` INTO realName,age FROM `user`  WHERE `user`.id=id;
    * END
    */
   @CallStoredProcedure(value = "call callOut(#{id},#realName,#{age})", out = {
           @CallStoredProcedure.Out(name = "realName"),
           @CallStoredProcedure.Out(name = "age", jdbcType = Types.INTEGER)
   })
   Optional<UserVO> callOut(@Param("id") int id);


   /**
    * 返回值是列表
    * CREATE  PROCEDURE `callList`()
    * BEGIN
    * SELECT * FROM user;
    * END
    *
    * @return
    */
   @CallStoredProcedure("call callList()")
   void callList1();

   /**
    * 通过出参返回单行数据
    * CREATE PROCEDURE `callOut`(IN id INT,OUT realName VARCHAR(15),OUT age INT(15))
    * BEGIN
    * SELECT real_name,user.`age` INTO realName,age FROM `user`  WHERE `user`.id=id;
    * END
    */
   @CallStoredProcedure(value = "call callOut(#{id},#realName,#{age})", out = {
           @CallStoredProcedure.Out(name = "realName"),
           @CallStoredProcedure.Out(name = "age", jdbcType = Types.INTEGER)
   })
   void callOut1(@Param("id") int id);


   @CallStoredProcedure("call callList()")
   List callList2();

   /**
    * 通过出参返回单行数据
    * CREATE PROCEDURE `callOut`(IN id INT,OUT realName VARCHAR(15),OUT age INT(15))
    * BEGIN
    * SELECT real_name,user.`age` INTO realName,age FROM `user`  WHERE `user`.id=id;
    * END
    */
   @CallStoredProcedure(value = "call callOut(#{id},#realName,#{age})", out = {
           @CallStoredProcedure.Out(name = "realName"),
           @CallStoredProcedure.Out(name = "age", jdbcType = Types.INTEGER)
   })
   Map callOut2(@Param("id") int id);


   /**
    * 插入
    *
    * @param name
    * @param ages
    * @param create
    * @return
    */
   @Insert(value = "INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (#{name},#{age}, now(),#{create})", loopFor = "age")
   int insertLoopForAge(String name, String create, @Param("age") List<Integer> ages);

   /**
    * 插入返回主键ID
    *
    * @param name
    * @param ages
    * @param create
    * @return
    */
   @Insert(value = "INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (#{name},#{age}, now(),#{create})", loopFor = "age", returnGeneratedKeys = true)
   List<Integer> insertLoopForAgeAndReturnId(String name, String create, @Param("age") List<Integer> ages);

   @Delete(value = "delete from user where age = #{object.test}", loopFor = "object")
   int deleteLoopForAge(@Param("object") List<NutMap> ages);
}



```

> ⚙️ 该接口支持：
> - 条件动态 SQL（`#[]`）
> - 实体映射（`@Entity`）
> - 返回类型自动适配（List、Map、Optional、Record）
> - 存储过程调用（`@CallStoredProcedure`）
> - 批量插入与自定义扩展（`@CustomProvider`）

---

## 🧪 测试类示例

```java
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
        List<UserDO> list = userDao.lambdaQuery().gte(UserDO::getAge, 17)
                .and(c -> c.gte(UserDO::getAge, 15).lte(UserDO::getAge, 40), c -> c.gte(UserDO::getId, 10))
                .list();
        assert list.size() == 1;
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

        userDao.fieldCalculation(userDO, "test");
        assert userDO.getUserDO() != null;
        assert userDO.getUserDO1() != null;

        userDao.fieldCalculation(userDO, "test2");
        assert userDO.getTest() == userDO.getId() + userDO.getAge();

        userDao.fieldCalculation(userDO);
        assert userDO.getUserDO3() != null;


    }


    @Test
    public void test_left_join_query() {
        List<UserDO> list = userDao.lambdaQuery()
                .selectAs(UserDO::getRealName, JobDO::getRealName)
                .leftJoin(JobDO.class, UserDO::getId, JobDO::getUserId)
                .like(UserDO.class, JobDO::getRealName, "测试")
                .groupBy(UserDO::getRealName)
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

}

```

> ✅ 自动建表、自动注入、事务回滚，全面验证增强功能的稳定性与一致性。

---

## 🧱 实体类定义示例

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
@IgnoreAutoDDL
public class UserDO extends BaseDO {

   @Id(auto = false)
   @AutoID
   @ColDefine(width = 9, type = ColType.INT)
   Integer id;

   @Column
   String realName;

   @Column
   Integer age;

   @FieldCalculation(groups = {"test"}, expression = "$ioc:filedCalcTestService.query($this)")
   UserDO userDO;

   @FieldCalculation(groups = {"test"}, order = 1, expression = "$ioc:filedCalcTestService.query($this)")
   UserDO userDO1;

   @FieldCalculation(groups = {"test2"}, order = 2, expression = "$this.age + $this.id")
   int test;

   @FieldCalculation(order = 1, expression = "$ioc:filedCalcTestService.query($this)")
   UserDO userDO3;
}
```

---

## ⚙️ 打包与发布

```bash
mvn clean package -P release
mvn clean install -P release
```

---

## 📚 附加说明

- 完全兼容原生 **NutzDao**
- 支持 **Spring Boot** 与 **Nutz Boot**
- 无侵入性设计，保留 Nutz 的灵活性与可扩展性
- 适合大规模企业项目配合统一 DAO 层规范与自动代码生成

---

## 🏁 结语

`nutz-dao-enhance` 致力于让 **NutzDao 更现代、更优雅、更易扩展**。  
通过注解、Lambda、动态 SQL 与自动化能力，大幅减少样板代码，让开发者聚焦于业务逻辑。

> 💬 欢迎贡献代码、提交 Issue 或 PR，一起完善这个项目。
