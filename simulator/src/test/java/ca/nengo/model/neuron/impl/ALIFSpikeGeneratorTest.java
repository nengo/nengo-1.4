package ca.nengo.model.neuron.impl;

import static org.junit.Assert.*;
import ca.nengo.math.Function;
import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.math.impl.PiecewiseConstantFunction;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.EnsembleImpl;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.plot.Plotter;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;
import org.junit.Test;

public class ALIFSpikeGeneratorTest {
	@Test
	public void testGetOnsetRate() throws SimulationException {
		float I = 10;
		ALIFSpikeGenerator g1 = new ALIFSpikeGenerator(.002f, .02f, .2f, .1f);
		float rate = run(g1, .001f, 1, I);
		assertEquals(rate, g1.getOnsetRate(I), .5f);

		ALIFSpikeGenerator g2 = new ALIFSpikeGenerator(.001f, .01f, .1f, .2f);
		rate = run(g2, .001f, 1, I);
		assertEquals(rate, g2.getOnsetRate(I), .5f);
	}

	@Test
	public void testGetAdaptedRate() throws SimulationException {
		float I = 10;
		ALIFSpikeGenerator g1 = new ALIFSpikeGenerator(.002f, .02f, .2f, .1f);
		float rate = run(g1, .001f, 1000, I);
		assertEquals(rate, g1.getAdaptedRate(I), .5f);

		ALIFSpikeGenerator g2 = new ALIFSpikeGenerator(.002f, .02f, .1f, .2f);
		rate = run(g2, .001f, 1000, I);
		assertEquals(rate, g2.getAdaptedRate(I), .75f);

		//TODO: are these too far off (~ 0.75%)?

		ALIFSpikeGenerator g3 = new ALIFSpikeGenerator(.001f, .01f, .1f, .2f);
		rate = run(g3, .001f, 1000, I);
		assertEquals(rate, g3.getAdaptedRate(I), 1.5f);

		I = 15;
		rate = run(g3, .001f, 1000, I);
		assertEquals(rate, g3.getAdaptedRate(I), 2f);
	}

	//returns final firing rate
	private static float run(ALIFSpikeGenerator generator, float dt, int steps, float current) throws SimulationException {
		generator.setMode(SimulationMode.RATE);

		for (int i = 0; i < steps; i++) {
			generator.run(new float[]{i*dt, (i+1)*dt}, new float[]{current, current});
		}

		TimeSeries history = generator.getHistory("rate");
		return history.getValues()[0][0];
	}

