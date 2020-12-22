public class RecordsForClass {
    private String className;
    private Integer frequency;
    private FeaturesMapping[] features;

    public RecordsForClass(String className, Integer numberOfFeatures) {
        this.className = className;
        this.features = new FeaturesMapping[numberOfFeatures];
        this.frequency = 1;

        for ( int i = 0; i < numberOfFeatures; i++){
            this.features[i] = new FeaturesMapping();
        }
    }

    public void printFeatures(){
        System.out.println(this.className.toUpperCase() + " " + this.frequency);
        int i =1 ;
        for (FeaturesMapping feature : this.features) {
            System.out.print(i + " ");
            feature.printFeature(this.frequency);
            System.out.println();
            i++;
        }
    }

    public void addRecord(String record){
        this.increaseClassFrequency();
        String[] featuresValues = record.split(",");
        for (int i = 0; i < this.features.length; i++){
                this.features[i].increaseFrequencyOfValue(featuresValues[i]);
        }
    }

    public double calculateClassProbability(Integer allRecordsCount){
        return (double)(this.frequency)/(allRecordsCount + 1);
    }

    public double computePosteriorProbabilities(String features, Integer allRecordsCount){
        double posteriorProbability = 0;
        String[] featuresValues = features.split(",");
        for (int i = 0; i < featuresValues.length; i++){
            posteriorProbability += Math.log10(this.features[i].calculateProbabilityOfFeatures(this.frequency, featuresValues[i]));
        }
        posteriorProbability += Math.log10(this.calculateClassProbability(allRecordsCount));
        return posteriorProbability;
    }

    private void increaseClassFrequency(){
        this.frequency++;
    }

    public void clear(){
        this.frequency = 1;
        for (FeaturesMapping fm :
                this.features) {
            fm.clear();
        }
    }
}
