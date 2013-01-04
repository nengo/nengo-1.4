package ca.nengo.model.neuron.impl;

import ca.nengo.config.ConfigUtil;
import ca.nengo.math.impl.IndicatorPDF;
import java.awt.Frame;
import org.junit.Test;

public class SpikingNeuronFactoryTest {
	@Test
    public void testNothing() {
    }

    public static void main(String[] args) {
        SpikingNeuronFactory factory = new SpikingNeuronFactory(
                new LinearSynapticIntegrator.Factory(),
                new LIFSpikeGenerator.Factory(),
                new IndicatorPDF(1),
                new IndicatorPDF(0));

        ConfigUtil.configure((Frame) null, factory);
    }
}
