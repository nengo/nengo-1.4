package ca.neo.math.impl;

import ca.neo.config.ConfigUtil;
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
	private Configuration myConfiguration;
	
	/**
	 * @param dim Input dimension of the function
	 */
	public AbstractFunction(int dim) {
		myDim = dim;
		myConfiguration = ConfigUtil.defaultConfiguration(this);
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
