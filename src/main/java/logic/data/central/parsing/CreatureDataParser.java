package logic.data.central.parsing;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import logic.data.AbstractDataParser;
import logic.data.AbstractDataResult;
import logic.data.central.CentralInfo;
import logic.lineage.holder.LootHolder;
import logic.lineage.model.Loot;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CreatureDataParser extends AbstractDataParser<String> {
    private static final String[] creatureParams = { "EnglishName", "Level", "HP", "MP", "EXP", "SP", "RP" };
    private static final Map<String, Loot.Type> lootTypes =
            ImmutableMap.of("Целые предметы", Loot.Type.Drop, "Другие предметы", Loot.Type.Other, "Предметы и ресурсы", Loot.Type.Resources);
    private Logger log = LoggerFactory.getLogger(getClass());
    private CentralInfo.DataType type;
    private HashMap<String, String> creatureData = new HashMap<>();
    private Loot[] fullLoot;

    public CreatureDataParser(String data, CentralInfo.DataType type) {
        super(data);

        this.type = type;
    }

    @Override
    public void parse() {
        Document document = Jsoup.parse(dataObject);
        parseCreature(document);
    }

    public void parseCreature(Document document) {
        // parse npc info
        parseCreatureData(document);
        parseCreatureLoot(document);
    }

    private void parseCreatureData(Document document) {
        Elements elements = document.getElementById("npc_brief_info_3").select("tbody").select("td");
        int x = 0;
        for (int i = 1; i < elements.size(); i += 2) {
            creatureData.put(creatureParams[x++], elements.get(i).text());

            if (creatureParams.length == x) {
                break;
            }
        }
    }

    private void parseCreatureLoot(Document document) {
        log.info("Parsing loot");
        Elements tables = document.getElementsByClass("quests");
        for (Element table : tables) {
            if (!table.hasAttr("width")) {
                continue;
            }

            if (!table.attr("width").equals("750")) {
                continue;
            }

            // ok its our boy
            Loot.Type tableType = Loot.Type.Drop;
            String tableName = table.select("th").get(0).text();
            for (Map.Entry<String, Loot.Type> lootEntry : lootTypes.entrySet()) {
                if (!lootEntry.getKey().equals(tableName))
                    continue;

                tableType = lootEntry.getValue();
                break;
            }

            Loot[] loot = parseCreatureLootTemplate(table.select("tr"), tableType);
            if (fullLoot == null) {
                fullLoot = loot;
            }
            else {
                fullLoot = ArrayUtils.addAll(fullLoot, loot);
            }
        }

        log.info("Done");
    }

    private Loot[] parseCreatureLootTemplate(Elements tableElements, Loot.Type type) {
        Loot[] fullLoot = new Loot[tableElements.size() - 1];
        for (int x = 0; x < tableElements.size(); ++x) {
            Element tableElement = tableElements.get(x);
            if (tableElement.text().contains("Целые предметы") ||
                tableElement.text().contains("Другие предметы") ||
                tableElement.text().contains("Предметы и ресурсы"))
            {
                continue;
            }

            Loot loot = new Loot();
            Elements lootParams = tableElement.select("td");
            for (int i = 0; i < lootParams.size(); ++i) {
                Element lootParam = lootParams.get(i);

                switch (i) {
                    // item id and name
                    case 0:
                        String itemLink = lootParam.select("a").get(0).attr("href");
                        itemLink = itemLink.substring(itemLink.lastIndexOf('_') + 1, itemLink.length());
                        itemLink = itemLink.substring(0, itemLink.lastIndexOf('.'));
                        loot.russianName = lootParam.select("a").get(1).text();
                        lootParam.select("a").remove();
                        if (!lootParam.text().isEmpty()) {
                            loot.name = lootParam.text().substring(1, lootParam.text().length() - 1);
                        }
                        else {
                            loot.name = "";
                        }
                        loot.itemId = Integer.parseInt(itemLink);
                        break;
                    // item loot count
                    case 1:
                        String[] data = CharMatcher.inRange('0', '9').or(CharMatcher.is(' ')).retainFrom(lootParam.text()).trim().
                            replace("  ", " ").split(" ");

                        if (data.length == 1) {
                            loot.min = Integer.parseInt(data[0]);
                            loot.max = loot.min;
                        }
                        else {
                            loot.min = Integer.parseInt(data[0]);
                            loot.max = Integer.parseInt(data[1]);
                        }
                        break;
                    // item loot chance
                    case 2:
                        data = CharMatcher.inRange('0', '9').or(CharMatcher.is(' ')).or(CharMatcher.is('.')).
                                retainFrom(lootParam.text()).replace("  ", " ").trim().split(" ");

                        if (data.length == 1) {
                            loot.minChance = Float.parseFloat(data[0]);
                            loot.maxChance = loot.minChance;
                        }
                        else {
                            loot.minChance = Float.parseFloat(data[0]);
                            loot.maxChance = Float.parseFloat(data[1]);
                        }
                        break;
                }
            }

            loot.type = type;
            fullLoot[x-1] = loot;
            log.info("Item (" + loot.russianName + ": " + loot.itemId + ") " + (x) + " of " + (tableElements.size() - 1) + " parsed. Group: " + type.toString());
        }

        return fullLoot;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T)fullLoot;
    }
}
