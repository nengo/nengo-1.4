/**
* JNumeric - a Jython port of Numerical Java
* Current Maintainer: Daniel Lemire, Ph.D.
* (c) 1998, 1999 Timothy Hochberg, tim.hochberg@ieee.org
*
* Free software under the Python license, see http://www.python.org
* Home page: http://jnumerical.sourceforge.net
*
*/

package ca.nengo.ui.JNumeric;
import org.python.core.*;
import java.lang.reflect.Array;



public class Umath implements InitModule {

	public void initModule(PyObject dict) {
		dict.__setitem__("__doc__", new PyString("Universal math functions."));

		// umath functions

		dict.__setitem__("add", add);
		dict.__setitem__("subtract", subtract);
		dict.__setitem__("multiply", multiply);
		dict.__setitem__("divide", divide);
		dict.__setitem__("remainder", remainder);
		dict.__setitem__("power", power);

		dict.__setitem__("arccos", arccos);
		dict.__setitem__("arccosh", arccosh);
		dict.__setitem__("arcsin", arcsin);
		dict.__setitem__("arcsinh", arcsinh);
		dict.__setitem__("arctan", arctan);
		dict.__setitem__("arctanh", arctanh);
		dict.__setitem__("ceil", ceil);
		dict.__setitem__("conjugate", conjugate);
		dict.__setitem__("imaginary", imaginary);
		dict.__setitem__("cos", cos); 
		dict.__setitem__("cosh", cosh); 
		dict.__setitem__("exp", exp);
		dict.__setitem__("floor", floor);
		dict.__setitem__("log", log);
		dict.__setitem__("log10", log10);
		dict.__setitem__("real", real);
		dict.__setitem__("sin", sin);
		dict.__setitem__("sinh", sinh);
		dict.__setitem__("sqrt", sqrt);
		dict.__setitem__("tan", tan); 
		dict.__setitem__("tanh", tanh);

		dict.__setitem__("maximum", maximum);
		dict.__setitem__("minimum", minimum);

		dict.__setitem__("equal", equal);
		dict.__setitem__("not_equal", not_equal);
		dict.__setitem__("less", less);
		dict.__setitem__("less_equal", less_equal);
		dict.__setitem__("greater", greater);
		dict.__setitem__("greater_equal", greater_equal);

		dict.__setitem__("logical_and", logical_and);
		dict.__setitem__("logical_or", logical_or);
		dict.__setitem__("logical_xor", logical_xor);
		dict.__setitem__("logical_not", logical_not);

		dict.__setitem__("bitwise_and", bitwise_and);
		dict.__setitem__("bitwise_or", bitwise_or);
		dict.__setitem__("bitwise_xor", bitwise_xor);
}

	// umath functions

	static final public BinaryUfunc add = new BinaryUfunc(BinaryUfunc.add);
	static final public BinaryUfunc subtract = new BinaryUfunc(BinaryUfunc.subtract);
	static final public BinaryUfunc multiply = new BinaryUfunc(BinaryUfunc.multiply);
	static final public BinaryUfunc divide = new BinaryUfunc(BinaryUfunc.divide);
	static final public BinaryUfunc remainder = new BinaryUfunc(BinaryUfunc.remainder);
	static final public BinaryUfunc power = new BinaryUfunc(BinaryUfunc.power);

	static final public UnaryUfunc arccos = new UnaryUfunc(UnaryUfunc.arccos);
	static final public UnaryUfunc arccosh = new UnaryUfunc(UnaryUfunc.arccosh);
	static final public UnaryUfunc arcsin = new UnaryUfunc(UnaryUfunc.arcsin);
	static final public UnaryUfunc arcsinh = new UnaryUfunc(UnaryUfunc.arcsinh);
	static final public UnaryUfunc arctan = new UnaryUfunc(UnaryUfunc.arctan);
	static final public UnaryUfunc arctanh = new UnaryUfunc(UnaryUfunc.arctanh);
	static final public UnaryUfunc ceil = new UnaryUfunc(UnaryUfunc.ceil);
	static final public UnaryUfunc conjugate = new UnaryUfunc(UnaryUfunc.conjugate);
	static final public UnaryUfunc imaginary = new UnaryUfunc(UnaryUfunc.imaginary);
	static final public UnaryUfunc cos = new UnaryUfunc(UnaryUfunc.cos); 
	static final public UnaryUfunc cosh = new UnaryUfunc(UnaryUfunc.cosh); 
	static final public UnaryUfunc exp = new UnaryUfunc(UnaryUfunc.exp);
	static final public UnaryUfunc floor = new UnaryUfunc(UnaryUfunc.floor);
	static final public UnaryUfunc log = new UnaryUfunc(UnaryUfunc.log);
	static final public UnaryUfunc log10 = new UnaryUfunc(UnaryUfunc.log10);
	static final public UnaryUfunc real = new UnaryUfunc(UnaryUfunc.real);
	static final public UnaryUfunc sin = new UnaryUfunc(UnaryUfunc.sin);
	static final public UnaryUfunc sinh = new UnaryUfunc(UnaryUfunc.sinh);
	static final public UnaryUfunc sqrt = new UnaryUfunc(UnaryUfunc.sqrt);
	static final public UnaryUfunc tan = new UnaryUfunc(UnaryUfunc.tan); 
	static final public UnaryUfunc tanh = new UnaryUfunc(UnaryUfunc.tanh); 

	static final public BinaryUfunc maximum = new BinaryUfunc(BinaryUfunc.maximum);
	static final public BinaryUfunc minimum = new BinaryUfunc(BinaryUfunc.minimum);

	static final public BinaryUfunc equal = new BinaryUfunc(BinaryUfunc.equal);
	static final public BinaryUfunc not_equal = new BinaryUfunc(BinaryUfunc.notEqual);
	static final public BinaryUfunc less = new BinaryUfunc(BinaryUfunc.less);
	static final public BinaryUfunc less_equal = new BinaryUfunc(BinaryUfunc.lessEqual);
	static final public BinaryUfunc greater = new BinaryUfunc(BinaryUfunc.greater);
	static final public BinaryUfunc greater_equal = new BinaryUfunc(BinaryUfunc.greaterEqual);

	static final public BinaryUfunc logical_and = new BinaryUfunc(BinaryUfunc.logicalAnd);
	static final public BinaryUfunc logical_or = new BinaryUfunc(BinaryUfunc.logicalOr);
	static final public BinaryUfunc logical_xor = new BinaryUfunc(BinaryUfunc.logicalXor);
	static final public UnaryUfunc  logical_not = new UnaryUfunc(UnaryUfunc.logicalNot);

	static final public BinaryUfunc bitwise_and = new BinaryUfunc(BinaryUfunc.bitwiseAnd);
	static final public BinaryUfunc bitwise_or = new BinaryUfunc(BinaryUfunc.bitwiseOr);
	static final public BinaryUfunc bitwise_xor = new BinaryUfunc(BinaryUfunc.bitwiseXor);
}
