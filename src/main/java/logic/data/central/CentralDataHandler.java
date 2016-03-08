package logic.data.central;

import logic.data.AbstractDataHandler;
import logic.data.central.parsing.CreatureDataParser;
import logic.lineage.model.Npc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CentralDataHandler extends AbstractDataHandler {
    private CentralDataLoader centralDataLoader;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void loadData() {
        centralDataLoader = new CentralDataLoader();
        centralDataLoader.load();
    }

    @Override
    public void handleData() {
        HashMap<Npc, String> results = centralDataLoader.getResult();
        for (HashMap.Entry<Npc, String> entry : results.entrySet()) {
            log.info("Parsing npc (" + entry.getKey().getName() + ", id: " + entry.getKey().getId() + ")");

            CreatureDataParser creatureDataParser = new CreatureDataParser(entry.getValue(), CentralInfo.DataType.CREATURE);
            creatureDataParser.parse();

            log.info("Done");
        }
    }
}
