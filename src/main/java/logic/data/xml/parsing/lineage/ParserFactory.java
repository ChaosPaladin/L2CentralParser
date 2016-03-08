package logic.data.xml.parsing.lineage;

import logic.data.AbstractDataParser;

import java.io.File;

public class ParserFactory {
    public static AbstractDataParser getParser(File file) {
        String parserName;
        if (file.getName().endsWith(".xml")) {
            parserName = file.getName().split("-")[0];
        } else {
            parserName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        }

        switch (parserName.toLowerCase()) {
            case "npcname":
                return new NpcParser(file);
            case "skillname":
                return new SkillParser(file);
            case "classlist":
                return new ClassListParser(file);
        }

        return new LDataParser(file);
    }
}
