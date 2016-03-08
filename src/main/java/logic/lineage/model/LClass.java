package logic.lineage.model;

public class LClass extends AbstractModelEntity {
    private String russianName;

    public LClass(int id, String name) {
        super(id, name);
    }

    public LClass(int id, String name, String russianName) {
        super(id, name);
        this.russianName = russianName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRussianName() {
        return russianName;
    }
}
