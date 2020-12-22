import java.util.HashMap;
import java.util.Map;

public class DataMapping {
    public static final String[] CLASS_NAMES = {"no-recurrence-events", "recurrence-events"};
    public static final String[] FEATURES = {"class-name", "age", "menopause", "tumor-size", "inv-nodes", "node-caps", "deg-malig", "breast", "breast-quad", "irradiat"};

    public static final Map<String, Map<String, String>> BINNING_FEATURES = new HashMap<>(){
        private final Map<String, String> AGE_MAPPING = new HashMap<>(){{
            put("10-19", "min");
            put("20-29", "min");
            put("30-39", "min");
            put("40-49", "mid");
            put("50-59", "mid");
            put("60-69", "mid");
            put("70-79", "max");
            put("80-89", "max");
            put("90-99", "max");
        }};

        private Map<String, String> TUMOR_SIZE_MAPPING = new HashMap<>(){{
            put("0-4", "min");
            put("5-9", "min");
            put("10-14", "min");
            put("15-19", "min");
            put("20-24", "mid");
            put("25-29", "mid");
            put("30-34", "mid");
            put("35-39", "mid");
            put("40-44", "max");
            put("45-49", "max");
            put("50-54", "max");
            put("55-59", "max");
        }};

        private Map<String, String> INV_NODES_MAPPING = new HashMap<>(){{
            put("0-2", "min");
            put("3-5", "min");
            put("6-8", "min");
            put("9-11", "min");
            put("12-14", "mid");
            put("15-17", "mid");
            put("18-20", "mid");
            put("21-23", "mid");
            put("24-26", "mid");
            put("27-29", "max");
            put("30-32", "max");
            put("33-35", "max");
            put("36-39", "max");
        }};

        {
        put("age", this.AGE_MAPPING);
        put("tumor-size", this.TUMOR_SIZE_MAPPING);
        put("inv-nodes", this.INV_NODES_MAPPING);
    }};
}
