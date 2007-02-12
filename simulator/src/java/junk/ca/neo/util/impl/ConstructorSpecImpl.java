/*
 * Created on 14-Jun-2006
 */
package ca.neo.util.impl;

import java.util.List;

import ca.neo.math.PDF;
import ca.neo.model.StructuralException;
import ca.neo.util.ConstructorSpec;

/**
 * Default implementation of ConstructorSpec. 
 * 
 * @author Bryan Tripp
 */
public class ConstructorSpecImpl implements ConstructorSpec {

	private Class myClassOfProduct;
	private List myParameters;
	
	/**
	 * @param classOfProduct Class of objects that are to be constructed (always the same in this 
	 * 		implementation)
	 * @param parameters Parameters for each constructor argument. Each parameter can be a Boolean,  
	 * 		Float, or Object, which is then returned as a constant literal value from the getXArg() 
	 * 		methods. Each parameter can also be a PDF, in which case the PDF is used to provide 
	 * 		different values on each call to getFloatArg() or getBooleanArg(). In the latter case 
	 * 		true is returned if the PDF sample is > 1/2.   
	 */
	public ConstructorSpecImpl(Class classOfProduct, List parameters) {
		myClassOfProduct = classOfProduct;
		myParameters = parameters;
	}

	/**
	 * @see ca.neo.util.ConstructorSpec#getClassOfProduct()
	 */
	public Class getClassOfProduct() {
		return myClassOfProduct;
	}

	/**
	 * @see ca.neo.util.ConstructorSpec#getNumConstructorArgs()
	 */
	public int getNumConstructorArgs() {
		return myParameters.size();
	}

	/**
	 * @see ca.neo.util.ConstructorSpec#getObjectArg(int)
	 */
	public Object getObjectArg(int index) throws StructuralException {
		checkIndex(index, myParameters.size());
		
		return myParameters.get(index);
	}

	/**
	 * @see ca.neo.util.ConstructorSpec#getFloatArg(int)
	 */
	public float getFloatArg(int index) throws StructuralException {
		checkIndex(index, myParameters.size());

		float result = 0;
		
		Object o = myParameters.get(index);		
		if (o instanceof Float) {
			result = ((Float) o).floatValue();
		} else if (o instanceof PDF) {
			result = ((PDF) o).sample()[0];
		} else {
			throw new StructuralException("Available parameters is of type " + o.getClass().getName());
		}
		
		return result;
	}

	/**
	 * @see ca.neo.util.ConstructorSpec#getBooleanArg(int)
	 */
	public boolean getBooleanArg(int index) throws StructuralException {
		checkIndex(index, myParameters.size());

		boolean result = false;
		
		Object o = myParameters.get(index);		
		if (o instanceof Boolean) {
			result = ((Boolean) o).booleanValue();
		} else if (o instanceof PDF) {
			result = ((PDF) o).sample()[0] > .5;
		} else {
			throw new StructuralException("Available parameters is of type " + o.getClass().getName());			
		}
		
		return result;
	}

	private static void checkIndex(int index, int parameters) throws StructuralException {
		if (index < 0 || index >= parameters) {
			throw new StructuralException("Index " + index + " is out of range. " 
					+ parameters + " parameters are specified");
		}		
	}
}
