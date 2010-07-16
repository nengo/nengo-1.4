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



public class BinaryUfunc extends KeywordFunction {

	private static final long serialVersionUID = 1L;
	
	static final public BinaryFunction add = new Add();
    static final public BinaryFunction subtract = new Subtract();
    static final public BinaryFunction multiply = new Multiply();
    static final public BinaryFunction divide = new Divide();
    static final public BinaryFunction remainder = new Remainder();
    static final public BinaryFunction power = new Power();

    static final public BinaryFunction maximum = new Maximum();
    static final public BinaryFunction minimum = new Minimum();

    static final public BinaryFunction equal = new Equal();
    static final public BinaryFunction notEqual = new NotEqual();
    static final public BinaryFunction less = new Less();
    static final public BinaryFunction lessEqual = new LessEqual();
    static final public BinaryFunction greater = new Greater();
    static final public BinaryFunction greaterEqual = new GreaterEqual();

    static final public BinaryFunction logicalAnd = new LogicalAnd();
    static final public BinaryFunction logicalOr = new LogicalOr();
    static final public BinaryFunction logicalXor = new LogicalXor();

    static final public BinaryFunction bitwiseAnd = new BitwiseAnd();
    static final public BinaryFunction bitwiseOr = new BitwiseOr();
    static final public BinaryFunction bitwiseXor = new BitwiseXor();

    static final public BinaryFunction argMax = new ArgMax();
    static final public BinaryFunction argMin = new ArgMin();

    String docString() {
	return
	    "This object has the following methods:\n" +
	    "   reduce(a [,axis])\n" +
	    "      Works just like reduce(ufunc, a, [ufunc's identity element]) except\n" +
	    "      you get to choose the axis to perform the reduction along. Note that\n" +
	    "      if the length of a long axis is 0, then the appropriate identity element\n" +
	    "      for the ufunc will be returned.\n" +
	    "   accumulate(a [,axis])\n" +
	    "      This is the same as reduce, except that all the intermediate results are\n" +
	    "      kept along the way.\n" +
	    "   outer(a, b)\n" +
	    "      This will take the outer product of a and b. The new results shape will\n" +
	    "      be the same as a.shape+b.shape (where plus means concatenate, not add!)\n" + 
	    "   reduceat(a, indices [,axis])\n" +
	    "      This is a weird function, and most people should just ignore it. It will\n" +
	    "      reduce a to each of the given indices so that as new size along the given\n" +
	    "      axis will be the same as the length of indices.\n" +
	    "If axis is not supplied it defaults to zero.";
    }
    BinaryFunction function;
	
    public BinaryUfunc(BinaryFunction function) {
	this.function = function;
	argNames = new String [] {"a", "b", "result"};
	defaultArgs = new PyObject [] {null, null, Py.None};
    }

    public PyObject __findattr__(String name) {
	if (name == "__doc__") return new PyString(function.docString() + docString());
	return super.__findattr__(name);
    }
	
    public PyObject outer(PyObject poa, PyObject pob) {
	PyMultiarray a = PyMultiarray.asarray(poa);
	PyMultiarray b = PyMultiarray.asarray(pob);
	char type = PyMultiarray.commonType(a._typecode, b._typecode);
	PyMultiarray af = PyMultiarray.reshape(PyMultiarray.ascontiguous(a, type), new int [] {-1});
	PyMultiarray bf = PyMultiarray.reshape(PyMultiarray.ascontiguous(b, type), new int [] {-1});
	PyMultiarray result = PyMultiarray.zeros(new int[] {af.dimensions[0] * bf.dimensions[0]}, type);
	for (int i = 0; i < af.dimensions[0]; i++) {
	    PyMultiarray temp = (PyMultiarray)function.call(bf, af.get(i));
	    System.arraycopy(temp.data, 0, result.data, i*bf.dimensions[0], bf.dimensions[0]); 
	}
	int [] newDimensions = new int[a.dimensions.length+b.dimensions.length];
	for (int i = 0; i < a.dimensions.length; i++)
	    newDimensions[i] = a.dimensions[i];
	for (int i = 0; i < b.dimensions.length; i++)
	    newDimensions[a.dimensions.length+i] = b.dimensions[i];
	return PyMultiarray.reshape(result, newDimensions);
    }
	
