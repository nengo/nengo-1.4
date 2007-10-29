package ca.neo.ui.configurable.descriptors.functions;

import ca.neo.math.Function;
import ca.neo.math.impl.DefaultFunctionInterpreter;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.descriptors.PString;

public class InterpretorFunction extends AbstractConfigurableFunction {
	private static final PropertyDescriptor pExpression = new PString(
			"Expression");
	private static final PropertyDescriptor pDimensions = new PInt("Dimensions");

	public InterpretorFunction() {
		super(DefaultFunctionInterpreter.class, "Function Interpreter",
				new PropertyDescriptor[] { pExpression, pDimensions });

	}

	@Override
	protected Function createFunction(PropertySet props) throws ConfigException {
		String expression = (String) props.getProperty(pExpression);
		int dimensions = (Integer) props.getProperty(pDimensions);

		DefaultFunctionInterpreter functionInterpreter = new DefaultFunctionInterpreter();

		Function function;
		try {
			function = functionInterpreter.parse(expression, dimensions);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage());
		}

		return function;
	}
}
