package naiveBayesClassifier.tdIdfVectorizer;

import naiveBayesClassifier.Feature;

public class FeatureTdIdf implements Feature {
    private String word;
    private Integer frequency;
    private double tdIdfValue;

    public FeatureTdIdf(String word) {
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
    public double calculateProbabilityOfFeatures(double sumOfTdIdf){
        return (this.tdIdfValue + 1)/(sumOfTdIdf + 1);
    }

    public double getTdIdfValue() {
        return this.tdIdfValue;
    }

    public void setTdIdfValue(double tdIdfValue) {
        this.tdIdfValue = tdIdfValue;
    }

}
