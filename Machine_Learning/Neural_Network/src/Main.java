public class Main {

    public static void main(String[] args) {
        final double[][] INPUTS = {{1,1}, {1,0}, {0,1}, {0,0}};
        final double[] EXPECTED_OUTPUTS_XOR = {0, 1, 1, 0};
        final double[] EXPECTED_OUTPUTS_AND = {1, 0, 0, 0};
        final double[] EXPECTED_OUTPUTS_OR = {1, 1, 1, 0};
        final int MAX_EPOCH = 20000;
        final double MIN_ERROR = 0.001;

        NeuralNetwork nn = new NeuralNetwork();
        nn.start(INPUTS, EXPECTED_OUTPUTS_XOR, MAX_EPOCH, MIN_ERROR);
    }
}
