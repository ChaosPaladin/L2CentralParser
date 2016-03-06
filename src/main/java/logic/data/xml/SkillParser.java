package logic.data.xml;

import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import logic.lineage.holder.SkillHolder;
import logic.lineage.model.Skill;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SkillParser extends AbstractDataParser {
    public SkillParser(String path) {
        super(path);
    }

    @Override
    public void parse() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = dbFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(path);
            document.getDocumentElement().normalize();
            NodeList skillNodeList = document.getElementsByTagName("data");

            for (int i = 0; i < skillNodeList.getLength(); ++i) {
                Node skillNode = skillNodeList.item(i);
                int id = 0, id2 = 0;
                String name = "";
                for (int x = 0; x < skillNode.getChildNodes().getLength(); ++x) {
                    Node skillDataNode = skillNode.getChildNodes().item(x);
                    if (skillDataNode.getNodeName().equals("id1")) {
                        id = Integer.parseInt(skillDataNode.getTextContent());
                    }
                    else if (skillDataNode.getNodeName().equals("id2")) {
                        id2 = Integer.parseInt(skillDataNode.getTextContent());
                    }
                    else if (skillDataNode.getNodeName().equals("name")) {
                        name = skillDataNode.getTextContent();
                    }
                }
                SkillHolder.getInstance().addSkill(new Skill(id, id2, name));
            }
        }
        catch (Exception e) {
            System.out.println("Error while parsing xml (" + getClass().getName() + "): " + e.getMessage());
        }

    }

    @Override
    public <T extends AbstractDataResult> T getResult() {
        return null;
    }
}
