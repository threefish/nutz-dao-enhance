package org.nutz.dao.enhance.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.enhance.annotation.AutoID;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/7/31
 * 在开启自动建表的情况下可以通过 @IgnoreAutoDDL 注解忽略自动创建表更新等
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
public class UserDO extends BaseDO {
    @Id(auto = false)
    @AutoID
    Integer id;
    @Column
    String realName;
    @Column
    Integer age;
}
