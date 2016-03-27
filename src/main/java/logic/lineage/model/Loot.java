package logic.lineage.model;

public class Loot {
    public enum Type {
        Drop(1),
        Other(2),
        Resources(3);

        private int type;
        private Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public float getAverageChance() {
        return (minChance + maxChance) / 2;
    }

    public Type type;
    public int itemId;
    public int min;
    public int max;
    public float minChance;
    public float maxChance;
    public String name;
    public String russianName;
}
