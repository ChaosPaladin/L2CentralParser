package logic.data;

public abstract class AbstractDataParser {
    protected String path;

    public AbstractDataParser(String path) {
        this.path = path;
    }

    public abstract void parse();
    public abstract <T extends AbstractDataResult> T getResult();
}
