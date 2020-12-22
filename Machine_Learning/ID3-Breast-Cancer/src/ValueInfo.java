import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ValueInfo {
    private Map<String, Integer> featureClasses;
    private BitSet rows;
    private Integer rowsCount;
    private double entropy;

    public ValueInfo(BitSet rows) {
        this.featureClasses = new HashMap<>();
        for (String className :
                DataMapping.CLASS_NAMES) {
            this.featureClasses.put(className, 0);
        }
        this.rows = rows;
        this.rowsCount = 0;
        this.entropy = 0;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public double getEntropy(){
        return this.entropy;
    }

    public BitSet getRows() {
        return this.rows;
    }

    public Map<String, Integer> getFeatureClasses() {
        return this.featureClasses;
    }

    public Integer getRowsCount(){
        return this.rowsCount;
    }

    public void increaseClassFrequency(String className){
        try {
            int currentFrequency = this.featureClasses.get(className);
            this.featureClasses.replace(className, ++currentFrequency);
        } catch (Exception e){
            System.out.print(e.getMessage());
        }
    }

    public void increaseRowCount(){
        this.rowsCount++;
    }

    public void addRow(Integer row){
        this.rows.set(row);
    }

}
