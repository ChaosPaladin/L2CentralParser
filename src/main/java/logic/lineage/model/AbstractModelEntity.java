package logic.lineage.model;

public abstract class AbstractModelEntity {
    protected final int id;
    protected final String name;

    public AbstractModelEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
