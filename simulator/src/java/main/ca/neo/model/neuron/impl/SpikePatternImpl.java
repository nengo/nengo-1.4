/*
 * Created on 22-Jun-2006
 */
package ca.neo.model.neuron.impl;

import ca.neo.model.neuron.SpikePattern;

/**
 * Default implementation of SpikePattern. 
 * 
 * @author Bryan Tripp
 */
public class SpikePatternImpl implements SpikePattern {

	private static final long serialVersionUID = 1L;
	
	int[] myIndices;
	float[][] mySpikeTimes;
	
	/**
	 * @param neurons Number of neurons in the Ensemble that this SpikePattern belongs to
	 */
	public SpikePatternImpl(int neurons) {
		myIndices = new int[neurons];
		
		mySpikeTimes = new float[neurons][];		
		for (int i = 0; i < neurons; i++) {
			mySpikeTimes[i] = new float[100];
		}
	}
	
	/**
	 * @param neuron Index of neuron
	 * @param time Spike time
	 */
	public void addSpike(int neuron, float time) {
		if (myIndices[neuron] == mySpikeTimes[neuron].length) {
			mySpikeTimes[neuron] = expand(mySpikeTimes[neuron]);
		}
		
		mySpikeTimes[neuron][myIndices[neuron]++] = time;
	}

	/**
	 * @see ca.neo.model.neuron.SpikePattern#getNumNeurons()
	 */
	public int getNumNeurons() {
		return myIndices.length;
	}

	/**
	 * @see ca.neo.model.neuron.SpikePattern#getSpikeTimes(int)
	 */
	public float[] getSpikeTimes(int neuron) {
		return contract(mySpikeTimes[neuron], myIndices[neuron]);
	}
	
	private static float[] expand(float[] list) {
		float[] result = new float[Math.round((float) list.length * 1.5f)]; //grow by 50%
		System.arraycopy(list, 0, result, 0, list.length);
		return result;
	}
	
	private static float[] contract(float[] list, int index) {
		float[] result = new float[index];
		System.arraycopy(list, 0, result, 0, index);
		return result;
	}

}
