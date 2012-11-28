package ca.nengo.ui.configurable.nodes;

import java.util.Map;

import ca.nengo.model.impl.NodeFactory;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.models.AbstractModel;
import ca.nengo.ui.configurable.properties.PFloat;
import ca.nengo.ui.configurable.properties.PIndicatorPDF;

public abstract class AbstractNode extends AbstractModel {
	
	// Parameters common to many neurons
    protected static final Property pInterceptDefault = new PIndicatorPDF(
    		"Intercept",
    		"Range of the uniform distribution of neuron x-intercepts, " +
    		"(typically -1 to 1)",
    		-1,
    		1);
    protected static final Property pMaxRateDefault = new PIndicatorPDF(
    		"Max rate [Hz]",
    		"Maximum neuron firing rate [10 to 100Hz for cortex]",
    		10,
    		100);
    protected static final Property pTauRCDefault = new PFloat(
    		"tauRC [s]",
    		"Membrane time constant, in seconds [typically ~0.02s]",
    		0.02f,
    		0f,
    		Float.MAX_VALUE);
    protected static final Property pTauRefDefault = new PFloat(
    		"tauRef [s]",
    		"Refractory period, in seconds [typically ~0.002s]",
    		0.002f,
    		0f,
    		Float.MAX_VALUE);
	
    private final String name;
    private final Class<? extends NodeFactory> type;

    public AbstractNode(String name, Class<? extends NodeFactory> type) {
        this.name = name;
        this.type = type;
    }

    protected final Object configureModel(Map<Property, Object> configuredProperties) throws ConfigException {
        NodeFactory nodeFactory = createNodeFactory(configuredProperties);

        if (!getType().isInstance(nodeFactory)) {
            throw new ConfigException("Expected type: " + getType().getSimpleName() + " Got: "
                    + nodeFactory.getClass().getSimpleName());
        } else {
            return nodeFactory;
        }
    }

    protected abstract NodeFactory createNodeFactory(
    		Map<Property, Object> configuredProperties) throws ConfigException;

    public Class<? extends NodeFactory> getType() {
        return type;
    }

    public final String getTypeName() {
        return name;
    }

    @Override public String toString() {
        return this.name;
    }

}