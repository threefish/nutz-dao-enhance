package org.nutz.dao.enhance.el;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.enhance.incrementer.IdentifierGenerator;
import org.nutz.el.opt.RunMethod;

import java.util.List;


/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/19
 * 审计实体运行方法
 */
@Slf4j
public class IdentifierGeneratorRunMethod implements RunMethod {

    public static final String NAME = "autoGeneratorID";
    public static final String FUN_NAME = NAME + "($this)";

    private final IdentifierGenerator identifierGenerator;

    public IdentifierGeneratorRunMethod(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
        if (log.isInfoEnabled()) {
            log.info("Init IdentifierGeneratorRunMethod:'{}'", identifierGenerator);
        }
        if (this.identifierGenerator == null) {
            if (log.isWarnEnabled()) {
                log.warn("identifierGenerator is null,Unable to generate id using autoid!!!");
            }
        }
    }

    @Override
    public Object run(List<Object> fetchParam) {
        if (identifierGenerator == null) {
            return null;
        }
        return identifierGenerator.nextId(fetchParam.get(0));
    }

    @Override
    public String fetchSelf() {
        return NAME;
    }
}
