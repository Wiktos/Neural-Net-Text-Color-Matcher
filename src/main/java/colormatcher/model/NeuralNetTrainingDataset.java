package main.java.colormatcher.model;

import javafx.util.Pair;

public class NeuralNetTrainingDataset {

    private final Color[] inputs;
    private final double[][] desiredOutputs;

    public NeuralNetTrainingDataset(Color[] inputs, double[][] desiredOutputs) throws IllegalArgumentException {
        if(!checkConstructorInputs(inputs.length, desiredOutputs.length)) {
            throw new IllegalArgumentException("Number of inputs does not equal number of desired outputs");
        }

        this.inputs = inputs;
        this.desiredOutputs = desiredOutputs;
    }

    public Pair<Color, double[]> getPairOfInputAndDesiredOutput(int idx) throws IllegalArgumentException{
        if(!isIndexInRange(idx, inputs.length) || !isIndexInRange(idx, desiredOutputs.length)) {
            throw new IllegalArgumentException("Index out of range");
        }

        return new Pair<>(inputs[idx], desiredOutputs[idx]);
    }

    public Color getInputData(int idx) throws IllegalArgumentException {
        if(!isIndexInRange(idx, inputs.length)) {
            throw new IllegalArgumentException("Index out of range");
        }

        return inputs[idx];
    }

    public double[] getDesiredOutputData(int idx) throws IllegalArgumentException {
        if(!isIndexInRange(idx, desiredOutputs.length)) {
            throw new IllegalArgumentException("Index out of range");
        }

        return desiredOutputs[idx];
    }

    private boolean checkConstructorInputs(int len1, int len2) {
        return len1 == len2;
    }

    private boolean isIndexInRange(int idx, int upperBound){
        return idx >= 0 && idx < upperBound;
    }
}
