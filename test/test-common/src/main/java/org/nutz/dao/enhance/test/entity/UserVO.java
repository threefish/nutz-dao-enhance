package org.nutz.dao.enhance.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 *  2020/7/31
 * 在开启自动建表的情况下可以通过 @IgnoreAutoDDL 注解忽略自动创建表更新等
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements java.io.Serializable {
    Integer id;
    String realName;
    Integer age;
    Date gmtCreate;
    String createBy;
}
