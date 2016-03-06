import logic.data.xml.XmlDataHandler;

public class L2CentralParser {
    public static void main(String[] args) {
        XmlDataHandler xmlDataHandler = new  XmlDataHandler();
        xmlDataHandler.loadData();
        xmlDataHandler.handleData();
    }
}
