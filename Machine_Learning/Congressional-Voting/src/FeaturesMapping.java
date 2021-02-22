import java.util.HashMap;
import java.util.Map;

public class FeaturesMapping {
    private final String[] POSSIBLE_VALUES = {"y", "n", "?"};

    //Mapping features by value and custom type feature
    private Map<String, Feature> featureValues;

    FeaturesMapping(){
        this.featureValues = new HashMap<>();
        for (String value : POSSIBLE_VALUES) {
            this.featureValues.put(value, new Feature());
        }
    }

    public double calculateProbabilityOfFeatures(int classFrequency, String value){
        return this.featureValues.get(value).getValueProbabilityInClass(classFrequency);
    }

    public void printFeature(Integer classFrequency){
        for (String value: this.POSSIBLE_VALUES){
            this.featureValues.get(value).getValueProbabilityInClass(classFrequency);
        }
    }

    public void increaseFrequencyOfValue(String value){
        this.featureValues.get(value).increaseFrequency();
    }

    public void clear(){
        for (Feature f :
                this.featureValues.values()) {
            f.clear();
        }
    }

}
