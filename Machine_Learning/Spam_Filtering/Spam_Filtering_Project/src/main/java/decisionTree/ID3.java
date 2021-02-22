package decisionTree;

import data.Data;
import java.util.*;

public class ID3 {
    private final Integer MIN_EMAILS = 100;
    private BitSet visitedWords;
    private Node rootNode;
    private List<List<String>> dataSet;

    public ID3() {
        this.rootNode = null;
        this.visitedWords = new BitSet();
    }

    public void train(List<List<String>> dataSet, List<String> words) {
        this.dataSet = dataSet;
        List<Integer> emailIndexes = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            emailIndexes.add(i);
        }

        this.rootNode = new Node(emailIndexes);
        this.build(this.rootNode, words, this.visitedWords);
    }

    private void build(Node node, List<String> words, BitSet visitedWordsN) {
        List<Integer> emailIndexes = node.getEmailIndexed();
        ID3Feature bestWord = this.getBestWord(emailIndexes, words, visitedWordsN);

        if (bestWord == null) {
            node.setLeaf(true);
            node.setClassName(this.getClassForUnvisitedEmails(node.getEmailIndexed()));
            return;
        }

        node.setFeature(bestWord);
        Node leftChild = new Node(bestWord.getEmailIndexes());

        List<Integer> emailsWithoutWord = new ArrayList<>();
        for (Integer index: emailIndexes) {
            if (bestWord.getEmailIndexes().indexOf(index) == -1) {
                emailsWithoutWord.add(index);
            }
        }

        Node rightNode = new Node(emailsWithoutWord);
        node.setLeftChild(leftChild);
        node.setRightChild(rightNode);

        if (!this.prePruning(leftChild)) {
            this.build(leftChild, words, visitedWordsN);
            visitedWordsN.clear(bestWord.getIndex());
        }

        if (!this.prePruning(rightNode)) {
            this.build(rightNode, words, visitedWordsN);
            visitedWordsN.clear(bestWord.getIndex());
        }
    }

    private boolean prePruning(Node node) {
        if (node.getEmailIndexed().size() <= this.MIN_EMAILS) {
            node.setLeaf(true);
            node.setClassName(this.getClassForUnvisitedEmails(node.getEmailIndexed()));
            return true;
        }

        return false;
    }

    public boolean classify(List<String> email) {
        return this.classify(email, this.rootNode);
    }

    public boolean classify(List<String> email, Node node) {
        if (node.isLeaf()) {
            String className = email.get(Data.INDEX_OF_CLASS);
            return className.equals(node.getClassName());
        }

        if (email.indexOf(node.getFeature().getWord()) == -1 ) {
            return this.classify(email, node.getRightChild());
        } else {
            return this.classify(email, node.getLeftChild());
        }
    }

    private ID3Feature getBestWord(List<Integer> emailIndexes, List<String> words, BitSet visitedWords) {

        ID3Feature bestFeature = null;
        double bestInformationGain = Double.MIN_VALUE;

        Map<String, ID3Feature> mapping = new HashMap<>();
        for (Integer index: emailIndexes) {
            for (String  word: this.dataSet.get(index)) {
                if (!mapping.containsKey(word)) {
                    mapping.put(word, new ID3Feature(words.indexOf(word), word));
                }

                mapping.get(word).addEmailIndex(index);
            }
        }

        for (String word: mapping.keySet()) {
            ID3Feature feature = mapping.get(word);

            if (visitedWords.get(feature.getIndex())) {
                continue;
            }

            double informationGain = this.calculateInformationGain(feature, emailIndexes);
            if (bestInformationGain < informationGain) {
                bestInformationGain = informationGain;
                bestFeature = feature;
            }
        }

        if (bestFeature != null) {
            visitedWords.set(bestFeature.getIndex());
        }

        return bestFeature;
    }

    private double calculateInformationGain(ID3Feature feature, List<Integer> unvisitedEmails) {
        double classesEntropy = this.calculateClassesEntropy(unvisitedEmails);
        double entropy = this.calculateTotalEntropy(feature, unvisitedEmails);

        return classesEntropy - entropy;
    }

    private double calculateClassesEntropy(List<Integer> unvisitedEmails) {
        double entropy = 0.0;
        Map<String, Integer> classesFrequency = new HashMap<>();
        for (String className: Data.CLASSES) {
            classesFrequency.put(className, 0);
        }

        for (Integer index: unvisitedEmails) {
            String className = this.dataSet.get(index).get(Data.INDEX_OF_CLASS);

            int frequency = classesFrequency.get(className);
            classesFrequency.replace(className, ++frequency);
        }

        for (String  className: classesFrequency.keySet()) {
            double probability = (double) classesFrequency.get(className) / unvisitedEmails.size();
            entropy -= probability * (Math.log(probability)/Math.log(2));
        }

        return entropy;
    }

    private double calculateTotalEntropy(ID3Feature feature, List<Integer> unvisitedEmails) {
        List<Integer> unvisitedIndexesWithoutWord = new ArrayList<>();
        for (int i = 0; i < unvisitedEmails.size(); i++){
            if (feature.getEmailIndexes().indexOf(i) == -1) {
                unvisitedIndexesWithoutWord.add(i);
            }
        }

        int unvisitedEmailsCounter = unvisitedEmails.size();
        double probabilityToHasWord = (double)feature.getEmailIndexes().size()/unvisitedEmailsCounter;
        double probabilityToHasNotWord = (double)unvisitedIndexesWithoutWord.size()/unvisitedEmailsCounter;

        double totalEntropy = probabilityToHasWord * this.calculateClassesEntropy(feature.getEmailIndexes()) +
                probabilityToHasNotWord * this.calculateClassesEntropy(unvisitedIndexesWithoutWord);

        return totalEntropy;
    }

    private String getClassForUnvisitedEmails(List<Integer> unvisitedEmails){
        Map<String, Integer> classFrequency = new HashMap<>();
        for (String className :
                Data.CLASSES) {
            classFrequency.put(className, 0);
        }
        String bestClass = "";
        int maxClassFrequency = 0;
        for (Integer index: unvisitedEmails) {
            String className = this.dataSet.get(index).get(Data.INDEX_OF_CLASS);
            int frequency = classFrequency.get(className);
            classFrequency.replace(className, ++frequency);
            if (maxClassFrequency < frequency + 1){
                maxClassFrequency = frequency + 1;
                bestClass = className;
            }
        }

        return bestClass;
    }
}
