/*
 * Created on 8-Jun-2006
 */
package ca.nengo.math;

import java.util.Map;

public interface FunctionInterpreter {

	/**
	 * Registers a non-standard function for use in future interpretation of 
	 * function expressions. Standard function names (eg sin, cos, ln) are 
	 * not allowed. The Function must be 1-dimensional. Example: suppose you 
	 * have a sophisticated Function implementation class called FooImpl.
	 * You could register is here as registerFunction("foo", new FooImpl())
	 * and thereafter use it in expressions like parse("x1 + x2 * foo(x3)")  
	 *  
	 * @param name Short name of function as it is to appear in parsed expressions
	 * @param function Function instance 
	 */
	public void registerFunction(String name, Function function);
	
	/**
	 * Removes a registered function.
	 * 
	 * @param name
	 *            Name of function to be removed
	 */
	public void removeRegisteredFunction(String name);

	/**
	 * Returns a map of registered functions. 
	 * 
	 * @return The registered functions
	 */
	public Map<String, Function> getRegisteredFunctions();
	
	/**
	 * <p>Parses a mathematical expression into a Function instance. The function can 
	 * include the unary operators - and !, the binary operators +-/^*&|, the standard 
	 * functions sin() cos() tan() asin(), acos(), atan(), exp(), and ln(), and 
	 * user-defined unary functions functions that have been registered with the 
	 * registerFunction(...) method. The constant "pi" and literal constants are also 
	 * allowed. The logical operators produce 1f or 0f, on the basis of whether their 
	 * operands are > 1/2. Functions must be given in infix notation and can include 
	 * brackets.</p>
	 * 
	 * <p>Function operands have the form x0, x1, etc., which serve as placeholders for 
	 * values that are given at each dimension of the map() method of the resulting 
	 * function.</p>
	 * 
	 * <p>An example of a valid expression would be "x0 * (x1 + x2)^x3 * ln(x4/3)"</p>
	 *  
	 * @param expression Mathematical expression
	 * @param dimension Input dimension of the desired Function (must be at least as great 
	 * 		as any referenced inputs, eg if x3 appears in the expression, the dimension 
	 * 		must be at least 4) 
	 * @return Instance of a Function implementing the given expression 
	 */
	public Function parse(String expression, int dimension);
	
}
