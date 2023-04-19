package org.nutz.dao.enhance.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.enhance.annotation.AutoID;
import org.nutz.dao.enhance.annotation.FieldCalculation;
import org.nutz.dao.enhance.annotation.IgnoreAutoDDL;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 * 在开启自动建表的情况下可以通过 @IgnoreAutoDDL 注解忽略自动创建表更新等
 */
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

    @FieldCalculation(groups = {"test"}, expression = "filedCalcTestService.query($me)")
    UserDO userDO;

    @FieldCalculation(groups = {"test"}, order = 1, expression = "filedCalcTestService.query($me)")
    UserDO userDO1;

    @FieldCalculation(groups = {"test1", "test2"}, order = 2, expression = "filedCalcTestService.query($me)")
    UserDO userDO2;

    @FieldCalculation(order = 1, expression = "filedCalcTestService.query($me)")
    UserDO userDO3;
}
