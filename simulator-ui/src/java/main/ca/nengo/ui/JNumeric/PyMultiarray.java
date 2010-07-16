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
import java.lang.Math;
//import java.util.Arrays;
import java.util.Comparator;
import java.io.UnsupportedEncodingException;




// TODO
// * Fix up interface to unary ufuncs.
// * Fix up object behaviour (doesn't work correctly(?) with e.g, tuples).
// * Pow uses Math.pow for integer types -- should be fixed to use an integer pow.
//   Strategy define Multiarray._pow(double, double), Multiarray._pow(int, int), etc and use instead of 
//   Math.pow. (double and float cases would use math.pow). (Look at CNumeric umath).

public class PyMultiarray extends PySequence {
	
	private static final long serialVersionUID = 1L;		// TCS
    public PyObject pyget(int index) { return get(index);}  // TCS
	

    //
    // Variables and the basic constructor for Multiarray.
    // (These should only be accessed from within the Numeric (java) package.)
    //

    // Class variables.
    ///** Python function that are used to format arrays for str([0]) and repr([1]). */
    //static PyFunction [] str_functions = {null, null};
    public static int maxLineWidth = 77;
    public static int precision = 8;
    public static boolean suppressSmall = false;
    /** Python class of PyMultiarray. @see PyObject */
    public static PyClass __class__;
    /** The docstring. */
    String docString = "PyMultiarray methods:\n" +
	"	astype(typecode)]\n" +
	"	itemsize()\n" +
	"	byteswapped()\n" +
	"	copy()\n" +
	"	typecode()\n" +
	"	iscontiguous()\n" +
	"	tostring()\n" +
	"	tolist()\n";
    // Instance variables.
    /** 1D Java array that holds array data. May be shared between arrays.*/ 
    Object data;
    /** Type code of the array. Allowable types are: 
	'1':Byte, 's':Short, 'i':Int, 'l':Long, 
	'f':Float, 'd':Double, 'F':ComplexFloat, 'D':ComplexDouble,
	'O':PyObject.
    */ 
    char _typecode;
    // Start, dimensions, and strides determine the structure of the array.
    int start;
    int [] dimensions;
    int [] strides;
    // Many functions work only on contiguous arrays, so we keep track of that here.
    boolean isContiguous;

    // Constructors.
    /** Create an multiarray object given values of instance variables. */
    public PyMultiarray(Object data, char _typecode, int start, int [] dimensions, int [] strides) {
    super();//super(__class__);   // TCS
	this.data = data;
	this._typecode = _typecode;
	this.start = start;
	this.dimensions = dimensions;
	this.strides = strides;
	this.isContiguous = true;
	setIsContiguous();
    }

    /** Create a multiarray object from a sequence and a type. */
    public PyMultiarray(PyObject seq, char typecode) {
	PyMultiarray a = array(seq, typecode);
	data = a.data; _typecode = a._typecode; start = a.start; 
	dimensions = a.dimensions; strides = a.strides; isContiguous = a.isContiguous;
    }
	
    /** Create a multiarray object from a sequence. */
    public PyMultiarray(PyObject seq) {
	PyMultiarray a = array(seq, '\0');
	data = a.data; _typecode = a._typecode; start = a.start; 
	dimensions = a.dimensions; strides = a.strides; isContiguous = a.isContiguous;
    }

    /** Create a multiarray object from a sequence (the slow general way) */
    private static PyMultiarray seqToMultiarray(PyObject seq, char typecode) {
	int [] newShape = shapeOf(seq);
	PyObject [] flatData = seqToObjects(seq, shapeToNItems(newShape));
	typecode = (typecode == '\0') ? objectsToType(flatData) : typecode;
	PyMultiarray newArray = zeros(shapeOf(seq), typecode);
	int size = typeToNElements(typecode);
	for (int i = 0; i < flatData.length; i++)
	    Array.set(newArray.data, size*i, objectToJava(flatData[i], typecode, true));
	// Set complex elements if array is complex.
	if (size == 2)
	    for (int i = 0; i < flatData.length; i++) 
		Array.set(newArray.data, size*i+1, objectToJava(flatData[i], typecode, false));
	return newArray;
    }


    private static char arrayClassToType(Class<?> klass) {
	if (klass.isArray())
	    return arrayClassToType(klass.getComponentType());
	return classToType(klass);
    }

    private static int [] arrayDataToShape(Object data, int depth) {
	int length = Array.getLength(data);
	Class<?> klass = data.getClass().getComponentType();
	// If data is an array of arrays:
	if (length != 0 && klass.isArray()) {
	    int [] shape = arrayDataToShape(Array.get(data, 0), depth+1);
	    shape[depth] = length;
	    // Verify that the array is well formed.
	    for (int i = 0; i < length; i++) {
		int [] shape2 = arrayDataToShape(Array.get(data, i), depth+1);
		if (shape.length != shape2.length)
		    throw Py.ValueError("malformed array");
		for (int j = depth+1; j < shape.length; j++)
		    if (shape[j] != shape2[j])
			throw Py.ValueError("malformed array");
	    }
	    return shape;
	}
	// If data is just an array of Primitives:
	int [] shape = new int[depth+1];
	shape[depth] = length;
	return shape;
    }

    private static int arrayDataToFlat(Object data, Object flat, int offset) {
    	Class<?> klass = data.getClass().getComponentType();
	int length = Array.getLength(data);
	if (klass.isArray()) {
	    for (int i = 0; i < length; i++)
		offset = arrayDataToFlat(Array.get(data, i), flat, offset);
	    return offset;
	}
	System.arraycopy(data, 0, flat, offset, length);
	return offset + length;
    }

    /** Create a multiarray object from a PyArray (jarray) don't coppy unless forced to. */
    private static PyMultiarray arrayToMultiarray(PyArray seq, char typecode) {
	Object data = seq.__tojava__(Object.class);
	char type = arrayClassToType(data.getClass());
	int [] shape = arrayDataToShape(data, 0);
	int [] strides = shapeToStrides(shape, typeToNElements(type));
	if (shape.length > 1) {
	    Object flat = Array.newInstance(typeToClass(type), shapeToNItems(shape));
	    arrayDataToFlat(data, flat, 0);
	    data = flat;
	}		 
	PyMultiarray ma = new PyMultiarray(data, type, 0, shape, strides); 
	return  (typecode == '\0' || typecode == type) ? ma : array(ma, typecode);
    }
	
    //
    // Multiarray creation functions.
    //
	
    /** Create a new multiarray from <code>seq</code> of type <code>typecode</code> 
	('/0' indicates the type should be inferred from seq).*/
    public static PyMultiarray array(PyObject seq, char typecode) {
	if (seq instanceof PyMultiarray) {
	    PyMultiarray a = (PyMultiarray)seq;
	    PyMultiarray b = zeros(a.dimensions, (typecode == '\0') ? a._typecode : typecode);
	    copyAToB(a, b);
	    return b;
	}
	if (seq instanceof PyArray) {
	    PyArray copyOfSeq = (PyArray)seq.__getslice__(Py.None, Py.None, Py.None);
	    return arrayToMultiarray(copyOfSeq, typecode);
	}
	return seqToMultiarray(seq, typecode);
    }

    /** Create a new multiarray from <code>seq</code>. The type is determined by examining <code>seq</code>.*/
    public static PyMultiarray array(PyObject seq) {
	return array(seq, '\0');
    }

    /** Create a new multiarray of zeros with shape <code>shape</code> of type <code>typecode</code>.*/
    public static PyMultiarray zeros(int [] shape, char typecode) {
	//int length = Math.max(1, typeToNElements(typecode)*shapeToNItems(shape));
	int length = typeToNElements(typecode)*shapeToNItems(shape);
	Object data = Array.newInstance(typeToClass(typecode), length);
	if (typecode == 'O')
	    for (int i = 0; i < Array.getLength(data); i++)
		Array.set(data, i, Py.Zero);
	int [] strides = shapeToStrides(shape, typeToNElements(typecode));
	return new PyMultiarray(data, typecode, 0, shape, strides);
    }

    /** Create a new multiarray of zeros with shape <code>shape</code> of type <code>typecode</code>.*/
    public static PyMultiarray zeros(Object shape, char typecode) {
	return zeros(objectToInts(shape, true), typecode);
    }

    /** Create a new multiarray of zeros with shape <code>shape</code> with type inferenced from shape.*/
    public static PyMultiarray zeros(PyObject shape) {
	return zeros(objectToInts(shape, true), asarray(shape).typecode());
    }

    /** Create a range of numbers in [start, stop) with the given step and typecode.*/
    static PyMultiarray arrayRange(double start, double stop, double step, char typecode) {
	int length = Math.max(0, (int)Math.ceil((stop - start) / step));
	PyMultiarray a = zeros(new int [] {length}, 'd');
	for (int i = 0; i < length; i++) 
	    Array.setDouble(a.data, i, i*step+start);
	return a.astype(typecode);
    }

    /** Like above, but with the semantics of Python range.*/
    public static PyMultiarray arrayRange(PyObject start, PyObject stop, PyObject step, char typecode) {
	if (stop instanceof PyNone) {
	    stop = start;
	    start = Py.Zero;
	}
	if (typecode == '\0')
	    typecode = objectsToType(new PyObject [] {start, stop, step});
	if (typecode != 'O') 
	    return arrayRange(start.__float__().getValue(), stop.__float__().getValue(), 
			      step.__float__().getValue(), typecode);
	// Treat objects specially.
	PyObject lengthObject = (stop._sub(start))._div(step);
	int length = Math.max(0, (int)Math.ceil(lengthObject.__float__().getValue()));
	start = __builtin__.coerce(start, lengthObject).__getitem__(0);
	PyMultiarray a = zeros(new int [] {length}, 'O');
	for (int i = 0; i < length; i++) { 
	    a.set(i, start);
	    start = start._add(step);
	}
	return a;
    }

    /** Create a set of indices for use with fromFunction.*/
    public static PyMultiarray indices(PyObject o, char typecode) {
	int [] baseShape = objectToInts(o, true);
	int [] shape = new int [baseShape.length+1];
	for (int i = 0; i < baseShape.length; i++)
	    shape[i+1] = baseShape[i];
	shape[0] = baseShape.length;
	if (typecode == '\0') 
	    typecode = asarray(o)._typecode;
	PyMultiarray result = zeros(shape, typecode);
	for (int i = 0; i < baseShape.length; i++) {
	    PyMultiarray subArray = swapAxes(asarray(result.get(i)), 0, i);
	    subArray.__add__(arrayRange(0, baseShape[i], 1, typecode), subArray);
	}
	return result;
    }

    /** Create an array by calling function the coordinates of each element of an array with the given shape.*/
    public static PyMultiarray fromFunction(PyObject function, PyObject shape) {
	PyMultiarray index = indices(shape, '\0');
	PyMultiarray result = zeros(shape);
	PyMultiarray flatIndex = swapAxes(reshape(index, new int [] {result.dimensions.length, -1}), 0, 1);
	PyMultiarray flatResult = reshape(result, new int [] {-1});
	PyObject [] args = new PyObject[0];
	String [] keywords = new String[0];
	Class<?> objectArray = args.getClass();
	for (int i = 0; i < flatResult.dimensions[0]; i++) {
	    args = (PyObject[])((PyMultiarray)flatIndex.get(i)).tolist().__tojava__(objectArray);
	    flatResult.set(i, function.__call__(args, keywords));
	}
	return result;
    }

    /** Create a 1D array from a string. @see tostring*/
    public static PyMultiarray fromString(String s, char type) {
	int itemsize = typeToNBytes(type)*typeToNElements(type);
	if (s.length() % itemsize != 0)
	    throw Py.ValueError("string size must be a multiple of element size");
	Object data;
	try { 
	    data = fromByteArray(s.getBytes("ISO-8859-1"), type);
	} 
	catch (UnsupportedEncodingException e) {
	    throw Py.RuntimeError("ISO-LATIN-1 encoding unavailable, can't convert from string.");
	}
	return new PyMultiarray(data, type, 0, 
				new int [] {Array.getLength(data) / typeToNElements(type)}, 
				new int [] {typeToNElements(type)});
    }

