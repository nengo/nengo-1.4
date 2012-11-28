package ca.nengo.ui.configurable.nodes;

import java.util.Map;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.impl.ALIFNeuronFactory;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PFloat;
import ca.nengo.ui.configurable.properties.PIndicatorPDF;

public class CALIFNeuronFactory extends AbstractNode {
	private static final Property pIncN = new PIndicatorPDF(
			"Adaptation increment",
			"Increment of adaptation-related ion with each spike",
			0.001f,
			0.200f);
	private static final Property pTauN = new PFloat(
			"Adaptation tau [s]",
			"Time constant of adaptation-related ion, in seconds",
			0.01f,
			0f,
			Float.MAX_VALUE);
    
	private static final Property pIntercept = pInterceptDefault;
	private static final Property pMaxRate = pMaxRateDefault;
	private static final Property pTauRC = pTauRCDefault;
	private static final Property pTauRef = pTauRefDefault;

	private static final Property[] zSchema = new Property[] {pTauRC,
            pTauN, pTauRef, pMaxRate, pIntercept, pIncN};

    public CALIFNeuronFactory() {
        super("Adapting LIF Neuron", ALIFNeuronFactory.class);
    }

    @Override protected NodeFactory createNodeFactory(Map<Property, Object> configuredProperties) {
        Float tauRC = (Float) configuredProperties.get(pTauRC);
        Float tauRef = (Float) configuredProperties.get(pTauRef);
        Float tauN = (Float) configuredProperties.get(pTauN);
        IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.get(pMaxRate);
        IndicatorPDF intercept = (IndicatorPDF) configuredProperties.get(pIntercept);
        IndicatorPDF incN = (IndicatorPDF) configuredProperties.get(pIncN);

        return new ALIFNeuronFactory(maxRate, intercept, incN, tauRef, tauRC, tauN);
    }

    @Override public Property[] getSchema() {
        return zSchema;
    }

}