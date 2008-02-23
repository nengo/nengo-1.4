package ca.neo.ui.models.constructors;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFunctionArray;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.nodes.UIFunctionInput;

public class CFunctionInput extends ConstructableNode {

	public CFunctionInput(INodeContainer nodeContainer) {
		super(nodeContainer);
	}

	private static PropertyDescriptor pFunctions = new PFunctionArray("Functions Generators", 1);

	/**
	 * Config Descriptors
	 */
	private static PropertyDescriptor[] zConfig = { pFunctions };

	@Override
	protected Node createNode(PropertySet props, String name) throws ConfigException {

		Function[] functions = (Function[]) props.getProperty(pFunctions);

		try {
			// setName((String) getProperty(pName));
			return new FunctionInput(name, functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());

		}

	}

	@Override
	public PropertyDescriptor[] getNodeConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UIFunctionInput.typeName;
	}
}
