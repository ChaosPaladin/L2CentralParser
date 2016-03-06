package logic.lineage.holder;

import logic.lineage.model.Npc;

import java.util.HashMap;

public class NpcHolder {
    private static final NpcHolder instance = new NpcHolder();
    private HashMap<Integer, Npc> npcStore = new HashMap<Integer, Npc>();

    public static NpcHolder getInstance() {
        return instance;
    }

    public void addNpc(Npc npc) {
        npcStore.putIfAbsent(npc.getId(), npc);
    }

    public Npc getNpc(int id) {
        return npcStore.get(id);
    }
}
