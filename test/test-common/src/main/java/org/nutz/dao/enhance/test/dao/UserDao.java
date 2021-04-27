package org.nutz.dao.enhance.test.dao;

import org.nutz.dao.Condition;
import org.nutz.dao.enhance.annotation.*;
import org.nutz.dao.enhance.execute.BaseDao;
import org.nutz.dao.enhance.pagination.PageRecord;
import org.nutz.dao.enhance.test.entity.UserDO;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/30
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
