/*
 * Created on 15-Mar-07
 */
package ca.neo.model.neuron.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.neo.model.ExpandableNode;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.model.plasticity.Plastic;
import ca.neo.model.plasticity.PlasticityRule;

/**
 * A SpikingNeuron with an ExpandableSynapticIntegrator that is Plastic. 
 * 
 * @author Bryan Tripp
 */
public class PlasticExpandableSpikingNeuron extends SpikingNeuron implements Plastic, ExpandableNode {

	private static final long serialVersionUID = 1L;
	private static Logger ourLogger = Logger.getLogger(PlasticExpandableSpikingNeuron.class);

	private Plastic mySynapticIntegrator;
//	private Map<String, PlasticityRule> myPlasticityRules;
	
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
	public PlasticExpandableSpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator, float scale, float bias, String name) {
		super(integrator, generator, scale, bias, name);
		
		if ( !(getIntegrator() instanceof Plastic) ) {
			throw new IllegalArgumentException("SynapticIntegrator must be Plastic (consider using SpikingNeuron instead)");
		}
		
		if ( !(getIntegrator() instanceof ExpandableSynapticIntegrator) ) {
			ourLogger.warn("Given SynapticIntegrator is not an ExpandableSynapticIntegrator (expansion-related methods will fail");
		}
		
		mySynapticIntegrator = (Plastic) integrator;
//		myPlasticityRules = new HashMap<String, PlasticityRule>(10);
	}
	
	/**
	 * @see ca.neo.model.neuron.impl.SpikingNeuron#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		Origin[] origins = getOrigins();
		
		//have to set origins before super runs the synaptic integrator
		String[] ruleNames = getPlasticityRuleNames();
		for (int i = 0; i < ruleNames.length; i++) {
			PlasticityRule rule;
			try {
				rule = getPlasticityRule(ruleNames[i]);
				if (rule != null) {
					for (int j = 0; j < origins.length; j++) {
						InstantaneousOutput output = origins[j].getValues();
						rule.setOriginState(origins[j].getName(), output, endTime);									
					}
				}
			} catch (StructuralException e) {
				throw new SimulationException("Expected plasticity rule unavailable: " + ruleNames[i], e);
			}
		}
//		Iterator<PlasticityRule> it = myPlasticityRules.values().iterator();
//		while (it.hasNext()) {
//			PlasticityRule rule = it.next();
//
//			for (int i = 0; i < origins.length; i++) {
//				InstantaneousOutput output = origins[i].getValues();
//				rule.setOriginState(origins[i].getName(), output, endTime);									
//			}
//		}
		
		super.run(startTime, endTime);		
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.neo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
//		myPlasticityRules.put(terminationName, rule);
		mySynapticIntegrator.setPlasticityRule(terminationName, rule);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityInterval(float)
	 */
	public void setPlasticityInterval(float time) {
		mySynapticIntegrator.setPlasticityInterval(time);
	}
	
	/**
	 * @see ca.neo.model.ExpandableNode#addTermination(java.lang.String, float[][], float, boolean)
	 */
	public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException {
		if ( !(mySynapticIntegrator instanceof ExpandableSynapticIntegrator) ) {
			throw new StructuralException("Underlying SynapticIntegrator is not expandable");
		}
		if (weights.length != 1) {
			throw new StructuralException("Weights matrix must have one row (has " + weights.length + ")");			
		}

		return ((ExpandableSynapticIntegrator) mySynapticIntegrator).addTermination(name, weights[0], tauPSC, modulatory);
	}

	/**
	 * @see ca.neo.model.ExpandableNode#getDimension()
	 */
	public int getDimension() {
		return 1;
	}

	/**
	 * @see ca.neo.model.ExpandableNode#removeTermination(java.lang.String)
	 */
	public void removeTermination(String name) throws StructuralException {
		if ( !(mySynapticIntegrator instanceof ExpandableSynapticIntegrator) ) {
			throw new StructuralException("Underlying SynapticIntegrator is not expandable");
		}
		
		((ExpandableSynapticIntegrator) mySynapticIntegrator).removeTermination(name);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityInterval()
	 */
	public float getPlasticityInterval() {
		return mySynapticIntegrator.getPlasticityInterval();
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityRule(java.lang.String)
	 */
	public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException {
		return mySynapticIntegrator.getPlasticityRule(terminationName);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityRuleNames()
	 */
	public String[] getPlasticityRuleNames() {
		return mySynapticIntegrator.getPlasticityRuleNames();
	}

}
