package ca.neo.model.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;

/**
 * <p>An Origin that obtains output from an underlying Probeable object.</p>
 *  
 * <p>As an example of use, suppose a Neuron has a SynapticIntegrator with a complex 
 * dendritic morphology, and that it is desired to model a gap junction between 
 * one of these dendrites and a dendrite on another Neuron. If the SynapticIntegrator
 * can provide gap-junctional Origins, there is no problem. But it might not (for 
 * example the implementor of the SynapticIntegrator may not have anticipated this
 * usage). However, if the SynapticIntegrator is Probeable and can be probed for the  
 * appropriate state variables, eg ion concentrations in the compartment of interest, 
 * then this class (ProbeableOrigin) provides a convenient way to model an Origin
 * that outputs the probed information.</p>
 * 
 * <p>For a Neuron, if multi-dimensional state is to be output, it is generally better 
 * to create multiple one-dimensional Outputs than to creat one multi-dimensional Output. 
 * Reasons for this include the following: </p>
 *    
 * <ul><li>As with all Origins, all the output values at a given instant have  
 * the same units. So, if you want to output states with different units, you must use  
 * separate Origins.</li>
 * 
 * <li>Ensembles may combine identically-named Ouputs of different Neurons  
 * into a single Ensemble-level Output (with the same dimension as the number of Neurons that 
 * have that Output). This doesn't work well with multi-dimensional Neuron Outputs. So, if your 
 * Neurons will be grouped into an Ensemble, it's better to stick with 1-D Outputs. The  
 * other option (which seems more convoluted) is to make sure that each Neuron's n-D Output 
 * has a distinct name (ie distinct from the names of the correspoding Outputs of other Neurons 
 * in the same Ensemble). Incorporating a number into the name is one way to do this.</li><ul>
 * 
 * <p>For these reasons, this class supports only 1-dimensional Output, as a way to keep you 
 * out of trouble. This limits its usefulness with Probeables that are Ensembles, but such
 * Probeables probably already provide the needed Outputs anyway.</p>
 * 
 * <p>If you really do want a Neuron to serve as a multi-dimensional Origin, you can do that, 
 * but not with this class. </p> 
 * 
 * @author Bryan Tripp
 */
public class ProbeableOrigin implements Origin {

	private static final long serialVersionUID = 1L;
	
	private Probeable myProbeable;
	private String myStateVariable;
	private int myDimension;
	private String myName;
	private Units myUnits;
	
	/**
	 * @param probeable The Probeable from which to obtain state variables to output
	 * @param state State variable to output 
	 * @param dimension Index of the dimension of the specified state variable that is to be output
	 * @param name Name of this Origin  
	 * @throws StructuralException if there is a problem running an initial Probeable.getHistory() to 
	 * 		ascertain the units.  
	 */
	public ProbeableOrigin(Probeable probeable, String state, int dimension, String name) throws StructuralException {
		myProbeable = probeable;
		myStateVariable = state;
		myDimension = dimension;
		myName = name;
		
		try {
			myUnits = probeable.getHistory(state).getUnits()[dimension];
		} catch (SimulationException e) {
			throw new StructuralException("Problem getting pre-simulation history in order to find state variable units", e);
		}		
	}
	
	/**
	 * @see ca.neo.model.Origin#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @return 1
	 * @see ca.neo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return 1;
	}

	/**
	 * @return The final value in the TimeSeries for the state variable that is retrieved 
	 * 		from the underlying Probeable   
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		float[] times = myProbeable.getHistory(myStateVariable).getTimes();
		float[][] series = myProbeable.getHistory(myStateVariable).getValues();
		
		float result = 0;
		
		if (series.length > 0) {
			result = series[series.length - 1][myDimension];
		}

		return new RealOutputImpl(new float[]{result}, myUnits, times[times.length-1]);
	}
	
}
