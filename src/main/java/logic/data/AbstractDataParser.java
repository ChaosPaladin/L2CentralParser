package logic.data;

public abstract class AbstractDataParser<T> {
    protected T dataObject;

    public AbstractDataParser(T dataObject) {
        this.dataObject = dataObject;
    }

    public abstract void parse();
    public abstract <R> R getResult();
}
