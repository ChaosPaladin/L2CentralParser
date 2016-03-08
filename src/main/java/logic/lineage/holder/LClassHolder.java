package logic.lineage.holder;

import logic.lineage.model.LClass;

import java.util.HashMap;

public class LClassHolder {
    private static final LClassHolder instance = new LClassHolder();
    private HashMap<Integer, LClass> classList = new HashMap<>();

    public static LClassHolder getInstance() {
        return instance;
    }

    public void addClass(LClass lclass) {
         classList.put(lclass.getId(), lclass);
    }

    public LClass getlClass(int id) {
        return classList.get(id);
    }

    public HashMap<Integer, LClass> getClassList() {
        return classList;
    }
}
