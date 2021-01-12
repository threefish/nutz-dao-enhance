package org.nutz.spring.boot.dao.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/7/31
 */
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
