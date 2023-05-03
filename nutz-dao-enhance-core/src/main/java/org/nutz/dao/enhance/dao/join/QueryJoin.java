package org.nutz.dao.enhance.dao.join;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/3
 */
@Data
@AllArgsConstructor(staticName = "of")
public class QueryJoin {

    JoinType type;

    Class<?> clazz;

    String mainFiled;

    String joinField;
}