    public PyObject reduceat(PyObject po, int [] indices, int axis) {
	// This could probably be made faster by doing it directly,
	// but I don't think I care.
	PyMultiarray a = PyMultiarray.ascontiguous(po);
	axis = (axis < 0) ? axis + a.dimensions.length : axis;
	if (axis < 0 || axis >= a.dimensions.length)
	    throw Py.ValueError("axis out of legal range");
	int [] eIndices = new int[indices.length+1];
	eIndices[indices.length] = a.dimensions[axis];
	for (int i = 0; i < indices.length; i++) {
	    if (indices[i] < 0 || indices[i] >= a.dimensions[axis])
		throw Py.IndexError("invalid index to reduceat");
	    eIndices[i] = indices[i];
	}
	int [] shape = (int [])a.dimensions.clone();
	shape[axis] = indices.length;
	PyMultiarray result = PyMultiarray.zeros(shape, a._typecode);
	a = PyMultiarray.rotateAxes(a, -axis);
	PyMultiarray r = PyMultiarray.rotateAxes(result, -axis);
	for (int i = 0; i < indices.length; i++)
	    r.set(i, reduce(a.getslice(eIndices[i], eIndices[i+1], 1)));
	return result;
    }
	
    public PyObject reduceat(PyObject po, int [] indices) {
	return reduceat(po, indices, 0);
    }
	
    public PyObject reduce(PyObject po, int axis) {
	PyMultiarray a = PyMultiarray.asarray(po);
	if (axis < 0) axis += a.dimensions.length;
	if (axis < 0 || axis >= a.dimensions.length)
	    throw Py.ValueError("axis out of legal range");
	a = PyMultiarray.rotateAxes(PyMultiarray.asarray(po), -axis);
	if (a.dimensions[0] == 0) return PyMultiarray.asarray(function.identity(), a._typecode);
	// Get the array b;
	int [] shape = new int[a.dimensions.length-1];
	for (int i = 0; i < a.dimensions.length-1; i++)
	    shape[i] = a.dimensions[i+1];
//	PyMultiarray b = PyMultiarray.zeros(shape, function.returnsInt ? 'i' : a._typecode);
	PyMultiarray b = PyMultiarray.zeros(shape, a._typecode);
	// Loop over other axes and reduce...
	a = PyMultiarray.reshape(a, new int [] {a.dimensions[0], -1});
	b = PyMultiarray.reshape(b, new int [] {1, -1});
	final int s0 = a.strides[0], d0 = a.dimensions[0];
	final int s1 = a.strides[a.dimensions.length-1], d1 = a.dimensions[a.dimensions.length-1];
	for (int i = 0; i < d1; i++) 
	    function.accumulate(a.data, a.start+i*s1, d0, s0, b.data, i*s1, 1, 0, a._typecode);
	b = PyMultiarray.rotateAxes(PyMultiarray.reshape(b, shape), axis);
	return PyMultiarray.returnValue(b); // This will need to be swapped in accumulate
    }
	
    public PyObject reduce(PyObject po) {
	return reduce(po, 0); 
    }

