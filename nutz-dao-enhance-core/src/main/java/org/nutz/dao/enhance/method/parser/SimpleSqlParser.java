package org.nutz.dao.enhance.method.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.enhance.method.holder.EntityClassInfoHolder;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 简单的sql解析
 */
@SuppressWarnings("ALL")
@Slf4j
public class SimpleSqlParser {
    private static final String WHITESPACE = " \n\r\f\t";
    private static final String ALL_COLUMN_KEY = "*";
    private static final Pattern CONDITION_PATTERN = Pattern.compile("#\\[.*?]");
    private static final Pattern CONDITION_PARAMETER_PATTERN = Pattern.compile("@[a-zA-Z0-9-.]+");
    private static final Pattern CONDITION_PARAMETER_PATTERN2 = Pattern.compile("@\\{[a-zA-Z0-9-.]+}");
    /**
     * 原始sql
     */
    @Getter
    private final String originalSql;
    @Getter
    private final Set<ColumnMapping> columns = new HashSet<>();
    @Getter
    private String sql;
    @Getter
    private Set<TableMapping> tables = new HashSet<>();
    @Getter
    private List<ConditionMapping> conditions = Collections.EMPTY_LIST;

    public SimpleSqlParser(String originalSql) {
        this.originalSql = originalSql;
        String[] strings = getTokens(originalSql);
        analyzeTableMapping(strings);
        analyzeConditionMapping(originalSql);
        for (ConditionMapping conditionMapping : this.conditions) {
            // 增加字段映射
            this.columns.addAll(conditionMapping.getColumns());
        }
        if (Strings.isBlank(this.sql)) {
            // sql 是空的，一定是未找到需要进行增强翻译的sql,直接返回原始sql
            this.sql = this.originalSql;
        } else {
            // 用占位符替换后的sql来再次分析字段
            final Set<ColumnMapping> columnMapping = getColumnMapping(getTokens(this.sql));
            if (Lang.isNotEmpty(columnMapping)) {
                this.columns.addAll(columnMapping);
            }
        }
    }


    /**
     * 解析,必须再运行时才能执行
     */
    public SimpleSqlParser parse() {
        this.replaceSql();
        this.replaceConditionSql();
        return this;
    }

    /**
     * 替换条件sql
     */
    private void replaceConditionSql() {
        for (ConditionMapping condition : this.conditions) {
            final Set<ColumnMapping> columns = condition.getColumns();
            String tempSql = condition.getOriginalCondition();
            for (ColumnMapping column : columns) {
                tempSql = replaceColumnSql(tempSql, column);
            }
            condition.setConditionSql(tempSql);
        }
    }

    /**
     * 替换SQL
     */
    private void replaceSql() {
        String tempSql = this.sql;
        for (TableMapping table : this.tables) {
            final Entity<?> entity = EntityClassInfoHolder.getEntity(table.getName());
            if (Objects.nonNull(entity)) {
                // 替换表名
                tempSql = tempSql.replaceAll(table.getName(), entity.getTableName());
            }
        }
        for (ColumnMapping columnMapping : this.columns) {
            tempSql = replaceColumnSql(tempSql, columnMapping);
        }
        this.sql = tempSql;
    }

    /**
     * 替换字段SQL
     *
     * @param tempSql
     * @param columnMapping
     */
    private String replaceColumnSql(String tempSql, ColumnMapping columnMapping) {
        final TableMapping table = columnMapping.getTable();
        final Entity<?> entity = EntityClassInfoHolder.getEntity(table.getName());
        if (Objects.nonNull(entity)) {
            final String fieldName = columnMapping.getFieldName();
            if (ALL_COLUMN_KEY.equals(fieldName)) {
                // 如果是查询全部
                final List<MappingField> mappingFields = entity.getMappingFields();
                StringJoiner joiner = new StringJoiner(",");
                mappingFields.forEach(mappingField -> {
                    final String columnName = mappingField.getColumnName();
                    joiner.add(String.format("%s.%s", table.getAliasName(), columnName));
                });
                return tempSql.replace(columnMapping.getName(), joiner.toString());
            }
            final MappingField field = entity.getField(columnMapping.getFieldName());
            if (Objects.nonNull(field)) {
                final String realColumnName = String.format("%s.%s", table.getAliasName(), field.getColumnName());
                if (!columnMapping.getName().equals(realColumnName)) {
                    return tempSql.replace(columnMapping.getName(), realColumnName);
                }
            }
        }
        return tempSql;
    }

