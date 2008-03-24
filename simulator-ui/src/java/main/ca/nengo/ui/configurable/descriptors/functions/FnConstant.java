package ca.nengo.ui.configurable.descriptors.functions;

import ca.nengo.math.Function;
import ca.nengo.math.impl.ConstantFunction;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchema;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.descriptors.PInt;

public class FnConstant extends AbstractFn {

	private PInt pDimension;
	private PFloat pValue = new PFloat("Value");

	public FnConstant(int dimension, boolean isEditable) {
		super("Constant Function", ConstantFunction.class);
		pDimension = new PInt("Input Dimension", dimension);
		pDimension.setEditable(isEditable);
	}

	@Override
	protected Function createFunction(ConfigResult props) throws ConfigException {
		return new ConstantFunction((Integer) props.getValue(pDimension), (Float) props
				.getValue(pValue));
	}

	public ConfigSchema getSchema() {
		if (getFunction() != null) {
			if (pDimension.isEditable())
				pDimension.setDefaultValue(getFunction().getDimension());

			pValue.setDefaultValue(getFunction().getValue());
		}

		return new ConfigSchemaImpl(new Property[] { pDimension, pValue });
	}

	@Override
	public ConstantFunction getFunction() {
		return (ConstantFunction) super.getFunction();
	}

}