    public PyObject accumulate(PyObject po, int axis) {
	PyMultiarray a = PyMultiarray.asarray(po);
	if (axis < 0) axis += a.dimensions.length;
	if (axis < 0 || axis >= a.dimensions.length)
	    throw Py.ValueError("axis out of legal range");
	a = PyMultiarray.rotateAxes(PyMultiarray.asarray(po), -axis);
	if (a.dimensions[0] == 0) return PyMultiarray.asarray(function.identity(), a._typecode);
	// Get the array b;
	int [] shape = (int[])a.dimensions.clone();
//	PyMultiarray b = PyMultiarray.zeros(shape, function.returnsInt ? 'i' : a._typecode);
	PyMultiarray b = PyMultiarray.zeros(shape, a._typecode);
	// Loop over other axes and reduce...
	a = PyMultiarray.reshape(a, new int [] {a.dimensions[0], -1});
	b = PyMultiarray.reshape(b, new int [] {1, -1});
	final int s0 = a.strides[0], d0 = a.dimensions[0];
	final int s1 = a.strides[a.dimensions.length-1], d1 = a.dimensions[a.dimensions.length-1];
	for (int i = 0; i < d1; i++) 
	    function.accumulate(a.data, a.start+i*s1, d0, s0, b.data, i*s1, d0, s0, a._typecode);
	b = PyMultiarray.rotateAxes(PyMultiarray.reshape(b, shape), axis);
	return PyMultiarray.returnValue(b); // This will need to be swapped in accumulate
    }

    public PyObject accumulate(PyObject po) {
	return accumulate(po, 0); 
    }

    public PyObject _call(PyObject args[]) {
	if (args[2] == Py.None)
	    return function.call(PyMultiarray.asarray(args[0]), 
				 PyMultiarray.asarray(args[1]));//__call__(args[0], args[1]);
	else {
	    if (!(args[2] instanceof PyMultiarray))
		throw Py.ValueError("result must be an array");
	    PyMultiarray a = PyMultiarray.asarray(args[0]);
	    PyMultiarray b = PyMultiarray.asarray(args[1]);
	    PyMultiarray result = (PyMultiarray)args[2];
	    // It is assumed that somewhere down the line, the dimensions of result get checked.
	    // This generally happens in __XX__(a,b).
	    return function.call(a, b, result);//return __call__(args[0], args[1], args[2]);
	}
    }
    /*	XXX remove later.
    final public PyObject __call__(PyObject oa, PyObject ob) {
	return function.call(PyMultiarray.asarray(oa), PyMultiarray.asarray(ob));
    }

    public PyObject __call__(PyObject oa, PyObject ob, PyObject result) {
	if (!(result instanceof PyMultiarray))
	    throw Py.ValueError("result must be an array");
	PyMultiarray a = PyMultiarray.asarray(oa);
	PyMultiarray b = PyMultiarray.asarray(ob);
	// It is assumed that somewhere down the line, the dimensions of result get checked.
	// This generally happens in __XX__(a,b).
	return function.call(a, b, (PyMultiarray)result);
    }
    */
}

class BinaryFunction {
    String docString() {return "binary_function(a, b [, r])\n";}
    static final PyMultiarray one = PyMultiarray.array(Py.One, '1');	
    static final PyMultiarray zero = PyMultiarray.array(Py.Zero, '1');
    // Change -- add returnType() and _returnType (= '\0');
    static final boolean returnsInt = false;

    PyMultiarray identity() {return null;}

    PyObject call(PyObject oa, PyObject ob) {
	throw Py.ValueError("call not implemented");
    }

    // Those wishing three argument calls to be efficient should override this.
    PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	PyMultiarray.copyAToB(PyMultiarray.asarray(call(a, b)), result);
	return result;
    }

    // Those wishing reduce and accumulate to be fast should to override this.
    void accumulate(Object aData, int aStart, int aDim, int aStride, 
		    Object rData, int rStart, int rDim, int rStride, char _typecode) {
	PyMultiarray array = new PyMultiarray(aData, _typecode, aStart, new int [] {aDim}, new int [] {aStride});
	PyMultiarray result = new PyMultiarray(rData, _typecode, rStart, new int [] {rDim}, new int [] {rStride});
	if (aDim == 0) return;
	PyObject r = array.get(0);
	result.set(0,r);
	int jStride = (rStride == 0) ? 0 : 1;
	for (int i=1, j=rStride; i < aDim; i++, j+=jStride) {
	    r = call(r, array.get(i));
	    result.set(j,r);
	}
    }
}


