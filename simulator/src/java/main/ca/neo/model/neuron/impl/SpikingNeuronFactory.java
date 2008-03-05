/**
 * 
 */
package ca.neo.model.neuron.impl;

import ca.neo.config.ConfigUtil;
import ca.neo.math.PDF;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.model.ExpandableNode;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.model.plasticity.Plastic;

/**
 * Creates spiking neurons by delegating to a SynapticIntegratorFactory and a 
 * SpikeGeneratorFactory. 
 * 
 * @author Bryan Tripp
 */
public class SpikingNeuronFactory implements NodeFactory {

	private SynapticIntegratorFactory myIntegratorFactory;
	private SpikeGeneratorFactory myGeneratorFactory;
	private PDF myScale;
	private PDF myBias;
	
	public SpikingNeuronFactory(SynapticIntegratorFactory intFact, SpikeGeneratorFactory genFact, PDF scale, PDF bias) {
		myIntegratorFactory = intFact;
		myGeneratorFactory = genFact;
		myScale = scale;
		myBias = bias;
	}
	
	public SynapticIntegratorFactory getIntegratorFactory() {
		return myIntegratorFactory;
	}
	
	public void setIntegratorFactory(SynapticIntegratorFactory factory) {
		myIntegratorFactory = factory;
	}
	
	public SpikeGeneratorFactory getGeneratorFactory() {
		return myGeneratorFactory;
	}
	
	public void setGeneratorFactory(SpikeGeneratorFactory factory) {
		myGeneratorFactory = factory;
	}
	
	/**
	 * @see ca.neo.model.impl.NodeFactory#getTypeDescription()
	 */
	public String getTypeDescription() {
		return "Customizable Neuron";
	}

	/**
	 * @see ca.neo.model.impl.NodeFactory#make(java.lang.String)
	 */
	public Node make(String name) throws StructuralException {
		SynapticIntegrator integrator = myIntegratorFactory.make();
		SpikeGenerator generator = myGeneratorFactory.make();
		float scale = myScale.sample()[0];
		float bias = myBias.sample()[0];
		
		Node result = null;
		
		if (integrator instanceof ExpandableNode && integrator instanceof Plastic) {
			result = new PlasticExpandableSpikingNeuron(integrator, generator, scale, bias, name);
		} else {
			result = new SpikingNeuron(integrator, generator, scale, bias, name);
		}
		
		return result;		
	}
	
	public static void main(String[] args) {
		SpikingNeuronFactory factory = new SpikingNeuronFactory(
				new LinearSynapticIntegrator.Factory(), 
				new LIFSpikeGenerator.Factory(), 
				new IndicatorPDF(1), 
				new IndicatorPDF(0));
		
		ConfigUtil.configure(null, factory);
	}

}
