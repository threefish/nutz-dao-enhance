package org.nutz.spring.boot.dao.spring.binding.helper;

import lombok.Data;
import lombok.NonNull;

import java.util.regex.Pattern;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
public class TableMapping {

    String name;

    String aliasName;

    Pattern pattern;

    public TableMapping(@NonNull String name, @NonNull String aliasName) {
        this.name = name;
        this.aliasName = aliasName;
        this.pattern = Pattern.compile("(\\b|\\^=)" + aliasName + ".[A-Za-z0-9]+");
    }
}