final class Add extends BinaryFunction {
    String docString() {return "add(a, b [,r]) returns a+b and stores the result in r if supplied.\n";}
    final PyMultiarray identity() {return zero;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 0;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 += aData1[sa]);
	    break;
	case 's': 
	    short lasts = 0;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts += aDatas[sa]);
	    break;
	case 'i': 
	    int lasti = 0;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti += aDatai[sa]);
	    break;
	case 'l': 
	    long lastl = 0;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl += aDatal[sa]);
	    break;
	case 'f': 
	    float lastf = 0;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf += aDataf[sa]);
	    break;
	case 'd': 
	    double lastd = 0;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd += aDatad[sa]);
	    break;
	case 'F': 
	    float lastfr = 0, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		rDataF[sr] = (lastfr += aDataF[sa]);
		rDataF[sr+1] = (lastfi += aDataF[sa+1]);
	    }
	    break;
	case 'D': 
	    double lastdr = 0, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		rDataD[sr] = (lastdr += aDataD[sa]);
		rDataD[sr+1] = (lastdi += aDataD[sa+1]);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final PyObject call(PyObject o1, PyObject o2) {
	return o1.__add__(o2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__add__(b, result);
    }
}

final class Subtract extends BinaryFunction {
    String docString() {return "subtract(a, b [,r]) returns a-b and stores the result in r if supplied.\n";}
    final PyMultiarray identity() {return zero;}	
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 0;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 -= aData1[sa]);
	    break;
	case 's': 
	    short lasts = 0;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts -= aDatas[sa]);
	    break;
	case 'i': 
	    int lasti = 0;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti -= aDatai[sa]);
	    break;
	case 'l': 
	    long lastl = 0;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl -= aDatal[sa]);
	    break;
	case 'f': 
	    float lastf = 0;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf -= aDataf[sa]);
	    break;
	case 'd': 
	    double lastd = 0;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd -= aDatad[sa]);
	    break;
	case 'F': 
	    float lastfr = 0, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		rDataF[sr] = (lastfr -= aDataF[sa]);
		rDataF[sr+1] = (lastfi -= aDataF[sa+1]);
	    }
	    break;
	case 'D': 
	    double lastdr = 0, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		rDataD[sr] = (lastdr -= aDataD[sa]);
		rDataD[sr+1] = (lastdi -= aDataD[sa+1]);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final PyObject call(PyObject po1, PyObject po2) {
	return po1.__sub__(po2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__sub__(b, result);
    }
}

final class Multiply extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 1;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 *= aData1[sa]);
	    break;
	case 's': 
	    short lasts = 1;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts *= aDatas[sa]);
	    break;
	case 'i': 
	    int lasti = 1;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti *= aDatai[sa]);
	    break;
	case 'l': 
	    long lastl = 1;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl *= aDatal[sa]);
	    break;
	case 'f': 
	    float lastf = 1;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf *= aDataf[sa]);
	    break;
	case 'd': 
	    double lastd = 1;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd *= aDatad[sa]);
	    break;
	case 'F': 
	    float lastfr = 1, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		float re = lastfr*aDataF[sa] - lastfi*aDataF[sa+1];
		rDataF[sr+1] = (lastfi = lastfr*aDataF[sa+1] + lastfi*aDataF[sa]);
		rDataF[sr] = (lastfr = re);
	    }
	    break;
	case 'D': 
	    double lastdr = 1, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		double re = lastdr*aDataD[sa] - lastdi*aDataD[sa+1];
		rDataD[sr+1] = (lastdi = lastdr*aDataD[sa+1] + lastdi*aDataD[sa]);
		rDataD[sr] = (lastdr = re);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final PyObject call(PyObject po1, PyObject po2) {
	return po1.__mul__(po2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__mul__(b, result);
    }
}

