package logic.lineage.model;

public class Npc extends AbstractModelEntity {
    private final String description;
    private final long rgb;

    public Npc(int id, String name, String description, long rgb) {
        super(id, name);

        this.description = description;
        this.rgb = rgb;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getRgb() {
        return rgb;
    }
}