    /** Return <code>seq</code> if it's a multiarray of type <code>typecode</code>, 
	otherwise returns a new multiarray.*/
    public static PyMultiarray asarray(PyObject seq, char typecode) {
	if (seq instanceof PyMultiarray && 
	    (typecode == '\0' || ((PyMultiarray)seq)._typecode == typecode)) 
	    return (PyMultiarray) seq;
	if (seq instanceof PyArray)
	    return arrayToMultiarray((PyArray)seq, typecode);
	return array(seq, typecode);
    }
	
    /** Return <code>seq</code> if it's a multiarray, otherwise returns a new multiarray.*/
    public static PyMultiarray asarray(PyObject seq) {
	if (seq instanceof PyMultiarray) 
	    return (PyMultiarray) seq;
	return array(seq);
    }

    /** Return <code>seq</code> if it's a contiguous multiarray, otherwise returns a new multiarray.*/
    public static PyMultiarray ascontiguous(PyObject seq, char _typecode) {
	if (seq instanceof PyMultiarray && ((PyMultiarray)(seq)).isContiguous &&
	    ((PyMultiarray)seq)._typecode == _typecode) 
	    return (PyMultiarray) seq;
	return array(seq, _typecode);
    }

    /** Return <code>seq</code> if it's a contiguous multiarray of type <code>typecode</code>, 
	otherwise returns a new multiarray.*/
    public static PyMultiarray ascontiguous(PyObject seq) {
	if (seq instanceof PyMultiarray && ((PyMultiarray)(seq)).isContiguous)
	    return (PyMultiarray) seq;
	return array(seq);
    }

    //
    // Public multiarray methods and attributes.
    //
	
    /** Return the typecode. @see _typecode*/
    public char typecode() {return _typecode;}

    /** Return the size (in bytes) of the items.*/ 
    public int itemsize() {return typeToNElements(_typecode) * typeToNBytes(_typecode);}

    /** Return (1) if the array is contiguous, 0 otherwise. */
    public int iscontiguous() {return isContiguous ? 1 : 0;}

    /** Return multiarray coerced to <code>type</code>

     Note that CNumeric does the equivalen of return array(this, type) here. */
    public PyMultiarray astype(char type) {return asarray(this, type);}

    /** Return multiarray data represented as a string.*/
    public String tostring() {
	try { 
	    return new String(toByteArray(array(this).data, _typecode), "ISO-8859-1");
	}
	catch (UnsupportedEncodingException  e) {
	    throw Py.RuntimeError("ISO-LATIN-1 encoding unavailable, can't convert to string.");
	}
    }
	
    /** Return a multiarray with data byte swapped (little to big endian).*/
    public final PyMultiarray byteswapped() {
	// This assumes typesize is even or 1 (it better be!).
	PyMultiarray result = array(this); 
	byte [] bytes = toByteArray(result.data, _typecode);
	int typesize = typeToNBytes(_typecode);
	int swaps = typesize / 2;
	for (int i = 0; i < Array.getLength(result.data); i++)
	    for (int j = 0; j < swaps; j++) {
		byte temp = bytes[i*typesize+j];
		bytes[i*typesize+j] = bytes[i*typesize+(typesize-1-j)];
		bytes[i*typesize+(typesize-1-j)] = temp;
	    }
	result.data = fromByteArray(bytes, _typecode);
	return result;
    }

    /** Return a copy .*/
    public final PyMultiarray copy() {
	return array(this);
    }

    /** Return multiarray as a Python list.*/
    public final PyList tolist() {
	if (dimensions.length == 0)
	    throw Py.ValueError("Can't convert a 0d array to a list");
	PyObject [] items = new PyObject[dimensions[0]];
	if (dimensions.length == 1)
	    for (int i = 0; i < dimensions[0]; i++)
		items[i] = get(i);
	else
	    for (int i = 0; i < dimensions[0]; i++)
		items[i] = ((PyMultiarray)get(i)).tolist();
	return new PyList(items);
    }


    //
    // Operations on matrices.
    // 

    /** Return a reshaped multiarray (shares data with <code>a</code> if it is contiguous).*/
    public static PyMultiarray reshape(PyObject o, int [] shape) {
	PyMultiarray a = ascontiguous(o);
	shape = fixedShape(shape, shapeToNItems(a.dimensions));
	int [] strides = shapeToStrides(shape, typeToNElements(a._typecode));
	return new PyMultiarray(a.data, a._typecode, a.start, shape, strides);
    }

    /** Return a new array with the specified shape.  The original array can have any total size.*/
    public static PyMultiarray resize(PyObject o, int [] shape) {
	PyMultiarray a = ascontiguous(reshape(asarray(o), new int [] {-1}));
	int nItems = shapeToNItems(shape);
	int nCopies = nItems / a.dimensions[0];
	int extra = nItems % a.dimensions[0];
	int nElements = typeToNElements(a._typecode);
	PyMultiarray result = zeros(shape, a._typecode);
	for (int i = 0; i < nCopies; i++)
	    System.arraycopy(a.data, a.start, result.data, i*a.dimensions[0]*nElements, a.dimensions[0]*nElements);
	System.arraycopy(a.data, a.start, result.data, nCopies*a.dimensions[0]*nElements, extra*nElements);
	return result;
    }

    /** Return a new, sorted array.*/
    public static PyMultiarray sort(PyObject o, int axis) {
	PyMultiarray a = array(swapAxes(asarray(o), axis, -1));
	int stride = a.dimensions[a.dimensions.length-1];
	for (int i = 0; i < Array.getLength(a.data); i += stride) {
	    switch (a._typecode) {
	    case '1': java.util.Arrays.sort((byte [])a.data, i, i+stride); break;
	    case 's': java.util.Arrays.sort((short [])a.data, i, i+stride); break;
	    case 'i': java.util.Arrays.sort((int [])a.data, i, i+stride); break;
	    case 'l': java.util.Arrays.sort((long [])a.data, i, i+stride); break;
	    case 'f': java.util.Arrays.sort((float [])a.data, i, i+stride); break;
	    case 'd': java.util.Arrays.sort((double [])a.data, i, i+stride); break;
	    default:
		throw Py.ValueError("unsortable array type");
	    }
	}
	return swapAxes(a, axis, -1);
    }

    /** See Numeric documentation. */
    public static PyMultiarray argSort(PyObject o, int axis) {
	// We depend on 'a' being 'vanilla' below.
	// There's a probable extra copy here though.
	PyMultiarray a = array(swapAxes(asarray(o), axis, -1)); 
	if (a._typecode == 'F' || a._typecode == 'D' || a._typecode == 'O')
	    throw Py.ValueError("unsortable array type");
	// 'data' holds the argsorted indices.
	int stride = a.dimensions[a.dimensions.length-1];
	int size = Array.getLength(a.data);
	int data[] = new int[size];
	// Create an array of Numbers tagged with indices
	class IndexedArray {Number item; int index;}
	IndexedArray ia[] = new IndexedArray[stride];
	for (int j = 0; j < stride; j++)
	    ia[j] = new IndexedArray();
	// Create a comparator that sorts an IndexArray
	Comparator<IndexedArray> comp = new Comparator<IndexedArray>() {
		public int compare(IndexedArray ia1, IndexedArray ia2) {
		    if (ia1.item.equals(ia2.item)) return 0;
		    double d1 = ia1.item.doubleValue();
		    double d2 = ia2.item.doubleValue();
		    if (d1 > d2) return 1;
		    return -1;
		}
	    };
	// Loop over all indices except the last.
	for (int i = 0; i < size; i += stride) {
	    for (int j = 0; j < stride; j++) {
		ia[j].item = (Number)Array.get(a.data, i+j);
		ia[j].index = j;
	    }
	    // sort 'ia' 
	    java.util.Arrays.sort(ia, comp);
	    // Load 'a' with the indices from 'ia' and change its type.
	    for (int j = 0; j < stride; j++)
		data[i+j] = ia[j].index;
	}
	a._typecode = 'i';
	a.data = data;
	return swapAxes(a, axis, -1);
    }


    /** Return a multiarray with the axes transposed according to <code>perms</code> (shares data with <code>a</code>).*/
    public static PyMultiarray transpose(PyObject o, int [] perms) {
	PyMultiarray a = asarray(o);
	boolean [] used = new boolean[perms.length];
	for (int i = 0; i < perms.length; i++) {
	    int axis = perms[i];
	    axis = (axis < 0) ? a.dimensions.length+axis : axis;
	    if (axis < 0 || axis >= a.dimensions.length || used[axis])
		throw Py.ValueError("illegal permuation");
	    perms[i] = axis;
	    used[axis] = true;
	}
	PyMultiarray ans = new PyMultiarray(a.data, a._typecode, a.start, (int [])a.dimensions.clone(), (int [])a.strides.clone());
	for (int i = 0; i < perms.length; i++) {
	    ans.dimensions[i] = a.dimensions[perms[i]];
	    ans.strides[i] = a.strides[perms[i]];
	}
	ans.setIsContiguous();
	return ans;
    }

    // See David Ascher's Numeric Python documentation for what these do.

    public static PyMultiarray repeat(PyObject oA, PyObject oRepeats, int axis) {
	PyMultiarray a = asarray(oA), repeats = asarray(oRepeats);
	// Check axis and swap axis to zero.
	if (axis < 0) axis += a.dimensions.length;
	if (axis < 0 || axis >= a.dimensions.length)
	    throw Py.ValueError("illegal axis");
	a = swapAxes(a, 0, axis);
	// Check repeats argument, copy and cast to integer.
	repeats = array(repeats, 'i');
	if (repeats.dimensions.length != 1)
	    throw Py.ValueError("wrong number of dimensions");
	// Create the result array.
	int [] dimensions = (int []) a.dimensions.clone();
	for (int i = dimensions[0] = 0; i < repeats.dimensions[0]; i++)
	    dimensions[0] += Array.getInt(repeats.data, i);
	PyMultiarray result = zeros(dimensions, a._typecode);
	int location = 0;
	for (int i = 0; i < repeats.dimensions[0]; i++) {
	    PyObject chunk = a.get(i);
	    for (int j = 0; j < Array.getInt(repeats.data, i); j++)
		result.set(location++, chunk);
	}
	// Swap axis of result back to where it belongs and return.
	return swapAxes(result, 0, axis);
    }

    public static PyMultiarray take(PyObject oA, PyObject oIndices, int axis) {
	PyMultiarray a = asarray(oA), indices = asarray(oIndices);
	// Check axis and rotate the axis to zero.
	if (axis < 0) axis += a.dimensions.length;
	if (axis < 0 || axis >= a.dimensions.length)
	    throw Py.ValueError("illegal axis");
	a = ascontiguous(rotateAxes(a, -axis));
	// Check indices argument, copy and cast to integer.
	indices = array(indices, 'i');
	if (indices.dimensions.length != 1)
	    throw Py.ValueError("wrong number of dimensions");
	// Create the result array.
	int [] dimensions = (int []) a.dimensions.clone();
	dimensions[0] = indices.dimensions[0];
	PyMultiarray result = zeros(dimensions, a._typecode);
	int stride = result.strides[0];
	int start = a.start;
	for (int i = 0; i < indices.dimensions[0]; i++) {
	    int item = Array.getInt(indices.data, i);
	    for (int j = 0; j < stride; j++)
		Array.set(result.data, i*stride+j, Array.get(a.data, start + item*stride+j));
	}
	// Rotate axis of result back to where it belongs and return.
	return rotateAxes(result, axis);
    }

