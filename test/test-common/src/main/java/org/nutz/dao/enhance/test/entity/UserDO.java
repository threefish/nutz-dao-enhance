package org.nutz.dao.enhance.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
// 在开启自动建表的情况下可以通过这个注解忽略自动创建表更新等，
//@IgnoreAutoDDL
public class UserDO implements java.io.Serializable {
    @Id
    Integer id;
    @Column
    String realName;
    @Column
    Integer age;
    @Column
    Date gmtCreate;
    @Column
    String createBy;
}
