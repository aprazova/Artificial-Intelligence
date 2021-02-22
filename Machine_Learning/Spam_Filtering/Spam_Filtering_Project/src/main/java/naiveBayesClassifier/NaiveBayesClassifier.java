package naiveBayesClassifier;

import data.Data;

import java.util.*;

public interface NaiveBayesClassifier {

    void trainData(List<List<String>> dataSet);
    boolean testEmail(List<String> email);
    double computePosteriorProbabilities(String className, List<String> words);
    void clear();
    Integer indexOfFeature(String className, String word);
    double calculateClassProbability(String className);
}
