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
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.actions.PlotTimeSeries;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.models.nodes.UIFunctionInput;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.widgets.UIStateProbe;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.ui.models.viewers.NodeViewer;

/**
 * In this example, an Integrator network is constructed
 * 
 * @author Shu Wu
 */
public class IntegratorExample {
	public static float tau = .05f;

	// public static Network createNetwork() throws StructuralException {
	//
	// Network network = new NetworkImpl();
	//
	// Function f = new ConstantFunction(1, 1f);
	// // Function f = new SineFunction();
	// FunctionInput input = new FunctionInput("input", new Function[] { f },
	// Units.UNK);
	// network.addNode(input);
	//
	// NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
	//
	// NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1",
	// false);
	// network.addNode(integrator);
	// integrator.collectSpikes(true);
	//
	// Termination interm = integrator.addDecodedTermination("input",
	// new float[][] { new float[] { tau } }, tau, false);
	// // Termination interm = integrator.addDecodedTermination("input", new
	// // float[][]{new float[]{1f}}, tau);
	// network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME),
	// interm);
	//
	// Termination fbterm = integrator.addDecodedTermination("feedback",
	// new float[][] { new float[] { 1f } }, tau, false);
	// network.addProjection(integrator.getOrigin(NEFEnsemble.X), fbterm);
	//
	// // System.out.println("Network creation: " + (System.currentTimeMillis()
	// // - start));
	// return network;
	// }

	public UINetwork createUINetwork(NeoGraphics neoGraphics) throws StructuralException,
			SimulationException {
		NetworkImpl integratorNet = new NetworkImpl();
		integratorNet.setName("Integrator");

		UINetwork network = new UINetwork(integratorNet);
		neoGraphics.addNeoNode(network);

		Function f = new ConstantFunction(1, 1f);
		UIFunctionInput uiInput = new UIFunctionInput(new FunctionInput("input",
				new Function[] { f }, Units.UNK));

		NodeViewer uiViewer = network.openViewer();

		uiViewer.addNeoNode(uiInput);

		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1", false);
		Termination interm = integrator.addDecodedTermination("input",
				new float[][] { new float[] { tau } }, tau, false);
		Termination fbterm = integrator.addDecodedTermination("feedback",
				new float[][] { new float[] { 1f } }, tau, false);

		UINEFEnsemble uiIntegrator = new UINEFEnsemble(integrator);
		uiViewer.addNeoNode(uiIntegrator);
		uiIntegrator.collectSpikes(true);

		UITermination uiInterm = uiIntegrator.showTermination(interm.getName());
		UITermination uiFbterm = uiIntegrator.showTermination(fbterm.getName());

		uiInput.showOrigin(FunctionInput.ORIGIN_NAME).connectTo(uiInterm);
		uiIntegrator.showOrigin(NEFEnsemble.X).connectTo(uiFbterm);

		uiIntegrator.collectSpikes(true);
		integratorProbe = uiIntegrator.addProbe(NEFEnsemble.X);

		uiViewer.getGround().setElasticEnabled(true);

		return network;
	}

	private UIStateProbe integratorProbe;

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
				PlotTimeSeries plotAction = new PlotTimeSeries(
						integratorProbe.getModel().getData(), integratorProbe.getName());
				plotAction.doAction();
			}
		});
		network = null;
	}

}
