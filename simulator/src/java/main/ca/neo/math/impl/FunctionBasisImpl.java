/*
 * Created on 23-May-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.math.FunctionBasis;

/**
 * Default implementation of FunctionBasis. 
 *  
 * @author Bryan Tripp
 */
public class FunctionBasisImpl implements FunctionBasis {

	private static final long serialVersionUID = 1L;
	
	private Function[] myFunctions;
	
	/**
	 * @param functions Ordered list of functions composing this basis 
	 */
	public FunctionBasisImpl(Function[] functions) {
		myFunctions = functions;
	}

	/**
	 * @see ca.neo.math.FunctionBasis#getDimensions()
	 */
	public int getDimensions() {
		return myFunctions.length;
	}

	/**
	 * @see ca.neo.math.FunctionBasis#getFunction(int)
	 */
	public Function getFunction(int dimension) {
		if (dimension < 1 || dimension > myFunctions.length) {
			throw new IllegalArgumentException("Dimension " + dimension + " does not exist");
		}
		
		return myFunctions[dimension-1];
	}

}