final class Divide extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 1;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 /= aData1[sa]);
	    break;
	case 's': 
	    short lasts = 1;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts /= aDatas[sa]);
	    break;
	case 'i': 
	    int lasti = 1;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti /= aDatai[sa]);
	    break;
	case 'l': 
	    long lastl = 1;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl /= aDatal[sa]);
	    break;
	case 'f': 
	    float lastf = 1;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf /= aDataf[sa]);
	    break;
	case 'd': 
	    double lastd = 1;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd /= aDatad[sa]);
	    break;
	case 'F': 
	    float lastfr = 1, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		float den = aDataF[sa]*aDataF[sa] + aDataF[sa+1]*aDataF[sa+1];
		float re = (lastfr*aDataF[sa] + lastfi*aDataF[sa+1]) / den;
		rDataF[sr+1] = (lastfi = (-lastfr*aDataF[sa+1] + lastfi*aDataF[sa]) / den);
		rDataF[sr] = (lastfr = re);
	    }
	    break;
	case 'D': 
	    double lastdr = 1, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
		double den = aDataD[sa]*aDataD[sa] + aDataD[sa+1]*aDataD[sa+1];
		double re = (lastdr*aDataD[sa] + lastdi*aDataD[sa+1]) / den;
		rDataD[sr+1] = (lastdi = (-lastdr*aDataD[sa+1] + lastdi*aDataD[sa]) / den);
		rDataD[sr] = (lastdr = re);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return po1.__div__(po2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__div__(b, result);
    }
}

final class Remainder extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 1;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 %= aData1[sa]);
	    break;
	case 's': 
	    short lasts = 1;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts %= aDatas[sa]);
	    break;
	case 'i': 
	    int lasti = 1;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti %= aDatai[sa]);
	    break;
	case 'l': 
	    long lastl = 1;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl %= aDatal[sa]);
	    break;
	case 'f': 
	    float lastf = 1;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf %= aDataf[sa]);
	    break;
	case 'd': 
	    double lastd = 1;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd %= aDatad[sa]);
	    break;
	case 'F': 
	    float lastfr = 1, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
				// This nomenclature is a little weird 'cause I stole this code from PyMultiarray.
		float reA = lastfr, imA = lastfi, reB = aDataF[sa], imB = aDataF[sa+1];
		float den = reB*reB + imB*imB;
		float n = (float)Math.floor((reA*reB+imA*imB) / den);
		rDataF[sr] = (lastfr = reA - n*reB); 
		rDataF[sr+1] = (lastfi = imA - n*imB);
	    }
	    break;
	case 'D': 
	    double lastdr = 1, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
				// This nomenclature is a little weird 'cause I stole this code from PyMultiarray.
		double reA = lastdr, imA = lastdi, reB = aDataD[sa], imB = aDataD[sa+1];
		double den = reB*reB + imB*imB;
		double n = (double)Math.floor((reA*reB+imA*imB) / den);
		rDataD[sr] = (lastdr = reA - n*reB); 
		rDataD[sr+1] = (lastdi = imA - n*imB);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return po1.__mod__(po2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__mod__(b, result);
    }
}

final class Power extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte last1 = 1;
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 = PyMultiarray.pow(last1,aData1[sa]));
	    break;
	case 's': 
	    short lasts = 1;
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts = PyMultiarray.pow(lasts,aDatas[sa]));
	    break;
	case 'i': 
	    int lasti = 1;
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti = PyMultiarray.pow(lasti,aDatai[sa]));
	    break;
	case 'l': 
	    long lastl = 1;
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl = PyMultiarray.pow(lastl,aDatal[sa]));
	    break;
	case 'f': 
	    float lastf = 1;
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = (lastf = PyMultiarray.pow(lastf,aDataf[sa]));
	    break;
	case 'd': 
	    double lastd = 1;
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = (lastd = PyMultiarray.pow(lastd,aDatad[sa]));
	    break;
	case 'F': 
	    float lastfr = 1, lastfi = 0;
	    float [] rDataF = (float[])rData, aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
				// This nomenclature is a little weird 'cause I stole this code from PyMultiarray.
		PyComplex Z = (PyComplex)new PyComplex(lastfr, lastfi).__pow__(new PyComplex(aDataF[sa], aDataF[sa+1]));
		rDataF[sr] = (lastfi = (float)Z.real); 
		rDataF[sr+1] = (lastfr = (float)Z.imag);
	    }
	    break;
	case 'D': 
	    double lastdr = 1, lastdi = 0;
	    double [] rDataD = (double[])rData, aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr) {
				// This nomenclature is a little weird 'cause I stole this code from PyMultiarray.
		PyComplex Z = (PyComplex)new PyComplex(lastdr, lastdi).__pow__(new PyComplex(aDataD[sa], aDataD[sa+1]));
		rDataD[sr] = (lastdi = Z.real); 
		rDataD[sr+1] = (lastdr = Z.imag);
	    }
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdFDO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return po1.__pow__(po2);
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__pow__(b, result);
    }
}

