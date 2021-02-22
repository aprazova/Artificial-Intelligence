package naiveBayesClassifier.tdIdfVectorizer;

import data.Data;
import naiveBayesClassifier.NaiveBayesClassifier;

import java.util.*;

public class NaiveBayesClassifierTdIdf implements NaiveBayesClassifier {
    private List<List<String>> dataSet;
    private Map<String, List<FeatureTdIdf>> classFeatures;
    private Map<String, Integer> classFrequency;
    private Map<String, Double> classTdIdf;

    public NaiveBayesClassifierTdIdf() {
        this.dataSet = new ArrayList<>();
        this.classFeatures = new HashMap<>();
        this.classFrequency = new HashMap<>();

        this.classTdIdf = new HashMap<>();
        for(String className: Data.CLASSES) {
            this.classFeatures.put(className, new ArrayList<>());
            this.classFrequency.put(className, 0);
            this.classTdIdf.put(className, 0.0);
        }
    }

    @Override
    public void trainData(List<List<String>> dataSet) {
        this.dataSet = dataSet;
        for (List<String> email: this.dataSet) {
            String className = email.get(Data.INDEX_OF_CLASS);
            if (email.size() == 1 || !this.classFrequency.containsKey(className)) continue;

            List<String> words = email.subList(Data.INDEX_OF_CLASS + 1, email.size());

            int classFrequency = this.classFrequency.get(className);
            this.classFrequency.put(className, ++classFrequency);
            for (String word: words) {
                int index = this.indexOfFeature(className, word);
                if (index != -1) {
                    this.classFeatures.get(className).get(index).increaseFrequency();
                } else {
                    this.classFeatures.get(className).add(new FeatureTdIdf(word));
                }
            }
        }

        for (String className: this.classFeatures.keySet()) {
            this.calculateTdIdf(this.dataSet, className, this.classFeatures.get(className));
            double sumOfTdIdf = 0.0;
            for (FeatureTdIdf feature: this.classFeatures.get(className)) {
                sumOfTdIdf += feature.getTdIdfValue();
            }

            this.classTdIdf.replace(className, sumOfTdIdf);
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
        double classNameTdIdf = this.classTdIdf.get(className);
        for (int i = 0; i < words.size(); i++){
            int index = this.indexOfFeature(className, words.get(i));
            if (index != -1) {
                FeatureTdIdf feature = this.classFeatures.get(className).get(index);
                    posteriorProbability += Math.log10(feature.calculateProbabilityOfFeatures(classNameTdIdf));
            } else {
                // This class in the set doesn't contain the word
                posteriorProbability += Math.log10((double) 1 / (classNameTdIdf + 1));
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
        List<FeatureTdIdf> features = this.classFeatures.get(className);
        for (int i = 0; i < features.size(); i++) {
            FeatureTdIdf feature = features.get(i);
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

    private void normalizeVector(List<FeatureTdIdf> features, Double min, Double max) {
        for (FeatureTdIdf feature: features) {
            double normalizedValued = (feature.getTdIdfValue() - min) / (max - min);
            feature.setTdIdfValue(normalizedValued);
        }
    }

    private Map<String,Double> calculateTermFrequency(List<FeatureTdIdf> features) {
        Map<String, Double> termFreqMap = new HashMap<>();
        double sum = 0.0;
        // Get the sum of all elements in list
        for (FeatureTdIdf feature : features) {
            sum += feature.getFrequency();
        }
        // Create a map with TD values in it.
        for(FeatureTdIdf feature: features) {
            double frequency = feature.getFrequency();
            double tf = frequency / sum;
            termFreqMap.put(feature.getWord(), tf);
        }
        return termFreqMap;
    }

    private Map<String, Double> calculateInverseDocFrequency(List<List<String>> data, String className, List<FeatureTdIdf> features) {
        HashMap<String,Double> inverseDocFreqMap = new HashMap<>();
        int size = this.classFrequency.get(className);
        double wordCount;
        for (FeatureTdIdf feature: features) {
            wordCount = 0;
            for(int i=0; i < data.size(); i++) {
                if (data.get(i).contains(feature.getWord()) && data.get(i).get(Data.INDEX_OF_CLASS).equals(className)) {
                    wordCount++;
                }
            }
            double temp = size/ wordCount;
            double idf = 1 + Math.log(temp);

            inverseDocFreqMap.put(feature.getWord(),idf);
        }
        return inverseDocFreqMap;
    }

    private void calculateTdIdf(List<List<String>> data, String className, List<FeatureTdIdf> features) {
        Map<String, Double> tdVector = this.calculateTermFrequency(features);
        Map<String, Double> idfVector = this.calculateInverseDocFrequency(data, className, features);
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        for (FeatureTdIdf feature: this.classFeatures.get(className)) {
            String word = feature.getWord();
            double tdIdfValue = tdVector.get(word)*idfVector.get(word);
            feature.setTdIdfValue(tdIdfValue);

            minValue = Math.min(minValue, tdIdfValue);
            maxValue = Math.max(maxValue, tdIdfValue);
        }
        this.normalizeVector(this.classFeatures.get(className), minValue, maxValue);
    }
}
