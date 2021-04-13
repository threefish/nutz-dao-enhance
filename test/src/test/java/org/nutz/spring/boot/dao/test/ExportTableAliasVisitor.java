package org.nutz.spring.boot.dao.test;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class ExportTableAliasVisitor extends MySqlASTVisitorAdapter {

    private Map<String, String> aliasMap = new HashMap<String, String>();

    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();
        String tableName = x.getTableName();
        aliasMap.put(tableName, alias);
        return true;
    }

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }
}
