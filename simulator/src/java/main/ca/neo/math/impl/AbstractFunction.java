package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.ConfigurationImpl;

/**
 * Base class for Function implementations. The default implementation of 
 * multiMap() calls map(). This will be a little slower than if both methods 
 * were to call a static function, so if multiMap speed is an issue this 
 * method could be overridden, or it might be better not to use this abstract class.  
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractFunction implements Function {

	public static final String DIMENSION_PROPERTY = "dimension";
	
	private int myDim;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param dim Input dimension of the function
	 */
	public AbstractFunction(int dim) {
		init(dim);
	}
	
	public AbstractFunction(Configuration properties) throws StructuralException {
		Object o = properties.getProperty(DIMENSION_PROPERTY).getValue();
		if (o instanceof Integer) {
			init(((Integer) o).intValue());
		} else {
			throw new StructuralException("Property " + DIMENSION_PROPERTY 
					+ " must be an Integer; was " + o.getClass().getCanonicalName());
		}
	}
	
	private void init(int dim) {
		myDim = dim;
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, false);		
	}
	
	public static Configuration getConstructionTemplate() {
		ConfigurationImpl result = new ConfigurationImpl(null);
		result.defineTemplateProperty(DIMENSION_PROPERTY, Integer.class, new Integer(1));
		return result;
	}

	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDim;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public abstract float map(float[] from);

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = map(from[i]);
		}
		
		return result;
	}
	
}
