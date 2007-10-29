package ca.neo.ui.actions;

import ca.neo.math.Function;
import ca.neo.model.impl.FunctionInput;
import ca.neo.plot.Plotter;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class PlotFunctionNodeAction extends StandardAction implements
		IConfigurable {
	private static final long serialVersionUID = 1L;

	static final PropertyDescriptor pEnd = new PFloat("End");
	static final PropertyDescriptor pIncrement = new PFloat("Increment");
	static final PropertyDescriptor pStart = new PFloat("Start");
	// static final PropDescriptor pTitle = new PTString("Title");
	private FunctionInput functionInput;
	private PropertyDescriptor pFunctionIndex;
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

		UserTemplateConfigurer config = new UserTemplateConfigurer(this);
		try {
			config.configureAndWait();
		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		}

	}

	public void completeConfiguration(PropertySet properties)
			throws ConfigException {
		String title = plotName + " - Function Plot";

		int functionIndex = (Integer) properties.getProperty(pFunctionIndex);
		float start = (Float) properties.getProperty(pStart);
		float end = (Float) properties.getProperty(pEnd);
		float increment = (Float) properties.getProperty(pIncrement);

		Function[] functions = functionInput.getFunctions();

		if (functionIndex >= functions.length) {
			throw new ConfigException("Function index out of bounds");

		}
		Function function = functionInput.getFunctions()[functionIndex];
		Plotter.plot(function, start, increment, end, title + " ("
				+ function.getClass().getSimpleName() + ")");

	}

	public PropertyDescriptor[] getConfigSchema() {

		PropertyDescriptor[] properties = { pFunctionIndex, pStart, pIncrement,
				pEnd };
		return properties;

	}

	public String getTypeName() {
		return "Function plotter";
	}

}