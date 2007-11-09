package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.sim.SimulatorEvent;
import ca.neo.sim.SimulatorListener;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.AbstractActivity;
import ca.shu.ui.lib.objects.activities.TrackedStatusMsg;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Runs the Simulator
 * 
 * @author Shu Wu
 */
public class RunSimulatorAction extends StandardAction {
	private static final PropertyDescriptor pEndTime = new PFloat("End time");
	private static final PropertyDescriptor pStartTime = new PFloat(
			"Start time");
	private static final PropertyDescriptor pStepSize = new PFloat("Step size");

	private static final PropertyDescriptor[] zProperties = { pStartTime,
			pEndTime, pStepSize };

	private static final long serialVersionUID = 1L;

	private Simulator simulator;

	/**
	 * @param actionName
	 *            Name of this action
	 * @param simulator
	 *            Simulator to run
	 */
	public RunSimulatorAction(String actionName, Simulator simulator) {
		super("Run simulator", actionName);
		this.simulator = simulator;
	}

	@Override
	protected void action() throws ActionException {

		try {
			PropertySet properties = UserTemplateConfigurer.configure(
					zProperties, "Simulator Runtime Configuration",
					UIEnvironment.getInstance());

			float startTime = (Float) properties.getProperty(pStartTime);
			float endTime = (Float) properties.getProperty(pEndTime);
			float stepTime = (Float) properties.getProperty(pStepSize);

			(new RunSimulatorActivity(startTime, endTime, stepTime))
					.startThread();
		} catch (ConfigException e) {
			e.defaultHandleBehavior();

			throw new ActionException("Simulator configuration not complete",
					false);
		}

	}

	/**
	 * Activity which will run the simulation
	 * 
	 * @author Shu Wu
	 */
	/**
	 * @author Shu
	 */
	class RunSimulatorActivity extends AbstractActivity implements
			SimulatorListener {
		private float startTime;
		private float endTime;
		private float stepTime;

		public RunSimulatorActivity(float startTime, float endTime,
				float stepTime) {
			super("Simulation started");
			this.startTime = startTime;
			this.endTime = endTime;
			this.stepTime = stepTime;
		}

		@Override
		public void doActivity() {
			try {
				simulator.resetNetwork(false);
				simulator.addSimulatorListener(this);

				simulator.run(startTime, endTime, stepTime);

				simulator.removeSimulatorListener(this);

			} catch (SimulationException e) {
				UserMessages.showError("Simulator problem: " + e.toString());
			}
		}

		private TrackedStatusMsg progressMsg;

		private float currentProgress = 0;

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
						progressMsg = new TrackedStatusMsg(
								(int) (currentProgress * 100)
										+ "% - simulation running");

					}
				});
			}
		}
	}
}
