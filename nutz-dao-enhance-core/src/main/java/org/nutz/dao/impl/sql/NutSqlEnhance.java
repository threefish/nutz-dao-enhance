package org.nutz.dao.impl.sql;

import org.nutz.dao.enhance.enhance.ElVarSet;
import org.nutz.dao.sql.SqlCallback;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 增强
 */
@SuppressWarnings("ALL")
public class NutSqlEnhance extends NutSql {

    public static final char PARAM_CHAR = '#';

    public static final char VAR_CHAR = '$';


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

    @Override
    protected SqlLiteral literal() {
        return new SqlLiteral(PARAM_CHAR, VAR_CHAR).valueOf(sourceSql);
    }
}
