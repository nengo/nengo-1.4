package ca.neo.ui.models.proxies;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.views.objects.configurable.PTFunction;
import ca.neo.ui.views.objects.configurable.PTString;
import ca.neo.ui.views.objects.configurable.PropertySchema;
import ca.shu.ui.lib.util.Util;

public class PFunctionInput extends PModelNode {

	public PFunctionInput(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		setIcon(new FunctionInputIcon(this));
	}

	static PropertySchema pName = new PTString("Name");

	static PropertySchema pFunction = new PTFunction("Function Type");

	static PropertySchema[] metaProperties = { pName, pFunction };

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
			return new FunctionInput((String) getProperty(pName), functions,
					Units.UNK);
		} catch (StructuralException e) {
			Util.Warning(e.toString());

			this.removeFromParent();
		}
		return null;
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
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
