package ca.nengo.ui.actions;

import ca.nengo.math.Function;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.plot.Plotter;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.descriptors.PInt;
import ca.nengo.ui.configurable.managers.ConfigManager;
import ca.nengo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;

public class PlotFunctionNodeAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	static final Property pEnd = new PFloat("End");
	static final Property pIncrement = new PFloat("Increment");
	static final Property pStart = new PFloat("Start");
	// static final PropDescriptor pTitle = new PTString("Title");
	private FunctionInput functionInput;
	private Property pFunctionIndex;
	private String plotName;

	public PlotFunctionNodeAction(String plotName, String actionName,
			FunctionInput functionInput) {
		super("Plot function input", actionName);
		this.plotName = plotName;
		this.functionInput = functionInput;

	}

	@Override
	protected void action() throws ActionException {
		pFunctionIndex = new PInt("Function index", 0, 0, functionInput
				.getFunctions().length - 1);
		Property[] propDescripters = { pFunctionIndex, pStart,
				pIncrement, pEnd };
		try {
			ConfigResult properties = ConfigManager.configure(propDescripters,
					"Function plotter", UIEnvironment.getInstance(),
					ConfigMode.TEMPLATE_NOT_CHOOSABLE);

			completeConfiguration(properties);

		} catch (ConfigException e) {
			e.printStackTrace();
		}

	}

	public void completeConfiguration(ConfigResult properties)
			throws ConfigException {
		String title = plotName + " - Function Plot";

		int functionIndex = (Integer) properties.getValue(pFunctionIndex);
		float start = (Float) properties.getValue(pStart);
		float end = (Float) properties.getValue(pEnd);
		float increment = (Float) properties.getValue(pIncrement);

		if (increment == 0) {
			throw new ConfigException(
					"Cannot plot with infinite steps because step size is 0");
		}

		Function[] functions = functionInput.getFunctions();

		if (functionIndex >= functions.length) {
			throw new ConfigException("Function index out of bounds");

		}
		Function function = functionInput.getFunctions()[functionIndex];
		Plotter.plot(function, start, increment, end, title + " ("
				+ function.getClass().getSimpleName() + ")");

	}
}