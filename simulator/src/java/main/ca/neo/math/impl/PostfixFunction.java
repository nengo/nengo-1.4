/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.neo.config.ConfigUtil;
import ca.neo.math.Function;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.Configuration.Property;
import ca.neo.model.impl.ConfigurationImpl;

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
 * TODO: need a way to manage user-defined functions that ensures they can be accessed from saved networks
 * 
 * @author Bryan Tripp
 */
public class PostfixFunction implements Function {

	private static final long serialVersionUID = 1L;
	private static Logger ourLogger = Logger.getLogger(PostfixFunction.class);
	
	public static final String EXPRESSION_PROPERTY = "expression";
	public static final String EXPRESSION_LIST_PROPERTY = "expressionList";
	public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
	
	private List myExpressionList;
	
	/**
	 * A human-readable string representation of the function
	 */
	private String myExpression;	
	private int myDimension;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param expressionList Postfix expression list (as described in class docs)
	 * @param expression String representation of the expression
	 * @param dimension Dimension of the function (must be at least as great as any operand 
	 * 		placeholders that appear in the expression)
	 */
	public PostfixFunction(List expressionList, String expression, int dimension) {
		init(expressionList, expression, dimension);
	}
	
	/**
	 * @param expression String representation of the expression (infix)
	 * @param dimension Dimension of the function (must be at least as great as any operand 
	 * 		placeholders that appear in the expression)
	 */
	public PostfixFunction(String expression, int dimension) {
		init(null, expression, dimension);
	}	
	
	/**
	 * @param properties As defined by getConstructionTemplate() or loaded from disk
	 * @throws StructuralException
	 */
	public PostfixFunction(Configuration properties) throws StructuralException {
		String expression = (String) ConfigUtil.get(properties, EXPRESSION_PROPERTY, String.class);
		int dimension = ((Integer) ConfigUtil.get(properties, DIMENSION_PROPERTY, Integer.class)).intValue();
		
		List expressionList = null;
		if (properties.getPropertyNames().contains(EXPRESSION_LIST_PROPERTY)) {
			expressionList = (List) ConfigUtil.get(properties, EXPRESSION_LIST_PROPERTY, List.class);
		}
		
		init(expressionList, expression, dimension);
	}
	
	public static Configuration getConstructionTemplate() {
		ConfigurationImpl result = new ConfigurationImpl(null);
		result.defineTemplateProperty(DIMENSION_PROPERTY, Integer.class, new Integer(1));
		result.defineTemplateProperty(EXPRESSION_PROPERTY, String.class, "x0");
		return result;
	}

	private void init(List expressionList, String expression, int dimension) {
		set(expressionList, expression, dimension);
		
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, true);
		myConfiguration.defineSingleValuedProperty(EXPRESSION_PROPERTY, String.class, true);

		Property p = new Property() {
			public Object getValue(int index) throws StructuralException {
				return getExpressionList().get(index);
			}
			public void setValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException {
			}
			public int getNumValues() {
				return getExpressionList().size();
			}			
			public void addValue(Object value) throws StructuralException {
			}
			public Object getDefaultValue() {
				return null;
			}
			public String getName() {
				return EXPRESSION_LIST_PROPERTY;
			}
			public Class getType() {
				return Object.class;
			}
			public Object getValue() {
				return getExpressionList().get(0);
			}
			public void insert(int index, Object value) throws StructuralException {
			}
			public boolean isFixedCardinality() {
				return true;
			}
			public boolean isMultiValued() {
				return true;
			}
			public boolean isMutable() {
				return false;
			}
			public void remove(int index) throws StructuralException {
			}
			public void setValue(Object value) throws StructuralException {
			}
		};
		myConfiguration.defineProperty(p);
	}
	
	private void set(List expressionList, String expression, int dimension) {
		if (expressionList == null) {
			expressionList = DefaultFunctionInterpreter.sharedInstance().getPostfixList(expression);
		} else {
			//TODO: register user-defined functions?
		}
		
		int highest = findHighestDimension(expressionList);
		if (dimension <= highest) {
			dimension = highest+1;			
			ourLogger.warn("Dimension adjusted to " + (highest+1) + " to satisfy expression " + expression);			
		}

		myDimension = dimension;
		myExpressionList = expressionList;
		myExpression = expression;
	}
	
	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */	
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @return Postfix expression list 
	 */
	protected List getExpressionList() {
		return myExpressionList;
	}
	
	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}
	
	/**
	 * @param dimension Dimension of the function (must be at least as great as any operand 
	 * 		placeholders that appear in the expression)
	 */
	public void setDimension(int dimension) {
		set(myExpressionList, myExpression, dimension);
	}

	/**
	 * @return A human-readable string representation of the function
	 */
	public String getExpression() {
		if (myExpression != null && myExpression.compareTo("") != 0) {
			return myExpression;
		} else {
			return "Postfix: " + myExpressionList.toString();
		}
	}
	
	/**
	 * @param expression String representation of the expression (infix)
	 */
	public void setExpression(String expression) {
		set(null, expression, myDimension);
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return doMap(myExpressionList, myDimension, from);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = doMap(myExpressionList, myDimension, from[i]);
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

}
