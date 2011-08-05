package ca.nengo.model.neuron.impl;

import ca.nengo.math.PDF;
import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.LinearExponentialTermination;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.SynapticIntegrator;
import ca.nengo.model.neuron.impl.LinearSynapticIntegrator;
import ca.nengo.model.neuron.impl.ExpandableSpikingNeuron;

public class GruberNeuronFactory implements NodeFactory {
	private static final long serialVersionUID = 1L;

	/**
	 * Name of distinguished dopamine termination. 
	 */
	public static final String DOPAMINE = "dopamine";
	
	private PDF myScalePDF;
	private PDF myBiasPDF;
	
	public GruberNeuronFactory(PDF scale, PDF bias) {
//		myScalePDF = new IndicatorPDF(2.9f, 3.1f);
//		myBiasPDF = new IndicatorPDF(-2f, 3f);
		myScalePDF = scale;
		myBiasPDF = bias;
	}
	
	public Node make(String name) throws StructuralException {
		GruberSpikeGenerator generator = new GruberSpikeGenerator();
		LinearSynapticIntegrator integrator = new LinearSynapticIntegrator(.001f, Units.uAcm2);
		LinearExponentialTermination dopamineTermination 
			= (LinearExponentialTermination) integrator.addTermination(DOPAMINE, new float[]{1}, .05f, true);
		
		float scale = myScalePDF.sample()[0];
//		float scale = 3;
		float bias = myBiasPDF.sample()[0];
		return new GruberNeuron(integrator, generator, scale, bias, name, dopamineTermination);
	}
	
	/**
	 * @see ca.nengo.model.impl.NodeFactory#getTypeDescription()
	 */
	public String getTypeDescription() {
		return "Adapting LIF Neuron";
	}
	
	
	public static class GruberNeuron extends ExpandableSpikingNeuron {

		private static final long serialVersionUID = 1L;
		
		private LinearExponentialTermination myDopamineTermination;
		private GruberSpikeGenerator mySpikeGenerator;
		
		public GruberNeuron(SynapticIntegrator integrator, GruberSpikeGenerator generator, float scale, float bias, String name, 
				LinearExponentialTermination dopamineTermination) {
			super(integrator, generator, scale, bias, name);
			
			myDopamineTermination = dopamineTermination;
			mySpikeGenerator = generator;
		}

		@Override
		public void run(float startTime, float endTime) throws SimulationException {
			float dopamine = myDopamineTermination.getOutput();
			mySpikeGenerator.setDopamine(dopamine);
			
			super.run(startTime, endTime);
		}
	}
	

}