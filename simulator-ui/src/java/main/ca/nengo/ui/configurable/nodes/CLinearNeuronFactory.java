package ca.nengo.ui.configurable.nodes;

import java.util.Map;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.impl.PoissonSpikeGenerator;
import ca.nengo.model.neuron.impl.PoissonSpikeGenerator.LinearNeuronFactory;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PBoolean;

/**
 * Constructable Linear Neuron Factory
 * 
 * @author Shu Wu
 */
public class CLinearNeuronFactory extends AbstractNode {
	private static final Property pRectified = new PBoolean(
			"Rectified",
			"Whether to constrain the neuron outputs to be positive",
			true);

	private static final Property pIntercept = pInterceptDefault;
	private static final Property pMaxRate = pMaxRateDefault;
    
	private static final Property[] zSchema = new Property[]
			{pMaxRate, pIntercept, pRectified};

    public CLinearNeuronFactory() {
        super("Linear Neuron", LinearNeuronFactory.class);
    }

    @Override protected NodeFactory createNodeFactory(Map<Property, Object> configuredProperties) {
        IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.get(pMaxRate);
        IndicatorPDF intercept = (IndicatorPDF) configuredProperties.get(pIntercept);
        Boolean rectified = (Boolean) configuredProperties.get(pRectified);

        LinearNeuronFactory factory = new PoissonSpikeGenerator.LinearNeuronFactory(
        		maxRate, intercept, rectified);

        return factory;
    }

    @Override public Property[] getSchema() {
        return zSchema;
    }

}