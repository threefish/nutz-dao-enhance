package org.nutz.dao.enhance.test;

import org.nutz.dao.enhance.incrementer.IdentifierGenerator;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/19
 */
@IocBean
public class TestAutoIdentifierGenerator implements IdentifierGenerator {

    AtomicInteger counter = new AtomicInteger();

    @Override
    public Number nextId(Object entity) {
        return counter.incrementAndGet();
    }
}
