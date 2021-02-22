package naiveBayesClassifier;

public interface Feature {

    String getWord();
    Integer getFrequency();
    void increaseFrequency();
    double calculateProbabilityOfFeatures(double classFrequency);
}
