package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.sim.SimulatorEvent;
import ca.neo.sim.SimulatorListener;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.managers.ConfigManager;
import ca.neo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.neo.ui.models.nodes.UINetwork;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.objects.activities.TrackedStatusMsg;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Runs the Simulator
 * 
 * @author Shu Wu
 */
public class RunSimulatorAction extends StandardAction {
	private static final Property pEndTime = new PFloat("End time");
	private static final Property pShowDataViewer = new PBoolean(
			"Open data viewer after simulation");
	private static final Property pStartTime = new PFloat("Start time");

	private static final Property pStepSize = new PFloat("Step size");

	private static final long serialVersionUID = 1L;

	private static final Property[] zProperties = { pStartTime, pEndTime, pStepSize,
			pShowDataViewer };

	private UINetwork uiNetwork;

	/**
	 * @param actionName
	 *            Name of this action
	 * @param simulator
	 *            Simulator to run
	 */
	public RunSimulatorAction(String actionName, UINetwork uiNetwork) {
		super("Run simulator", actionName);
		this.uiNetwork = uiNetwork;
	}

	private boolean configured = false;
	private float startTime;
	private float endTime;
	private float stepTime;
	private boolean showDataViewer;

	public RunSimulatorAction(String actionName, UINetwork uiNetwork, float startTime,
			float endTime, float stepTime) {
		super("Run simulator", actionName, false);
		this.uiNetwork = uiNetwork;
		this.startTime = startTime;
		this.endTime = endTime;
		this.stepTime = stepTime;
		configured = true;

	}

	@Override
	protected void action() throws ActionException {

		try {
			if (!configured) {
				ConfigResult properties = ConfigManager.configure(zProperties, "Run "
						+ uiNetwork.getFullName(), UIEnvironment.getInstance(),
						ConfigMode.TEMPLATE_NOT_CHOOSABLE);

				startTime = (Float) properties.getValue(pStartTime);
				endTime = (Float) properties.getValue(pEndTime);
				stepTime = (Float) properties.getValue(pStepSize);
				showDataViewer = (Boolean) properties.getValue(pShowDataViewer);
			}
			RunSimulatorActivity simulatorActivity = new RunSimulatorActivity(startTime, endTime,
					stepTime, showDataViewer);
			simulatorActivity.doAction();

		} catch (ConfigException e) {
			e.defaultHandleBehavior();

			throw new ActionException("Simulator configuration not complete", false, e);

		}

	}

	/**
	 * Activity which will run the simulation
	 * 
	 * @author Shu Wu
	 */
	class RunSimulatorActivity extends TrackedAction implements SimulatorListener {

		private static final long serialVersionUID = 1L;
		private float currentProgress = 0;
		private float endTime;
		private TrackedStatusMsg progressMsg;
		private boolean showDataViewer;

		private float startTime;

		private float stepTime;

		public RunSimulatorActivity(float startTime, float endTime, float stepTime,
				boolean showDataViewer) {
			super("Simulation started");
			this.startTime = startTime;
			this.endTime = endTime;
			this.stepTime = stepTime;
			this.showDataViewer = showDataViewer;
		}

		@Override
		protected void action() throws ActionException {
			try {
				Simulator simulator = uiNetwork.getSimulator();

				simulator.resetNetwork(false);
				simulator.addSimulatorListener(this);
				try {
					simulator.run(startTime, endTime, stepTime);
				} finally {
					simulator.removeSimulatorListener(this);
				}

				((NengoGraphics) (UIEnvironment.getInstance())).captureInDataViewer(uiNetwork
						.getModel());

				if (showDataViewer) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							((NengoGraphics) (UIEnvironment.getInstance()))
									.setDataViewerVisible(true);
						}
					});

				}

			} catch (SimulationException e) {
				UserMessages.showError("Simulator problem: " + e.toString());
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ca.neo.sim.SimulatorListener#processEvent(ca.neo.sim.SimulatorEvent)
		 */
		public void processEvent(SimulatorEvent event) {
			/*
			 * Track events from the simulator and show progress in the UI
			 */

			if (event.getType() == SimulatorEvent.Type.FINISHED) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (progressMsg != null) {
							progressMsg.finished();
						}
					}
				});

				return;
			}

			if ((event.getProgress() - currentProgress) > 0.01) {
				currentProgress = event.getProgress();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (progressMsg != null) {
							progressMsg.finished();
						}
						progressMsg = new TrackedStatusMsg((int) (currentProgress * 100)
								+ "% - simulation running");

					}
				});
			}
		}
	}
}
