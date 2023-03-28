package org.nutz.dao.enhance.dao.lambda;

import org.nutz.trans.Molecule;

import java.util.function.Supplier;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/27
 */
public class FieldFilterMolecule<T> extends Molecule<T> {

    private final Supplier<T> supplier;

    public FieldFilterMolecule(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void run() {
        this.setObj(supplier.get());
    }
}
