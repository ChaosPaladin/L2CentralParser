package logic.lineage.holder;

import logic.lineage.model.Loot;
import logic.lineage.model.Npc;

import java.util.HashMap;
import java.util.Map;

public class LootHolder {
    private static final LootHolder instance = new LootHolder();
    private HashMap<Npc, Loot[]> loot = new HashMap<>();

    public static LootHolder getInstance() {
        return instance;
    }

    public void addLoot(Npc npc, Loot[] loot) {
        this.loot.put(npc, loot);
    }

    public Loot[] getLoot(Npc npc) {
        return loot.get(npc);
    }

    public Loot[] getLoot(int npcId) {
        for (Map.Entry<Npc, Loot[]> entry : loot.entrySet()) {
            if (entry.getKey().getId() == npcId) {
                return entry.getValue();
            }
        }

        return null;
    }
}
