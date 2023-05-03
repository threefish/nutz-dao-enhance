package org.nutz.dao.enhance.dao.join;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/3
 */
@AllArgsConstructor
@Getter
public enum JoinType {
    LEFT("LEFT"), RIGHT("RIGHT"), INNER("INNER");
    String value;
}
