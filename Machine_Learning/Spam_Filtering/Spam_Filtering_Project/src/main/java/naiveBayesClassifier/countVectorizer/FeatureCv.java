package naiveBayesClassifier.countVectorizer;

import naiveBayesClassifier.Feature;

public class FeatureCv implements Feature {
    private String word;
    private Integer frequency;

    public FeatureCv(String word) {
        this.word = word;
        this.frequency = 1;
    }

    @Override
    public String getWord() {
        return this.word;
    }

    @Override
    public Integer getFrequency() {
        return this.frequency;
    }

    @Override
    public void increaseFrequency(){
        this.frequency++;
    }

    @Override
    public double calculateProbabilityOfFeatures(double classFrequency){
        return (this.frequency)/(classFrequency + 1);
    }
}
