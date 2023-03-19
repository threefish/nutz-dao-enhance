package org.nutz.dao.enhance.method.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
@AllArgsConstructor
public class ColumnMapping {

    TableMapping table;

    String name;

    String fieldName;
}
