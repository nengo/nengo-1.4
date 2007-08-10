package ca.neo.ui.models.nodes;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.plot.Plotter;
import ca.neo.ui.exceptions.ModelConfigurationException;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.managers.UserConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTFunction;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

public class PFunctionInput extends PNeoNode {

	public PFunctionInput() {
		super();
		init();
	}

	public PFunctionInput(FunctionInput model) {
		super(model);
		init();
	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		// MenuBuilder plotMenu = menu.createSubMenu("Plot");
		menu.addSection("Function");

		menu.addAction(new PlotFunctionAction("Plot function", getModel()));
		return menu;

	}

	private void init() {
		setIcon(new FunctionInputIcon(this));
	}

	static PropDescriptor pName = new PTString("Name");

	static PropDescriptor pFunction = new PTFunction("Function Type");

	static PropDescriptor[] metaProperties = { pName, pFunction };

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Node configureModel(PropertySet props)
			throws ModelConfigurationException {

		Function function = (Function) props.getProperty(pFunction);
		int dimensions = function.getDimension();

		Function[] functions = new Function[dimensions];

		for (int i = 0; i < dimensions; i++) {
			functions[i] = function;
		}

		// TODO Auto-generated method stub
		try {
			// setName((String) getProperty(pName));
			return new FunctionInput((String) props.getProperty(pName),
					functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ModelConfigurationException(e.getMessage());

		}

	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

	static final String typeName = "Function Input";

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	protected void afterModelCreated() {
		super.afterModelCreated();
		showAllOrigins();
	}

	@Override
	public FunctionInput getModel() {

		return (FunctionInput) super.getModel();
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new PropertyPart("# Functions", ""
				+ getModel().getFunctions().length));

		return tooltips;
	}
}

class PlotFunctionAction extends StandardAction implements IConfigurable {
	private static final long serialVersionUID = 1L;

	static final PropDescriptor pTitle = new PTString("Title");
	static final PropDescriptor pStart = new PTFloat("Start");
	static final PropDescriptor pIncrement = new PTFloat("Increment");
	static final PropDescriptor pEnd = new PTFloat("End");
	PropDescriptor pFunctionIndex;
	PropDescriptor[] zProperties = { pTitle, pStart, pIncrement, pEnd };
	FunctionInput functionInput;

	public PlotFunctionAction(String actionName, FunctionInput functionInput) {
		super("Plot function input", actionName);
		this.functionInput = functionInput;

	}

	@Override
	protected void action() throws ActionException {
		pFunctionIndex = new PTInt("Function index", 0, functionInput
				.getFunctions().length - 1);

		new UserConfig(this);

	}

	public void cancelConfiguration() {

	}

	public void completeConfiguration(PropertySet properties) {
		String title = (String) properties.getProperty(pTitle);
		int functionIndex = (Integer) properties.getProperty(pFunctionIndex);
		float start = (Float) properties.getProperty(pStart);
		float end = (Float) properties.getProperty(pEnd);
		float increment = (Float) properties.getProperty(pIncrement);

		Function[] functions = functionInput.getFunctions();

		if (functionIndex >= functions.length) {
			Util.Warning("Function index out of bounds");
			return;
		}
		Function function = functionInput.getFunctions()[functionIndex];
		Plotter.plot(function, start, increment, end, title + " ("
				+ function.getClass().getSimpleName() + ")");

	}

	public PropDescriptor[] getConfigSchema() {

		PropDescriptor[] properties = { pTitle, pFunctionIndex, pStart,
				pIncrement, pEnd };
		return properties;

	}

	public String getTypeName() {
		return "Function plotter";
	}

	public boolean isConfigured() {
		return true;
	}

}