package ca.neo.ui.models.proxies;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.views.objects.properties.PTFunction;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObject;

public class PFunctionInput extends PModelNode {
	public PFunctionInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	String name = "Unamed Ensemble";

	static String pName = "Name";

	static String pFunction = "Function Type";

	static PropertySchema[] metaProperties = { new PTString(pName),
			new PTFunction(pFunction) };

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

	@Override
	protected WorldObject createIcon() {

		return new FunctionInputIcon();
	}

}
