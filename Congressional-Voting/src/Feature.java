public class Feature {
    private final Double INITIAL_PROBABILITY = -1.0;
    private Integer frequency;
    private Double valueProbabilityInClass;

    Feature(){
        this.frequency = 1;
        this.valueProbabilityInClass = this.INITIAL_PROBABILITY;
    }

    private void calculateValueProbabilityInClass(int classFrequency){
        this.valueProbabilityInClass = (double)this.frequency/classFrequency;
    }

    public double getValueProbabilityInClass(int classFrequency){
        if (this.valueProbabilityInClass.equals(this.INITIAL_PROBABILITY)){
            this.calculateValueProbabilityInClass(classFrequency);
        }
        return this.valueProbabilityInClass;
    }

    public void increaseFrequency(){
        this.frequency++;
    }

    public void clear(){
        this.frequency = 1;
        this.valueProbabilityInClass = this.INITIAL_PROBABILITY;
    }
}