	@Test
	public void testRun() throws SimulationException {
		float maxTimeStep = .0005f;
		float[] current = new float[]{0f, 2f, 5f};
		float[] tauRC = new float[]{0.01f, .02f};
		float[] tauRef = new float[]{.001f, .002f};
		float[] tauN = new float[]{0.1f};

		ALIFSpikeGenerator sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[0], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 5);
		assertSpikesCloseToRate(sg, current[2], 44);

		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[1], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 4);
		assertSpikesCloseToRate(sg, current[2], 44);

		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[0], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 2);
		assertSpikesCloseToRate(sg, current[2], 10);

		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[1], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 1);
		assertSpikesCloseToRate(sg, current[2], 10);
	}

	private static void assertSpikesCloseToRate(ALIFSpikeGenerator sg, float current, float tolerance) throws SimulationException {
		float stepSize = .001f;
		int steps = 1000;
		sg.setMode(SimulationMode.RATE);
		sg.reset(false);
		float rate = ((RealOutput) sg.run(new float[1], new float[]{current})).getValues()[0];
		rate=rate*steps*stepSize;

		int spikeCount = 0;
		sg.setMode(SimulationMode.DEFAULT);
		sg.reset(false);
		for (int i = 0; i < steps; i++) {
			boolean spike = ((SpikeOutput) sg.run(new float[]{stepSize * i, stepSize * (i+1)},
					new float[]{current, current})).getValues()[0];
			if (spike) {
				spikeCount++;
			}
		}

		System.out.println(spikeCount + " spikes in simulation, " + rate + " expected");
		assertTrue(spikeCount + " spikes in simulation, " + rate + " expected",
				spikeCount > rate-tolerance && spikeCount < rate+tolerance);
	}

	@Test
	public void testAdaptation() throws StructuralException, SimulationException {
		NetworkImpl network = new NetworkImpl();
		LinearSynapticIntegrator integrator = new LinearSynapticIntegrator(.001f, Units.ACU);
		Termination t = integrator.addTermination("input", new float[]{1}, .005f, false);
		ALIFSpikeGenerator generator = new ALIFSpikeGenerator(.0005f, .02f, .2f, .05f);
		SpikingNeuron neuron = new SpikingNeuron(integrator, generator, 2, 5, "neuron");
		network.addNode(neuron);

		Function f = new PiecewiseConstantFunction(new float[]{1, 2}, new float[]{0, 1, -1});
		FunctionInput input = new FunctionInput("input", new Function[]{f}, Units.UNK);
		network.addNode(input);

		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), t);

		setTau(neuron, .1f);
		network.setMode(SimulationMode.RATE);
		network.run(0, 3);
	}

	private void setTau(SpikingNeuron neuron, float tau) {
		float alpha = getSlope(neuron) / neuron.getScale();
		float b = neuron.getBias();
		float c = neuron.getScale();

		float tauN = tau/2 * (b/c + 1);
		float A_N = (1/tau - 1/tauN) / alpha;

		//optimal A_N to maximize adaptation range with reasonable tau (see notes 14 April)
		((ALIFSpikeGenerator) neuron.getGenerator()).setIncN(A_N);
		((ALIFSpikeGenerator) neuron.getGenerator()).setTauN(tauN);
	}

	private static float getSlope(SpikingNeuron neuron) {
		SimulationMode mode = neuron.getMode();
		float slope = 0;

		try {
			neuron.setMode(SimulationMode.CONSTANT_RATE);
			neuron.setRadialInput(-1);
			neuron.run(0, 0);
			RealOutput low = (RealOutput) neuron.getOrigin(Neuron.AXON).getValues();
			neuron.setRadialInput(1);
			neuron.run(0, 0);
			RealOutput high = (RealOutput) neuron.getOrigin(Neuron.AXON).getValues();
			slope = (high.getValues()[0] - low.getValues()[0]) / 2f;
			System.out.println("high: " + high.getValues()[0] + " low: " + low.getValues()[0] + " slope: " + slope);
			neuron.setMode(mode);
		} catch (SimulationException e) {
			throw new RuntimeException(e);
		} catch (StructuralException e) {
			throw new RuntimeException(e);
		}

		return slope;
	}


	public static void main(String[] args) {
		ALIFSpikeGeneratorTest test = new ALIFSpikeGeneratorTest();
		try {
			test.testAdaptation();
		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

    //functional test
    public static void main2(String[] args) {

        try {
            Network network = new NetworkImpl();
            ALIFNeuronFactory factory = new ALIFNeuronFactory(new IndicatorPDF(200, 400), new IndicatorPDF(-2.5f, -1.5f),
                    new IndicatorPDF(.1f, .1001f), .0005f, .02f, .2f);

            Node[] neurons = new Node[50];
            float[][] weights = new float[neurons.length][];
            for (int i = 0; i < neurons.length; i++) {
                neurons[i] = factory.make("neuron"+i);
                weights[i] = new float[]{1};
            }
            EnsembleImpl ensemble = new EnsembleImpl("ensemble", neurons);
            ensemble.addTermination("input", weights, .005f, false);
            ensemble.collectSpikes(true);
            network.addNode(ensemble);

            FunctionInput input = new FunctionInput("input", new Function[]{new PiecewiseConstantFunction(new float[]{0.2f}, new float[]{0, 0.5f})}, Units.UNK);
            network.addNode(input);

            network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), ensemble.getTermination("input"));

            Probe rProbe = network.getSimulator().addProbe("ensemble", "rate", true);

            network.setMode(SimulationMode.RATE);
            network.run(0, 1);

            Plotter.plot(rProbe.getData(), "Rate");

        } catch (StructuralException e) {
            e.printStackTrace();
        } catch (SimulationException e) {
            e.printStackTrace();
        }
    }
}