    public static PyMultiarray choose(PyObject oA, PyObject b) {
	PyMultiarray a = array(oA);
	// Convert b into an array of PyMultiarrays.
	// (b must be an PyObject because it might not be rectangular).
	int n = b.__len__();
	PyMultiarray [] bs = new PyMultiarray[n];
	for (int i = 0; i < n; i++)
	    bs[i] = ascontiguous(b.__getitem__(i));
	// Find a common type for the arrays in bs.
	char type = (n > 0) ? bs[0]._typecode : 'i';
	for (int i = 1; i < n; i++)
	    type = commonType(type, bs[i]._typecode);
	// find bs array sizes and coerce its elements to correct type. 
	a = ascontiguous(a, 'i');
	int [] sizes = new int[n];
	for (int i = 0; i < n; i++) {
	    if (a.dimensions.length < bs[i].dimensions.length)
		throw Py.ValueError("too many dimensions");
	    for (int j = 0; j < bs[i].dimensions.length; j++)
		if (a.dimensions[j+a.dimensions.length-bs[i].dimensions.length] != bs[i].dimensions[j])
		    throw Py.ValueError("array dimensions must agree.");
	    bs[i] = array(bs[i], type);
	    sizes[i] = Array.getLength(bs[i].data);
	}
	PyMultiarray result = zeros(a.dimensions, type);
	PyMultiarray flat = reshape(result, new int [] {-1});
	for (int i = 0; i < Array.getLength(flat.data); i++) {
	    int index = Array.getInt(a.data, i);
	    if (index < 0 || index >= n)		
		throw Py.ValueError("invalid entry in choice array");
	    Array.set(flat.data, i, Array.get(bs[index].data, i % sizes[index]));
	}
	return result;
    }

    public static PyMultiarray concatenate(PyObject po, int axis) {
	if (po.__len__() == 0)
	    return zeros(new int [] {}, 'i');
	// Check axis and rotate the axis to zero.
	PyMultiarray proto = asarray(po.__getitem__(0));
	if (axis < 0) axis += proto.dimensions.length;
	if (axis < 0 || axis >= proto.dimensions.length)
	    throw Py.ValueError("illegal axis");
	// Make array of multiarrays.
	PyMultiarray [] as = new PyMultiarray[po.__len__()];
	as[0] = proto = rotateAxes(proto, -axis);
	char type = proto.typecode();
	int [] dimensions = (int []) proto.dimensions.clone();
	for (int i = 1; i < as.length; i++) {
	    as[i] = rotateAxes(asarray(po.__getitem__(i)), -axis);
	    type = commonType(type, as[i]._typecode);
	    if (as[i].dimensions.length != proto.dimensions.length)
		throw Py.ValueError("mismatched array dimensions");
	    for (int j = 1; j < proto.dimensions.length; j++)
		if (as[i].dimensions[j] != proto.dimensions[j])
		    throw Py.ValueError("mismatched array dimensions");
	    dimensions[0] += as[i].dimensions[0];
	}
	// Construct the result.
	PyMultiarray result = zeros(dimensions, type);
	int start = 0;
	for (int i = 0; i < as.length; i++) {
	    int end = start+as[i].dimensions[0];
	    result.setslice(start, end, 1, as[i]);
	    start = end;
	}
	// Rotate axes back and return result.
	return rotateAxes(result, axis);
    }

    /** Return the diagonal of a matrix.*/
    // XXX This is a direct translation from Python -- need to figure it out. 
    public static PyMultiarray diagonal(PyObject o, int offset, int axis) {
	// XXX Check arguments.
	PyMultiarray a = rotateAxes(asarray(o), -2-axis);
	int lastDimension = a.dimensions[a.dimensions.length-1];
	int [] shape = new int[a.dimensions.length-1];
	for (int i = 0; i < shape.length; i++)
	    shape[i] = a.dimensions[i];
	shape[shape.length-1] *= lastDimension;
	a = reshape(a, shape);
	if (offset < 0) offset = lastDimension - (offset+1);
	a = take(a, arrayRange(offset, shape[shape.length-1], lastDimension+1, 'i'), -1);
	return rotateAxes(a, 2+axis);
    }

    // XXX check again!
    public static PyObject innerProduct(PyObject oA, PyObject oB, int axisA, int axisB) {
	PyMultiarray a = ascontiguous(oA), b = ascontiguous(oB);
	// Check arguments
	// This next line emulates CNumeric behaviour that I'm not sure I like.
	if (a.dimensions.length == 0 || b.dimensions.length == 0) return a.__mul__(b);
	char type  = commonType(a._typecode, b._typecode);
	if (axisA < 0) axisA += a.dimensions.length;
	if (axisB < 0) axisB += b.dimensions.length;
	if (axisA < 0 || axisA >= a.dimensions.length || axisB < 0 || axisB >= b.dimensions.length)
	    throw Py.ValueError("illegal axis");
	if (a.dimensions[axisA] != b.dimensions[axisB])
	    throw Py.ValueError("arrays must be of same length along given axes");
	// Rotate given axes to 0.
	a = rotateAxes(a, -axisA);
	b = rotateAxes(b, -axisB);
	// Now do the inner product.
	int nDimsA = a.dimensions.length, nDimsB = b.dimensions.length;
	int [] dimensions = new int[nDimsA+nDimsB-2];
	int [] aDimensions = new int[nDimsA+nDimsB-1];
	int [] bDimensions = new int[nDimsA+nDimsB-1];
	for (int i = 1; i < nDimsA; i++) {
	    dimensions[i-1] = aDimensions[i] = a.dimensions[i];
	    bDimensions[i] = 1;
	}
	for (int i = 1; i < nDimsB; i++) {
	    dimensions[nDimsA+i-2] = bDimensions[nDimsA+i-1] = b.dimensions[i];
	    aDimensions[nDimsA+i-1] = 1;
	}
	aDimensions[0] = bDimensions[0] = a.dimensions[0];
	a = reshape(a, aDimensions);
	b = reshape(b, bDimensions);
	PyMultiarray result = zeros(dimensions, type);
	for (int i = 0; i < a.dimensions[0]; i++)
	    result.__add__(asarray(asarray(a.get(i)).__mul__(b.get(i))), result);
	// unrotate the axes and return.
	int [] axes = new int[result.dimensions.length];
	for (int i = 0; i < nDimsA-1; i++)
	    axes[i] = (i+axisA) % (nDimsA-1);
	for (int i = 0; i < nDimsB-1; i++)
	    axes[nDimsA-1+i] = (nDimsA-1) + (i+axisB-1) % (nDimsB-1);
	return returnValue(transpose(result, axes));
    }

    /** Return an array of indices of where the items would be locatated in the given array.*/
    public static PyObject searchSorted(PyObject o, PyObject v) {
	PyMultiarray a = ascontiguous(o);
	PyMultiarray values = ascontiguous(v);
	if (a.dimensions.length != 1 || values.dimensions.length > 1)
	    throw Py.ValueError("searchSorted only works on 1D arrays");
	if (a._typecode == 'F' || a._typecode == 'D')
	    throw Py.ValueError("cannot search complex arrays");
	boolean singleValue = (values.dimensions.length==0);
	if (singleValue) 
	    values = stretchAxes(values);
	PyMultiarray result = zeros(new int [] {values.dimensions[0]}, 'i');
	for (int i = 0; i < values.dimensions[0]; i++) {
	    int start = 0, stop = a.dimensions[0]-1;
	    int j = (start+stop)/2;
	    if (a._typecode != 'O' && values._typecode != 'O') {
		double value = ((Number)Array.get(values.data, values.start+i)).doubleValue();
		while (start != stop) {
		    double val = ((Number)Array.get(a.data, a.start+j)).doubleValue();
		    if ((val == value) || (stop == j)) 
			break;
		    else if (val < value ) 
			start = j;
		    else 
			stop = j;
		    j = (start+stop+1)/2;
		}
	    } else {
		PyObject value = values.get(i);
		while (start != stop) {
		    PyObject val = a.get(j);
		    if (val._eq(value).__nonzero__() || (stop == j)) 
			break;
		    else if (val._le(value).__nonzero__()) 
			start = j;
		    else 
			stop = j;
		    j = (start+stop+1)/2;
		}
	    }
	    Array.setInt(result.data, i, j);
	}
	if (singleValue)
	    return result.get(0);
	return result;
    }

    // XXX I want to rewrite this to remove the reference to umath
    // XXX perhaps move to JNumeric?
    public static PyMultiarray convolve(PyObject oA0, PyObject oB0, int mode) {
	PyMultiarray a0 = asarray(oA0), b0 = asarray(oB0);
	// Check arguments (If anyone cares, the 1d requirement could probably be relaxed.)
	if (a0.dimensions.length != 1 || b0.dimensions.length !=1)
	    throw Py.ValueError("convolve only works on 1D arrays");
	// Make the arrays contiguous and then make a copy of the nondata parts.
	a0 = ascontiguous(a0);
	b0 = ascontiguous(b0);
	PyMultiarray a = new PyMultiarray(a0.data, a0._typecode, a0.start, 
					  (int [])a0.dimensions.clone(), (int [])a0.strides.clone());
	PyMultiarray b = new PyMultiarray(b0.data, b0._typecode, b0.start, 
					  (int [])b0.dimensions.clone(), (int [])b0.strides.clone());
	// Create the result array. 		
	char type = commonType(a._typecode, b._typecode);
	int padl = 0;
	int length = Math.max(a.dimensions[0], b.dimensions[0]);
	int n = Math.min(a.dimensions[0], b.dimensions[0]);
	switch (mode) {
	case 0: 
	    length = length - n + 1;
	    break;
	case 1:
	    padl = n/2;
	    break;
	case 2:
	    length = length + n - 1;
	    padl = n - 1;
	    break;
	default:
	    Py.ValueError("mode must be 0,1, or 2");
	}
	// Create the result.
	PyMultiarray result = zeros(new int [] {length}, type);
	int aSize = typeToNElements(a._typecode);
	int bSize = typeToNElements(b._typecode);
	a.strides[0] = -1;
	a.isContiguous = false;
	for (int i = (n-padl-1); i < length+(n-padl-1); i++) {
	    int j0 = Math.max(0, i-(a0.dimensions[0]-1));
	    int j1 = Math.min(i+1, b0.dimensions[0]);
	    a.start = a0.start + aSize*(i-j0);
	    b.start = b0.start + bSize*j0;
	    a.dimensions[0] = b.dimensions[0] = j1 - j0;	
	    result.set(i-(n-padl-1), Umath.add.reduce(a.__mul__(b)));
	}
	return result;
    }

    //
    // Accessory functions (may be useful for other members of JNumeric package).
    //

    /** Return the real part of this multiarray.*/
    final PyMultiarray getReal() {
	if (_typecode == 'F' || _typecode == 'D') {
	    int [] dims = (int [])dimensions.clone();
	    int [] strs = (int [])strides.clone();
	    char type = (_typecode == 'F') ? 'f' : 'd';
	    return new PyMultiarray(data, type, start, dims, strs);
	}
	return this;
    }

    /** Return the imaginary part of this multiarray if its complex otherwise return null.*/
    final PyMultiarray getImag() {
	if (_typecode == 'F' || _typecode == 'D') {
	    int [] dims = (int [])dimensions.clone();
	    int [] strs = (int [])strides.clone();
	    char type = (_typecode == 'F') ? 'f' : 'd';
	    return new PyMultiarray(data, type, start+1, dims, strs);
	}
	return null;
    }

    /** Return the shape of a Python sequence.*/
    public final static int [] shapeOf(PyObject seq) {
	if (seq instanceof PyMultiarray)
	return (int []) ((PyMultiarray)seq).dimensions.clone();
	return _shapeOf(seq, 0);		
    }

    private final static int [] _shapeOf(PyObject seq, int depth) {
	int items;
	// Take care of special cases (strings, nonsequence, and empty sequence).
	if (seq instanceof PyString) return new int[depth]; 
	try {items = seq.__len__();} 
	catch (Throwable e) {return new int[depth];}
	if (items == 0) return new int[depth+1];
	// Loop over sequence elements and determine shape.
	int [] shape = _shapeOf(seq.__getitem__(0), depth+1);
	shape[depth] = items;
	for (int i = 1; i < items; i++) {
	    int [] shape2 = _shapeOf(seq.__getitem__(i), depth+1);
	    if (shape.length != shape2.length) 
		throw Py.ValueError("malformed array");
	    for (int j = depth+1; j < shape.length; j++)
		if (shape[j] != shape2[j])
		    throw Py.ValueError("malformed array");
	}
	return shape;
    }

