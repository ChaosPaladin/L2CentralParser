package logic.lineage.model;

import java.util.ArrayList;

public class Skill extends AbstractModelEntity {
    private final int id2;
    private ArrayList<SkillEffect> effects = new ArrayList<SkillEffect>();

    private String description;

    public Skill(int id, int id2, String name) {
        super(id, name);

        this.id2 = id2;
    }

    public int getId() {
        return id;
    }

    public int getId2() {
        return id2;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addSkillEffect(SkillEffect skillEffect) {
        this.effects.add(skillEffect);
    }
}