// Need fast ufuncs
final class Maximum extends BinaryFunction {
    final PyMultiarray identity() {throw Py.ValueError("zero size array to ufunc without identity");}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    byte last1 = aData1[sa];	
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = last1 = ((last1 > aData1[sa]) ? last1 : aData1[sa]);
	    break;
	case 's': 
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    short lasts = aDatas[sa];	
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = lasts = ((lasts > aDatas[sa]) ? lasts : aDatas[sa]);
	    break;
	case 'i': 
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    int lasti = aDatai[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = lasti = ((lasti > aDatai[sa]) ? lasti : aDatai[sa]);
	    break;
	case 'l': 
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    long lastl = aDatal[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = lastl = ((lastl > aDatal[sa]) ? lastl : aDatal[sa]);
	    break;
	case 'f': 
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    float lastf = aDataf[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = lastf = ((lastf > aDataf[sa]) ? lastf : aDataf[sa]);
	    break;
	case 'd': 
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    double lastd = aDatad[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = lastd = ((lastd > aDatad[sa]) ? lastd : aDatad[sa]);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__max(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__max(b, result);
    }
}

final class Minimum extends BinaryFunction {
    final PyMultiarray identity() {throw Py.ValueError("zero size array to ufunc without identity");}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    byte last1 = aData1[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = last1 = ((last1 < aData1[sa]) ? last1 : aData1[sa]);
	    break;
	case 's': 
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    short lasts = aDatas[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = lasts = ((lasts < aDatas[sa]) ? lasts : aDatas[sa]);
	    break;
	case 'i': 
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    int lasti = aDatai[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = lasti = ((lasti < aDatai[sa]) ? lasti : aDatai[sa]);
	    break;
	case 'l': 
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    long lastl = aDatal[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = lastl = ((lastl < aDatal[sa]) ? lastl : aDatal[sa]);
	    break;
	case 'f': 
	    float [] rDataf = (float[])rData, aDataf = (float[])aData;
	    float lastf = aDataf[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDataf[sr] = lastf = ((lastf < aDataf[sa]) ? lastf : aDataf[sa]);
	    break;
	case 'd': 
	    double [] rDatad = (double[])rData, aDatad = (double[])aData;
	    double lastd = aDatad[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatad[sr] = lastd = ((lastd < aDatad[sa]) ? lastd : aDatad[sa]);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__min(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__min(b, result);
    }
}


// I don't think equal, notEqual, etc need fast reductions (accumulate) since they don't make much sense.
final class Equal extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__eq(PyMultiarray.asarray(po2));
    }
}

final class NotEqual extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__neq(PyMultiarray.asarray(po2));
    }
}

final class Less extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__lt(PyMultiarray.asarray(po2));
    }
}

final class LessEqual extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__le(PyMultiarray.asarray(po2));
    }
}

final class Greater extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__gt(PyMultiarray.asarray(po2));
    }
}

final class GreaterEqual extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__ge(PyMultiarray.asarray(po2));
    }
}


// Back to fast reductions.
final class BitwiseAnd extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    byte last1 = aData1[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 &=  aData1[sa]);
	    break;
	case 's': 
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    short lasts = aDatas[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts &= aDatas[sa]);
	    break;
	case 'i': 
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    int lasti = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti &= aDatai[sa]);
	    break;
	case 'l': 
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    long lastl = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl &= aDatal[sa]);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__and__(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__and__(b, result);
    }
}

