package org.nutz.dao.enhance.el;

import lombok.extern.slf4j.Slf4j;
import org.nutz.el.opt.RunMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/18
 */
@Slf4j
public class NowDateRunMethod implements RunMethod {

    public static final String NAME = "nowDate";
    public static final String FUN_NAME = NAME + "('%s')";

    private static final Map<String, Supplier<?>> SUPPLIER_MAP = new HashMap() {
        {
            put("java.time.LocalDateTime", (Supplier<LocalDateTime>) () -> LocalDateTime.now());
            put("java.time.LocalDate", (Supplier<LocalDate>) () -> LocalDate.now());
            put("java.time.LocalTime", (Supplier<LocalTime>) () -> LocalTime.now());
            put("java.util.Date", (Supplier<java.util.Date>) () -> new java.util.Date());
            put("java.sql.Date", (Supplier<java.sql.Date>) () -> new java.sql.Date(System.currentTimeMillis()));
            put("java.sql.Timestamp", (Supplier<java.sql.Timestamp>) () -> new java.sql.Timestamp(System.currentTimeMillis()));
            put("java.lang.Long", (Supplier<java.lang.Long>) () -> System.currentTimeMillis());
        }
    };

    @Override
    public Object run(List<Object> fetchParam) {
        String targetFieldName = (String) fetchParam.get(0);
        return SUPPLIER_MAP.get(targetFieldName).get();
    }

    @Override
    public String fetchSelf() {
        return NAME;
    }
}
