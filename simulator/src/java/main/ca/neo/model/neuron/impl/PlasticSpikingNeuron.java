/*
 * Created on 15-Mar-07
 */
package ca.neo.model.neuron.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.model.plasticity.Plastic;
import ca.neo.model.plasticity.PlasticityRule;

/**
 * A SpikingNeuron with a Plastic SynapticIntegrator. 
 * 
 * @author Bryan Tripp
 */
public class PlasticSpikingNeuron extends SpikingNeuron implements Plastic {

	private static final long serialVersionUID = 1L;

	private Plastic mySynapticIntegrator;
	private Map<String, PlasticityRule> myPlasticityRules;
	
	/**
	 * Note: current = scale * (weighted sum of inputs at each termination) * (radial input) + bias.
	 * 
	 * @param integrator SynapticIntegrator used to model dendritic/somatic integration of inputs
	 * 		to this Neuron <b>(must be Plastic)</b>
	 * @param generator SpikeGenerator used to model spike generation at the axon hillock of this 
	 * 		Neuron
	 * @param scale A coefficient that scales summed input    
	 * @param bias A bias current that models unaccounted-for inputs and/or intrinsic currents 
	 * @param name A unique name for this neuron in the context of the Network or Ensemble to which 
	 * 		it belongs
	 */	
	public PlasticSpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator, float scale, float bias, String name) {
		super(integrator, generator, scale, bias, name);
		
		if ( !(integrator instanceof Plastic) ) {
			throw new IllegalArgumentException("SynapticIntegrator must be Plastic (consider using SpikingNeuron instead)");
		}
		
		mySynapticIntegrator = (Plastic) integrator;
		myPlasticityRules = new HashMap<String, PlasticityRule>(10);
	}

	/**
	 * @see ca.neo.model.neuron.impl.SpikingNeuron#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		Origin[] origins = getOrigins();
		
		//have to set origins before super runs the synaptic integrator
		Iterator<PlasticityRule> it = myPlasticityRules.values().iterator();
		while (it.hasNext()) {
			PlasticityRule rule = it.next();

			for (int i = 0; i < origins.length; i++) {
				InstantaneousOutput output = origins[i].getValues();
				//TODO: spikes should be allowed once spiking rules are supported
				if (output instanceof RealOutput) {
					rule.setOriginState(origins[i].getName(), ((RealOutput) origins[i].getValues()).getValues());									
				}
			}
		}
		
		super.run(startTime, endTime);		
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.neo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
		myPlasticityRules.put(terminationName, rule);
		mySynapticIntegrator.setPlasticityRule(terminationName, rule);
	}

}
