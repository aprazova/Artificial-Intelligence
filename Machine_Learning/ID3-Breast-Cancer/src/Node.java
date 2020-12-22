import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private List<Relation> children;
    private Map<String, Double> classesProbability;
    private String featureValue;
    private Integer index;
    private boolean isLeaf;

    public Node(){
        this.classesProbability = new HashMap<>();
        this.children = new ArrayList<>();
        this.isLeaf = false;
    }

    public void addRelation(Relation relation){
        this.children.add(relation);
    }

    public void setClassesProbability(Map<String, Double> classesFrequency) {
        this.classesProbability = classesFrequency;
    }

    public void setFeatureValue(String value){
        this.featureValue = value;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Map<String, Double> getClassesProbability() {
        return this.classesProbability;
    }
    public List<Relation> getChildren() {
        return this.children;
    }

    public String getFeatureValue() {
        return this.featureValue;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    public Integer getIndex() {
        return this.index;
    }

}
