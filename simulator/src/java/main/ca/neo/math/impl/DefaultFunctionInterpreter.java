/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import ca.neo.config.Configuration;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.math.Function;
import ca.neo.math.FunctionInterpreter;

/**
 * <p>Default implementation of FunctionInterpreter. This implementation produces
 * PostfixFunctions.</p> 
 * 
 * TODO: faster Functions could be produced by compiling expressions into Java classes. 
 * 
 * @author Bryan Tripp
 */
public class DefaultFunctionInterpreter implements FunctionInterpreter {

	private static DefaultFunctionInterpreter ourInstance;
	
	private Map<String, Function> myFunctions; 
	private Map<String, AbstractOperator> myOperators;
	private String myTokens;
	
	/**
	 * @return A singleton instance of DefaultFunctionInterpreter 
	 */
	public static synchronized DefaultFunctionInterpreter sharedInstance() {
		if (ourInstance == null) {
			ourInstance = new DefaultFunctionInterpreter();
		}
		return ourInstance;
	}
	
	public DefaultFunctionInterpreter() {
		myFunctions = new HashMap<String, Function>(20);
		myFunctions.put("sin", new SineFunction(1));
		//TODO: other standard functions

		myOperators = new HashMap<String, AbstractOperator>(20);
		myOperators.put("^", new ExponentOperator());
		myOperators.put("*", new MultiplicationOperator());
		myOperators.put("/", new DivisionOperator());
		myOperators.put("+", new AdditionOperator());
		myOperators.put("-", new SubtractionOperator());
		myOperators.put("~", new NegativeOperator()); //we substitute - for ~ based on context
		myOperators.put("!", new NotOperator());
		myOperators.put("<", new LessThanOperator());
		myOperators.put(">", new GreaterThanOperator());
		myOperators.put("&", new AndOperator());
		myOperators.put("|", new OrOperator());
		
		StringBuffer buf = new StringBuffer();
		Iterator it = myOperators.keySet().iterator();
		while (it.hasNext()) {
			buf.append(it.next());
		}
		myTokens = buf.toString() + "(), ";
	}

	/**
	 * @see ca.neo.math.FunctionInterpreter#registerFunction(java.lang.String, ca.neo.math.Function)
	 */
	public void registerFunction(String name, Function function) {
		if (name.matches(".*\\s.*")) {
			throw new IllegalArgumentException("Function name '" + name + "' is invalid (can not contain whitespace)");
		}
		
		if (myFunctions.containsKey(name)) {
			throw new IllegalArgumentException("There is already a function named " + name);
		}
		
		myFunctions.put(name, function);
	}

	/**
	 * @see ca.neo.math.FunctionInterpreter#parse(java.lang.String, int)
	 */
	public Function parse(String expression, int dimension) {
		List postfix = getPostfixList(expression);		
		return new PostfixFunction(postfix, expression, dimension);
	}
	
	/**
	 * @param expression Mathematical expression, as in parse(...) 
	 * @return List of operators and operands in postfix order
	 */
	public List getPostfixList(String expression) {
		//Dijkstra's shunting yard algorithm to convert infix to postfix
		// see http://www.engr.mun.ca/~theo/Misc/exp_parsing.htm
		// see also http://en.wikipedia.org/wiki/Reverse_Polish_notation

		StringTokenizer tok = new StringTokenizer(expression, myTokens, true);
		Stack stack = new Stack();
		List result = new ArrayList(100); //postfix operand & operator list
		
		boolean negativeUnary = true; //contextual flag to indicate that "-" should be treated as unary
		
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken().trim();
			if (token.equals("-") && negativeUnary) token = "~";
			
			if (token.length() > 0) {
				if (token.equals("(")) {
					stack.push(token);
					negativeUnary = true;
				} else if (token.equals(")")) {
					Object o;
					while ( !(o = stack.pop()).equals("(") ) { //TODO: error if empty
						assert o instanceof AbstractOperator;
						result.add(o); 
					}
					if ( !stack.empty() && isFunction(stack.peek()) ) {
						result.add(stack.pop());
					}
					negativeUnary = false;
				} else if (token.matches("x\\d+")) { //input placeholder in form x0, x1, ...
					int index = Integer.parseInt(token.substring(1));
					result.add(new Integer(index));
					negativeUnary = false;
				} else if (token.matches("\\d*?\\.?\\d+")) { //literal floating point number
					result.add(new Float(Float.parseFloat(token)));
					negativeUnary = false;
				} else if (token.equalsIgnoreCase("pi")) {
					result.add(new Float(Math.PI));
					negativeUnary = false;
				} else if (token.equals(",")) {
					while ( !stack.peek().equals("(") ) { //TODO: error if empty (separator misplaces or parentheses mismatched)
						result.add(stack.pop());
					}
					negativeUnary = true;
				} else if (myFunctions.get(token) != null) {
					stack.push(myFunctions.get(token));
					negativeUnary = false;
				} else if (myOperators.get(token) != null) {
					AbstractOperator op = (AbstractOperator) myOperators.get(token);
					
					oploop: while ( !stack.isEmpty() && isOperator(stack.peek()) ) {
						AbstractOperator op2 = (AbstractOperator) stack.peek();
						if (op.getPrecedence() > op2.getPrecedence() 
								|| (op.isRightAssociative() && op.getPrecedence() == op2.getPrecedence())) {
							break oploop;
						}
						
						result.add(stack.pop());
					}
					
					stack.push(op);
					
					negativeUnary = true;					
				} else {
					throw new RuntimeException("The function '" + token + "' is not recognized");
				}				
			}
		}
		
