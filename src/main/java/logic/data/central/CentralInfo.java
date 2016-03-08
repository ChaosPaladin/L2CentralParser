package logic.data.central;

public class CentralInfo {
    public static String BASE_LINK = "https://l2central.info";
    public static String CREATURE_LINK = "https://l2central.info/wiki/";

    public static enum DataType {
        CREATURE(1);

        private int type;
        private DataType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
