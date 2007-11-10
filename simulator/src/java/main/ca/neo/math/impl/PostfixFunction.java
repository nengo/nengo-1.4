/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import java.util.List;
import java.util.Stack;

import ca.neo.math.Function;

/**
 * <p>A Function based on a mathematical expression and on other functions. The expression 
 * must be given as a list of list of operators, literal operands, and operand placeholders.</p>
 * 
 * <p>An operator can be any Function. A literal operand must be a Float.</p>
 * 
 * <p>An operand placeholder is an Integer, which points to the dimension of the input vector 
 * from which the corresponding operand is to be drawn. For example the expression for a 
 * PostfixFunction with 2 dimensions can include the Integers 0 and 1. When map(float[] from) 
 * is called, Integer 0 will be replaced with from[0] and so on.</p>
 * 
 * <p>The expression list must be given in postfix order.</p> 
 * 
 * @author Bryan Tripp
 */
public class PostfixFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private List myExpression;
	
	/**
	 * A human-readable string representation of the function
	 */
	private String myExpressionStr;
	
	private int myDimension;
	
	/**
	 * @param expression Postfix expression list (as described in class docs)
	 * @param dimension Dimension of the function (must be at least as great as any operand 
	 * 		placeholders that appear in the expression)
	 */
	public PostfixFunction(List expression, int dimension) {
		this(expression,"", dimension);
	}
	
	/**
	 * @param expression Postfix expression list (as described in class docs)
	 * @param expressionStr String representation of the expression
	 * @param dimension Dimension of the function (must be at least as great as any operand 
	 * 		placeholders that appear in the expression)
	 */
	public PostfixFunction(List expression, String expressionStr, int dimension) {
		int highest = findHighestDimension(expression);
		if (dimension <= highest) {
			throw new IllegalArgumentException("Dimension must be at least " + (highest+1) + " so satisfy expresion");			
		}
		
		myDimension = dimension;
		myExpression = expression;
		myExpressionStr = expressionStr;
	}
	
	/**
	 * @return Postfix expression list 
	 */
	protected List getExpression() {
		return myExpression;
	}
	
	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return doMap(myExpression, myDimension, from);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = doMap(myExpression, myDimension, from[i]);
		}
		
		return result;
	}
	
	private static float doMap(List expression, int dimension, float[] from) {
		if (dimension != from.length) {
			throw new IllegalArgumentException("Input dimension " + from.length + ", expected " + dimension);
		}
		
		float result = 0;
		int i = 0;
		
		try {
			Stack stack = new Stack();
			
			for ( ; i < expression.size(); i++) {
				Object o = expression.get(i);
				
				if (o instanceof Float) {
					stack.push(o);
				} else if (o instanceof Integer) {
					int index = ((Integer) o).intValue();
					stack.push(new Float(from[index]));
				} else {
					Function f = (Function) o;
					
					float[] args = new float[f.getDimension()];
					for (int dim = args.length-1; dim >= 0; dim--) {
						args[dim] = ((Float) stack.pop()).floatValue();
					}
					
					stack.push(new Float(f.map(args)));
				}
			}
			
			result = ((Float) stack.pop()).floatValue();
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to evaluate expression list at index " + i, e);
		}
		
		return result;
	}
	
	//and check everything is a Float, Integer, or Function while we're at it
	private static int findHighestDimension(List expression) {
		int highest = -1;
		
		for (int i = 0; i < expression.size(); i++) {
			Object o = expression.get(i);
			
			if (o instanceof Integer) {
				int current = ((Integer) o).intValue();
				if (current > highest) {
					highest = current;
				}
			} else if ( !(o instanceof Float) && !(o instanceof Function) ) {
				throw new IllegalArgumentException("Expression must consist of Integers, Floats, and Functions");
			}
		}
		
		return highest;
	}

	/**
	 * @return A human-readable string representation of the function
	 */
	public String getExpressionStr() {
		if (myExpressionStr != null && myExpressionStr.compareTo("") != 0) {
			return myExpressionStr;
		} else {
			return "Postfix: " + myExpression.toString();
		}
	}

}