    /** Return a multiarray with the axes rotated by n (axis 0->n, 1->n+1, etc.)*/
    static PyMultiarray rotateAxes(PyMultiarray a, int n) {
	PyMultiarray result = new PyMultiarray(a.data, a._typecode, a.start, 
					       (int [])a.dimensions.clone(), (int [])a.strides.clone());
	while (n < 0) n += a.dimensions.length;
	for (int i = 0; i < a.dimensions.length; i++) {
	    result.dimensions[(i+n)%a.dimensions.length] = a.dimensions[i];
	    result.strides[(i+n)%a.dimensions.length] = a.strides[i];
	}
	result.setIsContiguous();
	return result;
    }

    /** Return a multiarray with the axess n0 and n1 swapped.*/
    static PyMultiarray swapAxes(PyMultiarray a, int n0, int n1) {
	PyMultiarray result = new PyMultiarray(a.data, a._typecode, a.start, 
					       (int [])a.dimensions.clone(), (int [])a.strides.clone());
	if (n0 < 0) n0 += a.dimensions.length;
	if (n1 < 0) n1 += a.dimensions.length;
	if (n0 < 0 || n0 >= a.dimensions.length || n1 < 0 || n1 >= a.dimensions.length)
	    throw Py.ValueError("illegal axis");
	result.dimensions[n0] = a.dimensions[n1];
	result.strides[n0] = a.strides[n1];
	result.dimensions[n1] = a.dimensions[n0];
	result.strides[n1] = a.strides[n0];
	result.setIsContiguous();
	return result;
    }

    /** Return the number of elements for this typecode (2 if complex, otherwise 1).*/
    final static int typeToNElements(char typecode) {
	switch (typecode) {
	case 'F': 
	case 'D': return 2;
	default: return 1;
	}
    }

    /** Return an common typecode given two typecodes <code>a</code> and <code>b</code>.*/
    final static char commonType(char a, char b) {
	if (a == b) return a;
	short atype = typeToKind(a), btype = typeToKind(b);
	short newtype = (atype > btype) ? atype : btype;
	if (newtype == PYOBJECT) return 'O';
	short asize = typeToNBytes(a), bsize = typeToNBytes(b);
	short newsize = (asize > bsize) ? asize : bsize;
	return kindAndNBytesToType(newtype, newsize);
    }

    /** Convert a sequence to an array of ints. */
    static int [] objectToInts(Object jo, boolean forgiving) {
	try {return (int [])jo;} // Is there a better way of doing this?
	catch (Throwable t) {}
	if (!(jo instanceof PyObject))
	throw Py.ValueError("cannot convert argument to array of ints");
	PyObject o = (PyObject)jo;
	if (shapeOf(o).length == 0)
	if (forgiving)
	//return new int [] {asarray(o).__getitem__(Py.Ellipsis).__int__().getValue()};  //TCS
	return new int [] {((PyInteger)(asarray(o).__getitem__(Py.Ellipsis).__int__())).getValue()};
	else
	throw Py.ValueError("cannot convert argument to array of ints");
	int length = o.__len__();
	int [] intArray = new int[length];
	for (int i = 0; i < intArray.length; i++) {
	    PyObject item = o.__getitem__(i);
	    if (!forgiving && !(item instanceof PyInteger))
		throw Py.ValueError("cannot convert argument to array of ints");
	    //intArray[i] = o.__getitem__(i).__int__().getValue();  //TCS
	    intArray[i] = ((PyInteger)(o.__getitem__(i).__int__())).getValue();
	}
	return intArray;
    }

    //
    // Private methods (I can't see any other classes needing these.)
    //

    /** Return an array of PyObjects that is a flattened version of sequence.
	<code>size</code> is the size of the resulting array (determined beforehand using shapeToNItems).*/
    private final static PyObject [] seqToObjects(PyObject seq, int size) {
	PyObject [] flat = new PyObject[size];
	_seqToObjects(seq, flat, 0);
	return flat;
    }

    private final static int _seqToObjects(PyObject seq, PyObject [] flat, int offset) {
	int items;
	if (seq instanceof PyString) {
	    flat[offset] = seq;
	    return offset+1;
	}
	try {items = seq.__len__();} 
	catch (Throwable t) {
	    flat[offset] = seq;
	    return offset+1;
	}
	for (int i = 0; i < items; i++)
	    offset = _seqToObjects(seq.__getitem__(i), flat, offset);
	return offset;
    }

    /** Check shape for errors and replace the rubber index with any with an appropriate size. */
    private static int [] fixedShape(int [] shape, int totalSize) {
	shape = (int [])shape.clone();
	int size = 1;
	int rubberAxis = -1;
	for (int i = 0; i < shape.length; i++) {
	    if (shape[i] == -1) {
		if (rubberAxis != -1) throw Py.ValueError("illegal shape");
		rubberAxis = i;
	    }
	    else size *= shape[i];
	}
	if (rubberAxis != -1) {
	    shape[rubberAxis] = totalSize / size;
	    size *= shape[rubberAxis];
	}
	if (totalSize != size) throw Py.ValueError("total size of new array must be unchanged");
	return shape;
    }

    /** Convert a PyObject into a native Java type based on <code>typecode</code>.
	If returnReal is false, the complex part of the given object is returned,
	this defaults to zero if the PyObject is not an instance of PyComplex.*/
    private final static Object objectToJava(PyObject o, char typecode, boolean returnReal) {
	if (typecode == 'O') 
	    return o;
	if (o instanceof PyComplex)
	    o = Py.newFloat(returnReal ? ((PyComplex)o).real : ((PyComplex)o).imag);
	else if (!returnReal)
	    o = Py.Zero;
	Object number = o.__tojava__(typeToClass(typecode));
	if (number == Py.NoConversion)
	    throw Py.ValueError("coercion error");
	return (Number)number;
    }

    /** Return the appropriate typecode for a PyObject.*/ 
    private final static char objectToType(PyObject o) {
	if (o instanceof PyInteger) {return 'i';}
	else if (o instanceof PyFloat) {return 'd';}
	else if (o instanceof PyComplex) {return 'D';}
	else {return'O';}
    }

    /** Find an appropriate common type for an array of PyObjects.*/
    private final static char objectsToType(PyObject [] objects) {
	if (objects.length == 0) return 'i';
	short new_no, no = -1;
	short new_sz, sz = -1;
	for(int i = 0; i < objects.length; i++) {
	    PyObject o = objects[i];
	    char _typecode = objectToType(o);
	    new_no = typeToKind(_typecode);
	    new_sz = typeToNBytes(_typecode);
	    no = (no > new_no) ? no : new_no;
	    sz = (sz > new_sz) ? sz : new_sz;
	}
	return kindAndNBytesToType(no, sz);
    }