    /**
     * 获取条件表达式
     *
     * @param sql
     */
    private void analyzeConditionMapping(String sql) {
        List<ConditionMapping> mappingList = new ArrayList<>();
        Matcher matcher = CONDITION_PATTERN.matcher(sql);
        int i = 0;
        String tempSql = sql;
        while (matcher.find()) {
            String key = String.format("$Condition%s$", i);
            int start = matcher.start();
            int end = matcher.end();
            final String token = sql.substring(start + 2, end - 1);
            final Set<ColumnMapping> columns = getColumnMapping(getTokens(token));
            mappingList.add(new ConditionMapping(key, columns, token, getConditionParameter(token)));
            tempSql = tempSql.replace(String.format("#[%s]", token), key);
            i++;
        }
        this.sql = tempSql;
        this.conditions = mappingList;
    }

    /**
     * 获取条件参数名
     *
     * @param part
     */
    private Set<String> getConditionParameter(String part) {
        Matcher matcher = CONDITION_PARAMETER_PATTERN.matcher(part);
        Set<String> strings = new HashSet<>();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            strings.add(part.substring(start + 1, end));
        }
        Matcher matcher2 = CONDITION_PARAMETER_PATTERN2.matcher(part);
        while (matcher2.find()) {
            int start = matcher2.start();
            int end = matcher2.end();
            strings.add(part.substring(start + 2, end - 1));
        }
        return strings;
    }

    /**
     * 分析sql字符串
     *
     * @param str
     */
    private String[] getTokens(String str) {
        String[] tokens = split(WHITESPACE + "(),", str, true);
        List<String> allTokens = new ArrayList<>();
        boolean inQuote = false;
        for (int i = 0, l = tokens.length; i < l; i++) {
            String token = tokens[i];
            if (isWhitespace(token)) {
                continue;
            } else if (isQuoteCharacter(token)) {
                inQuote = !inQuote;
                continue;
            } else if (isTokenStartWithAQuoteCharacter(token)) {
                if (!isTokenEndWithAQuoteCharacter(token)) {
                    inQuote = true;
                }
                continue;
            } else if (isTokenEndWithAQuoteCharacter(token)) {
                inQuote = false;
                continue;
            } else if (inQuote) {
                continue;
            }
            allTokens.add(token);
        }
        return allTokens.toArray(new String[0]);
    }

    /**
     * 获取字段信息
     *
     * @param allTokens
     */
    private Set<ColumnMapping> getColumnMapping(String[] allTokens) {
        Set<ColumnMapping> columnMappings = new HashSet<>();
        if (Lang.isNotEmpty(this.tables)) {
            for (TableMapping tableMapping : this.tables) {
                for (String token : allTokens) {
                    Pattern pattern = tableMapping.getPattern();
                    Matcher matcher = pattern.matcher(token);
                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        final String name = token.substring(start, end);
                        final String[] split = name.split("\\.");
                        columnMappings.add(new ColumnMapping(tableMapping, name, split[1]));
                    }
                }
            }
        }
        return columnMappings;
    }

    /**
     * 获取表信息
     *
     * @param tokens
     */
    private void analyzeTableMapping(String[] tokens) {
        Set<TableMapping> mappings = new HashSet<>();
        for (int i = 0, l = tokens.length; i < l; i++) {
            String token = tokens[i];
            if (isEntityJavaIdentifier(token)) {
                final String as = nextNonWhite(tokens, i).toLowerCase();
                if ("as".equals(as)) {
                    int aliasIndex = i + 1;
                    if (aliasIndex < l) {
                        String alias = nextNonWhite(tokens, aliasIndex).toLowerCase();
                        mappings.add(new TableMapping(token, alias));
                    }
                }
            }
        }
        this.tables = mappings;
    }

    /**
     * 获取下一个不为空的token
     *
     * @param tokens
     * @param start
     */
    private String nextNonWhite(String[] tokens, int start) {
        for (int i = start + 1; i < tokens.length; i++) {
            if (!isWhitespace(tokens[i])) {
                return tokens[i];
            }
        }
        return tokens[tokens.length - 1];
    }

    private boolean isQuoteCharacter(String token) {
        return "'".equals(token) || "\"".equals(token);
    }

    private boolean isTokenStartWithAQuoteCharacter(String token) {
        return token.startsWith("'") || token.startsWith("\"");
    }

    private boolean isTokenEndWithAQuoteCharacter(String token) {
        return token.endsWith("'") || token.endsWith("\"");
    }

    /**
     * 是java实体字段
     *
     * @param token
     */
    private boolean isEntityJavaIdentifier(String token) {
        // 字符串长度大于1
        return token.length() >= 2
                // 是Java标识符开始
                && Character.isJavaIdentifierStart(token.charAt(0))
                // 首字母必须大写
                && Character.isUpperCase(token.charAt(0))
                // 第二个字母必须是小写
                && Character.isLowerCase(token.charAt(1));
    }

    public String[] split(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer(list, separators, include);
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken();
        }
        return result;
    }

    public boolean isWhitespace(String str) {
        return WHITESPACE.contains(str);
    }


}
