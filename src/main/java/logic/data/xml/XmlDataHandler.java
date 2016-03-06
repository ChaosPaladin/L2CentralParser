package logic.data.xml;

import logic.data.AbstractDataHandler;

import java.io.File;

public class XmlDataHandler extends AbstractDataHandler {
    private XmlLoader xmlLoader;

    @Override
    public void loadData() {
        xmlLoader = new XmlLoader();
        xmlLoader.load();
    }

    @Override
    public void handleData() {
        File[] files = xmlLoader.getResult();
        for (File file : files) {
            if (!file.getName().contains("SkillName"))
                continue;

            new SkillParser(file.getAbsolutePath()).parse();
        }
    }
}
