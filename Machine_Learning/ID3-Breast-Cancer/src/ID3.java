import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class ID3 {

    private static final Integer K_FOLD_CROSS_VALIDATION = 10;
    private String[][] dataSet;
    private Integer row;
    private Integer col;

    ID3(String filename) {
        this.row = 0;
        this.col = 0;
        BufferedReader br = null;

        try(BufferedReader pre_br = new BufferedReader(new FileReader(filename))){
            String st;

            while ((st = pre_br.readLine()) != null){
                String[] features = st.split(",");
                this.row++;
                this.col = features.length;

            }

            this.dataSet = new String[row + 1][col];
            int index = 0;
            for (int i = 0; i < DataMapping.FEATURES.length; i++) {
                this.dataSet[index][i] = DataMapping.FEATURES[i];
            }

            br = new BufferedReader(new FileReader(filename));
            while ((st = br.readLine()) != null){
                index++;
                String[] features = st.split(",");
                for (int i = 0; i < this.col; i++) {
                    this.dataSet[index][i] = DataMapping.BINNING_FEATURES.containsKey(this.dataSet[0][i]) ?
                            DataMapping.BINNING_FEATURES.get(this.dataSet[0][i]).get(features[i]) :
                            features[i];
                }
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("File not found.");
                }
            }
        }
    }

    public void start() {
        if (this.dataSet != null) {
            int sizeOfDataSet = this.row + 1;
            int sizeOfValidationData = sizeOfDataSet/this.K_FOLD_CROSS_VALIDATION + 1;
            int sizeOfTrainingData = sizeOfDataSet - sizeOfValidationData + 1;
            double accuracy = 0;
            double avgAccuracy = 0;
            String[][] trainingData = new String[sizeOfTrainingData][this.col];
            String[][] validationData = new String[sizeOfValidationData][this.col];

            for (int i = 0; i < this.col; i++) {
                validationData[0][i] = this.dataSet[0][i];
                trainingData[0][i] = this.dataSet[0][i];
            }
            for (int i = 0; i < this.K_FOLD_CROSS_VALIDATION; i++){
                this.randomizeTable();
                int validationDataRow = 1;
                int trainingDataRow = 1;
                int iteration = sizeOfDataSet/sizeOfValidationData + 1;
                for (int j = 1; j < sizeOfDataSet; j++){
                    if ( (j % iteration - i) == 0 && validationDataRow < sizeOfValidationData){

                        // Fill validation data
                        for (int s = 0; s < this.col; s++){
                            validationData[validationDataRow][s] = this.dataSet[j][s];
                        }
                        validationDataRow++;
                    } else {

                        // Fill training data
                        for (int s = 0; s < this.col; s++){
                            trainingData[trainingDataRow][s] = this.dataSet[j][s];
                        }
                        trainingDataRow++;
                    }
                }

                DecisionTree decisionTree = new DecisionTree(trainingData, trainingDataRow);
                int success = 0;
                for(int v = 1; v < validationDataRow; v++){
                    String className = decisionTree.classify(validationData[v]);
                    if (className.equals(validationData[v][0])){
                        success++;
                    }
                }

                accuracy = (double)success/(sizeOfValidationData - 1) * 100;
                avgAccuracy += accuracy;
                System.out.printf("Accuracy in iteration %d: %f\n", i + 1, accuracy);
            }
            System.out.printf("Average accuracy: %f\n", avgAccuracy/this.K_FOLD_CROSS_VALIDATION);
        }
    }

    private void randomizeTable(){
        for (int i = 1; i < this.row; i++){
            Random random = new Random();
            int randomIndex = random.nextInt(this.row - i) + i;
            String[] rowSwap = this.dataSet[randomIndex];
            this.dataSet[randomIndex] = this.dataSet[i];
            this.dataSet[i] = rowSwap;
        }
    }

}
