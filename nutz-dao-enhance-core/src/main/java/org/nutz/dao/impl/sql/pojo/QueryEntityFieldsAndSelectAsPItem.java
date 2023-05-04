package org.nutz.dao.impl.sql.pojo;

import lombok.AllArgsConstructor;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.enhance.dao.join.SelectAsColumn;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.lang.Lang;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2023/5/4
 */
@SuppressWarnings("all")
@AllArgsConstructor
public class QueryEntityFieldsAndSelectAsPItem extends QueryEntityFieldsPItem {

    private static final long serialVersionUID = 1L;

    private final List<SelectAsColumn> selectAsColumns;

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (Lang.isEmpty(selectAsColumns)) {
            super.joinSql(en, sb);
            return;
        }
        FieldMatcher fm = getFieldMatcher();
        List<MappingField> efs = _en(en).getMappingFields();
        int old = sb.length();
        Map<String, String> selectAsColumnsMap = selectAsColumns.stream().collect(Collectors.toMap(SelectAsColumn::getMainFieldName, SelectAsColumn::getJoinFieldName));
        for (MappingField ef : efs) {
            if (fm == null || fm.match(ef.getName())) {
                String asName = selectAsColumnsMap.get(ef.getName());
                if (asName == null) {
                    sb.append(String.format("%s.%s,", en.getTableName(), ef.getColumnNameInSql()));
                } else {
                    sb.append(String.format("%s as %s,", asName, ef.getColumnNameInSql()));
                }
            }
        }

        if (sb.length() == old) {
            throw Lang.makeThrow("No columns be queryed: '%s'", _en(en));
        }

        sb.setCharAt(sb.length() - 1, ' ');
    }

}

