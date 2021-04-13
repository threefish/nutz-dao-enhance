package org.nutz.spring.boot.dao.test;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.foundationdb.sql.StandardException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class Test {

    public static void main(String[] args) throws StandardException {
        String oldSql = "select * from UserDO as u where u.realName=@name and u.id=?  group by u.name order by u.id desc";
        SQLStatementParser parser = new SQLStatementParser(oldSql, DbType.mysql);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();

        ExportTableAliasVisitor exportTableAliasVisitor = new ExportTableAliasVisitor();

        final List<SQLStatement> sqlStatements = parser.parseStatementList();
        for (SQLStatement sqlStatement : sqlStatements) {

            sqlStatement.accept(exportTableAliasVisitor);

            sqlStatement.accept(visitor);
        }


        final Map<String, String> aliasMap = exportTableAliasVisitor.getAliasMap();

        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
            String tableName = entry.getKey();
            String alias = entry.getValue();
            System.out.println("tableName:["+tableName+"] alias:["+alias+"]");
        }


        final Collection<TableStat.Column> columns = visitor.getColumns();
        for (TableStat.Column column : columns) {
            String name = column.getName();
            System.out.println("table:" + column.getTable() + " fullname:" + column.getFullName() + " name:" + name);
        }
    }

    /**
     * 修改表名
     */
    public void refactorTableName() {
        String oldSql = "select * from UserDO as u where u.realName=@name and u.id=?  group by u.name order by u.id desc";
        Map<String, String> map = new HashMap<>();
        map.put("UserDO", "test");
        String newSql = SQLUtils.refactor(oldSql, DbType.mysql, map);
        System.out.println(newSql);
    }
}
