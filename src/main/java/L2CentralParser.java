import logic.data.central.CentralDataHandler;
import logic.data.xml.LDataHandler;
import org.apache.log4j.PropertyConfigurator;

public class L2CentralParser {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        LDataHandler lDataHandler = new LDataHandler();
        lDataHandler.loadData();
        lDataHandler.handleData();

        CentralDataHandler centralDataHandler = new CentralDataHandler();
        centralDataHandler.loadData();
        centralDataHandler.handleData();
    }
}
