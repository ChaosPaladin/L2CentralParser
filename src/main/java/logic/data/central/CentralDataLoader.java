package logic.data.central;

import logic.data.AbstractDataLoader;
import logic.lineage.holder.NpcHolder;
import logic.lineage.model.Npc;
import logic.util.LUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;

public class CentralDataLoader extends AbstractDataLoader {
    public static String fileToParse = "npc_to_parse.txt";

    private Logger log = LoggerFactory.getLogger(getClass());
    private HashMap<Npc, String> result = new HashMap<>();

    @Override
    public void load() {
        log.info("Disabling SSL checks");
        try {
            LUtil.disableSSLCertCheck();
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn("Error while disabling SSL checks", e);
        }

        log.info("Loading npcs to parse");
        Npc[] npcList = getNpcsToParse();
        if (npcList == null) {
            return;
        }

        HashSet<Npc> npcSet = new HashSet<>();
        for (Npc npc : npcList) {
            if (npcSet.stream().anyMatch(n -> n.getName().equals(npc.getName()))) {
                log.warn("Found duplicate (name: " + npc.getName() + "), confusing what id should use?");
                continue;
            }

            npcSet.add(npc);
        }

        log.info(npcSet.size() + " npc(s) to parse");
        npcSet.stream().forEach(n -> log.info(n.getName() + ":" + n.getDescription() + "; "));
        for (Npc npc : npcSet) {
            Document document = loadSite(CentralInfo.CREATURE_LINK + npc.getName());
            result.put(npc, document.body().outerHtml());
        }
    }

    public Document loadSite(String link) {
        Document document = null;
        try {
            log.info("Loading page " + link);
            document = Jsoup.connect(link).timeout(50 * 1000).get();
        }
        catch (Exception e) {
            log.warn("Error while loading " + link, e);
        }
        log.info("Done");
        return document;
    }

    public Npc[] getNpcsToParse() {
        String text = "";
        try (BufferedReader br = new BufferedReader(new FileReader(CentralDataLoader.fileToParse))) {
            text = br.readLine();
        }
        catch (Exception e) {
            log.warn("Error while opening file '" + CentralDataLoader.fileToParse + "', nothing to parse", e);
        }

        if (text.isEmpty()) {
            log.warn("File '" + CentralDataLoader.fileToParse + "' empty!");
            return null;
        }

        log.info("Check data is ids or names");

        String[] data = text.trim().replace(" ", "").split(";");
        boolean isNumeric = StringUtils.isNumeric(data[0]);
        if (isNumeric) {
            log.info("Found id instead of name, get npcs by id");
        }
        else {
            log.info("Found name instead of id, get npcs by name");
        }

        if (data.length == 0) {
            log.warn("File '" + CentralDataLoader.fileToParse + "' empty!");
            return null;
        }

        return isNumeric ? NpcHolder.getInstance().getNpcList(data) : NpcHolder.getInstance().getNpcListByName(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T)result;
    }
}
