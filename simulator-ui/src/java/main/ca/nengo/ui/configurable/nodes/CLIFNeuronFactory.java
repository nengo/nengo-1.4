package ca.nengo.ui.configurable.nodes;

import java.util.Map;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.impl.LIFNeuronFactory;
import ca.nengo.ui.configurable.Property;

public class CLIFNeuronFactory extends AbstractNode {
    private static final Property pIntercept = pInterceptDefault;
    private static final Property pMaxRate = pMaxRateDefault;
    private static final Property pTauRC = pTauRCDefault;
    private static final Property pTauRef = pTauRefDefault;

    private static final Property[] zSchema = new Property[]
    		{pTauRC, pTauRef, pMaxRate, pIntercept};

    public CLIFNeuronFactory() {
        super("LIF Neuron", LIFNeuronFactory.class);
    }

    @Override protected NodeFactory createNodeFactory(Map<Property, Object> configuredProperties) {
        Float tauRC = (Float) configuredProperties.get(pTauRC);
        Float tauRef = (Float) configuredProperties.get(pTauRef);
        IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.get(pMaxRate);
        IndicatorPDF intercept = (IndicatorPDF) configuredProperties.get(pIntercept);
        return new LIFNeuronFactory(tauRC, tauRef, maxRate, intercept);
    }
    
    @Override public Property[] getSchema() {
        return zSchema;
    }

}