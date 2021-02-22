package naiveBayesClassifier.countVectorizer;

import data.Data;
import naiveBayesClassifier.NaiveBayesClassifier;

import java.util.*;

public class NaiveBayesClassifierCv implements NaiveBayesClassifier {
    private List<List<String>> dataSet;
    private Map<String, List<FeatureCv>> classFeatures;
    private Map<String, Integer> classFrequency;

    public NaiveBayesClassifierCv() {
        this.dataSet = new ArrayList<>();
        this.classFeatures = new HashMap<>();
        this.classFrequency = new HashMap<>();

        for(String className: Data.CLASSES) {
            this.classFeatures.put(className, new ArrayList<>());
            this.classFrequency.put(className, 0);
        }
    }

    @Override
    public void trainData(List<List<String>> dataSet) {
        this.dataSet = dataSet;
        for (List<String> email: this.dataSet) {
            String className = email.get(Data.INDEX_OF_CLASS);
            if (email.size() == 1 || !this.classFrequency.containsKey(className)) continue;

            Set<String> words = new HashSet<>(email.subList(Data.INDEX_OF_CLASS + 1, email.size()));

            int classFrequency = this.classFrequency.get(className);
            this.classFrequency.put(className, ++classFrequency);
            for (String word: words) {

                int index = this.indexOfFeature(className, word);
                if (index != -1) {
                    this.classFeatures.get(className).get(index).increaseFrequency();
                } else {
                    this.classFeatures.get(className).add(new FeatureCv(word));
                }
            }
        }
    }

    @Override
    public boolean testEmail(List<String> email) {
        double probability = Integer.MIN_VALUE;
        String emailClass = email.get(Data.INDEX_OF_CLASS);
        String probableClass = "";
        List<String> emailContent = email.subList(Data.INDEX_OF_CLASS + 1, email.size());
        for (String className :
                Data.CLASSES) {
            double classNameProbability = this.computePosteriorProbabilities(className, emailContent);
            if (probability < classNameProbability){
                probability = classNameProbability;
                probableClass = className;
            }
        }

        return probableClass.equals(emailClass);
    }

    @Override
    public double computePosteriorProbabilities(String className, List<String> words){
        double posteriorProbability = 0;
        int classFrequency = this.classFrequency.get(className);
        for (int i = 0; i < words.size(); i++){
            int index = this.indexOfFeature(className, words.get(i));
            if (index != -1) {
                FeatureCv feature = this.classFeatures.get(className).get(index);
                posteriorProbability += Math.log10(feature.calculateProbabilityOfFeatures(classFrequency));
            } else {
                posteriorProbability += Math.log10((double) 1 / (classFrequency + 1));
            }
        }
        posteriorProbability += Math.log10(this.calculateClassProbability(className));
        return posteriorProbability;
    }

    @Override
    public void clear() {
        this.dataSet.clear();
        for (String className: Data.CLASSES) {
            this.classFeatures.get(className).clear();
            this.classFrequency.replace(className, 0);
        }
    }

    @Override
    public Integer indexOfFeature(String className, String word) {
        List<FeatureCv> features = this.classFeatures.get(className);
        for (int i = 0; i < features.size(); i++) {
            FeatureCv feature = features.get(i);
            if (feature.getWord().equals(word)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public double calculateClassProbability(String className){
        return (double)(this.classFrequency.get(className))/this.dataSet.size();
    }
}
