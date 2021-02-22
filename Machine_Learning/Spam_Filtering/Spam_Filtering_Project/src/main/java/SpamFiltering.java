import data.Data;
import data.DataPreprocessingService;
import decisionTree.ID3;
import naiveBayesClassifier.NaiveBayesClassifier;
import naiveBayesClassifier.countVectorizer.NaiveBayesClassifierCv;
import naiveBayesClassifier.tdIdfVectorizer.NaiveBayesClassifierTdIdf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SpamFiltering {
    private final Integer K = 10;
    private List<List<String>> dataSet;
    private final DataPreprocessingService dataService = new DataPreprocessingService();

    private NaiveBayesClassifier nbcTdIdf;
    private NaiveBayesClassifier nbcCv;
    private ID3 decisionTree;

    SpamFiltering(String filename) {
        this.dataSet = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String st;
            while ((st = br.readLine()) != null){
                this.dataSet.add(this.dataPreProcessing(st));
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

        this.dataService.close();
        this.nbcCv = new NaiveBayesClassifierCv();
        this.nbcTdIdf = new NaiveBayesClassifierTdIdf();
        this.decisionTree = new ID3();
    }

    public void start() {
        Collections.shuffle(this.dataSet);
        List<List<String>> validationData = new ArrayList<>();
        int sizeOfValidationDataSet = this.dataSet.size()/this.K;

        double[] nbcCvMetrics = new double[3];
        double[] nbcCvAvrMetrics = new double[]{0.0, 0.0, 0.0};

        double[] nbcTfIdfMetrics = new double[3];
        double[] nbcTfIdfAvrMetrics = new double[]{0.0, 0.0, 0.0};

        double[] dtMetrics = new double[3];
        double[] dtAvrMetrics = new double[]{0.0, 0.0, 0.0};

        List<String> words = new ArrayList<>();
        for(int i = 0; i < this.K; i++){
            int[] nbcCvPredictionResultCounter = new int[]{0, 0, 0, 0};
            int[] nbcTfIdfPredictionResultCounter = new int[]{0, 0, 0, 0};
            int[] dtPredictionResultCounter = new int[]{0, 0, 0, 0};
            List<List<String>> trainingDataSet = new ArrayList<>();
            for (int j = 0; j < this.dataSet.size(); j++){
                if (j >= i * sizeOfValidationDataSet && j < (i + 1) * sizeOfValidationDataSet){
                    validationData.add(this.dataSet.get(j));
                } else {
                    trainingDataSet.add(this.dataSet.get(j));
                    for (String word: this.dataSet.get(j)) {
                        if (words.indexOf(word) == -1) {
                            words.add(word);
                        }
                    }
                }
            }

            this.nbcCv.trainData(trainingDataSet);
            this.nbcTdIdf.trainData(trainingDataSet);
            this.decisionTree.train(trainingDataSet, words);

            for (int s = 0; s < sizeOfValidationDataSet; s++){
                List<String> email = validationData.get(s);

                boolean nbcCvPrediction = this.nbcCv.testEmail(email);
                this.prediction(email, nbcCvPrediction, nbcCvPredictionResultCounter);

                boolean nbcTfIdfPrediction = this.nbcTdIdf.testEmail(email);
                this.prediction(email, nbcTfIdfPrediction, nbcTfIdfPredictionResultCounter);

                boolean dtPrediction = this.decisionTree.classify(email);
                this.prediction(email, dtPrediction, dtPredictionResultCounter);
            }

            this.updateMetrics(nbcCvMetrics, nbcCvPredictionResultCounter, nbcCvAvrMetrics, sizeOfValidationDataSet);
            this.updateMetrics(nbcTfIdfMetrics, nbcTfIdfPredictionResultCounter, nbcTfIdfAvrMetrics, sizeOfValidationDataSet);
            this.updateMetrics(dtMetrics, dtPredictionResultCounter, dtAvrMetrics, sizeOfValidationDataSet);

            System.out.println("Iteration: " + (i + 1));
            this.printTable(nbcCvMetrics, nbcTfIdfMetrics, dtMetrics);

            // Reset all structures and clear NBC models.
            this.nbcCv.clear();
            this.nbcTdIdf.clear();
            trainingDataSet.clear();
            validationData.clear();
        }

        System.out.println("Final results: " );
        this.printTable(nbcCvAvrMetrics, nbcTfIdfAvrMetrics, dtAvrMetrics);
    }

    private List<String> dataPreProcessing(String email) {
        try {
            String cleanEmail = email.trim()
                    .replaceAll(Data.HYPERLINKS_REGEX, "")
                    .replaceAll(Data.NUMBERS_REGEX, "");
            // Additional data preprocessing for english dataset
//                    .replaceAll("[^a-яА-Яa-zA-z][a-яА-Яa-zA-z]{0,3}[^a-яА-Яa-zA-z]", " ");
            return this.dataService.analyze(cleanEmail);
        } catch (IOException | IllegalStateException e) {
            System.out.println("Preprocessing actions under email \"" + email + "\" failed." );
            return Arrays.asList(email.split(Data.DELIMITER));
        }
    }

    private void prediction(List<String> email, boolean prediction, int[] predictionResultsCounter) {
        String className = email.get(Data.INDEX_OF_CLASS);
        if (className.equals(Data.SPAM_CLASS)) {
            if (prediction) {
                // truePositive
                predictionResultsCounter[1]++;
                // success
                predictionResultsCounter[0]++;
            } else {
                // falseNegative
                predictionResultsCounter[2]++;
            }
        } else if (className.equals(Data.NON_SPAM_CLASS) && !prediction) {
            // falsePositive
            predictionResultsCounter[3]++;
        } else {
            //success
            predictionResultsCounter[0]++;
        }
    }

    private void updateMetrics(double[] metrics, int[] predictionResultsCounter, double[] avrMetrics, int sizeOfValidationDataSet ) {
        // accuracy
        metrics[0] = (double)predictionResultsCounter[0]/sizeOfValidationDataSet * 100;
        // precision
        metrics[1] = (double)predictionResultsCounter[1] / (predictionResultsCounter[1] + predictionResultsCounter[3]) * 100;
        // recall
        metrics[2] = (double)predictionResultsCounter[1] / (predictionResultsCounter[1] + predictionResultsCounter[2]) * 100;

        avrMetrics[0] += metrics[0] / this.K;
        avrMetrics[1] += metrics[1] / this.K;
        avrMetrics[2] += metrics[2] / this.K;
    }

    private void printTable(double[] nbcCvMetrics, double[] nbcTdIdtMetrics, double[] drMetrics) {
        System.out.println(String.format("%15s %10s %20s %10s %25s %10s %10s %10s", "Metric", "|", "NBC (with CV)", "|", "NBC (with TF-IDF)", "|", "DT", "|"));
        System.out.println(String.format("%s", "---------------------------------------------------------------------------------------------------------------------"));
        System.out.println(String.format("%15s %10s %20.4f %10s %25.4f %10s %10.4f %10s", "Accuracy", "|", nbcCvMetrics[0], "|", nbcTdIdtMetrics[0], "|", drMetrics[0], "|"));
        System.out.println(String.format("%15s %10s %20.4f %10s %25.4f %10s %10.4f %10s", "Precision", "|", nbcCvMetrics[1], "|", nbcTdIdtMetrics[1], "|", drMetrics[1], "|"));
        System.out.println(String.format("%15s %10s %20.4f %10s %25.4f %10s %10.4f %10s", "Recall", "|", nbcCvMetrics[2], "|", nbcTdIdtMetrics[2], "|", drMetrics[2], "|"));
        System.out.println(String.format("%s\n", "---------------------------------------------------------------------------------------------------------------------"));

    }
}
