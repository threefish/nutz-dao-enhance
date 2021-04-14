package org.nutz.spring.boot.dao.spring.binding.helper;

import lombok.Data;

import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
public class ConditionMapping {
    /**
     * 占位符
     */
    String key;
    /**
     * 这里面的字段如果没有值或为空则条件不成立
     */
    Set<ColumnMapping> columns;
    /**
     * 条件表达式
     */
    String originalCondition;
    /**
     * 条件sql
     */
    String conditionSql;
    /**
     * 入参字段
     */
    Set<String> conditionParameter;

    public ConditionMapping(String key, Set<ColumnMapping> columns, String originalCondition, Set<String> conditionParameter) {
        this.key = key;
        this.columns = columns;
        this.originalCondition = originalCondition;
        this.conditionParameter = conditionParameter;
    }
}
