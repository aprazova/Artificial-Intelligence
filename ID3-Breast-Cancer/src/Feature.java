import java.util.Map;

public class Feature {
    private String featureName;
    private Map<String, ValueInfo> values;
    private Integer rowCount;
    private Integer index;

    Feature(Integer index) {
        this.index = index;
    }

    public Map<String, ValueInfo> getValues() {
        return this.values;
    }
    public void setValues(Map<String, ValueInfo> values) {
        this.values = values;
    }
    public Integer getRowCount() {
        return rowCount;
    }
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
    public String getFeatureName() {
        return this.featureName;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return this.index;
    }
}
