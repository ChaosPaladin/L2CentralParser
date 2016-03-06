package logic.data.xml;

import logic.data.AbstractDataLoader;

import java.io.File;

public class XmlLoader extends AbstractDataLoader {
    private File[] files;

    @Override
    public void load() {
        File dir = new File(System.getProperty("user.dir") + "/data");
        if (!dir.exists()) {
            throw new Error("Error! Directory " + dir.getAbsolutePath() + " not exist");
        }
        this.files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".xml") || name.toLowerCase().endsWith(".cnf"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T)files;
    }
}
