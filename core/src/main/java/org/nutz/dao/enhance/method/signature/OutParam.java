package org.nutz.dao.enhance.method.signature;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2023/3/15
 */
@Data
@AllArgsConstructor(staticName = "of")
public class OutParam {

    public final String name;

    public final int type;
}