package ca.nengo.ui.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.models.nodes.UINetwork;

/**
 * Test Data Viewer
 */
public class DataViewerTest {
	public static float tau = .05f;

	private UINetwork network;

	public void createUINetwork(NengoGraphics nengoGraphics)
			throws StructuralException, SimulationException {

		network = new UINetwork(new NetworkImpl());
		nengoGraphics.getWorld().getGround().addChild(network);
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
		TestUtil.buildNetwork(network);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doPostUIStuff();
			}
		});

	}

	private void doPostUIStuff() {
		RunSimulatorAction simulatorRunner = new RunSimulatorAction("Run",
				network, 0f, 1f, 0.0002f);
		simulatorRunner.doAction();

		NengoGraphics.getInstance().setDataViewerPaneVisible(true);
	}

	// private UIStateProbe integratorProbe;

	public static void main(String[] args) {
		new DataViewerTest();
	}

	public DataViewerTest() {
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
					createUINetwork(new NengoGraphics());
				} catch (StructuralException e) {
					e.printStackTrace();
				} catch (SimulationException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
