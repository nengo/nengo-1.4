package ca.neo.ui.actions;

import java.awt.event.ActionEvent;

import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.ui.views.objects.configurable.AbstractConfigurable;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedTask;
import ca.shu.ui.lib.util.Util;

public class RunSimulatorAction extends StandardAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Simulator simulator;

	public RunSimulatorAction(Simulator simulator) {
		super("run");
		this.simulator = simulator;
	}

	class RunSimulatorThread extends Thread {
		SimulatorConfig config;

		public RunSimulatorThread(SimulatorConfig config) {
			super();
			this.config = config;
		}

		public void run() {
			TrackedTask trackedTask = new TrackedTask("Running simulator");

			try {
				simulator.run(config.getStartTime(), config.getEndTime(),
						config.getStepSize());
			} catch (SimulationException e) {
				Util.Error("Simulator problem: " + e.toString());
			}

			trackedTask.finished();
		}
	}

	@Override
	protected void action() throws ActionException {
		SimulatorConfig simulatorConfig = new SimulatorConfig();

		/*
		 * Configures the simulatorConfig
		 */
		new DialogConfig(simulatorConfig);

		if (simulatorConfig.isConfigured()) {
			(new RunSimulatorThread(simulatorConfig)).start();

		} else {
			throw new ActionException("Simulator configuration not complete",
					false);
		}

	}
}

/**
 * Configures the simulator run options
 * 
 * @author Shu Wu
 * 
 */
class SimulatorConfig extends AbstractConfigurable {
	static final PropDescriptor pEndTime = new PTFloat("End time");
	static final PropDescriptor pStartTime = new PTFloat("Start time");
	static final PropDescriptor pStepSize = new PTFloat("Step size");

	static final PropDescriptor[] zProperties = { pStartTime, pEndTime,
			pStepSize };

	public float getEndTime() {
		return (Float) configuredProperties.getProperty(pEndTime);
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		// TODO Auto-generated method stub
		return zProperties;
	}

	public float getStartTime() {
		return (Float) configuredProperties.getProperty(pStartTime);
	}

	public float getStepSize() {
		return (Float) configuredProperties.getProperty(pStepSize);
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Simulator Runtime Configuration";
	}

	PropertySet configuredProperties;

	@Override
	public void completeConfiguration(PropertySet properties) {
		super.completeConfiguration(properties);
		configuredProperties = properties;
	}

}