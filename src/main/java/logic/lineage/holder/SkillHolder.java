package logic.lineage.holder;

import logic.lineage.model.Skill;

import java.util.HashMap;

public class SkillHolder {
    private static final SkillHolder instance = new SkillHolder();
    private HashMap<Integer, Skill> skillStore = new HashMap<Integer, Skill>();

    public static SkillHolder getInstance() {
        return instance;
    }

    public void addSkill(Skill skill) {
        skillStore.putIfAbsent(skill.getId(), skill);
    }

    public Skill getSkill(int id) {
        return skillStore.get(id);
    }
}
