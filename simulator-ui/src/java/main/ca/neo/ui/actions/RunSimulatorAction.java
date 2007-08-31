package ca.neo.ui.actions;

import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.descriptors.CFloat;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.AbstractActivity;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Runs the Simulator
 * 
 * @author Shu Wu
 * 
 */
public class RunSimulatorAction extends StandardAction {

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
		SimulatorConfig simulatorConfig = new SimulatorConfig();

		/*
		 * Configures the simulatorConfig
		 */
		UserTemplateConfigurer config = new UserTemplateConfigurer(simulatorConfig);
		try {
			config.configureAndWait();
			(new RunSimulatorActivity(simulatorConfig)).startThread();
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
	 * 
	 */
	class RunSimulatorActivity extends AbstractActivity {
		SimulatorConfig config;

		public RunSimulatorActivity(SimulatorConfig config) {
			super("Simulation in progress");
			this.config = config;
		}

		@Override
		public void doActivity() {
			try {
				simulator.run(config.getStartTime(), config.getEndTime(),
						config.getStepSize());
			} catch (SimulationException e) {
				UserMessages.showError("Simulator problem: " + e.toString());
			}
		}
	}
}

/**
 * Contains configurable properties used to set up the Simulation
 * 
 * @author Shu Wu
 * 
 */
class SimulatorConfig implements IConfigurable {
	private static final ConfigParamDescriptor pEndTime = new CFloat(
			"End time");
	private static final ConfigParamDescriptor pStartTime = new CFloat(
			"Start time");
	private static final ConfigParamDescriptor pStepSize = new CFloat(
			"Step size");

	private static final ConfigParamDescriptor[] zProperties = { pStartTime,
			pEndTime, pStepSize };

	private ConfigParam configuredProperties;

	public void completeConfiguration(ConfigParam properties) {
		configuredProperties = properties;
	}

	public ConfigParamDescriptor[] getConfigSchema() {
		return zProperties;
	}

	public float getEndTime() {
		return (Float) configuredProperties.getProperty(pEndTime);
	}

	public float getStartTime() {
		return (Float) configuredProperties.getProperty(pStartTime);
	}

	public float getStepSize() {
		return (Float) configuredProperties.getProperty(pStepSize);
	}

	public String getTypeName() {
		return "Simulator Runtime Configuration";
	}


}