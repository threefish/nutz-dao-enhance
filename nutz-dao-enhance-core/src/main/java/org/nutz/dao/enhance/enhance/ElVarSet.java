package org.nutz.dao.enhance.enhance;

import org.nutz.dao.sql.VarSet;
import org.nutz.el.El;
import org.nutz.lang.Lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class ElVarSet implements VarSet {

    public static final String DOT = ".";
    private static final long serialVersionUID = 1L;
    private final HashMap<String, Object> map;
    /**
     * 缓存值
     */
    private Object elValCatche;

    private boolean init = false;

    public ElVarSet() {
        this.map = new HashMap<>();
    }

    @Override
    public VarSet set(String name, Object value) {
        map.put(name, value);
        return this;
    }

    @Override
    public Object get(String name) {
        // 变量中存在 . 符合才会当el表达式来计算
        if (name.indexOf(DOT) > -1) {
            return getElValCatche(name);
        }
        return map.get(name);
    }

    private Object getElValCatche(String name) {
        if (init == false) {
            // 计算值
            this.elValCatche = El.eval(Lang.context(this.map), name);
            this.init = true;
        }
        // 返回缓存值
        return this.elValCatche;
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public VarSet putAll(Map<String, Object> map) {
        if (map != null) {
            this.map.putAll(map);
        }
        return this;
    }

    @Override
    public VarSet putAll(Object pojo) {
        if (pojo != null) {
            Map<String, Object> pojoMap = Lang.obj2map(pojo);
            this.map.putAll(pojoMap);
        }
        return this;
    }

    @Override
    public int size() {
        return map.size();
    }
}
