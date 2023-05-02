package org.nutz.dao.enhance.pagination;

import lombok.Data;
import org.nutz.dao.pager.Pager;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author 黄川 2020/12/16
 * QueryResult
 */
@Data
public class PageRecord<T> implements java.io.Serializable, Iterable {
    /**
     * 数据
     */
    private List<T> records;
    /**
     * 总数
     */
    private long total;
    /**
     * 当前的分页信息
     */
    private Pager pager;

    @Override
    public Iterator iterator() {
        return records.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        records.forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        return records.spliterator();
    }
}
