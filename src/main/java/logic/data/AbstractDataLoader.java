package logic.data;

public abstract class AbstractDataLoader {
    public abstract void load();
    public abstract <T> T getResult();
}
