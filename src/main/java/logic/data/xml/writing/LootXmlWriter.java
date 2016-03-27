package logic.data.xml.writing;

import logic.lineage.model.Loot;
import logic.lineage.model.Npc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LootXmlWriter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private File dirPath;

    public LootXmlWriter() {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        dirPath = new File(System.getProperty("user.dir") + "/out/loot_data_" + dateFormat.format(date));
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    public void write(Npc npc, Loot[] lootData) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            File outputFile = new File(dirPath + "/loot_" + npc.getId() + ".xml");

            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element rootElement = document.createElement("list");
            document.appendChild(rootElement);

            Comment npcComment = document.createComment(npc.getName());
            Element lootElement = document.createElement("loot");
            lootElement.setAttribute("id", Integer.toString(npc.getId()));
            rootElement.appendChild(npcComment);
            rootElement.appendChild(lootElement);

            Element rewardListNode = document.createElement("database_rewardlist");
            lootElement.appendChild(rewardListNode);

            for (Loot loot : lootData) {
                Comment lootComment = document.createComment(loot.name + " (" + loot.russianName + ")");
                Element lootNode = document.createElement("reward");
                lootNode.setAttribute("chance", Float.toString(loot.getAverageChance()));
                lootNode.setAttribute("item_id", Integer.toString(loot.itemId));
                lootNode.setAttribute("max", Integer.toString(loot.max));
                lootNode.setAttribute("min", Integer.toString(loot.min));
                rewardListNode.appendChild(lootComment);
                rewardListNode.appendChild(lootNode);
            }

            // write the content into xml file
            StringWriter outputXmlStringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(document), new StreamResult(outputXmlStringWriter));
            // comments should be at the same line that npc id so do it
            String outputXmlString = outputXmlStringWriter.toString().replaceAll("\r\n    <!--", "<!--");;
            // save file
            FileOutputStream outputXml = new FileOutputStream(outputFile);
            outputXml.write(outputXmlString.getBytes("UTF-8"));
        }
        catch (Exception e) {
            log.warn("Error while writing xml", e);
        }
    }
}
