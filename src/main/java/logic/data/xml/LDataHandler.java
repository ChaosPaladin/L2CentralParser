package logic.data.xml;

import logic.data.AbstractDataHandler;
import logic.data.xml.parsing.lineage.ParserFactory;

import java.io.File;

public class LDataHandler extends AbstractDataHandler {
    private LDataLoader ldataLoader;

    @Override
    public void loadData() {
        ldataLoader = new LDataLoader();
        ldataLoader.load();
    }

    @Override
    public void handleData() {
        File[] files = ldataLoader.getResult();
        for (File file : files) {
            ParserFactory.getParser(file).parse();
        }
    }
}
