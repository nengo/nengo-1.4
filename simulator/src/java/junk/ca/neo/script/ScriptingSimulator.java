/*
 * Created on 8-Jun-2006
 */
package ca.neo.script;

/**
 * Script-oriented facade for running network simulations.  
 * 
 * @author Bryan Tripp
 */
public interface ScriptingSimulator {

	/**
	 * Sets the simulator to run simulations of the given network.
	 * 
	 * TO DO: ScriptingNetwork should re-initialize the underlying simulator any time there is a change to the network following this call
	 * 
	 * @param network The ScriptingNetwork to simulate
	 */
	public void setNetwork(ScriptingNetwork network);
	
	/**
	 * @param ensembleName Name of an ensemble in the network 
	 * @param neuron Integer index (starting from 0) of a neuron in the specified ensemble 
	 * @return A list of the variables that can be probed in the specified neuron
	 */	
	public String[] getProbeableVariables(String ensembleName, int neuron);
	
	/**
	 * Instructs the simulator to record the time history of a certain variable during subsequent 
	 * simulations. 
	 * 
	 * @param probeName A unique name for the probe (used in subsequent calls to getProbeTimes() and 
	 * 		getProbeValues())  
	 * @param ensembleName Name of Ensemble that contains the neuron to record from
	 * @param neuron Integer index of the neuron in the specified ensemble (from 0)
	 * @param state Name of the variable to be recorded (eg 'membrane potential'). This must be one of
	 * 		the values returned by getProbeableVariables(...) 
	 */
	public void connectProbe(String probeName, String ensembleName, int neuron, String variable);
	
	/**
	 * Instructs the simulator to no longer record a certain variable
	 *  
	 * @param probeName Name of probe as specified in connectProbe
	 */
	public void disconnectProbe(String probeName);

	/**
	 * Runs a simulation. 
	 * 
	 * @param startTime Simulation time at which running is to start (s) 
	 * @param endTime Simulation time at which running is to end (s)
	 * @param stepSize Size of time step at which network is to run (s). Individual 
	 * 		neurons may run at shorter time steps.  
	 * @param mode Name of simulation mode in which to run. Valid values include the static 
	 * 		variables of this class
	 */
	public void run(float startTime, float endTime, float stepSize, String mode);
	
	/**
	 * @param probeName Name of a probe that has been connected via connectProbe() 
	 * @return Simulation times at which values were collected in the most recent run
	 */
	public float[] getProbeTimes(String probeName); 
	
	/**
	 * @param probeName Name of a probe that has been connected via connectProbe() 
	 * @return Variable values at getProbeTimes()
	 */
	public float[] getProbeValues(String probeName);  
	
	/**
	 * @param ensemble Name of an ensemble in the simulated network
	 * @param channel Name of a function output channel of the ensemble
	 * @return History of vector state at each time step in the most recent run
	 */
	public float[][] getHistory(String ensemble, String channel);
	
}
