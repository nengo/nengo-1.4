package ca.neo.ui.models.wrappers;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.views.objects.configurable.struct.PTFunction;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.util.Util;

public class PFunctionInput extends PModelNode {

	/**
	 * Default constructor, uses the default configuration manager to set up
	 * function input
	 */
	public PFunctionInput() {
		super(true);
		init();
	}

	public PFunctionInput(String name, Function function) {
		super(false);
		setProperty(pName, name);
		setProperty(pFunction, function);
		init();
		initModel();
	}

	private void init() {
		setIcon(new FunctionInputIcon(this));
	}

	static PropertyStructure pName = new PTString("Name");

	static PropertyStructure pFunction = new PTFunction("Function Type");

	static PropertyStructure[] metaProperties = { pName, pFunction };

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Node createModel() {
		Function function = (Function) getProperty(pFunction);
		int dimensions = function.getDimension();

		Function[] functions = new Function[dimensions];

		for (int i = 0; i < dimensions; i++) {
			functions[i] = function;
		}

		// TODO Auto-generated method stub
		try {
			setName((String) getProperty(pName));
			return new FunctionInput((String) getProperty(pName), functions,
					Units.UNK);
		} catch (StructuralException e) {
			Util.Warning(e.toString());

			this.removeFromParent();
		}
		return null;
	}

	@Override
	public PropertyStructure[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

	static final String typeName = "Function Input";

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

}
