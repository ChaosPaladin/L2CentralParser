package logic.data.xml.parsing.lineage;

import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import logic.lineage.holder.SkillHolder;
import logic.lineage.model.Skill;
import logic.lineage.model.SkillEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class SkillParser extends AbstractDataParser<File> {
    private Logger log = LoggerFactory.getLogger(getClass());

    public SkillParser(File file) {
        super(file);
    }

    @Override
    public void parse() {
        log.info("Parsing skills");

        long start = System.currentTimeMillis();
        int effectCount = 0;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(dataObject);
            document.getDocumentElement().normalize();
            NodeList skillNodeList = document.getElementsByTagName("data");

            for (int i = 0; i < skillNodeList.getLength(); ++i) {
                Node skillNode = skillNodeList.item(i);
                int id = 0, id2 = 0, level = 0, level2 = 0;
                String name = "", description = "";
                SkillEffect skillEffect = new SkillEffect();

                for (int x = 0; x < skillNode.getChildNodes().getLength(); ++x) {
                    Node skillDataNode = skillNode.getChildNodes().item(x);
                    switch (skillDataNode.getNodeName()) {
                        case "id1":
                            id = Integer.parseInt(skillDataNode.getTextContent());
                            break;
                        case "id2":
                            id2 = Integer.parseInt(skillDataNode.getTextContent());
                            break;
                        case "name":
                            name = skillDataNode.getTextContent();
                            break;
                        case "level1":
                            skillEffect.level = Integer.parseInt(skillDataNode.getTextContent());
                            break;
                        case "level2":
                            skillEffect.level2 = Integer.parseInt(skillDataNode.getTextContent());
                            break;
                        case "description":
                            description = skillDataNode.getTextContent();
                            break;
                        case "enchant_power":
                            skillEffect.enchantPower = skillDataNode.getTextContent();
                            break;
                        case "enchant_desc1":
                            skillEffect.enchantDescription = skillDataNode.getTextContent();
                            break;
                        case "enchant_val1":
                            skillEffect.enchantValue = skillDataNode.getTextContent();
                            break;
                        case "enchant_desc2":
                            skillEffect.enchantValueDescription = skillDataNode.getTextContent();
                            break;
                        case "enchant_val2":
                            skillEffect.enchantValue2 = skillDataNode.getTextContent();
                            break;
                    }
                }


                Skill skill = SkillHolder.getInstance().getSkill(id);
                if (skill == null) {
                    skill = new Skill(id, id2, name);
                }
                else {
                    ++effectCount;
                }
                skill.setDescription(description);
                skill.addSkillEffect(skillEffect);
                SkillHolder.getInstance().addSkill(skill);
            }
        }
        catch (Exception e) {
            log.warn("Error while parsing xml (" + getClass().getName() + "): ", e);
        }
        finally {
            long diff = System.currentTimeMillis() - start;
            log.info("Parsed " + SkillHolder.getInstance().getSkills().size() + " skills and " + effectCount + " effects with " + diff + "ms");
        }
    }

    @Override
    public <T> T getResult() {
        return null;
    }
}