final class BitwiseOr extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    byte last1 = aData1[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 |=  aData1[sa]);
	    break;
	case 's': 
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    short lasts = aDatas[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts |= aDatas[sa]);
	    break;
	case 'i': 
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    int lasti = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti |= aDatai[sa]);
	    break;
	case 'l': 
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    long lastl = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl |= aDatal[sa]);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__or__(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__or__(b, result);
    }
}

final class BitwiseXor extends BinaryFunction {
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa;
	switch (type) {
	case '1': 
	    byte [] rData1 = (byte[])rData, aData1 = (byte[])aData;
	    byte last1 = aData1[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rData1[sr] = (last1 ^=  aData1[sa]);
	    break;
	case 's': 
	    short [] rDatas = (short[])rData, aDatas = (short[])aData;
	    short lasts = aDatas[sa];
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatas[sr] = (lasts ^= aDatas[sa]);
	    break;
	case 'i': 
	    int [] rDatai = (int[])rData, aDatai = (int[])aData;
	    int lasti = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = (lasti ^= aDatai[sa]);
	    break;
	case 'l': 
	    long [] rDatal = (long[])rData, aDatal = (long[])aData;
	    long lastl = 1;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatal[sr] = (lastl ^= aDatal[sa]);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__xor__(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__xor__(b, result);
    }
}

final class LogicalAnd extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa, last = 1;
	int [] rDatai = (int[])rData;
	switch (type) {
	case '1': 
	    byte [] aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aData1[sa] != 0)) ? 1 : 0);
	    break;
	case 's': 
	    short [] aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDatas[sa] != 0)) ? 1 : 0);
	    break;
	case 'i': 
	    int [] aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDatai[sa] != 0)) ? 1 : 0);
	    break;
	case 'l': 
	    long [] aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDatal[sa] != 0)) ? 1 : 0);
	    break;
	case 'f': 
	    float [] aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDataf[sa] != 0)) ? 1 : 0);
	    break;
	case 'd': 
	    double [] aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDatad[sa] != 0)) ? 1 : 0);
	    break;
	case 'F':
	    float [] aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDataF[sa] != 0 || aDataF[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'D':
	    double [] aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) & (aDataD[sa] != 0 || aDataD[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__land(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__land(b, result);
    }
}

final class LogicalOr extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return zero;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa, last = 0;
	int [] rDatai = (int[])rData;
	switch (type) {
	case '1': 
	    byte [] aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aData1[sa] != 0)) ? 1 : 0);
	    break;
	case 's': 
	    short [] aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDatas[sa] != 0)) ? 1 : 0);
	    break;
	case 'i': 
	    int [] aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDatai[sa] != 0)) ? 1 : 0);
	    break;
	case 'l': 
	    long [] aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDatal[sa] != 0)) ? 1 : 0);
	    break;
	case 'f': 
	    float [] aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDataf[sa] != 0)) ? 1 : 0);
	    break;
	case 'd': 
	    double [] aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDatad[sa] != 0)) ? 1 : 0);
	    break;
	case 'F':
	    float [] aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDataF[sa] != 0 || aDataF[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'D':
	    double [] aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) | (aDataD[sa] != 0 || aDataD[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__lor(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__lor(b, result);
    }
}

final class LogicalXor extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return one;}
    final void accumulate(Object aData, int sa, int dim, int dsa, Object rData, int sr, int rDim, int dsr, char type) {
	int maxSa = sa + dim*dsa, last = 1;
	int [] rDatai = (int[])rData;
	switch (type) {
	case '1': 
	    byte [] aData1 = (byte[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aData1[sa] != 0)) ? 1 : 0);
	    break;
	case 's': 
	    short [] aDatas = (short[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDatas[sa] != 0)) ? 1 : 0);
	    break;
	case 'i': 
	    int [] aDatai = (int[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDatai[sa] != 0)) ? 1 : 0);
	    break;
	case 'l': 
	    long [] aDatal = (long[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDatal[sa] != 0)) ? 1 : 0);
	    break;
	case 'f': 
	    float [] aDataf = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDataf[sa] != 0)) ? 1 : 0);
	    break;
	case 'd': 
	    double [] aDatad = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDatad[sa] != 0)) ? 1 : 0);
	    break;
	case 'F':
	    float [] aDataF = (float[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDataF[sa] != 0 || aDataF[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'D':
	    double [] aDataD = (double[])aData;
	    for (; sa != maxSa; sa += dsa, sr += dsr)
		rDatai[sr] = last = (((last != 0) ^ (aDataD[sa] != 0 || aDataD[sa+1] != 0)) ? 1 : 0);
	    break;
	case 'O':
	    super.accumulate(aData, sa, dim, dsa, rData, sr, rDim, dsr, type);
	    break;
	default:
	    throw Py.ValueError("typecd must be in [zcbhilfdO]");
	}
    }
    final public PyObject call(PyObject po1, PyObject po2) {
	return PyMultiarray.asarray(po1).__lxor(PyMultiarray.asarray(po2));
    }
    final PyObject call(PyMultiarray a, PyMultiarray b, PyMultiarray result) {
	return a.__lxor(b, result);
    }
}

