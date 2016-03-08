package logic.data.xml.parsing.lineage;

import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LDataParser extends AbstractDataParser<File> {
    private Logger log = LoggerFactory.getLogger(getClass());

    public LDataParser(File file) {
        super(file);
    }

    @Override
    public void parse() {
        log.warn("Parser for file " + dataObject.getAbsolutePath() + " not found");
    }

    @Override
    public <T> T getResult() {
        return null;
    }
}
