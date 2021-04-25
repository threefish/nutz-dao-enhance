package org.nutz.dao.enhance.execute;

/**
 * @author 黄川 2020/12/16
 */
public interface Execute {
    /**
     * 真正执行操作
     *
     * @return
     */
    Object invoke();
}
