package org.nutz.dao.enhance.dao.lambda;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/27
 */
@FunctionalInterface
public interface MoleculeSupplier<T> {

    T call();

}
