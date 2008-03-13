package ca.neo.model.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SpikeOutput;
import ca.neo.model.Termination;
import ca.neo.util.Configuration;
import ca.neo.util.impl.ConfigurationImpl;

/**
 * <p>A Termination at which incoming spikes induce exponentially decaying post-synaptic 
 * currents that are combined linearly. Real-valued spike rate inputs have approximately 
 * the same effect over time as actual (boolean) spike inputs at the same rate.</p> 
 * 
 * <p>Each input is weighted (weights specified in the constructor) so that the time integral 
 * of the post-synaptic current arising from one spike equals the weight. The time integral 
 * of post-synaptic current arising from real-valued input of 1 over a period of 1s also  
 * equals the weight. This means that spike input and spike-rate input have roughly the 
 * same effects.</p> 
 *  
 * @author Bryan Tripp
 */
public class LinearExponentialTermination implements Termination {

	private static final long serialVersionUID = 1L;
	
	private Node myNode;
	private String myName;
	private float[] myWeights;
	private float myTauPSC;
	
	private float myCurrent = 0;
	private float myNetSpikeInput;
	private float myNetRealInput;
	private InstantaneousOutput myRawInput;
	
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param node The parent Node
	 * @param name Name of the Termination (must be unique within the Neuron or Ensemble to 
	 * 		which it is attached)
	 * @param weights Ordered list of synaptic weights of each input channel 
	 * @param tauPSC Time constant of exponential post-synaptic current decay  
	 */
	public LinearExponentialTermination(Node node, String name, float[] weights, float tauPSC) {
		myNode = node;
		myName = name;
		myWeights = weights;
		myTauPSC = tauPSC;
		
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.addProperty(Termination.TAU_PSC, Float.class, new Float(tauPSC));
		myConfiguration.addProperty(Termination.MODULATORY, Boolean.class, new Boolean(false));
		myConfiguration.addProperty(Termination.WEIGHTS, float[][].class, new float[][]{myWeights});
	}
	
	/**
	 * Resets current to 0 (randomize arg is ignored). 
	 *  
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myCurrent = 0;
		myRawInput = null;
		myNetRealInput = 0;
		myNetSpikeInput = 0;
	}

	/**
	 * @see ca.neo.model.Termination#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Termination#getDimensions()
	 */
	public int getDimensions() {
		return myWeights.length;
	}
	
	/**
	 * @return List of synaptic weights for each input channel
	 */
	public float[] getWeights() {
		return myWeights;
	}
	
	/**
	 * @return The most recent input to the Termination
	 */
	public InstantaneousOutput getInput() {
		return myRawInput;
	}
	
	/**
	 * @return The most recent output of the Termination (after summation and dynamics)
	 */
	public float getOutput() {
		return myCurrent;
	}

	/**
	 * @param values Can be either SpikeOutput or RealOutput 
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Input must have dimension " + getDimensions());
		}
		
		myRawInput = values;

		myNetSpikeInput = (values instanceof SpikeOutput) ? combineSpikes((SpikeOutput) values, myWeights) : 0;
		myNetRealInput = (values instanceof RealOutput) ? combineReals((RealOutput) values, myWeights) : 0;
	}
	
	/**
	 * Updates net post-synaptic current for this Termination according to new inputs and exponential 
	 * dynamics applied to previous inputs. 
	 * 
	 * The arguments provide flexibility in updating the current, in terms of whether spike inputs are 
	 * applied, for how long real-valued inputs are applied, and for how long the net current decays 
	 * exponentially. A usage example follows:  
	 * 
	 * Suppose the SynapticIntegrator that contains this Termination models each network time step in three  
	 * steps of its own. Suppose also that the SynapticIntegrator uses updateCurrent() to find the current 
	 * at the beginning and end of each network time step, and at the two points in between. A reasonable way 
	 * for the SynapticIntegrator to use updateCurrent() in this scenario would be as follows (the variable 
	 * tau represents 1/3 of the length of the network time step):
	 *  
	 * <ol><li>At the beginning of the network time step call updateCurrent(true, tau, 0) to model the 
	 * application of spikes and real-valued inputs from the previous time step, without decaying them.</li>
	 * <li>To advance to each of the two intermediate times call updateCurrent(false, tau, tau). Spikes  
	 * are not re-applied (a given spike should only be applied once). Real-valued inputs are continuous in 
	 * time, so they are integrated again. Currents also begin to decay.</li>
	 * <li>At the end of the network time step call updateCurrent(false, 0, tau). Real-valued inputs for this 
	 * time interval are not applied at the end of this network time step, since they will be applied at the 
	 * (identical) beginning of the next network time step. </li><ol> 
	 * 
	 * <p>The essential points are that spikes are only applied once during a network time step, and that 
	 * the total integration and decay times over a network time step both equal the length of the network 
	 * time step.</p>   
	 * 
	 * @param applySpikes True if spike inputs are to be applied
	 * @param integrationTime Time over which real-valued inputs are to be integrated
	 * @param decayTime Time over which post-synaptic currents are to decay 
	 * @return Net synaptic current flowing into this termination after specified input and decay 
	 */
	public float updateCurrent(boolean applySpikes, float integrationTime, float decayTime) {
		if (decayTime > 0) {
			//TODO: is there a correction we can do here when tau isn't much larger than the timestep? (will decay to zero if tau=step)   
			myCurrent = myCurrent - myCurrent * ( 1f/myTauPSC ) * decayTime;
		}
		
		if (applySpikes) {
			myCurrent = myCurrent + myNetSpikeInput / myTauPSC; //normalized so that unweighted PSC integral is 1
		}
		if (integrationTime > 0) {
			//normalized so that real input x has same current integral as x spike inputs/s (with same weight)
			myCurrent = myCurrent + myNetRealInput * integrationTime / myTauPSC; //this might be normalizing in the wrong direction
		}
		
		return myCurrent;
	}

	/** 
	 * @see ca.neo.util.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/** 
	 * @see ca.neo.util.Configurable#propertyChange(java.lang.String, java.lang.Object)
	 */
	public void propertyChange(String propertyName, Object newValue) {
		if (propertyName.equals(Termination.TAU_PSC)) {
			myTauPSC = ((Float) newValue).floatValue();
		} 		
	}
	
	private static float combineSpikes(SpikeOutput input, float[] weights) {
		float result = 0;
		boolean[] spikes = input.getValues();
		
		for (int i = 0; i < spikes.length; i++) {
			if (spikes[i]) {
				result += weights[i];
			}
		}			
	
		return result;
	}
	
	private static float combineReals(RealOutput input, float[] weights) {
		float result = 0;
		float[] reals = input.getValues();
		
		for (int i = 0; i < reals.length; i++) {
			result += weights[i] * reals[i];
		}			
		
		return result;		
	}

	/**
	 * @see ca.neo.model.Termination#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

	@Override
	public Termination clone() throws CloneNotSupportedException {
		LinearExponentialTermination result = new LinearExponentialTermination(myNode, myName, myWeights.clone(), myTauPSC);
		result.myCurrent = myCurrent;
		result.myNetRealInput = myNetRealInput;
		result.myNetSpikeInput = myNetSpikeInput;
		result.myRawInput = myRawInput.clone();
		return result;
	}

}
