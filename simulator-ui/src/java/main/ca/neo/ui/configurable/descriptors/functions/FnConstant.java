package ca.neo.ui.configurable.descriptors.functions;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PInt;

public class FnConstant extends AbstractFn {

	private PInt pDimension;
	private PFloat pValue = new PFloat("Value");

	public FnConstant(int dimension,
			boolean isEditable) {
		super("Constant Function", ConstantFunction.class);
		pDimension = new PInt("Input Dimension", dimension);
		pDimension.setEditable(isEditable);
	}

	@Override
	protected Function createFunction(PropertySet props) throws ConfigException {
		return new ConstantFunction((Integer) props.getProperty(pDimension),
				(Float) props.getProperty(pValue));
	}

	public PropertyDescriptor[] getConfigSchema() {
		if (getFunction() != null) {
			if (pDimension.isEditable())
				pDimension.setDefaultValue(getFunction().getDimension());

			pValue.setDefaultValue(getFunction().getValue());
		}

		return new PropertyDescriptor[] { pDimension, pValue };
	}

	@Override
	public ConstantFunction getFunction() {
		return (ConstantFunction) super.getFunction();
	}

}
