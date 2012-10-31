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

public class JNumeric extends PyObject implements ClassDictInit {
	private static final long serialVersionUID = 1L;
    public JNumeric() {
      super(PyType.fromClass(JNumeric.class)) ;
      this.javaProxy = this ;
    }

	@SuppressWarnings({"UnusedDeclaration"})
    public static void classDictInit(PyObject dict) {

		// import modules
        Umath umath = new Umath() ;
		dict.__setitem__("umath", umath);       //dict.__setitem__("umath", PyJavaClass.lookup(Umath.class));
		dict.__setitem__("FFT", new JN_FFT());  //dict.__setitem__("FFT", PyJavaClass.lookup(JN_FFT.class));

		dict.__setitem__("__doc__", Py.newString(__doc__));
		dict.__setitem__("__version__", Py.newString("0.2a6"));

		// from umath import * (more or less).
		Umath.classDictInit(dict);

		// constants

		dict.__setitem__("pi", Py.newFloat(java.lang.Math.PI));
		dict.__setitem__("e", Py.newFloat(java.lang.Math.E));

		dict.__setitem__("Int8", Py.newString("1"));
		dict.__setitem__("Int16", Py.newString("s"));
		dict.__setitem__("Int32", Py.newString("i"));
		dict.__setitem__("Int64", Py.newString("l"));
		// I'm using Int32 here because that is the native JPython integer type and the default type for
		// an integer multiarray. The documentation claims this should be "the largest version of the 
		// given type," but I feel this is more natural. This will have to be hashed out.
		dict.__setitem__("Int", Py.newString("i"));
		dict.__setitem__("Float32", Py.newString("f"));
		dict.__setitem__("Float64", Py.newString("d"));
		dict.__setitem__("Float", Py.newString("d"));
		dict.__setitem__("Complex64", Py.newString("F"));
		dict.__setitem__("Complex128", Py.newString("D"));
		dict.__setitem__("Complex", Py.newString("D"));

		dict.__setitem__("ArrayType", PyMultiarray.ATYPE);
		dict.__setitem__("NewAxis", Py.None);
		if (Py.py2int(PyMultiarray.fromString("\001\000\000\000\000\000\000\000", 'i').get(0)) == 1)
			dict.__setitem__("LittleEndian", Py.One);
		else
			dict.__setitem__("LittleEndian", Py.Zero);

		// numeric functions
		dict.__setitem__("arrayrange", arrayrange);
		dict.__setitem__("arange", arrayrange);
		dict.__setitem__("argmax", argmax);
		dict.__setitem__("argsort", argsort);
		dict.__setitem__("argmin", argmin);
		dict.__setitem__("array", array);
		dict.__setitem__("asarray", asarray);
		dict.__setitem__("bitwise_not", bitwise_not);
		dict.__setitem__("choose", choose);
		dict.__setitem__("clip", clip);
		dict.__setitem__("compress", compress);
		dict.__setitem__("concatenate", concatenate);
		dict.__setitem__("convolve", convolve);
		dict.__setitem__("cross_correlate", cross_correlate);
		dict.__setitem__("diagonal", diagonal);
		dict.__setitem__("dot", dot);
		dict.__setitem__("fromfunction", fromfunction);
		dict.__setitem__("fromstring", fromstring);
		dict.__setitem__("identity", identity);
		dict.__setitem__("indices", indices);
		dict.__setitem__("innerproduct", innerproduct);
		dict.__setitem__("nonzero", nonzero);
		dict.__setitem__("ones", ones);
		dict.__setitem__("repeat", repeat);
		dict.__setitem__("reshape", reshape);
		dict.__setitem__("resize", resize);
		dict.__setitem__("ravel", ravel);
		dict.__setitem__("searchsorted", searchsorted);
		dict.__setitem__("shape", shape);
		dict.__setitem__("sort", sort);
		dict.__setitem__("take", take);
		dict.__setitem__("trace", trace);
		dict.__setitem__("transpose", transpose);
		dict.__setitem__("where", where);
		dict.__setitem__("zeros", zeros);

		// Abbreviations
		dict.__setitem__("sum", sum);
		dict.__setitem__("cumsum", cumsum);
		dict.__setitem__("product", product);
		dict.__setitem__("cumproduct", cumproduct);
		dict.__setitem__("alltrue", alltrue);
		dict.__setitem__("sometrue", sometrue);
    }

