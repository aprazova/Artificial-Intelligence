import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neuron {
    protected Double bias = -1.0;
    private double output;
    private Map<Neuron, Arc> incomingArcs;
    private Arc biasArc;

    public Neuron() {
        this.incomingArcs = new HashMap<>();
    }

    public void setOutput(Double output) {
        this.output = output;
    }

    public Double getOutput() {
        return output;
    }

    public Arc getIncomingArc(Neuron neuron) {
        return this.incomingArcs.get(neuron);
    }

    public Map<Neuron, Arc> getIncomingArcs() {
        return this.incomingArcs;
    }

    public void addIncomingArcs(List<Neuron> incomingArcs) {
        for (Neuron from: incomingArcs) {
            this.incomingArcs.put(from, new Arc(from, this));
        }
    }

    public void addBiasArc(Neuron bias) {
        this.biasArc = new Arc(bias, this);
        this.incomingArcs.put(bias, this.biasArc);
    }

    public void updateOutput() {
        double output = 0;
        for (Arc in: incomingArcs.values()) {
            Neuron from = in.getFromNeuron();
            output += from.getOutput() * in.getWeight();
        }

        output += this.bias * this.biasArc.getWeight();
        this.output = this.applyActivationFunction(output);
    }

    private Double applyActivationFunction(double sum) {
        return 1.0 / (1.0 + Math.exp(-1.0 * sum));
    }

    public double derivative () {
        return this.output * (1.0 - this.output);
    }
}
