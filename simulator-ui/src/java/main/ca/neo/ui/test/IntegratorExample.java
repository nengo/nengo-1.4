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
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.util.Probe;
import ca.shu.ui.lib.util.Util;

/**
 * In this example, an Integrator network is constructed
 * 
 * @author Shu Wu
 */
public class IntegratorExample {
	public static float tau = .05f;

	private UINetwork network;

	public void createUINetwork(NeoGraphics neoGraphics) throws StructuralException,
			SimulationException {

		network = new UINetwork(new NetworkImpl());
		neoGraphics.getWorld().getGround().addChild(network);
		network.openViewer();
		network.getViewer().getGround().setElasticEnabled(true);

		(new Thread(new Runnable() {

			public void run() {
				try {
					buildNetwork(network.getModel());
				} catch (StructuralException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SimulationException e) {
					e.printStackTrace();
				}

			}

		})).start();

	}

	private void buildNetwork(NetworkImpl network) throws StructuralException,
			InterruptedException, SimulationException {
		network.setName("Integrator");

		Util.debugMsg("Network building started");

		Function f = new ConstantFunction(1, 1f);
		FunctionInput input = new FunctionInput("input", new Function[] { f }, Units.UNK);

		// uiViewer.addNeoNode(uiInput);

		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1", false);
		Termination interm = integrator.addDecodedTermination("input",
				new float[][] { new float[] { tau } }, tau, false);
		Termination fbterm = integrator.addDecodedTermination("feedback",
				new float[][] { new float[] { 1f } }, tau, false);

		network.addNode(integrator);
		Thread.sleep(1000);
		network.addNode(input);
		Thread.sleep(1000);
		// UINEFEnsemble uiIntegrator = new UINEFEnsemble(integrator);
		// uiViewer.addNeoNode(uiIntegrator);
		// uiIntegrator.collectSpikes(true);

		// UITermination uiInterm =
		// uiIntegrator.showTermination(interm.getName());
		// UITermination uiFbterm =
		// uiIntegrator.showTermination(fbterm.getName());

		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), interm);
		Thread.sleep(500);
		network.addProjection(integrator.getOrigin(NEFEnsemble.X), fbterm);
		Thread.sleep(500);

		/*
		 * Test removing projections
		 */

		network.removeProjection(interm);
		Thread.sleep(500);
		// add the projection back
		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), interm);
		Thread.sleep(500);
		/*
		 * Add probes
		 */
		Probe integratorXProbe = network.getSimulator().addProbe("integrator", NEFEnsemble.X, true);
		Thread.sleep(500);
		/*
		 * Test adding removing probes
		 */
		network.getSimulator().removeProbe(integratorXProbe);
		Thread.sleep(500);
		// add the probe back
		network.getSimulator().addProbe("integrator", NEFEnsemble.X, true);
		Thread.sleep(500);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doPostUIStuff();
			}
		});

		Util.debugMsg("Network building finished");

	}

	private void doPostUIStuff() {
		// RunSimulatorAction simulatorRunner = new RunSimulatorAction("Run",
		// network, 0f, 1f, 0.0002f);
		// simulatorRunner.doAction();

		// SwingUtilities.invokeAndWait(new Runnable() {
		// public void run() {
		// // PlotTimeSeries plotAction = new PlotTimeSeries(
		// // integratorProbe.getModel().getData(),
		// // integratorProbe.getName());
		// // plotAction.doAction();
		// }
		// });
		network = null;
	}

	// private UIStateProbe integratorProbe;

	public static void main(String[] args) {
		new IntegratorExample();
	}

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
					createUINetwork(new NeoGraphics());
				} catch (StructuralException e) {
					e.printStackTrace();
				} catch (SimulationException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