    /** Return a Java Class that matches typecode.*/
    private final static Class<?> typeToClass(char typecode) {
	switch (typecode) {
	case '1': return Byte.TYPE;
	case 's': return Short.TYPE;
	case 'i': return Integer.TYPE;
	case 'l': return Long.TYPE;
	case 'f': 
	case 'F': return Float.TYPE;
	case 'd': 
	case 'D': return Double.TYPE;
	case 'O': return PyObject.class;
	default:
	    throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
    }

    /** Return a typecode that matches the given Java class*/
    private final static char classToType(Class<?> klass) {
	if (klass.equals(Byte.TYPE)) return '1';
	if (klass.equals(Short.TYPE)) return 's';
	if (klass.equals(Integer.TYPE)) return 'i';
	if (klass.equals(Long.TYPE)) return 'l';
	if (klass.equals(Float.TYPE)) return 'f';
	if (klass.equals(Double.TYPE)) return 'd';
	if (klass.equals(PyObject.class)) return 'O';
	throw Py.ValueError("unknown class in classToType");
    }

    /** Return the number of items of a multiarray based on its shape (dimensions).*/
    private final static int shapeToNItems(int [] shape) {
	int size = 1;
	for (int i = 0; i < shape.length; i++) {
	    if (shape[i] < 0) throw Py.ValueError("negative dimensions are not allowed");
	    size *= shape[i];
	}
	return size;
    }

    /** Return the strides for a new multiarray based on its shape and the number of elements per item.*/
    private final static int [] shapeToStrides(int [] shape, int nElements) {
	int [] strides = new int[shape.length];
	int stride = nElements;
	for (int i = shape.length - 1; i >= 0; i--) {
	    strides[i] = stride;
	    stride *= shape[i];
	}
	return strides;
    }

    // These enumerate the kinds of types. Lower numbered types can be cast to higher, but not vice versa.
    private final static short INTEGER = 1;
    private final static short FLOATINGPOINT = 2;
    private final static short COMPLEX = 3;
    private final static short PYOBJECT = 4;

    /** Return the kind of type this is (INTEGER, FLOATINGPOINT, COMPLEX, or PYOBJECT).*/
    private final static short typeToKind(char typecode) {
	switch (typecode) {
	case '1': case 's': case 'i': case 'l': return INTEGER;
	case 'f': case 'd': return FLOATINGPOINT;
	case 'F': case 'D': return COMPLEX;
	case 'O': return PYOBJECT;
	default: throw Py.ValueError("internal error in typeToKind");
	}
    }

    /** Return the number of bytes per element for the given typecode.*/
    private final static short typeToNBytes(char typecode) {
	switch (typecode)	{
	case '1': return 1;
	case 's': return 2;
	case 'i': case 'f': case 'F': return 4;
	case 'l': case 'd': case 'D': return 8;
	case 'O': return 0;
	default: throw Py.ValueError("internal error in typeToNBytes");
	}
    }

    /** Return a typecode that matches a given type kind and size (in bytes).*/
    private final static char kindAndNBytesToType(short kind, short nbytes) {
	switch (kind) {
	case INTEGER:
	    switch (nbytes) {
	    case 1: return '1';
	    case 2: return 's';
	    case 4: return 'i';
	    case 8: return 'l';
	    default: break;
	    }
	    break;
	case FLOATINGPOINT:
	    switch (nbytes) {
	    case 4: return 'f';
	    case 8: return 'd';
	    default: break;
	    }
	    break;
	case COMPLEX:
	    switch (nbytes) {
	    case 4: return 'F';
	    case 8: return 'D';
	    default: break;
	    }
	    break;
	case PYOBJECT:
	    return 'O';
	}
	throw Py.ValueError("internal error in kindAndNBytesToType");
    }

    /** Set isContiguous to true if the strides indicate that this array is contiguous.*/
    private final void setIsContiguous() {
	int [] contiguousStrides = shapeToStrides(dimensions, typeToNElements(_typecode));
	for (int i = 0; i < strides.length; i++)
	    if (strides[i] != contiguousStrides[i]) {
		isContiguous = false;
		break;
	    }
    }
	

    /** Return an array derived from <code>a</code> whose axes are suitable for binary operations.*/
    private final static PyMultiarray stretchAxes(PyMultiarray a, PyMultiarray b) {
	if (a.dimensions.length > b.dimensions.length) {
	    int [] dimensions = (int[])a.dimensions.clone();
	    int [] strides = (int[])a.strides.clone();
	    int excess = a.dimensions.length - b.dimensions.length;
	    for (int i = excess; i < dimensions.length; i++)
		if (dimensions[i] != b.dimensions[i-excess]) {
		    if (dimensions[i] == 1) {
			dimensions[i] = b.dimensions[i-excess];
			strides[i] = 0;
		    }
		    else if (b.dimensions[i-excess] != 1)
			throw Py.ValueError("matrices not alligned");
		}
	    return new PyMultiarray(a.data, a._typecode, a.start, dimensions, strides);	
	}
	else {
	    int [] dimensions = (int[])b.dimensions.clone();
	    int [] strides = new int[dimensions.length];
	    int excess = b.dimensions.length - a.dimensions.length;
	    for (int i = excess; i < dimensions.length; i++) {
		if (dimensions[i] != a.dimensions[i-excess]) {
		    if (dimensions[i] == 1)
			dimensions[i] = a.dimensions[i-excess];
		    else if (a.dimensions[i-excess] == 1)
			continue; // Don't set strides.
		    else 
			throw Py.ValueError("matrices not alligned");
		}
		strides[i] = a.strides[i-excess];
	    }
	    return new PyMultiarray(a.data, a._typecode, a.start, dimensions, strides);		
	}
    }

    /** Return an array with shape [1] for use when multiplying shape [] arrays.*/
    private final static PyMultiarray stretchAxes(PyMultiarray a) {
	int [] dimensions = new int [] {1};
	int [] strides = new int[] {1};
	return new PyMultiarray(a.data, a._typecode, a.start, dimensions, strides);
    }


    /** Return an array for storing the result of a binary operation on a and b.*/
    private final static PyMultiarray getResultArray(PyMultiarray a, PyMultiarray b, char type) {
	if (type == '\0') type = a._typecode;
	int [] dimensions = new int[a.dimensions.length];
	for (int i = 0; i < a.dimensions.length; i++)
	    dimensions[i] = Math.max(a.dimensions[i], b.dimensions[i]);
	return zeros(dimensions, type);
    }

    /** Check that <code>result</code> is consistent with <code>a</code> and <code>v</code>.*/
    private final static void checkResultArray(PyMultiarray result, PyMultiarray a, PyMultiarray b) {
	if (result._typecode != a._typecode)
	    throw Py.ValueError("return array has incorrect type.");
	if (result.dimensions.length != a.dimensions.length)
	    throw Py.ValueError("return array has the wrong number of dimensions");
	for (int i = 0; i < result.dimensions.length; i++)
	    if (result.dimensions[i] != Math.max(a.dimensions[i], b.dimensions[i]))
		throw Py.ValueError("return array has incorrect dimensions");
    }

    //
    // Python special methods. 
    //

    /** Convert <code>this</code> to a java object of Class <code>c</code>.*/
    @SuppressWarnings("unchecked")
	public Object __tojava__(Class c) {
    	Class<?> type = typeToClass(_typecode);
	if (dimensions.length == 0 || _typecode == 'F' || _typecode == 'D')
	    return super.__tojava__(c); // Punt!
	if (c == Object.class || (c.isArray() && c.getComponentType().isAssignableFrom(type))) {
	    Object jarray = Array.newInstance(type, dimensions);
	    PyMultiarray contiguous = ascontiguous(this);
	    if (dimensions.length == 1) {
		for (int i = 0; i < contiguous.__len__(); i++) 
		    Array.set(jarray, i, Array.get(contiguous.data, contiguous.start+i));
		return jarray;
	    }
	    else {
		for (int i = 0; i < contiguous.__len__(); i++) {
		    Object subData = get(i).__tojava__(c);
		    Array.set(jarray, i, subData);
		}
		return jarray;	
	    }

	}
	return super.__tojava__(c);
    }

    /** Return the length of the array (along the first axis).*/
    public int __len__() {
	if (dimensions.length == 0)
	    throw Py.ValueError("__len__ of zero dimensional array");
	return dimensions[0];
    }

    /** Overide PyObject method so that get is invoked instead of __finditem__.*/
    public PyObject __getitem__(int index) {
	return get(index);
    }

    /** Disable comparison of Multiarrays/ */ 
    public int __cmp__(PyObject other) {
	throw Py.TypeError("Comparison of multiarray objects is not implemented.");
    }

    /** Return the subarray or item indicated by indices.*/
    public PyObject __getitem__(PyObject indices) {
	return returnValue(indicesToStructure(indices));
    }

    /** Set the subarray based on indices to PyValue.*/
    public void __setitem__(PyObject indices, PyObject pyValue) {
	// Get the shape of the subarray to set.
	PyMultiarray toStructure = indicesToStructure(indices);
	// Convert value to array.
	PyMultiarray value = array(pyValue, _typecode);
	// Check that array shapes are consistent and get new shape for source array.
	int [] shape = (int [])toStructure.dimensions.clone();
	int [] strides = new int [toStructure.dimensions.length];
	int excess = toStructure.dimensions.length-value.dimensions.length;
	if (toStructure.dimensions.length < value.dimensions.length)
	    throw Py.ValueError("object too deep for desired array");
	for (int i = 0; i < excess; i++)
	    shape[i] = toStructure.dimensions[i];
	for (int i = excess; i < toStructure.dimensions.length; i++) {
	    shape[i] = value.dimensions[i-excess];
	    strides[i] = value.strides[i-excess];
	}
	value.dimensions = shape;
	value.strides = strides;
	value.setIsContiguous();
	copyAToB(value, toStructure);
    } 

    // XXX Optimize!!
    protected void setslice(int start, int stop, int step, PyObject value) {
	PyObject startObject = (start >= 0) ? Py.newInteger(start) : Py.None;
	PyObject stopObject = (stop >= 0) ? Py.newInteger(stop) : Py.None;
	__setitem__(new PySlice(startObject, stopObject, Py.newInteger(step)), value);
    }

    // XXX Optimize!!
    protected PyObject getslice(int start, int stop, int step) {
	PyObject startObject = (start >= 0) ? Py.newInteger(start) : Py.None;
	PyObject stopObject = (stop >= 0) ? Py.newInteger(stop) : Py.None;
	return __getitem__(new PySlice(startObject, stopObject, Py.newInteger(step)));
    }

    /** Return the repr for this Multiarray.*/
    public PyString __repr__() {
	return Py.newString(PyMultiarrayPrinter.array2string(this, maxLineWidth, precision, suppressSmall, ", ", true));
    }

    /** Return the str for this Multiarray.*/
    public PyString __str__() {
	return Py.newString(PyMultiarrayPrinter.array2string(this, maxLineWidth, precision, suppressSmall, " ", false));
    }

    /** Multiarray attributes are found using <code>__findattr__</code>.*/
    public PyObject __findattr__(String name) {
	if (name == "__class__") return __class__;
	if (name == "__doc__") return Py.newString(docString);
	if (name == "shape") return __builtin__.tuple(new PyArray(int.class, dimensions)); 
	if (name == "real") return getReal();
	if (name == "imag" || name == "imaginary") return getImag();
	if (name == "flat" && isContiguous) return reshape(this, new int [] {-1});
	
	if (name == "T" && dimensions.length==2) return transpose(this,new int []{1,0});
	return super.__findattr__(name);
    }

    /** Multiarray attributes are set using <code>__setattr__</code>.*/
    public void __setattr__(String name, PyObject value) throws PyException {
	if (name == "shape") {
	    if (!isContiguous)
		throw Py.ValueError("reshape only works on contiguous matrices");
	    int [] shape = (int [])value.__tojava__(dimensions.getClass());
	    dimensions = fixedShape(shape, shapeToNItems(dimensions));
	    strides = shapeToStrides(shape, typeToNElements(_typecode));	
	    return;
	}
	if (name == "imag" || name == "imaginary") {
	    PyMultiarray imag = getImag();
	    if (imag !=  null) {
		imag.__setitem__(new PySlice(null,null,null), value);
		return;
	    }
	}
	if (name == "real") {
	    getReal().__setitem__(new PySlice(null,null,null), value);
	    return;
	}
	super.__setattr__(name, value);
    }

    // The numeric special methods are defined later because they tend to be obnoxiously long...

    //
    // Sequence special methods.
    //

    protected PyObject get(int i) {
	if (dimensions.length < 1)
	    throw Py.IndexError("too many dimensions");
	int newStart = start + fixIndex(i, 0) * strides[0];
	int [] newDimensions = new int[dimensions.length-1];
	int [] newStrides = new int[dimensions.length-1];
	for (int j = 0; j < dimensions.length - 1; j++) { 
	    newDimensions[j] = dimensions[j+1];
	    newStrides[j] = strides[j+1];
	}
	return returnValue(new PyMultiarray(data, _typecode, newStart, newDimensions, newStrides));
    }

    protected PyObject repeat(int count) {
	throw Py.TypeError("can't apply '*' to arrays");
    }

    protected void del(int i) {
	throw Py.TypeError("can't remove from array");
    }

    protected void delRange(int start, int stop, int step) {
	throw Py.TypeError("can't remove from array");
    }

    protected void set(int i, PyObject pyValue) {
	if (dimensions.length < 1)
	    throw Py.IndexError("too many dimensions");
	int newStart = start + fixIndex(i, 0) * strides[0];
	int [] newDimensions = new int[dimensions.length-1];
	int [] newStrides = new int[dimensions.length-1];
	for (int j = 0; j < dimensions.length - 1; j++) { 
	    newDimensions[j] = dimensions[j+1];
	    newStrides[j] = strides[j+1];
	}
	copyAToB(asarray(pyValue, _typecode), new PyMultiarray(data, _typecode, newStart, newDimensions, newStrides));
    }

    /** Convert negative indices to positive and throw exception if index out of range.*/
    protected int fixIndex(int index, int axis) {
	if (index < 0) index += dimensions[axis];
	if (index < 0 || index >= dimensions[axis])
	    throw Py.IndexError("index out of range");
	else return index;
    }


    /** pulled out of PySequence */
    private static final int getIndex(PyObject index, int defaultValue) {
        if (index == Py.None || index == null)
            return defaultValue;
        if (!(index instanceof PyInteger))
            throw Py.TypeError("slice index must be int");
        return ((PyInteger)index).getValue();
    }
	
    /* Should go in PySequence */
    protected static final int getStart1(PyObject s_start, int step, int length)
    {
        int start;
        if (step < 0) {
            start = getIndex(s_start, length-1);
            if (start < -1) start = length+start;
            if (start < -1) start = -1;
	    if (start > length-1) start = length-1;
        } else {
            start = getIndex(s_start, 0);
            if (start < 0) start = length+start;
            if (start < 0) start = 0;
	    if (start > length) start = length;
        }
        return start;
    }

    protected static final int getStop1(PyObject s_stop, int start, int step, int length)
    {
        int stop;
        if (step < 0) {
            stop = getIndex(s_stop, -1);
            if (stop < -1) stop = length+stop;
            if (stop < -1) stop = -1;
	    if (stop > length-1) stop = length-1;
        } else {
            stop = getIndex(s_stop, length);
            if (stop < 0) stop = length+stop;
            if (stop < 0) stop = 0;
	    if (stop > length) stop = length;
        }
	if ((stop-start)*step < 0) stop = start;
        return stop;
    }


    /** Convert a set of indices into a Multiarray object, but do not fill in the data.*/
    private final PyMultiarray indicesToStructure(PyObject pyIndices) {
	// Convert the pyIndices into an array of PyObjects.
	//PyObject indices[] = (pyIndices instanceof PyTuple) ? ((PyTuple)pyIndices).list : new PyObject[] {pyIndices};   //TCS
	PyObject indices[] = (pyIndices instanceof PyTuple) ? ((PyTuple)pyIndices).getArray() : new PyObject[] {pyIndices};
	// First pass: determine the size of the new dimensions.
	int nDimensions = dimensions.length, ellipsisLength = 0, axis = 0;
	for (int i = 0; i < indices.length; i++) {
	    PyObject index = indices[i];
	    if (index instanceof PyEllipsis) {
		if (ellipsisLength > 0) continue;
		ellipsisLength = dimensions.length - (indices.length - i - 1 + axis);
		for (int j = i+1; j < indices.length; j++)
		    if (indices[j] instanceof PyNone) ellipsisLength++;			
		if (ellipsisLength < 0) throw Py.IndexError("too many indices");
		axis += ellipsisLength;
	    }
	    else if (index instanceof PyNone) nDimensions++;
	    else if (index instanceof PyInteger) {nDimensions--; axis++;}
	    else if (index instanceof PySlice) axis++;
	    else throw Py.ValueError("invalid index");
	}
	if (axis > dimensions.length) throw Py.ValueError("invalid index");
	// Second pass: now generate the dimensions.
	int newStart = start, newAxis = 0, oldAxis = 0;
	int [] newDimensions = new int[nDimensions], newStrides = new int[nDimensions];
	for (int i = 0; i < indices.length; i++) {
	    PyObject index = indices[i];
	    if (index instanceof PyEllipsis) {
		if (ellipsisLength > 0) {
		    for (int j = 0; j < ellipsisLength; j++) {
			newDimensions[newAxis+j] = dimensions[oldAxis+j];
			newStrides[newAxis+j] = strides[oldAxis+j];
		    }
		    oldAxis += ellipsisLength;
		    newAxis += ellipsisLength;
		    ellipsisLength = 0;
		}
	    }
	    else if (index instanceof PyNone) {
		newDimensions[newAxis] = 1;
		newStrides[newAxis] = 0;
		newAxis++;
	    }
	    else if (oldAxis >= dimensions.length)
		throw Py.IndexError("too many dimensions");
	    else if (index instanceof PyInteger) {
		PyInteger integer = (PyInteger) index;
		newStart += fixIndex(integer.getValue(), oldAxis) * strides[oldAxis];
		oldAxis++;
	    }
	    else if (index instanceof PySlice) {
		PySlice slice = (PySlice)index;
		int sliceStep = getStep(slice.step);
		int sliceStart = getStart1(slice.start, sliceStep, dimensions[oldAxis]);
		int sliceStop = getStop1(slice.stop, sliceStart, sliceStep, dimensions[oldAxis]);
		if (sliceStep > 0)
		    newDimensions[newAxis] = 1 + (sliceStop - sliceStart - 1) / sliceStep;
		else
		    newDimensions[newAxis] = 1 - (sliceStart - sliceStop - 1) / sliceStep;
		newStart += sliceStart * strides[oldAxis];
		newStrides[newAxis] = sliceStep * strides[oldAxis];
		oldAxis++;
		newAxis++;
	    }
	    else 
		throw Py.ValueError("illegal index");
	}
	// Tack any extra indices onto the end.
	for (int i = 0; i < nDimensions - newAxis; i++) { 
	    newDimensions[newAxis+i] = dimensions[oldAxis+i];
	    newStrides[newAxis+i] = strides[oldAxis+i];
	}
	return new PyMultiarray(data, _typecode, newStart, newDimensions, newStrides);
    }

    /** Return <code>a</code> unless it is zero dimensional, otherwise convert to a PyObject and return.*/ 
    static final PyObject returnValue(PyMultiarray a) {
	if (a.dimensions.length == 0) {
	    if (typeToNElements(a._typecode) == 1)
		return Py.java2py(Array.get(a.data, a.start));
	    else
		return new PyComplex(((Number)Array.get(a.data, a.start)).doubleValue(), 
				     ((Number)Array.get(a.data, a.start+1)).doubleValue());
	}
	return a;
    }

    /** Copy the array A to the array B. The array's must be the same shape.
	(Length 1 axes can be converted to length N axes if the appropriate stride is set to zero.)*/
    static void copyAToB(PyMultiarray a, PyMultiarray b) {
	if (a.dimensions.length != b.dimensions.length)
	    throw Py.ValueError("copied matrices must have the same number of dimensions");
	for (int i = 0; i < a.dimensions.length; i++)
	    if (a.dimensions[i] != b.dimensions[i])
		throw Py.ValueError("matrices not alligned for copy");
	if (a._typecode == b._typecode && a.isContiguous && b.isContiguous)
	    System.arraycopy(a.data, a.start, b.data, b.start, typeToNElements(a._typecode)*shapeToNItems(a.dimensions));
	else if (a.dimensions.length == 0) 
	    b.__setitem__(Py.Ellipsis, a.__getitem__(Py.Ellipsis));
	else if (a._typecode == b._typecode && a._typecode != 'F' && a._typecode != 'D')
	    copyAxToBx(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0);
	else if (a._typecode == 'O')  
	    copyAOToB(a.data, a.start, a.strides, b.data, b.start, b.strides, b._typecode, b.dimensions, 0);
	else 
	    switch (b._typecode) {
	    case '1': 
		copyAToBb(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 's': 
		copyAToBs(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 'i': 
		copyAToBi(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 'l':
		copyAToBl(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 'f':
		copyAToBf(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 'd':
		copyAToBd(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    case 'F':
		if (a._typecode == 'F' || a._typecode == 'D')
		    copyAToBF(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0);
		else
		    copyAToBf(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0);
		break;
	    case 'D':
		if (a._typecode == 'F' || a._typecode == 'D')
		    copyAToBD(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0);
		else
		    copyAToBd(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0);
		break;
	    case 'O':	
		copyAToBO(a.data, a.start, a.strides, b.data, b.start, b.strides, b.dimensions, 0); 
		break;
	    default:
		throw Py.ValueError("typecode must be in [1silfFdDO]");
	    }
    }

    static void copyAxToBx(Object aData, int aStart, int [] aStrides,
		           Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth]; 
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAxToBx(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.set(bData, j, Array.get(aData, i));
    }

    static void copyAOToB(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, char bType, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth]; 
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAOToB(aData, i, aStrides, bData, j, bStrides, bType, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.set(bData, j, ((PyObject)Array.get(aData, i)).__tojava__(typeToClass(bType)));
    }

    static void copyAToBb(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth]; 
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBb(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setByte(bData, j, ((Number)Array.get(aData, i)).byteValue());
    }

    static void copyAToBs(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBs(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setShort(bData, j, ((Number)Array.get(aData, i)).shortValue());
    }

    static void copyAToBi(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBi(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setInt(bData, j, ((Number)Array.get(aData, i)).intValue());
    }
	
    static void copyAToBl(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBl(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setLong(bData, j, ((Number)Array.get(aData, i)).longValue());
    }

    static void copyAToBf(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBf(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setFloat(bData, j, ((Number)Array.get(aData, i)).floatValue());
    }
	
    static void copyAToBd(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBd(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		Array.setDouble(bData, j, ((Number)Array.get(aData, i)).doubleValue());
    }

    static void copyAToBF(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBF(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth]) {
		Array.setFloat(bData, j, ((Number)Array.get(aData, i)).floatValue());
		Array.setFloat(bData, j+1, ((Number)Array.get(aData, i+1)).floatValue());
	    }
    }

    static void copyAToBD(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBD(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth]) {
		Array.setDouble(bData, j, ((Number)Array.get(aData, i)).doubleValue());
		Array.setDouble(bData, j+1, ((Number)Array.get(aData, i+1)).doubleValue());
	    }
    }

    static void copyAToBO(Object aData, int aStart, int [] aStrides,
			  Object bData, int bStart, int [] bStrides, int [] dimensions, int depth) {
	int jMax = bStart + dimensions[depth]*bStrides[depth];
	if (depth < dimensions.length - 1) 
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth])
		copyAToBO(aData, i, aStrides, bData, j, bStrides, dimensions, depth+1);
	else
	    for (int i = aStart, j = bStart; j != jMax; i += aStrides[depth], j+= bStrides[depth]) 
		Array.set(bData, j, Py.java2py(Array.get(aData, i)));
    }

    /** This converts data (which must be an array) to an array of bytes. */
    static byte [] toByteArray(Object data, char type) {
	try {
	    java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
	    java.io.DataOutputStream dataStream = new java.io.DataOutputStream(byteStream);
	    switch (type) {
	    case '1':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeByte(Array.getByte(data, i));
		break;
	    case 's':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeShort(Array.getShort(data, i));
		break;
	    case 'i':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeInt(Array.getInt(data, i));
		break;
	    case 'l':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeLong(Array.getLong(data, i));
		break;
	    case 'f': case 'F':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeFloat(Array.getFloat(data, i));
		break;
	    case 'd': case 'D':
		for (int i = 0; i < Array.getLength(data); i++)
		    dataStream.writeDouble(Array.getDouble(data, i));
		break;
	    default:
		throw Py.ValueError("typecode must be in [1silfFdDO]");
	    }
	    return byteStream.toByteArray();
	} catch (java.io.IOException e) {
	    throw Py.RuntimeError("ioexception:" + e.getMessage());
	}
    }

    static Object fromByteArray(byte [] bytes, char type) {
	if (bytes.length % typeToNBytes(type) != 0)
	    throw Py.ValueError("array size must be a multiple of the type size");
	try {
	    java.io.ByteArrayInputStream byteStream = new java.io.ByteArrayInputStream(bytes);
	    java.io.DataInputStream dataStream = new java.io.DataInputStream(byteStream);
	    Object data = Array.newInstance(typeToClass(type), bytes.length/typeToNBytes(type));
	    switch (type) {
	    case '1':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setByte(data, i, dataStream.readByte());
		break;
	    case 's':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setShort(data, i, dataStream.readShort());
		break;
	    case 'i':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setInt(data, i, dataStream.readInt());
		break;
	    case 'l':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setLong(data, i, dataStream.readLong());
		break;
	    case 'f': case 'F':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setFloat(data, i, dataStream.readFloat());
		break;
	    case 'd': case 'D':
		for (int i = 0; i < Array.getLength(data); i++)
		    Array.setDouble(data, i, dataStream.readDouble());
		break;
	    default:
		throw Py.ValueError("typecode must be in [1silfFdDO]");
	    }
	    return data;
	} catch (java.io.IOException e) {
	    throw Py.RuntimeError("ioexception:" + e.getMessage());
	}
    }

    /** Return a string representation of the byte array corresponding to this array*/
    public String toString() { 
	StringBuffer buf = new StringBuffer("arrayObject(data=[");
	int dataLength = Array.getLength(data);
	for (int i = 0; i < dataLength - 1; i++) {
	    buf.append((Array.get(data, i)).toString());
	    buf.append(", ");
	}
	if (dataLength > 0) buf.append((Array.get(data, dataLength-1)).toString());
	buf.append("], typecode=");
	buf.append(_typecode);
	buf.append(", start=");
	buf.append((new Integer(start)).toString());
	buf.append(", dimensions=[");
	for (int i = 0; i < dimensions.length-1; i++) {
	    buf.append((new Integer(dimensions[i])).toString());
	    buf.append(",");
	}
	if (dimensions.length > 0) 
	    buf.append((new Integer(dimensions[dimensions.length-1])).toString());
	buf.append("], strides=[");
	for (int i = 0; i < strides.length-1; i++) {
	    buf.append((new Integer(strides[i])).toString());
	    buf.append(",");
	}
	if (strides.length > 0) 
	    buf.append((new Integer(strides[strides.length-1])).toString());
	buf.append("])");
	return buf.toString();
    }

    //
    // Functions used by __add__, etc that are NOT generated.
    //

    public PyObject __radd__(PyObject po) {
	return __add__(po);
    }

    public PyObject __rsub__(PyObject po) {
	return array(po).__sub__(this);
    }

    public PyObject __rmul__(PyObject po) {
	return __mul__(po);
    }

    public PyObject __rdiv__(PyObject po) {
	return array(po).__div__(this);
    }

    public PyObject __rmod__(PyObject po) {
	return array(po).__mod__(this);
    }

    public PyObject __divmod__(PyObject po) {
	PyObject mod = __mod__(po);
	PyObject div = __sub__(mod).__div__(po);
	return new PyTuple(new PyObject [] {div, mod});
    }

    public PyObject __rdivmod__(PyObject po) {
	return array(po).__divmod__(this);
    }

    final static void mulComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		float re = aData[sa]*bData[sb] - aData[sa+1]*bData[sb+1];
		rData[sr+1] = aData[sa]*bData[sb+1] + aData[sa+1]*bData[sb];
		rData[sr] = re;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    mulComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void mulComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		double re = aData[sa]*bData[sb] - aData[sa+1]*bData[sb+1];
		rData[sr+1] = aData[sa]*bData[sb+1] + aData[sa+1]*bData[sb];
		rData[sr] = re;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    mulComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void divComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		float den = bData[sb]*bData[sb] + bData[sb+1]*bData[sb+1];
		float re = (aData[sa]*bData[sb] + aData[sa+1]*bData[sb+1]) / den;
		rData[sr+1] = (-aData[sa]*bData[sb+1] + aData[sa+1]*bData[sb]) / den;
		rData[sr] = re;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    divComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void divComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		double den = bData[sb]*bData[sb] + bData[sb+1]*bData[sb+1];
		double re = (aData[sa]*bData[sb] + aData[sa+1]*bData[sb+1]) / den;
		rData[sr+1] = (-aData[sa]*bData[sb+1] + aData[sa+1]*bData[sb]) / den;
		rData[sr] = re;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    divComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void addComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sr] = aData[sa] + bData[sb];
		rData[sr+1] = aData[sa+1] + bData[sb+1];
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    addComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void addComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sr] = aData[sa] + bData[sb];
		rData[sr+1] = aData[sa+1] + bData[sb+1];
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    addComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void subComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sr] = aData[sa] - bData[sb];
		rData[sr+1] = aData[sa+1] - bData[sb+1];
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    subComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void subComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sr] = aData[sa] - bData[sb];
		rData[sr+1] = aData[sa+1] - bData[sb+1];
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    subComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void modComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		float reA = aData[sa], imA = aData[sa+1], reB = bData[sb], imB = bData[sb+1];
		float den = reB*reB + imB*imB;
		float n = (float)Math.floor((reA*reB+imA*imB) / den);
		rData[sr] = reA - n*reB; 
		rData[sr+1] = imA - n*imB;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    modComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void modComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		double reA = aData[sa], imA = aData[sa+1], reB = bData[sb], imB = bData[sb+1];
		double den = reB*reB + imB*imB;
		double n = Math.floor((reA*reB+imA*imB) / den);
		rData[sr] = reA - n*reB; 
		rData[sr+1] = imA - n*imB;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    modComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void powComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		PyComplex Z = (PyComplex)new PyComplex(aData[sa], aData[sa+1]).__pow__(new PyComplex(bData[sb], bData[sb+1]));
		rData[sr] = (float)Z.real; 
		rData[sr+1] = (float)Z.imag;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    powComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void powComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		PyComplex Z = (PyComplex)new PyComplex(aData[sa], aData[sa+1]).__pow__(new PyComplex(bData[sb], bData[sb+1]));
		rData[sr] = Z.real; 
		rData[sr+1] = Z.imag;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    powComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void maxComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	throw Py.ValueError("cannot perform ordered compare on complex numbers");
    }

    final static void maxComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	throw Py.ValueError("cannot perform ordered compare on complex numbers");
    }

    final static void minComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	throw Py.ValueError("cannot perform ordered compare on complex numbers");
    }

    final static void minComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	throw Py.ValueError("cannot perform ordered compare on complex numbers");
    }
	
