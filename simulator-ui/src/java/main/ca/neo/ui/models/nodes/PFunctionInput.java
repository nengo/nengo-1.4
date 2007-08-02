package ca.neo.ui.models.nodes;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTFunction;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
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

	// /**
	// * Default constructor, uses the default configuration manager to set up
	// * function input
	// */
	// public PFunctionInput(boolean useDefaultConfigManager) {
	// super(useDefaultConfigManager);
	// init();
	// }

	// public PFunctionInput(String name, Function function) {
	// super(false);
	// setProperty(pName, name);
	// setProperty(pFunction, function);
	// init();
	// initModel();
	// }

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
	protected Node configureModel(PropertySet props) {

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
			Util.Warning(e.toString());

			this.removeFromParent();
		}
		return null;
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

}
