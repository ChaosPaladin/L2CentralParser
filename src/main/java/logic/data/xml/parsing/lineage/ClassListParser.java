package logic.data.xml.parsing.lineage;

import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import logic.lineage.holder.LClassHolder;
import logic.lineage.model.LClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class ClassListParser extends AbstractDataParser<File> {
    private Logger log = LoggerFactory.getLogger(getClass());

    public ClassListParser(File data) {
        super(data);
    }

    @Override
    public void parse() {
        log.info("Parsing classlist");
        long start = System.currentTimeMillis();

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataObject))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (Exception e) {
            log.warn("Error while parsing " + dataObject.getAbsolutePath(), e);
        }

        if (lines.isEmpty()) {
            log.warn("File " + dataObject.getAbsolutePath() + " is empty!");
            return;
        }

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("[")) {
                continue;
            }

            String[] data = line.split("=");
            LClassHolder.getInstance().addClass(new LClass(Integer.parseInt(data[0]), data[1], data[2]));
        }

        long diff = System.currentTimeMillis() - start;
        log.info("Parsed " + LClassHolder.getInstance().getClassList().size() + " lineage classes with " + diff + "ms");
    }

    @Override
    public <T> T getResult() {
        return null;
    }
}
