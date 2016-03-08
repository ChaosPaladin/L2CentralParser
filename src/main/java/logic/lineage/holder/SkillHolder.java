package logic.lineage.holder;

import logic.lineage.model.Skill;

import java.util.HashMap;

public class SkillHolder {
    private static final SkillHolder instance = new SkillHolder();
    private HashMap<Integer, Skill> skills = new HashMap<Integer, Skill>();

    public static SkillHolder getInstance() {
        return instance;
    }

    public void addSkill(Skill skill) {
        skills.putIfAbsent(skill.getId(), skill);
    }

    public Skill getSkill(int id) {
        return skills.get(id);
    }

    public HashMap<Integer, Skill> getSkills() {
        return skills;
    }
}
