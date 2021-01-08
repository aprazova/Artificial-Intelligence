import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    private final Integer[] NEURONS_IN_LAYERS = {2,4,1};
    private final Double LEARNING_RATE = 0.9;
    private final double MOMENTUM = 0.7;
    private List<Neuron> inputLayer;
    private List<Neuron> hiddenLayer;
    private List<Neuron> outputLayer;
    private Neuron bias;

    public NeuralNetwork() {
        this.inputLayer = new ArrayList<>();
        this.hiddenLayer = new ArrayList<>();
        this.outputLayer = new ArrayList<>();
        this.bias = new Neuron();

        for (int i = 0; i < this.NEURONS_IN_LAYERS.length; i++) {
            if (i == 0) {
                for (int n = 0; n < this.NEURONS_IN_LAYERS[i]; n++) {
                    this.inputLayer.add(new Neuron());
                }
            } else if (i == this.NEURONS_IN_LAYERS.length - 1) {
                for (int n = 0; n < this.NEURONS_IN_LAYERS[i]; n++) {
                    this.outputLayer.add(new Neuron());
                }
            } else {
                for (int n = 0; n < this.NEURONS_IN_LAYERS[i]; n++) {
                    this.hiddenLayer.add(new Neuron());
                }
            }
        }

        this.addArcs();
    }

    public void start(double[][] inputs, double[] expectedOutput, int maxEpoch, double minError){
        int epoch = 0;
        double error = 1;
        double[] result = new double[expectedOutput.length];
        while (epoch < maxEpoch && minError < error) {
            epoch++;
            error = 0;
            for (int i = 0; i < inputs.length; i++) {
                this.forwardPropagation(inputs[i]);

                double output = 0;
                for (Neuron oNeuron: this.outputLayer) {
                    output += oNeuron.getOutput();
                }

                result[i] = output;
                double err = Math.pow(output - expectedOutput[i], 2);
                error += err;
                this.backwardPropagation(expectedOutput[i]);
            }
        }

        System.out.println("Epoch: " + epoch + "\nError: " + error);
        for (Double el: result) {
            System.out.println(el);
        }
    }

    private void addArcs() {
        for (Neuron hNeuron: this.hiddenLayer) {
            hNeuron.addBiasArc(this.bias);
            hNeuron.addIncomingArcs(inputLayer);
        }

        for (Neuron oNeuron: this.outputLayer) {
            oNeuron.addBiasArc(this.bias);
            oNeuron.addIncomingArcs(this.hiddenLayer);
        }
    }

    private void forwardPropagation(double[] input) {
        for (int i = 0; i < this.NEURONS_IN_LAYERS.length; i++) {
            if (i == 0) {
                for (int n = 0; n < this.inputLayer.size(); n++) {
                    Neuron iNeuron = this.inputLayer.get(n);
                    iNeuron.setOutput(input[n]);
                }
            } else if (i == this.NEURONS_IN_LAYERS.length - 1) {
                for (Neuron oNeuron: this.outputLayer) {
                    oNeuron.updateOutput();
                }
            } else {
                for (Neuron hNeuron: this.hiddenLayer) {
                    hNeuron.updateOutput();
                }
            }
        }
    }

    private void backwardPropagation(double expectedOutput) {
        for (Neuron oNeuron: this.outputLayer) {
            double oNeuronErr = (expectedOutput - oNeuron.getOutput()) * oNeuron.derivative();
            for (Arc incomingArc: oNeuron.getIncomingArcs().values()) {
                double ai = incomingArc.getFromNeuron().getOutput();
                double partialDerivative = -ai * oNeuronErr;
                double deltaWeight = -this.LEARNING_RATE * partialDerivative;
                double updatedWeight = incomingArc.getWeight() + deltaWeight;

                incomingArc.setWeight(updatedWeight + this.MOMENTUM * incomingArc.getDeltaWeight());
                incomingArc.setDeltaWeight(deltaWeight);
            }
        }

        for (Neuron hNeuron: this.hiddenLayer) {
            for (Arc incomingArc: hNeuron.getIncomingArcs().values()) {
                double ai = incomingArc.getFromNeuron().getOutput();
                double sum = 0;
                for (Neuron oNeuron: this.outputLayer) {
                    double oNeuronErr = (expectedOutput - oNeuron.getOutput()) * oNeuron.derivative();

                    double wjk = oNeuron.getIncomingArc(hNeuron).getWeight();
                    sum += (-wjk * oNeuronErr);
                }

                double partialDerivative = hNeuron.derivative() * ai * sum;
                double deltaWeight = -this.LEARNING_RATE * partialDerivative;
                double updatedWeight = incomingArc.getWeight() + deltaWeight;
                incomingArc.setWeight(updatedWeight + this.MOMENTUM * incomingArc.getDeltaWeight());
                incomingArc.setDeltaWeight(deltaWeight);
            }}
    }
}