    final static void eqComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = (aData[sa] == bData[sb] && aData[sa+1] == bData[sb+1]) ? 1 : 0;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    eqComplexFloat(sa, a, sb, b, sr, r, d+1);
    }
	
    final static void eqComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = (aData[sa] == bData[sb] && aData[sa+1] == bData[sb+1]) ? 1 : 0;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    eqComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void neqComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = (aData[sa] == bData[sb] && aData[sa+1] == bData[sb+1]) ? 0 : 1;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    neqComplexFloat(sa, a, sb, b, sr, r, d+1);
    }
	
    final static void neqComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = (aData[sa] == bData[sb] && aData[sa+1] == bData[sb+1]) ? 0 : 1;
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    neqComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void landComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) && (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0; 
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    landComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void landComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) && (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0; 
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    landComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    final static void lorComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) || (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0; 
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    lorComplexFloat(sa, a, sb, b, sr, r, d+1);
    }

    final static void lorComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], finalSr =  sr+r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length-1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr) {
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) || (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0; 
	    }
	}
	else for (; sr != finalSr; sa+=dsa, sb+=dsb, sr+=dsr)
	    lorComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorComplexFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) ^ (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0;
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorComplexFloat(sa, a, sb, b, sr, r, d+1);
    }
	
    private final static void lxorComplexDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sa] = ((aData[sa] != 0 || aData[sa+1] != 0) ^ (bData[sb] != 0 || bData[sb+1] != 0)) ? 1 : 0;
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorComplexDouble(sa, a, sb, b, sr, r, d+1);
    }

    PyMultiarray absComplexFloat(PyMultiarray a) {
	// Assumes a continuous array.
	int length = shapeToNItems(a.dimensions);
	float [] aData = (float[])a.data;
	float [] result = new float[length]; 
	for (int i = a.start, j = 0; j < length; i += 2, j++) 
	    result[j] = (float)Math.sqrt(aData[i]*aData[i] + aData[i+1]*aData[i+1]);
	return new PyMultiarray(result, 'f', 0, new int [] {length}, new int [] {1});
    }

    PyMultiarray absComplexDouble(PyMultiarray a) {
	// Assumes a continuous array.
	int length = shapeToNItems(a.dimensions);
	double [] aData = (double[])a.data;
	double [] result = new double[length]; 
	for (int i = a.start, j = 0; j < length; i += 2, j++) 
	    result[j] = (double)Math.sqrt(aData[i]*aData[i] + aData[i+1]*aData[i+1]);
	return new PyMultiarray(result, 'd', 0, new int [] {length}, new int [] {1});
    }

    PyMultiarray negComplexFloat(PyMultiarray a) {
	float [] aData = (float [])a.data;
	for (int i = 0; i < aData.length; i++)
	    aData[i] = -aData[i];
	return a;
    }

    PyMultiarray negComplexDouble(PyMultiarray a) {
	double [] aData = (double [])a.data;
	for (int i = 0; i < aData.length; i++)
	    aData[i] = -aData[i];
	return a;
    }


    // These pow routines adopted from CNumeric.

    static final long pow(long x, long n) {
	/* Overflow check: overflow will occur if log2(abs(x)) * n > nbits. */
	int nbits = 63;
	long r = 1;
	long p = x;
	double logtwox;
	long mask = 1;
	if (n < 0) throw Py.ValueError("Integer to a negative power");
	if (x != 0) {
	    logtwox = Math.log (Math.abs( (double) x)) / Math.log( (double) 2.0);
	    if (logtwox * (double) n > (double) nbits)
		throw new PyException(Py.ArithmeticError, "Integer overflow in power.");
	}
	while (mask > 0 && n >= mask) {
	    if ((n & mask) != 0)
		r *= p;
	    mask <<= 1;
	    p *= p;
	}
	return r;
    }
	
    static final int pow(int x, int n) {
	/* Overflow check: overflow will occur if log2(abs(x)) * n > nbits. */
	int nbits = 31;
	long r = 1;
	long p = x;
	double logtwox;
	long mask = 1;
	if (n < 0) throw Py.ValueError("Integer to a negative power");
	if (x != 0) {
	    logtwox = Math.log (Math.abs( (double) x)) / Math.log( (double) 2.0);
	    if (logtwox * (double) n > (double) nbits)
		throw new PyException(Py.ArithmeticError, "Integer overflow in power.");
	}
	while (mask > 0 && n >= mask) {
	    if ((n & mask) != 0)
		r *= p;
	    mask <<= 1;
	    p *= p;
	}
	return (int)r;
    }
	
    static final short pow(short x, short n) {
	/* Overflow check: overflow will occur if log2(abs(x)) * n > nbits. */
	int nbits = 15;
	long r = 1;
	long p = x;
	double logtwox;
	long mask = 1;
	if (n < 0) throw Py.ValueError("Integer to a negative power");
	if (x != 0) {
	    logtwox = Math.log (Math.abs( (double) x)) / Math.log( (double) 2.0);
	    if (logtwox * (double) n > (double) nbits)
		throw new PyException(Py.ArithmeticError, "Integer overflow in power.");
	}
	while (mask > 0 && n >= mask) {
	    if ((n & mask) != 0)
		r *= p;
	    mask <<= 1;
	    p *= p;
	}
	return (short)r;
    }
	
	
    static final byte pow(byte x, byte n) {
	/* Overflow check: overflow will occur if log2(abs(x)) * n > nbits. */
	int nbits = 7;
	long r = 1;
	long p = x;
	double logtwox;
	long mask = 1;
	if (n < 0) throw Py.ValueError("Integer to a negative power");
	if (x != 0) {
	    logtwox = Math.log (Math.abs( (double) x)) / Math.log( (double) 2.0);
	    if (logtwox * (double) n > (double) nbits)
		throw new PyException(Py.ArithmeticError, "Integer overflow in power.");
	}
	while (mask > 0 && n >= mask) {
	    if ((n & mask) != 0)
		r *= p;
	    mask <<= 1;
	    p *= p;
	}
	return (byte)r;
    }
	
    static final double pow(double x, double n) {
	return Math.pow(x,n);
    }
	
    static final float pow(float x, float n) {
	return (float)Math.pow(x,n);
    }

    /*
      Generated code.
    */

    // Begin generated code (genOps).

    private final static void addByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(aData[sa] + bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void addObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._add(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		addObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __add__(PyObject o) {return __add__(o, null);}

    PyObject __add__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__add__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': addByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': addShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': addInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': addLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': addFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': addDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': addObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': addComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': addComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void subByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(aData[sa] - bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void subObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._sub(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		subObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __sub__(PyObject o) {return __sub__(o, null);}

    PyObject __sub__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__sub__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': subByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': subShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': subInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': subLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': subFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': subDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': subObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': subComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': subComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void mulByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(aData[sa] * bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void mulObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._mul(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		mulObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __mul__(PyObject o) {return __mul__(o, null);}

    PyObject __mul__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__mul__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': mulByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': mulShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': mulInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': mulLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': mulFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': mulDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': mulObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': mulComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': mulComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void divByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(aData[sa] / bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void divObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._div(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		divObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __div__(PyObject o) {return __div__(o, null);}

    PyObject __div__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = this.astype(type); b = b.astype(type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	try {
	    if (result.dimensions.length == 0)
		result.__setitem__(Py.Ellipsis, a.__div__(stretchAxes(b)).__getitem__(0));
	    else switch(type) {
	    case '1': divByte(a.start, a, b.start, b, result.start, result, 0); break;
	    case 's': divShort(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'i': divInt(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'l': divLong(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'f': divFloat(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'd': divDouble(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'O': divObject(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'F': divComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	    case 'D': divComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	    default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	    }
	}
	catch (java.lang.ArithmeticException ex) {
	    if (ex.getMessage().equals("/ by zero"))
		throw Py.ZeroDivisionError("divide by zero");
	    throw ex;
	}		
	return returnValue(result);
    }

    private final static void modByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(aData[sa] % bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void modObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._mod(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		modObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __mod__(PyObject o) {return __mod__(o, null);}

    PyObject __mod__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__mod__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': modByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': modShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': modInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': modLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': modFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': modDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': modObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': modComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': modComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void powByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)(pow(aData[sa], bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void powObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa]._pow(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		powObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __pow__(PyObject o) {return __pow__(o, null);}

    PyObject __pow__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__pow__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': powByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': powShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': powInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': powLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': powFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': powDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': powObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': powComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': powComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void maxByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)((aData[sa] > bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void maxObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(__builtin__.max(new PyObject [] {aData[sa], bData[sb]}));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		maxObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __max(PyObject o) {return __max(o, null);}

    PyObject __max(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__max(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': maxByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': maxShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': maxInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': maxLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': maxFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': maxDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': maxObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': maxComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': maxComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void minByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final float[] rData = (float[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (float)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final double[] rData = (double[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (double)((aData[sa] < bData[sb]) ? aData[sa] : bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void minObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(__builtin__.min(new PyObject [] {aData[sa], bData[sb]}));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		minObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __min(PyObject o) {return __min(o, null);}

    PyObject __min(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__min(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': minByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': minShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': minInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': minLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': minFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': minDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': minObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': minComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': minComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void eqByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] == bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void eqObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) == 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		eqObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __eq(PyObject o) {return __eq(o, null);}

    PyObject __eq(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__eq(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': eqByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': eqShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': eqInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': eqLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': eqFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': eqDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': eqObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': eqComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': eqComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void neqByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] != bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void neqObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) != 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		neqObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __neq(PyObject o) {return __neq(o, null);}

    PyObject __neq(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__neq(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': neqByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': neqShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': neqInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': neqLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': neqFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': neqDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': neqObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': neqComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': neqComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void leByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] <= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void leObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) <= 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		leObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __le(PyObject o) {return __le(o, null);}

    PyObject __le(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__le(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': leByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': leShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': leInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': leLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': leFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': leDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': leObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("le not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("le not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void ltByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] < bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void ltObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) < 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		ltObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __lt(PyObject o) {return __lt(o, null);}

    PyObject __lt(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__lt(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': ltByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': ltShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': ltInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': ltLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': ltFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': ltDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': ltObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("lt not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("lt not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void geByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] >= bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void geObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) >= 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		geObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __ge(PyObject o) {return __ge(o, null);}

    PyObject __ge(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__ge(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': geByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': geShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': geInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': geLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': geFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': geDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': geObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("ge not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("ge not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void gtByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa] > bData[sb]) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void gtObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((__builtin__.cmp(aData[sa], bData[sb]) > 0) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		gtObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __gt(PyObject o) {return __gt(o, null);}

    PyObject __gt(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__gt(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': gtByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': gtShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': gtInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': gtLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': gtFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': gtDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': gtObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("gt not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("gt not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void landByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)&(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void landObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa].__nonzero__()&bData[sb].__nonzero__()) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		landObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __land(PyObject o) {return __land(o, null);}

    PyObject __land(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__land(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': landByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': landShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': landInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': landLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': landFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': landDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': landObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': landComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': landComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void lorByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa] != 0)|(bData[sb] != 0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lorObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa].__nonzero__()|bData[sb].__nonzero__()) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lorObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __lor(PyObject o) {return __lor(o, null);}

    PyObject __lor(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__lor(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': lorByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': lorShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': lorInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': lorLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': lorFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': lorDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': lorObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': lorComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': lorComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void lxorByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorFloat(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final float[] aData = (float[])a.data, bData = (float[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorFloat(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorDouble(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final double[] aData = (double[])a.data, bData = (double[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(((aData[sa]!=0)^(bData[sb]!=0)) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorDouble(sa, a, sb, b, sr, r, d+1);
    }

    private final static void lxorObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)((aData[sa].__nonzero__()^bData[sb].__nonzero__()) ? 1 : 0);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		lxorObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __lxor(PyObject o) {return __lxor(o, null);}

    PyObject __lxor(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, 'i');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__lxor(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': lxorByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': lxorShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': lxorInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': lxorLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': lxorFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'd': lxorDouble(a.start, a, b.start, b, result.start, result, 0); break;
	case 'O': lxorObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': lxorComplexFloat(a.start, a, b.start, b, result.start, result, 0); break;
	case 'D': lxorComplexDouble(a.start, a, b.start, b, result.start, result, 0); break;
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void andByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] & bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		andByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void andShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] & bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		andShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void andInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] & bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		andInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void andLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] & bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		andLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void andObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa].__and__(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		andObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __and__(PyObject o) {return __and__(o, null);}

    PyObject __and__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__and__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': andByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': andShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': andInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': andLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': throw Py.ValueError("and not supported for type Float");
	case 'd': throw Py.ValueError("and not supported for type Double");
	case 'O': andObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("and not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("and not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void orByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] | bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		orByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void orShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] | bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		orShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void orInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] | bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		orInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void orLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] | bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		orLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void orObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa].__or__(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		orObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __or__(PyObject o) {return __or__(o, null);}

    PyObject __or__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__or__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': orByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': orShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': orInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': orLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': throw Py.ValueError("or not supported for type Float");
	case 'd': throw Py.ValueError("or not supported for type Double");
	case 'O': orObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("or not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("or not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    private final static void xorByte(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final byte[] aData = (byte[])a.data, bData = (byte[])b.data;
	    final byte[] rData = (byte[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (byte)(aData[sa] ^ bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		xorByte(sa, a, sb, b, sr, r, d+1);
    }

    private final static void xorShort(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final short[] aData = (short[])a.data, bData = (short[])b.data;
	    final short[] rData = (short[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (short)(aData[sa] ^ bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		xorShort(sa, a, sb, b, sr, r, d+1);
    }

    private final static void xorInt(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final int[] aData = (int[])a.data, bData = (int[])b.data;
	    final int[] rData = (int[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (int)(aData[sa] ^ bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		xorInt(sa, a, sb, b, sr, r, d+1);
    }

    private final static void xorLong(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final long[] aData = (long[])a.data, bData = (long[])b.data;
	    final long[] rData = (long[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (long)(aData[sa] ^ bData[sb]);
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		xorLong(sa, a, sb, b, sr, r, d+1);
    }

    private final static void xorObject(int sa, PyMultiarray a, int sb, PyMultiarray b, int sr, PyMultiarray r, int d) {
	final int dsa = a.strides[d], dsb = b.strides[d], dsr = r.strides[d], maxSr =  sr + r.dimensions[d]*r.strides[d];
	if (d == r.dimensions.length - 1) {
	    final PyObject[] aData = (PyObject[])a.data, bData = (PyObject[])b.data;
	    final PyObject[] rData = (PyObject[])r.data;
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		rData[sr] = (PyObject)(aData[sa].__xor__(bData[sb]));
	}
	else	
	    for (; sr != maxSr; sa+=dsa, sb+=dsb, sr+=dsr)
		xorObject(sa, a, sb, b, sr, r, d+1);
    }

    public PyObject __xor__(PyObject o) {return __xor__(o, null);}

    PyObject __xor__(PyObject o, PyMultiarray result) {
	PyMultiarray a, b = asarray(o);
	char type = commonType(this._typecode, b._typecode);
	a = asarray(this, type); b = asarray(b, type);
	a = stretchAxes(a, b); b = stretchAxes(b, this);
	if (result == null) result = getResultArray(a, b, '\0');
	else checkResultArray(result, a, b);
	if (result.dimensions.length == 0)
	    result.__setitem__(Py.Ellipsis, a.__xor__(stretchAxes(b)).__getitem__(0));
	else switch(type) {
	case '1': xorByte(a.start, a, b.start, b, result.start, result, 0); break;
	case 's': xorShort(a.start, a, b.start, b, result.start, result, 0); break;
	case 'i': xorInt(a.start, a, b.start, b, result.start, result, 0); break;
	case 'l': xorLong(a.start, a, b.start, b, result.start, result, 0); break;
	case 'f': throw Py.ValueError("xor not supported for type Float");
	case 'd': throw Py.ValueError("xor not supported for type Double");
	case 'O': xorObject(a.start, a, b.start, b, result.start, result, 0); break;
	case 'F': throw Py.ValueError("xor not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("xor not supported for type ComplexDouble");
	default: throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return returnValue(result);
    }

    public PyObject __abs__() {
	return returnValue(__abs__(array(this)));
    }

    PyMultiarray __abs__(PyMultiarray a) {
	if (!a.isContiguous) 
	    throw Py.ValueError("internal __abs__ requires contiguous matrix as argument");
//	int maxI = a.start + shapeToNItems(a.dimensions);
	switch (a._typecode) {
	case '1':
	    byte aData1[] = (byte []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aData1[i] = (byte)((aData1[i] > 0) ? aData1[i] : -aData1[i]);
	    break;
	case 's':
	    short aDatas[] = (short []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatas[i] = (short)((aDatas[i] > 0) ? aDatas[i] : -aDatas[i]);
	    break;
	case 'i':
	    int aDatai[] = (int []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatai[i] = (int)((aDatai[i] > 0) ? aDatai[i] : -aDatai[i]);
	    break;
	case 'l':
	    long aDatal[] = (long []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatal[i] = (long)((aDatal[i] > 0) ? aDatal[i] : -aDatal[i]);
	    break;
	case 'f':
	    float aDataf[] = (float []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDataf[i] = (float)((aDataf[i] > 0) ? aDataf[i] : -aDataf[i]);
	    break;
	case 'd':
	    double aDatad[] = (double []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatad[i] = (double)((aDatad[i] > 0) ? aDatad[i] : -aDatad[i]);
	    break;
	case 'O':
	    PyObject aDataO[] = (PyObject []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDataO[i] = (PyObject)(aDataO[i].__abs__());
	    break;
	case 'F':
	    a = absComplexFloat(a);
	    break;
	case 'D':
	    a = absComplexDouble(a);
	    break;
	default:
	    throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return a;
    }

    public PyObject __neg__() {
	return returnValue(__neg__(array(this)));
    }

    PyMultiarray __neg__(PyMultiarray a) {
	if (!a.isContiguous) 
	    throw Py.ValueError("internal __neg__ requires contiguous matrix as argument");
//	int maxI = a.start + shapeToNItems(a.dimensions);
	switch (a._typecode) {
	case '1':
	    byte aData1[] = (byte []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aData1[i] = (byte)(-aData1[i]);
	    break;
	case 's':
	    short aDatas[] = (short []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatas[i] = (short)(-aDatas[i]);
	    break;
	case 'i':
	    int aDatai[] = (int []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatai[i] = (int)(-aDatai[i]);
	    break;
	case 'l':
	    long aDatal[] = (long []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatal[i] = (long)(-aDatal[i]);
	    break;
	case 'f':
	    float aDataf[] = (float []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDataf[i] = (float)(-aDataf[i]);
	    break;
	case 'd':
	    double aDatad[] = (double []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatad[i] = (double)(-aDatad[i]);
	    break;
	case 'O':
	    PyObject aDataO[] = (PyObject []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDataO[i] = (PyObject)(aDataO[i].__neg__());
	    break;
	case 'F':
	    a = negComplexFloat(a);
	    break;
	case 'D':
	    a = negComplexDouble(a);
	    break;
	default:
	    throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return a;
    }

    public PyObject __not__() {
	return returnValue(__not__(array(this)));
    }

    PyMultiarray __not__(PyMultiarray a) {
	if (!a.isContiguous) 
	    throw Py.ValueError("internal __not__ requires contiguous matrix as argument");
//	int maxI = a.start + shapeToNItems(a.dimensions);
	switch (a._typecode) {
	case '1':
	    byte aData1[] = (byte []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aData1[i] = (byte)(~aData1[i]);
	    break;
	case 's':
	    short aDatas[] = (short []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatas[i] = (short)(~aDatas[i]);
	    break;
	case 'i':
	    int aDatai[] = (int []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatai[i] = (int)(~aDatai[i]);
	    break;
	case 'l':
	    long aDatal[] = (long []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDatal[i] = (long)(~aDatal[i]);
	    break;
	case 'f': throw Py.ValueError("not not supported for type Float");
	case 'd': throw Py.ValueError("not not supported for type Double");
	case 'O':
	    PyObject aDataO[] = (PyObject []) a.data;
	    for (int i = 0; i < Array.getLength(a.data); i++)
		aDataO[i] = (PyObject)(aDataO[i].__not__());
	    break;
	case 'F': throw Py.ValueError("not not supported for type ComplexFloat");
	case 'D': throw Py.ValueError("not not supported for type ComplexDouble");
	default:
	    throw Py.ValueError("typecode must be in [1silfFdDO]");
	}
	return a;
    }

    // End generated code (genOps).


}
