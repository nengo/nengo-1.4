package ca.neo.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.ui.views.objects.configurable.AbstractConfigurable;
import ca.neo.ui.views.objects.configurable.UIConfigManager;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.objects.widgets.TrackedTask;
import ca.shu.ui.lib.util.GraphicsEnvironment;
import ca.shu.ui.lib.util.Util;

public class RunSimulatorAction extends AbstractAction {

	Simulator simulator;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RunSimulatorAction(Simulator simulator) {
		super("run");
		this.simulator = simulator;
	}

	public void actionPerformed(ActionEvent arg0) {
		UIConfigManager configManager = new UIConfigManager(GraphicsEnvironment
				.getInstance());

		SimulatorConfig simulatorConfig = new SimulatorConfig();

		configManager.configure(simulatorConfig);

		if (simulatorConfig.isConfigured()) {
			(new RunSimulatorThread(simulatorConfig)).start();

		}

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
}

class SimulatorConfig extends AbstractConfigurable {
	static final PropertyStructure pStartTime = new PTFloat("Start time");
	static final PropertyStructure pEndTime = new PTFloat("End time");
	static final PropertyStructure pStepSize = new PTFloat("Step size");

	static final PropertyStructure[] zProperties = { pStartTime, pEndTime,
			pStepSize };

	public float getStartTime() {
		return (Float) getProperty(pStartTime);
	}

	public float getEndTime() {
		return (Float) getProperty(pEndTime);
	}

	public float getStepSize() {
		return (Float) getProperty(pStepSize);
	}

	@Override
	public PropertyStructure[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return zProperties;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Simulator Runtime Configuration";
	}

}