# nutz-dao-enhance

## 增强NutzDao,无需写dao实现类也能完成数据库基础crud、复杂sql查询、存储过程调用。

1. @Entity(UserDO.class)可添加到Dao接口类上或放在方法上，该方法返回实体优先采用当前指定的class
2. 在开启自动建表的情况下，将@IgnoreAutoDDL添加到实体类上将忽略当前实体自动创建表和更新功能
3. 实现 AuditHandler 接口，可以在更新和插入数据时自动设置当前操作人(对应注解 @CreatedBy @LastModifiedBy)，同时也实现了@EntityListener 注解
4. 实现 IdentifierGenerator 接口，可以在插入时对添加了 @AutoID 字段进行赋值。如果搭配 @Id 注解，需要在 @Id(auto=false)时才生效
5. @CustomProvider 可以方便你扩展更多基础的功能 参考 org.nutz.dao.enhance.dao.BaseDao
6. # [] 语法使用介绍 例子： #[ and u.realName=@name and u.gmtCreate=@gmtCreate ] name或gmtCreate 入参不存在，则当前#[]中的全部条件都不生效，且会忽略这段sql
7. 查询预计中可以使用和java实体类名称如： UserDO as u ,会翻译为 user as u 而 u.realName 则会翻译为 u.real_name
8. 自定义SQL下可按参数循环执行sql语句（batch操作）
9. @FieldCalculation 字段计算注解可以方便的在查询结果下对查询对象进行计算（通过表达式赋值）
PS：3-4项的原理实际是采用NutzDao原生@PrevInsert @PrevUpdate搭配el表达式等方式来实现，原生功能更强大，请自由选择使用。

### lambda常用方法

```java
//lambda方式更新
userDao.lambdaUpdate().set(UserDO::getAge, 123).eq(UserDO::getAge, 15).update();
userDao.lambdaUpdate().eq(UserDO::getAge, 15).delete();
userDao.lambdaUpdate().set(UserDO::getAge, 250).insert();

//lambda方式查询
userDao.lambdaQuery().isNotNull(UserDO::getRealName).in(UserDO::getAge,Arrays.asList(15,16)).list();
userDao.lambdaQuery().isNull(UserDO::getRealName).one();
userDao.lambdaQuery().gte(UserDO::getAge, 17).count();
userDao.lambdaQuery().gte(UserDO::getAge, 17)
        .and(c -> c.gte(UserDO::getAge, 15).lte(UserDO::getAge, 40), c -> c.gte(UserDO::getId, 10))
        .list();

```

### 更多用法请查看 org.nutz.dao.enhance.SpringDaoTest

#### 自定义SQL操作 dao

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

#### 测试类

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

}




```

#### 实体类 entity

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
   /**
    * 字段计算功能，可按分组进行计算
    */
   @FieldCalculation(groups = {"test"}, expression = "$ioc:filedCalcTestService.query($me)")
   UserDO userDO;

   @FieldCalculation(groups = {"test"}, order = 1, expression = "$ioc:filedCalcTestService.query($me)")
   UserDO userDO1;

   @FieldCalculation(groups = {"test2"}, order = 2, expression = "$me.age + $me.id")
   int test;

   @FieldCalculation(order = 1, expression = "$ioc:filedCalcTestService.query($me)")
   UserDO userDO3;

}
```

### 打包

```
mvn clean package -P releasse
mvn clean install -P releasse
```
