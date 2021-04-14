package org.nutz.spring.boot.dao.spring.binding.helper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 简单的sql解析
 */
public class SqlSimpleParserHelper {

    public static final String WHITESPACE = " \n\r\f\t";

    /**
     * 解析
     *
     * @param sql
     * @return
     */
    public static Set<ColumnMapping> parseSql(String sql) {
        String[] strings = analyzeTokens(sql);
        return getColumnMapping(strings, getTableMapping(strings));
    }

    /**
     * 分析sql字符串
     * @param str
     * @return
     */
    private static String[] analyzeTokens(String str) {
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
     * @param allTokens
     * @param mappings
     * @return
     */
    private static Set<ColumnMapping> getColumnMapping(String[] allTokens, Set<TableMapping> mappings) {
        Set<ColumnMapping> columnMappings = new HashSet<>();
        for (TableMapping tableMapping : mappings) {
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
        return columnMappings;
    }

    /**
     * 获取表信息
     * @param tokens
     * @return
     */
    private static Set<TableMapping> getTableMapping(String[] tokens) {
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
        return mappings;
    }

    /**
     * 获取下一个不为空的token
     * @param tokens
     * @param start
     * @return
     */
    private static String nextNonWhite(String[] tokens, int start) {
        for (int i = start + 1; i < tokens.length; i++) {
            if (!isWhitespace(tokens[i])) {
                return tokens[i];
            }
        }
        return tokens[tokens.length - 1];
    }

    private static boolean isQuoteCharacter(String token) {
        return "'".equals(token) || "\"".equals(token);
    }

    private static boolean isTokenStartWithAQuoteCharacter(String token) {
        return token.startsWith("'") || token.startsWith("\"");
    }

    private static boolean isTokenEndWithAQuoteCharacter(String token) {
        return token.endsWith("'") || token.endsWith("\"");
    }

    /**
     * 是java实体字段
     * @param token
     * @return
     */
    private static boolean isEntityJavaIdentifier(String token) {
        // 字符串长度大于1
        return token.length() >= 2
                // 是Java标识符开始
                && Character.isJavaIdentifierStart(token.charAt(0))
                // 首字母必须大写
                && Character.isUpperCase(token.charAt(0))
                // 第二个字母必须是小写
                && Character.isLowerCase(token.charAt(1));
    }

    public static String[] split(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer(list, separators, include);
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken();
        }
        return result;
    }

    public static boolean isWhitespace(String str) {
        return WHITESPACE.contains(str);
    }


}
