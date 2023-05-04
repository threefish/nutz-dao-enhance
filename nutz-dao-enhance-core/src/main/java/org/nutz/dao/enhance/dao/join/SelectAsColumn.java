package org.nutz.dao.enhance.dao.join;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/4
 */
@Data
@AllArgsConstructor(staticName = "of")
public class SelectAsColumn {

    String mainFieldName;
    String joinFieldName;
}
