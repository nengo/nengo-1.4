package ca.neo.ui.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.sim.Simulator;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.viewers.NodeViewer;

/**
 * In this example, an Integrator network is constructed
 * 
 * @author Shu Wu
 */
public class IntegratorExample {
	public static float tau = .05f;

	public UINetwork createUINetwork(NeoGraphics neoGraphics) throws StructuralException,
			SimulationException {
		NetworkImpl integratorNet = new NetworkImpl();
		Simulator simulator = integratorNet.getSimulator();

		integratorNet.setName("Integrator");

		neoGraphics.addNodeModel(integratorNet);

		Function f = new ConstantFunction(1, 1f);
		FunctionInput input = new FunctionInput("input", new Function[] { f }, Units.UNK);

		NodeViewer uiViewer = network.openViewer();

		// uiViewer.addNeoNode(uiInput);

		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1", false);
		Termination interm = integrator.addDecodedTermination("input",
				new float[][] { new float[] { tau } }, tau, false);
		Termination fbterm = integrator.addDecodedTermination("feedback",
				new float[][] { new float[] { 1f } }, tau, false);

		integratorNet.addNode(integrator);
		integratorNet.addNode(input);
		// UINEFEnsemble uiIntegrator = new UINEFEnsemble(integrator);
		// uiViewer.addNeoNode(uiIntegrator);
		// uiIntegrator.collectSpikes(true);

		// UITermination uiInterm =
		// uiIntegrator.showTermination(interm.getName());
		// UITermination uiFbterm =
		// uiIntegrator.showTermination(fbterm.getName());

		integratorNet.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), interm);
		integratorNet.addProjection(integrator.getOrigin(NEFEnsemble.X), fbterm);

		simulator.addProbe("integrator", NEFEnsemble.X, true);

		uiViewer.getGround().setElasticEnabled(true);

		return network;
	}

	// private UIStateProbe integratorProbe;

	public static void main(String[] args) {
		new IntegratorExample();
	}

	private UINetwork network;

	public IntegratorExample() {
		try {
			run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}

	}

	private void run() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				try {
					network = createUINetwork(new NeoGraphics());
				} catch (StructuralException e) {
					e.printStackTrace();
				} catch (SimulationException e) {
					e.printStackTrace();
				}
			}
		});

		RunSimulatorAction simulatorRunner = new RunSimulatorAction("Run", network, 0f, 1f, 0.0002f);
		simulatorRunner.doAction();
		simulatorRunner.blockUntilCompleted();

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				// PlotTimeSeries plotAction = new PlotTimeSeries(
				// integratorProbe.getModel().getData(),
				// integratorProbe.getName());
				// plotAction.doAction();
			}
		});
		network = null;
	}

}
