package ca.neo.ui.models.constructors;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.descriptors.PFunctionArray;
import ca.neo.ui.models.nodes.UIFunctionInput;

public class CFunctionInput extends ConstructableNode {

	public CFunctionInput() {
		super();
	}

	private static Property pFunctions = new PFunctionArray("Functions Generators", 1);

	@Override
	protected Node createNode(ConfigResult props, String name) throws ConfigException {

		Function[] functions = (Function[]) props.getValue(pFunctions);

		try {
			// setName((String) getProperty(pName));
			return new FunctionInput(name, functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());

		}

	}

	@Override
	public ConfigSchemaImpl getNodeConfigSchema() {
		return new ConfigSchemaImpl(pFunctions);
	}

	public String getTypeName() {
		return UIFunctionInput.typeName;
	}
}