final class ArgMax extends BinaryFunction { 	
    boolean returnsInt = true;
    final PyMultiarray identity() {return zero;}
    final void accumulate(Object aData, int aStart, int aDim, int aStride, 
			  Object rData, int rStart, int rDim, int rStride, char _typecode) {	
	if (aDim == 0) return;
	int lastIndex = 0;
	switch (_typecode) {
	case '1': case 's': case 'i': case 'l': case 'f': case 'd':
	    double lastd = ((Number)Array.get(aData, aStart)).doubleValue(), tempd;
	    for (int i = 0; i < aDim; i++, aStart += aStride, rStart += rStride) {
		if ((tempd = ((Number)Array.get(aData, aStart)).doubleValue()) > lastd) {
		    lastIndex = i;
		    lastd = tempd;
		}
		Array.setInt(rData, rStart, lastIndex);
	    }
	    break;
	case 'O':
	    PyObject lastO = (PyObject)Array.get(aData, aStart), tempO;
	    for (int i = 0; i < aDim; i++, aStart += aStride, rStart += rStride) {
		if ((tempO = ((PyObject)Array.get(aData, aStart))).__cmp__(lastO) > 0) {
		    lastIndex = i;
		    lastO = tempO;
		}
		Array.setInt(rData, rStart, lastIndex);
	    }
	    break;
	default:
	    throw Py.ValueError("typecode must be in [zcbhilfd]");
	}
    }
}

final class ArgMin extends BinaryFunction {
    boolean returnsInt = true;
    final PyMultiarray identity() {return zero;}
    final void accumulate(Object aData, int aStart, int aDim, int aStride, 
			  Object rData, int rStart, int rDim, int rStride, char _typecode) {	
	if (aDim == 0) return;
	int lastIndex = 0;
	switch (_typecode) {
	case '1': case 's': case 'i': case 'l': case 'f': case 'd':
	    double lastd = ((Number)Array.get(aData, aStart)).doubleValue(), tempd;
	    for (int i = 0; i < aDim; i++, aStart += aStride, rStart += rStride) {
		if ((tempd = ((Number)Array.get(aData, aStart)).doubleValue()) < lastd) {
		    lastIndex = i;
		    lastd = tempd;
		}
		Array.setInt(rData, rStart, lastIndex);
	    }
	    break;
	case 'O':
	    PyObject lastO = (PyObject)Array.get(aData, aStart), tempO;
	    for (int i = 0; i < aDim; i++, aStart += aStride, rStart += rStride) {
		if ((tempO = ((PyObject)Array.get(aData, aStart))).__cmp__(lastO) < 0) {
		    lastIndex = i;
		    lastO = tempO;
		}
		Array.setInt(rData, rStart, lastIndex);
	    }
	    break;
	default:
	    throw Py.ValueError("typecode must be in [zcbhilfd]");
	}
    }
}


