package ca.nengo.ui.configurable.nodes;

import java.lang.reflect.Constructor;
import java.util.Map;

import ca.nengo.config.ClassRegistry;
import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.neuron.impl.SpikeGeneratorFactory;
import ca.nengo.model.neuron.impl.SpikingNeuronFactory;
import ca.nengo.model.neuron.impl.SynapticIntegratorFactory;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PIndicatorPDF;
import ca.nengo.ui.configurable.properties.PListSelector;

/**
 * Customizable Neuron Factory Description Schema
 * 
 * @author Shu Wu
 */
public class CSpikingNeuronFactory extends AbstractNode {
	private static final Property pBias = new PIndicatorPDF(
			"bias",
			"Range of biases for the spiking neurons",
			-1,
			1);
	private static final Property pScale = new PIndicatorPDF(
			"scale",
			"Range of gains for the spiking neurons",
			0.5f,
			2.0f);

    private Property pSpikeGenerator;
    private Property pSynapticIntegrator;

    public CSpikingNeuronFactory() {
        super("Customizable Neuron", SpikingNeuronFactory.class);
    }

    private Object constructFromClass(Class<?> type) throws ConfigException {
        try {
            Constructor<?> ct = type.getConstructor();
            try {
                return ct.newInstance();
            } catch (Exception e) {
                throw new ConfigException("Error constructing " + type.getSimpleName() + ": "
                        + e.getMessage());
            }
        } catch (SecurityException e1) {
            e1.printStackTrace();
            throw new ConfigException("Security Exception");
        } catch (NoSuchMethodException e1) {
            throw new ConfigException("Cannot find zero-arg constructor for: "
                    + type.getSimpleName());
        }
    }

    @Override protected NodeFactory createNodeFactory(Map<Property, Object> configuredProperties)
            throws ConfigException {
        Class<?> synapticIntegratorClass = ((ClassWrapper) configuredProperties
                .get(pSynapticIntegrator)).getWrapped();
        Class<?> spikeGeneratorClass = ((ClassWrapper) configuredProperties
                .get(pSpikeGenerator)).getWrapped();

        IndicatorPDF scale = (IndicatorPDF) configuredProperties.get(pScale);
        IndicatorPDF bias = (IndicatorPDF) configuredProperties.get(pBias);

        SynapticIntegratorFactory synapticIntegratorFactory = (SynapticIntegratorFactory) constructFromClass(synapticIntegratorClass);
        SpikeGeneratorFactory spikeGeneratorFactory = (SpikeGeneratorFactory) constructFromClass(spikeGeneratorClass);

        return new SpikingNeuronFactory(synapticIntegratorFactory,
        		spikeGeneratorFactory, scale, bias);
    }
    
    private static PListSelector getClassSelector(String selectorName, Class<?>[] classes) {
        ClassWrapper[] classWrappers = new ClassWrapper[classes.length];

        for (int i = 0; i < classes.length; i++) {
            classWrappers[i] = new ClassWrapper(classes[i]);
        }

        return new PListSelector(selectorName, "All known classes", 0, classWrappers);
    }

    @Override public Property[] getSchema() {
        pSynapticIntegrator = getClassSelector("Synaptic Integrator", ClassRegistry.getInstance()
                .getImplementations(SynapticIntegratorFactory.class).toArray(new Class<?>[] {}));
        pSpikeGenerator = getClassSelector("Spike Generator", ClassRegistry.getInstance()
                .getImplementations(SpikeGeneratorFactory.class).toArray(new Class<?>[] {}));

        return new Property[] {pSynapticIntegrator, pSpikeGenerator, pScale, pBias};
    }

    /**
     * Wraps a Class as a list item
     */
    private static class ClassWrapper {
        Class<?> type;

        public ClassWrapper(Class<?> type) {
            this.type = type;
        }

        public Class<?> getWrapped() {
            return type;
        }

        /**
         * Return a name string that is at most two atoms long
         */
        @Override public String toString() {
            String canonicalName = type.getCanonicalName();
            String[] nameAtoms = canonicalName.split("\\.");
            if (nameAtoms.length > 2) {
                return nameAtoms[nameAtoms.length - 2] + "." + nameAtoms[nameAtoms.length - 1];
            } else {
                return canonicalName;
            }
        }
    }
}