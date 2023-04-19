package org.nutz.dao.enhance.method.fieldcalculation;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/4/19
 */
@AllArgsConstructor(staticName = "of")
@Data
public class FieldCalculationInfo {

    /**
     * EL 表达式
     * 例如: $ioc.userService.findById($me.id)
     * 例如: $me.age + $me.name
     */
    String expression;

    /**
     * 顺序 越小越先执行
     */
    int order;

    /**
     * 分组计算
     */
    String group;
}
