package logic.data.xml.parsing.lineage;

import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import logic.lineage.holder.NpcHolder;
import logic.lineage.model.Npc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class NpcParser extends AbstractDataParser<File> {
    private Logger log = LoggerFactory.getLogger(getClass());

    public NpcParser(File file) {
        super(file);
    }

    @Override
    public void parse() {
        log.info("Parsing npc(s)");
        long start = System.currentTimeMillis();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(dataObject);
            document.getDocumentElement().normalize();
            NodeList npcNodeList = document.getElementsByTagName("data");
            for (int i = 0; i < npcNodeList.getLength(); ++i) {
                Node npcDataList = npcNodeList.item(i);
                int id = 0;
                long rgb = 0;
                String name = "", description = "";
                for (int x = 0; x < npcDataList.getChildNodes().getLength(); ++x) {
                    Node npcDataNode = npcDataList.getChildNodes().item(x);
                    switch (npcDataNode.getNodeName()) {
                        case "id":
                            id = Integer.parseInt(npcDataNode.getTextContent());
                            break;
                        case "name":
                            name = npcDataNode.getTextContent();
                            break;
                        case "description":
                            description = npcDataNode.getTextContent();
                            break;
                        case "rgb":
                            rgb = Long.parseLong(npcDataNode.getTextContent());
                            break;
                    }
                }

                NpcHolder.getInstance().addNpc(new Npc(id, name, description, rgb));
            }
        }
        catch (Exception e) {
            log.warn("Error while parsing npc(s)", e);
        }
        finally {
            long diff = System.currentTimeMillis() - start;
            log.info("Parsed " + NpcHolder.getInstance().getNpcStore().size() + " npc(s) with " + diff + "ms");
        }
    }

    @Override
    public <T> T getResult() {
        return null;
    }
}
