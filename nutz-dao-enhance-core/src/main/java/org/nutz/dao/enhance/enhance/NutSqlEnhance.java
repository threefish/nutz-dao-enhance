package org.nutz.dao.enhance.enhance;

import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.SqlCallback;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 增强
 */
@SuppressWarnings("ALL")
public class NutSqlEnhance extends NutSql {

    public NutSqlEnhance() {
        super(null, null);
        this.params = new ElVarSet();
        this.rows.clear();
        this.rows.add(params);
    }


    public NutSqlEnhance(String source) {
        super(source, null);
        this.params = new ElVarSet();
        this.rows.clear();
        this.rows.add(params);
    }

    public NutSqlEnhance(String source, SqlCallback callback) {
        super(source, callback);
        this.params = new ElVarSet();
        this.rows.clear();
        this.rows.add(params);
    }

    @Override
    public void addBatch() {
        params = new ElVarSet();
        rows.add(params);
    }

    @Override
    public void clearBatch() {
        params = new ElVarSet();
        rows.clear();
        rows.add(params);
    }
}
