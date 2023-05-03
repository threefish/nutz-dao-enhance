package org.nutz.dao.enhance.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.enhance.annotation.AutoID;
import org.nutz.dao.enhance.annotation.FieldCalculation;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2020/7/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("job")
public class JobDO extends BaseDO {

    @Id(auto = false)
    @AutoID
    @ColDefine(width = 9, type = ColType.INT)
    Integer id;

    @Column
    @ColDefine(width = 9, type = ColType.INT)
    Integer userId;

    @Column
    String realName;

}
