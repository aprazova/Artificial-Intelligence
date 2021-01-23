import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class NaiveBayesClassifier {

    private final Integer K = 10;
    private final Integer NUMBER_OF_FEATURES = 16;
    private final String[] CLASSES = {"democrat", "republican"};

    private Map<String, RecordsForClass> trainingDataSet;
    private List<String> dataSet;

    NaiveBayesClassifier(String data){
        this.trainingDataSet = new HashMap<>();

        //Initialize dataSet mapping
        for (String className : this.CLASSES) {
            this.trainingDataSet.put(className, new RecordsForClass(className, this.NUMBER_OF_FEATURES));
        }

        this.dataSet = new ArrayList<>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(data))){
            String st;
            while ((st = br.readLine()) != null){
                this.dataSet.add(st);
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void start(){
        List<String> validationData = new ArrayList<>();
        int sizeOfValidationDataSet = this.dataSet.size()/this.K;
        int sizeOfTrainingDataSet = this.dataSet.size() - sizeOfValidationDataSet;
        double avrAccuracy = 0;
        double accuracy = 0;
        int success = 0;
        for(int i = 0; i < this.K; i++){
            for (int j = 0; j < this.dataSet.size(); j++){
                if (j >= i * sizeOfValidationDataSet && j < (i + 1) * sizeOfValidationDataSet){
                    validationData.add(this.dataSet.get(j));
                } else {
                    String[] record = this.dataSet.get(j).split(",", 2);
                    String className = record[0];
                    String features = record[1];
                    this.trainingDataSet.get(className).addRecord(features);
                }
            }
            for (int s = 0; s < sizeOfValidationDataSet; s++){
                double probability = Integer.MIN_VALUE;
                String probableClass = "";
                String[] record = validationData.get(s).split(",", 2);
                String features = record[1];
                for (String className :
                        this.CLASSES) {
                    double classNameProbability = this.trainingDataSet.get(className).computePosteriorProbabilities(features, sizeOfTrainingDataSet);
                    if (probability < classNameProbability){
                        probability = classNameProbability;
                        probableClass = className;
                    }
                }
                if (probableClass.equals(record[0])) {
                    success++;
                } else {
                    this.trainingDataSet.get(record[0]).addRecord(features);
                }
            }
            accuracy = (double)success/sizeOfValidationDataSet * 100;
            System.out.println("Accuracy in iteration " + (i + 1) + ": " + accuracy);
            avrAccuracy += accuracy;
            for (RecordsForClass mapping :
                    this.trainingDataSet.values()) {
                mapping.clear();
            }
            validationData.clear();
            success = 0;
        }
        System.out.println("Average accuracy: " + avrAccuracy/this.K);
    }
}
