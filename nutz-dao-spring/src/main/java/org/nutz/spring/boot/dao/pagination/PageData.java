package org.nutz.spring.boot.dao.pagination;

import lombok.Data;
import org.nutz.dao.pager.Pager;

import java.util.List;

/**
 * @author 黄川 2020/12/16
 */
@Data
public class PageData<T> implements java.io.Serializable {
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

}
