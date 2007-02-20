/*
 * Created on 22-Jun-2006
 */
package ca.neo.util;

import java.io.Serializable;

/**
 * A temporal pattern of spiking in an Ensemble. 
 * 
 * @author Bryan Tripp
 */
public interface SpikePattern extends Serializable {

	/**
	 * @return Number of neurons in the ensemble 
	 */
	public int getNumNeurons();
	
	/**
	 * @param neuron Index of a neuron in the ensemble (from 0)
	 * @return Times at which neuron spiked since the Ensemble was last reset
	 */
	public float[] getSpikeTimes(int neuron);
}
