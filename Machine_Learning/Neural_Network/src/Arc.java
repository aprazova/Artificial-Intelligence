import java.util.Random;

public class Arc {
    private double weight;
    private Neuron from;
    private Neuron to;
    private double deltaWeight;

    public Arc(Neuron from, Neuron to) {
        this.from = from;
        this.to = to;

        Random rand = new Random();
        this.weight = rand.nextDouble() - 0.5; //value in interval [-0.5, 0.5]
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Neuron getFromNeuron() {
        return this.from;
    }

    public double getDeltaWeight() {
        return this.deltaWeight;
    }

    public void setDeltaWeight(double deltaWeight) {
        this.deltaWeight = deltaWeight;
    }
}
