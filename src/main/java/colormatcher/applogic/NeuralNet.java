package main.java.colormatcher.applogic;

import javafx.util.Pair;

public class NeuralNet {

    /*
    Training data

    Input : Colors are coded using values RGB which are in range [0, 1]
            3 zeros means black
            3 ones means white

    Output : First number indicate white second black
    */
    private final double LEARNING_RATE = 0.9;

    private final static Color[] STANDARD_TRAINING_INPUTS = new Color[]{new Color(0.6, 0.6, 0.6),
                                                new Color(0.4, 0.4, 0.4)};
    private final double[][] STANDARD_DESIRED_OUTPUT = new double[][]{{0.0, 1.0},
                                                            {1.0, 0.0}};

    private NeuralNetTrainingDataset trainingDataset = new NeuralNetTrainingDataset(STANDARD_TRAINING_INPUTS, STANDARD_DESIRED_OUTPUT);


    private Synapse syn0;
    private Synapse syn1;

    public NeuralNet() {
        syn0 = new Synapse(3, 2);
        syn1 = new Synapse(2, 2);

        performFullTraining(6000);
    }

    public NeuralNet(NeuralNetTrainingDataset trainingDataset) {
        syn0 = new Synapse(3, 2);
        syn1 = new Synapse(2, 2);

        this.trainingDataset = trainingDataset;

        performFullTraining(6000);
    }

    public NeuralNetLayer think(Color color) {
        NeuralNetLayer hiddenLayer = computeHiddenLayer(color);
        NeuralNetLayer outputLayer = computeOutputLayer(hiddenLayer);

        return outputLayer;
    }

    private void performFullTraining(int iter) {
        if(iter < 0) {
            throw new IllegalArgumentException("Number of iteration less than 0");
        }

        for (int i = 0; i < iter; i++) {
            oneTraining(trainingDataset.getPairOfInputAndDesiredOutput(i & 1));
        }
    }

    private void oneTraining(Pair<Color, double[]> inputWithDesiredOutput) {
        NeuralNetLayer hiddenLayer = computeHiddenLayer(inputWithDesiredOutput.getKey());
        NeuralNetLayer outputLayer = computeOutputLayer(hiddenLayer);

        backPropagation(outputLayer, hiddenLayer, inputWithDesiredOutput);
    }

    private NeuralNetLayer computeHiddenLayer(Color X){
        NeuralNetLayer retv = new NeuralNetLayer(2);

        double sum = dotProduct(X, syn0, 0);
        retv.setNodeValue(0, sigmoid(sum));
        sum = dotProduct(X, syn0, 1);
        retv.setNodeValue(1, sigmoid(sum));

        return retv;
    }

    private NeuralNetLayer computeOutputLayer(NeuralNetLayer hiddenLayer){
        NeuralNetLayer retv = new NeuralNetLayer(2);

        double sum = dotProduct(hiddenLayer, syn1, 0);
        retv.setNodeValue(0, sigmoid(sum));

        sum = dotProduct(hiddenLayer, syn1, 1);
        retv.setNodeValue(1, sigmoid(sum));

        return retv;
    }

    private void backPropagation(NeuralNetLayer outputLayer, NeuralNetLayer hiddenLayer, Pair<Color, double[]> inputAndDesiredOut ) {
        double[] X = inputAndDesiredOut.getKey().asArray();
        double[] desiredOut = inputAndDesiredOut.getValue();

        double[] outputError = new double[2];
        outputError[0] = desiredOut[0] - outputLayer.getNodeValue(0);
        outputError[1] = desiredOut[1] - outputLayer.getNodeValue(1);

        double[] outputDelta = new double[]{(outputError[0] * sigmoidDerivative(outputLayer.getNodeValue(0))),
                (outputError[1] * sigmoidDerivative(outputLayer.getNodeValue(1)))};

        double[][] syn1Delta = new double[2][2];
        for(int i = 0; i < syn1Delta.length; i++) {
            for (int j = 0; j < syn1Delta[i].length; j++) {
                syn1Delta[j][i] = outputDelta[i] * hiddenLayer.getNodeValue(j);
            }
        }

        double[] hiddenDelta = new double[]{((outputDelta[0] * syn1.getWeight(0, 0) + outputDelta[1] * syn1.getWeight(0, 1)) * sigmoidDerivative(hiddenLayer.getNodeValue(0))),
                                             ((outputDelta[0] * syn1.getWeight(1, 0) + outputDelta[1] * syn1.getWeight(1, 1)) * sigmoidDerivative(hiddenLayer.getNodeValue(1)))};

        double[][] syn0Delta = new double[3][2];
        for (int i = 0; i < syn1.getRow(); i++) {
            for (int j = 0; j < syn1.getCol(); j++) {
                syn0Delta[i][j] = hiddenDelta[j] * X[i];
            }
        }

        for (int i = 0; i < syn1.getRow(); i++) {
            for (int j = 0; j < syn1.getCol(); j++) {
                syn1.setWeight(i, j,  syn1.getWeight(i, j) + LEARNING_RATE * syn1Delta[i][j]);
            }
        }

        for (int i = 0; i < syn0.getRow(); i++) {
            for (int j = 0; j < syn0.getCol(); j++) {
                syn0.setWeight(i, j,  syn0.getWeight(i, j) + LEARNING_RATE * syn0Delta[i][j]);
            }
        }
    }

    private double dotProduct(Color input, Synapse syn, int synNodeIdx) {
        double[] colArr = input.asArray();
        double sum = 0.0;
        for (int i = 0; i < colArr.length; i++) {
            sum += colArr[i] * syn0.getWeight(i, synNodeIdx);
        }
        return sum;
    }

    private double dotProduct(NeuralNetLayer layer, Synapse syn, int synNodeIdx) {
        double sum = 0.0;
        for (int i = 0; i < layer.length(); i++) {
            sum += layer.getNodeValue(i) * syn.getWeight(i, synNodeIdx);
        }
        return sum;
    }

    private double sigmoid(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double sigmoidDerivative(final double x) {
        return x * (1 - x);
    }
}
