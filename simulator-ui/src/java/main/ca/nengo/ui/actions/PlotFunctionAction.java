package ca.nengo.ui.actions;

import javax.swing.JDialog;

import ca.nengo.math.Function;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.managers.ConfigManager;
import ca.nengo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.nengo.ui.util.DialogPlotter;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;

/**
 * Plots a function node, which can contain multiple functions
 * 
 * @author Shu Wu
 */
public class PlotFunctionAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	static final Property pEnd = new PFloat("End");
	static final Property pIncrement = new PFloat("Increment");
	static final Property pStart = new PFloat("Start");

	static final Property[] propD = { pStart, pIncrement, pEnd };
	private Function function;

	private String plotName;

	private JDialog dialogParent;

	public PlotFunctionAction(String plotName, Function function,
			JDialog dialogParent) {
		super("Plot function");
		this.plotName = plotName;
		this.function = function;
		this.dialogParent = dialogParent;
	}

	@Override
	protected void action() throws ActionException {

		try {
			ConfigResult properties = ConfigManager.configure(propD,
					"Function Node plotter", UIEnvironment.getInstance(),
					ConfigMode.TEMPLATE_NOT_CHOOSABLE);
			String title = plotName + " - Function Plot";

			float start = (Float) properties.getValue(pStart);
			float end = (Float) properties.getValue(pEnd);
			float increment = (Float) properties.getValue(pIncrement);
			
			if (increment == 0) {
				throw new ActionException("Please use a non-zero increment");
			}

			DialogPlotter plotter = new DialogPlotter(dialogParent);

			try {
				plotter.doPlot(function, start, increment, end, title + " ("
						+ function.getClass().getSimpleName() + ")");
			} catch (Exception e) {
				throw new ConfigException(e.getMessage());
			}

		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		}

	}

}