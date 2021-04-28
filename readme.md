# nutz-dao-enhance

通过动态代理实现，不需要实现类就可以操作数据库，参考Jpa

#### 测试类

```java

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
public class DaoTest {

    @Autowired
    UserMapper userMapper;

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

```

#### 实体类 entity

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
@Dao
@Component
public interface UserMapper extends BaseDao<UserDO> {

    /**
     * gmtCreate 入参不存在，所以当前#[]中的全部条件不生效
     * 输出SQL：select u.id,u.real_name,u.age,u.gmt_create,u.create_by from user as u where 1=1   and u.real_name='测试11'
     *
     * @param name
     * @return
     */
    @Query("select u.* from UserDO as u where 1=1" +
            "#[ and u.realName=@name and u.gmtCreate=@gmtCreate ] " +
            "#[ and u.realName=@name ] ")
    UserDO queryByCndHql(String name);

    /**
     * 查询2
     * 输出SQL： select u.id,u.real_name,u.age,u.gmt_create,u.create_by from user as u where 1=1  and u.real_name='测试11'
     *
     * @param user
     */
    @Query("select u.* from UserDO as u where 1=1 " +
            "#[ and u.realName=@{user.realName} ] ")
    UserDO queryByVoHql(UserDO user);

    /**
     * 返回当前实体类
     *
     * @param id
     * @return
     */
    @Query("select * from user where id=@id")
    UserDO queryUserById(int id);

    /**
     * 返回当前实体类
     *
     * @param id
     * @return
     */
    @Query("select * from user where id=@id")
    Optional<UserDO> queryOptionalUserById(int id);

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
    @Query("select * from user where id=@id")
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
    @Query("select * from user where id=@id")
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
    @Query("select * from user")
    PageRecord listUserPage(Pager pager);

    /**
     * 插入获取自增ID
     *
     * @param name
     * @param age
     * @param create
     * @return
     */
    @Insert("INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (@name,@age, now(),@create)")
    int insert(String name, int age, String create);

    /**
     * 插入
     *
     * @param name
     * @param age
     * @param create
     */
    @Insert("INSERT INTO user(`real_name`, `age`,`gmt_create`,`create_by`) VALUES (@name,@age, now(),@create)")
    void insertVoid(String name, int age, String create);

    /**
     * 更新数据
     *
     * @param age
     * @param id
     * @return
     */
    @Update("UPDATE user SET age = @age WHERE id = @id")
    int updateAgeById(int age, int id);

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM user WHERE id=@id")
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


}

```
