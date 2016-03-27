package logic.data.central;

import logic.data.AbstractDataHandler;
import logic.data.central.parsing.CreatureDataParser;
import logic.data.xml.writing.LootXmlWriter;
import logic.lineage.model.Loot;
import logic.lineage.model.Npc;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.*;

public class CentralDataHandler extends AbstractDataHandler {
    private CentralDataLoader centralDataLoader;
    private LootXmlWriter lootWriter = new LootXmlWriter();
    private Logger log = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<ImmutablePair<Npc, String>> parsingQueue = new LinkedBlockingQueue<>();
    private boolean loadingData;
    private Exception parsingError;


    @Override
    public void loadData() {
        centralDataLoader = new CentralDataLoader();
        centralDataLoader.load();
        loadingData = true;

        HashSet<Npc> results = centralDataLoader.getResult();
        ForkJoinPool forkJoinPool = new ForkJoinPool(3);
        try {
            handleData();
            forkJoinPool.submit(() -> results.parallelStream().forEach(this::getDocument)).get();
        }
        catch (InterruptedException | ExecutionException e) {
            log.warn("Error while waiting loading all pages", e);
        }
        finally {
            loadingData = false;
        }

        while (!parsingQueue.isEmpty() && parsingError == null) {
            try {
                Thread.sleep(300);
            }
            catch (InterruptedException e) {
                log.warn("Error while sleeping main thread", e);
            }
        }

        if (parsingError != null) {
            log.warn("Parsing isn't finished because of: " + parsingError.getMessage());
        }
    }

    @Override
    public void handleData() {
        new Thread(() -> {
            try {
                while (!parsingQueue.isEmpty() || loadingData) {
                    ImmutablePair<Npc, String> data =  parsingQueue.take();

                    try {
                        // npc not found
                        if (data.getValue() == null) {
                            continue;
                        }

                        log.info("Parsing npc (" + data.getKey().getName() + ", id: " + data.getKey().getId() + ")");

                        CreatureDataParser creatureDataParser = new CreatureDataParser(data.getValue(), CentralInfo.DataType.CREATURE, data.getKey());
                        creatureDataParser.parse();
                        log.info("Npc (" + data.getKey().getName() + ", id: " + data.getKey().getId() + ") has parsed");

                        Loot[] lootData = creatureDataParser.getResult();
                        if (lootData == null || lootData.length == 0) {
                            continue;
                        }

                        log.info("Writing npc to xml");
                        lootWriter.write(data.getKey(), creatureDataParser.getResult());
                        log.info("Writing xml done");
                    } catch (Exception e) {
                        log.warn("Error while parsing npc (id: " + data.getKey().getId() + ", name: " + data.getKey().getName() + "): " + e.getMessage());
                    }

                    Thread.sleep(100);
                }
            }
            catch (InterruptedException e) {
                parsingError = e;
                log.warn("Error while parsing data", e);
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    private void getDocument(Npc npc) {
        String data = centralDataLoader.getDocument(npc);
        ImmutablePair dataPair = new ImmutablePair(npc, data);
        parsingQueue.add(dataPair);
    }
}
