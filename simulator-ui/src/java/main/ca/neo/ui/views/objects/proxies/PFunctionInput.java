package ca.neo.ui.views.objects.proxies;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.views.objects.properties.PTFunction;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.sw.util.Util;

public class PFunctionInput extends ProxyNode {
	String name = "Unamed Ensemble";

	static String pName = "Name";

	static String pFunction = "Function Type";

	static PropertySchema[] metaProperties = { new PTString(pName),
			new PTFunction(pFunction)

	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Node createProxy() {
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
			Util.Error(e.toString());
			

			this.removeFromParent();
		}
		return null;
	}

	@Override
	public PropertySchema[] getMetaProperties() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

}
