package org.nutz.dao.enhance.enhance;

import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.jdbc.NutPojo;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.SqlType;
import org.nutz.el.El;
import org.nutz.lang.util.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/3/18
 */
public class AuditElFieldMacro extends NutPojo {

    private static final long serialVersionUID = -6033506416935454704L;

    private El bin;

    private MappingField entityField;

    private boolean nullEffective;

    public AuditElFieldMacro(MappingField field, String str, boolean nullEffective) {
        this.entityField = field;
        this.bin = new El(str);
        this.nullEffective = nullEffective;
    }

    private AuditElFieldMacro() {
    }

    public SqlType getSqlType() {
        return SqlType.RUN;
    }

    public void onAfter(Connection conn, ResultSet rs, Statement stmt) {
        Context context = entityField.getEntity().wrapAsContext(getOperatingObject());
        context.set("field", entityField.getColumnName());
        context.set("view", entityField.getEntity());
        if (this.nullEffective) {
            if (entityField.getValue(getOperatingObject()) == null) {
                entityField.setValue(getOperatingObject(), bin.eval(context));
            }
        } else {
            entityField.setValue(getOperatingObject(), bin.eval(context));
        }
    }

    @Override
    public Pojo duplicate() {
        AuditElFieldMacro re = new AuditElFieldMacro();
        re.bin = bin;
        re.entityField = entityField;
        return re;
    }

    @Override
    public String forPrint() {
        return "// NOT SQL // ElFieldMacro=" + this.bin;
    }
}
