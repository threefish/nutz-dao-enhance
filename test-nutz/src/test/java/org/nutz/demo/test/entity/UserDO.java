package org.nutz.demo.test.entity;

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
