package ca.nengo.ui.configurable.nodes;

import java.util.Map;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.impl.PoissonSpikeGenerator;
import ca.nengo.model.neuron.impl.PoissonSpikeGenerator.SigmoidNeuronFactory;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PIndicatorPDF;

public class CSigmoidNeuronFactory extends AbstractNode {
	private static final Property pMaxRate = pMaxRateDefault;
	
	private static final Property pInflection = new PIndicatorPDF(
			"Inflection",
			"Range of x-values for the center point of the sigmoid",
			-1,
			1);
	private static final Property pSlope = new PIndicatorPDF(
			"Slope",
			"Range of slopes for the sigmoid",
			1,
			10);
	
	private static final Property[] zSchema = new Property[]
			{pSlope, pInflection, pMaxRate};

    public CSigmoidNeuronFactory() {
        super("Sigmoid Neuron", SigmoidNeuronFactory.class);
    }

    @Override protected NodeFactory createNodeFactory(Map<Property, Object> configuredProperties) {
        IndicatorPDF slope = (IndicatorPDF) configuredProperties.get(pSlope);
        IndicatorPDF inflection = (IndicatorPDF) configuredProperties.get(pInflection);
        IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.get(pMaxRate);

        return new PoissonSpikeGenerator.SigmoidNeuronFactory(slope, inflection, maxRate);
    }

    @Override public Property[] getSchema() {
        return zSchema;
    }

}