		while ( !stack.empty() ) {
			result.add(stack.pop());
		}
		
		return result;
	}

	
	//true if Function but not AbstractOperator
	private static boolean isFunction(Object o) {
		return (o instanceof Function) && !(o instanceof AbstractOperator); 
	}
	
	//true if AbstractOperator
	private static boolean isOperator(Object o) {
		return (o instanceof AbstractOperator);
	}
	

	/************ PRIVATE OPERATOR CLASSES *********************************/
	
	private abstract static class AbstractOperator implements Function {

		private int myDimension;
		private boolean myRightAssociative;
		private int myPrecendence;
		private ConfigurationImpl myConfiguration;
		
		public AbstractOperator(int dimension, boolean rightAssociative, int precedence) {
			myDimension = dimension;
			myRightAssociative = rightAssociative;
			myPrecendence = precedence;
			myConfiguration = new ConfigurationImpl(this);
			//TODO: add immutable properties
		}
		
		public Configuration getConfiguration() {
			return myConfiguration;
		}

		public int getDimension() {
			return myDimension;
		}
		
		public boolean isRightAssociative() {
			return myRightAssociative;
		}
		
		public int getPrecedence() {
			return myPrecendence;
		}

		public float[] multiMap(float[][] from) {
			float[] result = new float[from.length];
			
			for (int i = 0; i < result.length; i++) {
				result[i] = this.map(from[i]);
			}
			
			return result;
		}		
	}
	
	private static class ExponentOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public ExponentOperator() {
			super(2, true, 4);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return (float) Math.pow(from[0], from[1]);
		}		
		
		public String toString() {
			return "^";
		}
	}
	
	private static class MultiplicationOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public MultiplicationOperator() {
			super(2, false, 3);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return from[0] * from[1];
		}		
		
		public String toString() {
			return "*";
		}
	}
	
	private static class DivisionOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public DivisionOperator() {
			super(2, false, 3);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return from[0] / from[1];
		}		
		
		public String toString() {
			return "/";
		}
	}
	
	private static class AdditionOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public AdditionOperator() {
			super(2, false, 2);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return from[0] + from[1];
		}		
		
		public String toString() {
			return "+";
		}
	}
	
	private static class SubtractionOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public SubtractionOperator() {
			super(2, false, 2);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return from[0] - from[1];
		}		
		
		public String toString() {
			return "-";
		}
	}
	
	private static class NegativeOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public NegativeOperator() {
			super(1, false, 5);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return -from[0];
		}		
		
		public String toString() {
			return "~";
		}		
	}
	
	private static class NotOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public NotOperator() {
			super(1, false, 5);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return (from[0] > .5) ? 0f : 1f;
		}		
		
		public String toString() {
			return "!";
		}
	}
	
	private static class LessThanOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public LessThanOperator() {
			super(2, false, 1);
		}
		
		public float map(float[] from) {
			assert from.length == getDimension();
			return from[0] < from[1] ? 1f : 0f;
		}
		
		public String toString() {
			return "<";
		}
	}
	
	private static class GreaterThanOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public GreaterThanOperator() {
			super(2, false, 1);
		}
		
		public float map(float[] from) {
			assert from.length == getDimension();
			return from[0] > from[1] ? 1f : 0f;
		}
		
		public String toString() {
			return ">";
		}
	}
	
	private static class AndOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public AndOperator() {
			super(2, false, 0);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return (from[0] > .5 && from[1] > .5) ? 1f : 0f;
		}		
		
		public String toString() {
			return "&";
		}
	}
	
	private static class OrOperator extends AbstractOperator {

		private static final long serialVersionUID = 1L;

		public OrOperator() {
			super(2, false, 0);
		}

		public float map(float[] from) {
			assert from.length == getDimension();			
			return (from[0] > .5 || from[1] > .5) ? 1f : 0f;
		}		
		
		public String toString() {
			return "|";
		}
	}

	public Map<String, Function> getRegisteredFunctions() {
		return myFunctions;
	}

	public void removeRegisteredFunction(String name) {
		myFunctions.remove(name);
		
	}
		
}
