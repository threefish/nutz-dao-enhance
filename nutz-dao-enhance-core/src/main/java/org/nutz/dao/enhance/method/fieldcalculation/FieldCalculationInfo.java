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
     * 目标bean的名称
     */
    String fieldName;

    /**
     * 目标bean的名称
     */
    String beanName;


    /**
     * EL 表达式
     * 例如: $ioc.userService.findById($this.id)
     * 例如: $this.age + $this.name
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
    /**
     * 忽略返回对象的 Optional 包裹
     */
    boolean ignoreOptionalWrapper;
}
