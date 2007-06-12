package ca.neo.ui.views.objects;

import ca.neo.math.impl.ConstantFunction;

public class ConstantFunctionProxy extends ProxyObject<ConstantFunction> {
	static String pDimension = "Dimension";

	static String pValue = "Value";

	static MetaProperty[] metaProperties = {
			new MetaProperty(pDimension, Integer.class),
			new MetaProperty(pValue, Float.class),

	};

	@Override
	protected ConstantFunction createProxy() {
		return new ConstantFunction((Integer) getProperty(pDimension),
				(Float) getProperty(pValue));
	}

	@Override
	protected MetaProperty[] getMetaProperties() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

}
