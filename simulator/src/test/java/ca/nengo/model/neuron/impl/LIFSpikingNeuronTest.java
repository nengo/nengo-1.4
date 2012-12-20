package ca.nengo.model.neuron.impl;

import org.junit.Test;

public class LIFSpikingNeuronTest {
	@Test
	public void testPerformance() {
		int n = 500;
		boolean[] spikes = new boolean[n];
		float[] weights = new float[n];
		for (int i = 0; i < n; i++) {
			spikes[i] = i < n/2.5;
			weights[i] = (float) Math.random() / 20000;
		}
	}
	
	public static void main(String[] args) {
		LIFSpikingNeuronTest test = new LIFSpikingNeuronTest();
		test.testPerformance();
	}
}