	// Numeric functions

	static final public PyObject arrayrange = new ArrayrangeFunction();
	static final public PyObject argmax = new ArgmaxFunction();
	static final public PyObject argsort = new ArgsortFunction();
	static final public PyObject argmin = new ArgminFunction();
	static final public PyObject array = new ArrayFunction();
	static final public PyObject asarray = new AsarrayFunction();
	static final public PyObject bitwise_not = new BitwiseNotFunction();
	static final public PyObject choose = new ChooseFunction();
	static final public PyObject clip = new ClipFunction();
	static final public PyObject compress = new CompressFunction();
	static final public PyObject concatenate = new ConcatenateFunction();
	static final public PyObject convolve = new ConvolveFunction();
	static final public PyObject cross_correlate = new Cross_correlateFunction();
	static final public PyObject diagonal = new DiagonalFunction();
	static final public PyObject dot = new DotFunction();
 	static final public PyObject fromfunction = new FromfunctionFunction();
	static final public PyObject fromstring = new FromstringFunction();
	static final public PyObject identity = new IdentityFunction();
	static final public PyObject indices = new IndicesFunction();
	static final public PyObject innerproduct = new InnerproductFunction();
	static final public PyObject nonzero = new NonzeroFunction();
	static final public PyObject ones = new OnesFunction();
	static final public PyObject repeat = new RepeatFunction();
	static final public PyObject reshape = new ReshapeFunction();
	static final public PyObject resize = new ResizeFunction();
	static final public PyObject ravel = new RavelFunction();
	static final public PyObject searchsorted = new SearchsortedFunction();
	static final public PyObject shape = new ShapeFunction();
	static final public PyObject sort = new SortFunction();
	static final public PyObject take = new TakeFunction();
	static final public PyObject trace = new TraceFunction();
	static final public PyObject transpose = new TransposeFunction();
	static final public PyObject where = new WhereFunction();
	static final public PyObject zeros = new ZerosFunction();

	// Abbreviations.

	static final public PyObject sum = new SumFunction();
	static final public PyObject cumsum = new CumsumFunction();
	static final public PyObject product = new ProductFunction();
	static final public PyObject cumproduct = new CumproductFunction();
	static final public PyObject alltrue = new AlltrueFunction();
	static final public PyObject sometrue = new SometrueFunction();

	static final public String __doc__ = "JNumeric -- Numeric for the Jython platform\n"; 
}

class ArrayrangeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ArrayrangeFunction() {
		docString = "arrayrange(start=0, stop, step=1, typecode=None)";
		argNames = new String[] {"start", "stop", "step", "typecode"};
		defaultArgs = new PyObject [] {null, Py.None, Py.One, Py.newString("\0")};
	}
	public PyObject _call(PyObject args[]) {
		if (args[3] == Py.None)
			args[3] = defaultArgs[3];
		return PyMultiarray.arrayRange(args[0], args[1], args[2], Py.py2char(args[3]));
	}
}
class ArgmaxFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ArgmaxFunction() {
		docString = "argmax(a, axis=-1)";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.newInteger(-1)};
	}
	static final BinaryUfunc argmax_ = new BinaryUfunc(BinaryUfunc.argMax);
	public PyObject _call(PyObject args[]) {
		return argmax_.reduce(args[0], Py.py2int(args[1]));
	}
}
class ArgsortFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ArgsortFunction() {
		docString = "argsort(a, axis=-1)";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.newInteger(-1)};
	}
	public PyObject _call(PyObject args[]) {
	return PyMultiarray.argSort(args[0], Py.py2int(args[1]));
	}
}
class ArgminFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ArgminFunction() {
		docString = "argmin(a, axis=-1)";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.newInteger(-1)};
	}
	static final BinaryUfunc argmin_ = new BinaryUfunc(BinaryUfunc.argMin);
	public PyObject _call(PyObject args[]) {
		return argmin_.reduce(args[0], Py.py2int(args[1]));
	}
}
class ArrayFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ArrayFunction() {
		docString = "array(sequence, typecode=None, copy=1)";
		argNames = new String[] {"sequence", "typecode", "copy"};
		defaultArgs = new PyObject [] {null, Py.newString("\0"), Py.One};
	}
	public PyObject _call(PyObject args[]) {
		if (args[1] == Py.None)
			args[1] = defaultArgs[1];
		if (args[2].__nonzero__()) // copy
			return PyMultiarray.array(args[0], Py.py2char(args[1]));
		else // don't copy //
			return PyMultiarray.asarray(args[0], Py.py2char(args[1]));
	}
}
class AsarrayFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	AsarrayFunction() {
		docString = "asarray(sequence, typecode=None)";
		argNames = new String[] {"sequence", "typecode"};
		defaultArgs = new PyObject [] {null, Py.newString("\0")};
	}
	public PyObject _call(PyObject args[]) {
		if (args[1] == Py.None)
			args[1] = defaultArgs[1];
		return PyMultiarray.asarray(args[0], Py.py2char(args[1]));
	}
}
class BitwiseNotFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	BitwiseNotFunction() {
		docString = "bitwise_not(a)";
		argNames = new String[] {"a"};
		defaultArgs = new PyObject [] {null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.asarray(args[0]).__not__();
	}
}
class ChooseFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ChooseFunction() {
		docString = "choose(a, indices)";
		argNames = new String[] {"a", "indices"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.choose(args[0], args[1]);
	}
}
class ClipFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ClipFunction() {
		docString = "clip(a, a_min, a_max)";
		argNames = new String[] {"a", "a_min", "a_max"};
		defaultArgs = new PyObject [] {null, null, null};
	}
	public PyObject _call(PyObject args[]) {
		final PyObject Two = Py.newInteger(2);
		// XXX Turn into PyMultiarray.clip ? 
		return PyMultiarray.choose(Umath.less.__call__(args[0], args[1]).__add__( 
			Umath.greater.__call__(args[0], args[2]).__mul__(Two)), new PyTuple(args));
	}
}
class CompressFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	CompressFunction() {
		docString = "clip(condition, a, [dimension=-1])";
		argNames = new String[] {"condition", "a", "dimension"};
		defaultArgs = new PyObject [] {null, null, Py.newInteger(-1)};
	}
	public PyObject _call(PyObject args[]) {
		// XXX Turn into PyMultiarray.compress ? 
		PyObject nonZero = PyMultiarray.repeat(JNumeric.arrayrange.__call__(Py.newInteger(args[0].__len__())), 
			Umath.not_equal.__call__(args[0], Py.Zero), 0);
		return PyMultiarray.take(args[1], nonZero, Py.py2int(args[2]));
	}
}
class ConcatenateFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ConcatenateFunction() {
		docString = "concatenate(arrays, axis=0)";
		argNames = new String[] {"arrays", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.concatenate(args[0], Py.py2int(args[1]));
	}
}
class ConvolveFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ConvolveFunction() {
		docString = "convolve(a, b, mode=0)";
		argNames = new String[] {"a", "b", "mode"};
		defaultArgs = new PyObject [] {null, null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.convolve(args[0], args[1], Py.py2int(args[2]));
	}
}
class Cross_correlateFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	Cross_correlateFunction() {
		docString = "cross_correlate(a, b, mode=0)";
		argNames = new String[] {"a", "b", "mode"};
		defaultArgs = new PyObject [] {null, null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.cross_correlate(args[0], args[1], Py.py2int(args[2]));
	}
}
class DiagonalFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	DiagonalFunction() {
		docString = "diagonal(a, offset=0, axis=-2)";
		argNames = new String[] {"a", "offset", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero, Py.newInteger(-2)};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.diagonal(args[0], Py.py2int(args[1]), Py.py2int(args[2]));
	}
}
class DotFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	DotFunction() {
		docString = "dot(a, b, axisA=-1, axisB=0)";
		argNames = new String[] {"a", "b", "axisA", "axisB"};
		defaultArgs = new PyObject [] {null, null, Py.newInteger(-1), Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.innerProduct(args[0], args[1], Py.py2int(args[2]), Py.py2int(args[3]));
	}
}
class FromfunctionFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	FromfunctionFunction() {
		docString = "fromfunction(function, dimensions)";
		argNames = new String[] {"function", "dimensions"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.fromFunction(args[0], args[1]);
	}
}
class FromstringFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	FromstringFunction() {
		docString = "fromstring(string, typecode)";
		argNames = new String[] {"string", "typecode"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		// XXX add error checking to args[0]?
		return PyMultiarray.fromString(args[0].toString(), Py.py2char(args[1]));
	}
}
class IdentityFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	IdentityFunction() {
		docString = "identity(n)"; 
		argNames = new String[] {"n"};
		defaultArgs = new PyObject [] {null};
	}
	public PyObject _call(PyObject args[]) {
		// XXX move to pyMultiarrray?
		int n = Py.py2int(args[0]);
		PyMultiarray a = PyMultiarray.zeros(new int [] {n*n}, 'i');
		for (int i = 0; i < n; i++)
			a.set(i*(n+1), Py.One);
		return PyMultiarray.reshape(a, new int [] {n, n});
	}
}
class IndicesFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	IndicesFunction() {
		docString = "indices(dimensions, typecode=None)"; 
		argNames = new String[] {"dimensions", "typecode"};
		defaultArgs = new PyObject [] {null, Py.newString('\0')};
	}
	public PyObject _call(PyObject args[]) {
		if (args[1] == Py.None)
			args[1] = defaultArgs[1];
		return PyMultiarray.indices(args[0], Py.py2char(args[1]));
	}
}
class InnerproductFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	InnerproductFunction() {
		docString = "innerproduct(a, b, axisA=-1, axisB=-1)";
		argNames = new String[] {"a", "b", "axisA", "axisB"};
		defaultArgs = new PyObject [] {null, null, Py.newInteger(-1), Py.newInteger(-1)};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.innerProduct(args[0], args[1], Py.py2int(args[2]), Py.py2int(args[3]));
	}
}
class NonzeroFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	NonzeroFunction() {
		docString = "nonzero(a)";
		argNames = new String[] {"a"};
		defaultArgs = new PyObject [] {null};
	}
	public PyObject _call(PyObject args[]) {
		// XXX Turn into PyMultiarray.nonzero ? 
		return PyMultiarray.repeat(JNumeric.arrayrange.__call__(Py.newInteger(args[0].__len__())), 
				                   Umath.not_equal.__call__(args[0], Py.Zero), 0);
	}
}
class OnesFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	OnesFunction() {
		docString = "ones(shape, typecode=None)";
		argNames = new String[] {"shape", "typecode"};
		defaultArgs = new PyObject [] {null, Py.None};
	}
	public PyObject _call(PyObject args[]) {
		if (args[1].equals(Py.None))
			return PyMultiarray.zeros(args[0]).__add__(Py.One);
		else
			return PyMultiarray.zeros(args[0], Py.py2char(args[1])).__add__(
			PyMultiarray.array(Py.One, '1'));
	}
}
class RepeatFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	RepeatFunction() {
		docString = "repeat(a, repeats, axis=0)";
		argNames = new String[] {"a", "repeats", "axis"};
		defaultArgs = new PyObject [] {null, null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.repeat(args[0], args[1], Py.py2int(args[2]));
	}
}
class ReshapeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ReshapeFunction() {
		docString = "reshape(a, shape)";
		argNames = new String[] {"a", "shape"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.reshape(args[0], PyMultiarray.objectToInts(args[1], false));
	}
}
class ResizeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ResizeFunction() {
		docString = "resize(a, shape)";
		argNames = new String[] {"a", "shape"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.resize(args[0],  PyMultiarray.objectToInts(args[1], false));
	}
}
class RavelFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	RavelFunction() {
		docString = "ravel(a)";
		argNames = new String[] {"a"};
		defaultArgs = new PyObject [] {null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.reshape(args[0], new int [] {-1});
	}
}
class SearchsortedFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	SearchsortedFunction() {
		docString = "searchsorted(a, values)";
		argNames = new String[] {"a", "values"};
		defaultArgs = new PyObject [] {null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.searchSorted(args[0], args[1]);
	}
}
class ShapeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ShapeFunction() {
		docString = "shape(a)";
		argNames = new String[] {"a"};
		defaultArgs = new PyObject [] {null};
	}
	public PyObject _call(PyObject args[]) {
		int [] shapeOf = PyMultiarray.shapeOf(args[0]);
		PyObject [] pyShapeOf = new PyObject[shapeOf.length];
		for (int i = 0; i < shapeOf.length; i++)
			pyShapeOf[i] = Py.newInteger(shapeOf[i]);
		return new PyTuple(pyShapeOf);
	}
}
class SortFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	SortFunction() {
		docString = "sort(a, axis=-1)";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.newInteger(-1)};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.sort(args[0], Py.py2int(args[1]));
	}
}
class TakeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	TakeFunction() {
		docString = "sort(a, indices, axis=-1)";
		argNames = new String[] {"a", "indices", "axis"};
		defaultArgs = new PyObject [] {null, null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.take(args[0], args[1], Py.py2int(args[2]));
	}
}
class TraceFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	TraceFunction() {
		docString = "trace(a, offset=0, axis1=-2, axis1=-1)";
		argNames = new String[] {"a", "offset", "axis1", "axis2"};
		defaultArgs = new PyObject [] {null, Py.Zero, Py.newInteger(-2), Py.newInteger(-1)};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.add.reduce(
					JNumeric.diagonal.__call__(args[0], args[1], args[2]), -1);
	} 
}
class TransposeFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	TransposeFunction() {
		docString = "transpose(a, axes=None)";
		argNames = new String[] {"a", "axes"};
		defaultArgs = new PyObject [] {null, Py.None};
	}
	public PyObject _call(PyObject args[]) {
		int [] axes;
		// Move some of this to PyMultiarray?
		if (args[1].equals(Py.None)) {
			axes = new int[PyMultiarray.shapeOf(args[0]).length];
			for (int i = 0; i < axes.length; i++)
				axes[i] = axes.length-1-i;
		}
		else {
			axes = new int[args[1].__len__()];
			for (int i = 0; i < axes.length; i++)
				axes[i] = Py.py2int(args[1].__getitem__(i));
		}
		return PyMultiarray.transpose(args[0], axes);			
	}
}
class WhereFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	WhereFunction() {
		docString = "where(condition, x, y)";
		argNames = new String[] {"condition", "x", "y"};
		defaultArgs = new PyObject [] {null, null, null};
	}
	public PyObject _call(PyObject args[]) {
		return PyMultiarray.choose(Umath.not_equal.__call__(args[0], Py.Zero),
			new PyTuple(args[2], args[1]));
	}
}
class ZerosFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ZerosFunction() {
		docString = "zeros(shape, typecode=None)";
		argNames = new String[] {"shape", "typecode"};
		defaultArgs = new PyObject [] {null, Py.None};
	}
	public PyObject _call(PyObject args[]) {
		if (args[1].equals(Py.None))
			return PyMultiarray.zeros(args[0]);
		else
			return PyMultiarray.zeros(args[0], Py.py2char(args[1]));
	}
}

// Abbreviations
class SumFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	SumFunction() {
		docString = "sum(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.add.reduce(args[0], Py.py2int(args[1]));
	}
}
class CumsumFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	CumsumFunction() {
		docString = "cumsum(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.add.accumulate(args[0], Py.py2int(args[1]));
	}
}
class ProductFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	ProductFunction() {
		docString = "product(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.multiply.reduce(args[0], Py.py2int(args[1]));
	}
}
class CumproductFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	CumproductFunction() {
		docString = "cumproduct(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.multiply.accumulate(args[0], Py.py2int(args[1]));
	}
}
class AlltrueFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	AlltrueFunction() {
		docString = "alltrue(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.logical_and.reduce(args[0], Py.py2int(args[1]));
	}
}
class SometrueFunction extends KeywordFunction {
	private static final long serialVersionUID = 1L;
	SometrueFunction() {
		docString = "sometrue(a, [axis])";
		argNames = new String[] {"a", "axis"};
		defaultArgs = new PyObject [] {null, Py.Zero};
	}
	public PyObject _call(PyObject args[]) {
		return Umath.logical_or.reduce(args[0], Py.py2int(args[1]));
	}
}

