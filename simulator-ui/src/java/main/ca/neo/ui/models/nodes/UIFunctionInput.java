package ca.neo.ui.models.nodes;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.actions.PlotFunctionAction;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.descriptors.CFunctionArray;
import ca.neo.ui.configurable.descriptors.CString;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * UI Wrapper of FunctionInput
 * 
 * @author Shu Wu
 * 
 */
public class UIFunctionInput extends UINeoNode {

	private static ConfigParamDescriptor pFunctions = new CFunctionArray(
			"Functions Generators");

	private static ConfigParamDescriptor pName = new CString("Name");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Function Input";

	/**
	 * Config Descriptors
	 */
	private static ConfigParamDescriptor[] zConfig = { pName, pFunctions };

	public UIFunctionInput() {
		super();
		init();
	}

	public UIFunctionInput(FunctionInput model) {
		super(model);
		init();
	}

	private void init() {
		setIcon(new FunctionInputIcon(this));
	}

	@Override
	protected void afterModelCreated() {
		super.afterModelCreated();
		showAllOrigins();
	}

	@Override
	protected Node configureModel(ConfigParam props) throws ConfigException {

		Function[] functions = (Function[]) props.getProperty(pFunctions);

		try {
			// setName((String) getProperty(pName));
			return new FunctionInput((String) props.getProperty(pName),
					functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());

		}

	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		// MenuBuilder plotMenu = menu.createSubMenu("Plot");
		menu.addSection("Function");

		menu.addAction(new PlotFunctionAction(getName(), "Plot function",
				getModel()));
		return menu;

	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getFunctions().length));

		return tooltips;
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		return zConfig;
	}

	@Override
	public FunctionInput getModel() {

		return (FunctionInput) super.getModel();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}
}
