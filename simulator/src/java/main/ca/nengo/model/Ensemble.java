/*
 * Created on May 16, 2006
 */
package ca.nengo.model;

import ca.nengo.util.SpikePattern;

/**
 * <p>
 * A group of Nodes with largely overlapping inputs and outputs.
 * </p>
 * 
 * <p>
 * There are no strict rules for how to group Nodes into Ensembles, but here are
 * some things to consider:
 * 
 * <ul>
 * <li>A group of Nodes that together 'represent' something through a
 * population code should be modelled as an Ensemble. (Also consider using
 * NEFEnsemble to make such representation explicit.) </li>
 * 
 * <li>Making ensembles that correspond to physical structures (e.g. nuclei)
 * and naming them appropriately will make the model clearer.</li>
 * 
 * <li>Outputs from an Ensemble are grouped together and passed to other
 * Ensembles during a simulation, and practical issues may arise from this. For
 * example, putting all your Nodes in a single large ensemble could result in a
 * very large matrix of synaptic weights, which would impair performance. </li>
 * </ul>
 * </p>
 * 
 * <p>
 * The membership of an Ensemble is fixed once the Ensemble is created. This
 * means that the Ensemble model doesn't deal explicitly with growth and death
 * of components during simulation (although you can set input/output weights to
 * zero to mimic this). It also means that an Ensemble isn't a good model of a
 * functional "assembly".
 * </p>
 * 
 * @author Bryan Tripp
 */
public interface Ensemble extends Node {

	/**
	 * @return Nodes that make up the Ensemble
	 */
	public Node[] getNodes();

	/**
	 * This method provides a means of efficiently storing the output of an
	 * Ensemble if the component Nodes have Origins that produce SpikeOutput.
	 * 
	 * @return A SpikePattern containing a record of spikes, provided
	 *         collectSpikes(boolean) has been set to true
	 */
	public SpikePattern getSpikePattern();

	/**
	 * @param collect
	 *            If true, the spike pattern is recorded in subsequent runs and
	 *            is available through getSpikePattern() (defaults to false)
	 */
	public void collectSpikes(boolean collect);

	/**
	 * 
	 * @return true if the spike pattern will be recorded in subsequen runs
	 */
	public boolean isCollectingSpikes();
}
