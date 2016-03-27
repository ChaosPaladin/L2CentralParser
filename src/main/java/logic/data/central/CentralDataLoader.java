package logic.data.central;

import logic.data.AbstractDataLoader;
import logic.lineage.holder.NpcHolder;
import logic.lineage.model.Npc;
import logic.util.LUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class CentralDataLoader extends AbstractDataLoader {
    public static String fileToParse = "npc_to_parse.txt";

    private Logger log = LoggerFactory.getLogger(getClass());
    private HashSet<Npc> result = new HashSet<>();

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
            log.warn("Found 0 npc(s) in npc dat");
            return;
        }

        log.info("Found " + npcList.length + " npc(s) in npc dat");
        log.info("Filter npc w/o name and duplicates");
        for (Npc npc : npcList) {
            if (npc == null) {
                continue;
            }

            if (npc.getName() == null || npc.getName().trim().isEmpty()) {
                log.warn("Npc (id: " + npc.getId() + ") hasn't name, skip it");
                continue;
            }

            if (result.stream().anyMatch(n -> n.getName().equals(npc.getName()))) {
                log.warn("Found duplicate (name: " + npc.getName() + "), confusing what id should use?");
                continue;
            }

            result.add(npc);
        }

        if (result.isEmpty()) {
            log.warn("Found 0 npc(s) in npc dat to parse");
            return;
        }

        log.info(result.size() + " npc(s) to parse");
    }

    public String getDocument(Npc npc) {
        Document document = loadSite(CentralInfo.CREATURE_LINK + npc.getName().replaceAll(" ", "_"));
        if (document != null) {
            return document.body().outerHtml();
        }

        return null;
    }

    private Document loadSite(String link) {
        Document document;
        try {
            log.info("Loading page " + link);
            Connection.Response response = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(50 * 1000)
                    .execute();

            int statusCode = response.statusCode();
            if (statusCode == 200) {
                document = response.parse();
            }
            else if (statusCode == 404) {
                log.info("Page (" + link + ") doesn't exist at l2central.info");
                return null;
            }
            else {
                log.warn("Page " + link + " doesn't exist and returned status code: " + statusCode);
                return null;
            }
        }
        catch (Exception e) {
            log.warn("Error while loading " + link + " (" + e.getMessage() + ")");
            return null;
        }

        if (document == null) {
            log.warn("Something went wrong while loading " + link);
            return null;
        }

        if (document.body().outerHtml().contains("В настоящее время на этой странице нет текста.")) {
            log.warn("Page " + link + " doesnt exist at l2central.info");
            return null;
        }

        log.info("Loading done successfully for " + link);

        return document;
    }

    public Npc[] getNpcsToParse() {
        String text = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CentralDataLoader.fileToParse), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                text = line;
                break;
            }
        }
        catch (Exception e) {
            log.warn("Error while opening file '" + CentralDataLoader.fileToParse + "', nothing to parse", e);
        }

        if (text.isEmpty()) {
            log.warn("File '" + CentralDataLoader.fileToParse + "' empty!");
            return null;
        }

        log.info("Check data is ids or names");

        text = text.trim().replace(" ", "");
        String[] data;
        Npc[] npcList;
        boolean isRange = false;
        boolean isNumeric = false;
        if (text.contains("-")) {
            text = text.replaceAll(";", "");
            isRange = true;
            data = text.split("-");
            log.info("(" + CentralDataLoader.fileToParse + ") Found id range to parse");
        }
        else {
            data = text.split(";");
            isNumeric = StringUtils.isNumeric(data[0]);;
        }

        if (data.length == 0) {
            log.warn("File '" + CentralDataLoader.fileToParse + "' empty!");
            return null;
        }

        if (!isRange) {
            if (isNumeric) {
                npcList = Integer.parseInt(data[0]) != 0 ? NpcHolder.getInstance().getNpcList(data) : NpcHolder.getInstance().getNpcList();
                log.info("(" + CentralDataLoader.fileToParse + ") Found id list to parse");
            }
            else {
                npcList = NpcHolder.getInstance().getNpcListByName(data);
                log.info("(" + CentralDataLoader.fileToParse + ") Found name list to parse");
            }
        }
        else {
            npcList = NpcHolder.getInstance().getNpcList(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
        }

        return npcList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized  <T> T getResult() {
        return (T)result;
    }
}
