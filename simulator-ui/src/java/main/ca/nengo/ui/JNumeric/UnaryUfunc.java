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




// XXX Get rid of dependance on umath
// XXX change format of calls to Complex(a, r) where result is stuff into r.
//     this way two argument ufuncs could be made to work correctly also.
// XXX Change __call__ signature to official JPython call signature.

public class UnaryUfunc extends PyObject {
	private static final long serialVersionUID = 1L;
	static final public UnaryFunction arccos = new Arccos();
    static final public UnaryFunction arccosh = new Arccosh();
    static final public UnaryFunction arcsin = new Arcsin();
    static final public UnaryFunction arcsinh = new Arcsinh();
    static final public UnaryFunction arctan = new Arctan();
    static final public UnaryFunction arctanh = new Arctanh();
    static final public UnaryFunction ceil = new Ceil();
    static final public UnaryFunction conjugate = new Conjugate();
    static final public UnaryFunction imaginary = new Imaginary();
    static final public UnaryFunction cos = new Cos(); 
    static final public UnaryFunction cosh = new Cosh(); 
    static final public UnaryFunction exp = new Exp();
    static final public UnaryFunction floor = new Floor();
    static final public UnaryFunction log = new Log();
    static final public UnaryFunction log10 = new Log10();
    static final public UnaryFunction logicalNot = new LogicalNot();
    static final public UnaryFunction real = new Real();
    static final public UnaryFunction sin = new Sin();
    static final public UnaryFunction sinh = new Sinh();
    static final public UnaryFunction sqrt = new Sqrt();
    static final public UnaryFunction tan = new Tan(); 
    static final public UnaryFunction tanh = new Tanh(); 

    UnaryFunction function;

    public PyObject __findattr__(String name) {
	if (name == "__doc__") return new PyString(function.docString());
	return super.__findattr__(name);
    }

    public UnaryUfunc(UnaryFunction function) {
	this.function = function;
    }

    public PyObject __call__(PyObject o) {
	// XXX rework to provide separate functions for all cases
	// XXX have default behaviour of most functions call 'd' and 'D'
	PyMultiarray result;
	PyMultiarray a = PyMultiarray.asarray(o);
	switch (a._typecode) {
	case 'F': 
	    result = function.ComplexFloat(PyMultiarray.array(a));
	    break;
	case 'D':
	    result = function.ComplexDouble(PyMultiarray.array(a));
	    break;
	case 'f':
	    result = function.Float(PyMultiarray.array(a));
	    break;
	case '1': case 's': case 'i': case 'l': case 'd':
	    // while sensible, this is not what Numeric does!
	    //result = function.Double(PyMultiarray.array(a,'d')).astype(a._typecode);
	    result = function.Double(PyMultiarray.array(a,'d'));
	    break;
	default:
	    throw Py.ValueError("typecode must be in [1silfFdD]");
	}
	return PyMultiarray.returnValue(result);
    }

    // Two argument unary functions are provided for compatibility with
    // CNumeric. As implemented, they are no more efficient in memory usage
    // or speed than one argument unary functions (and perhaps less so). 
    // However, I have never used a two argument unary ufunc, so I'm not too
    // concerned. However if people complain I suppose I'll do something
    // about it. (Nah I don't sound defensive!)
    public PyObject __call__(PyObject o, PyMultiarray result) {
	PyMultiarray.copyAToB(PyMultiarray.asarray(__call__(o)), result);
	return result;
    }
}

class UnaryFunction {
    String docString() {return "unary_function(a, [,r])\n";}
    // Some constants that are useful when using complex numbers
    static final PyMultiarray cp1 = PyMultiarray.array(new PyComplex( 1,  0));
    static final PyMultiarray cpj = PyMultiarray.array(new PyComplex( 0,  1));
    static final PyMultiarray cn1 = PyMultiarray.array(new PyComplex(-1,  0));
    static final PyMultiarray cnj = PyMultiarray.array(new PyComplex( 0, -1));
    static final PyMultiarray c0 = PyMultiarray.array( new PyComplex( 0,  0));
    static final PyMultiarray cpj_2 = PyMultiarray.array(new PyComplex(0,   0.5));
    static final PyMultiarray cp1_2 = PyMultiarray.array(new PyComplex(0.5, 0));
    static final PyMultiarray cp2 = PyMultiarray.array(new PyComplex(2,   0));
    // a is used as scratch space by these functions, so make sure to pass a copy!
    PyMultiarray Double(PyMultiarray a) {throw Py.ValueError("Double not implemented");}
    PyMultiarray ComplexDouble(PyMultiarray a) {throw Py.ValueError("ComplexDouble not implemented");} 
    // This should be overridden by functions concerned with efficiency / speed.
    PyMultiarray Float(PyMultiarray a) {
	return ComplexDouble(a.astype('d')).astype('f');
    }
    PyMultiarray ComplexFloat(PyMultiarray a) {
	return ComplexDouble(a.astype('D')).astype('F');
    }
}

// XXX Someday the calls to umath should be removed from these unary functions.

final class Arccos extends UnaryFunction {
    String docString() {return "arccos(a [,r]) returns arccos(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.acos(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    // arccos(z) = -j*log(z + j*sqrt(1 - z**2)  
	    final double re = Array.getDouble(a.data, i), im = Array.getDouble(a.data, i+1);
	    final double re1 = 1 + im*im - re*re, im1 = -2*im*re;
	    final double mag = Math.pow(re1*re1+im1*im1, 0.25), phi = Math.atan2(im1, re1)/2.;
	    final double re2 = mag*Math.cos(phi), im2 = mag*Math.sin(phi);
	    final double re3 = re - im2, im3 = im + re2;
	    final double re4 = Math.log(re3*re3+im3*im3) / 2., im4 = Math.atan2(im3, re3);
	    Array.setDouble(a.data, i, im4);
	    Array.setDouble(a.data, i+1, -re4);
	}
	return a;
    }
}

final class Arccosh extends UnaryFunction {
    String docString() {return "arccosh(a [,r]) returns arccosh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    Array.setDouble(a.data, i, Math.log(d+Math.sqrt(d*d-1)));
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(Umath.log.__call__(a.__add__(
	          cpj.__mul__(Umath.sqrt.__call__(cp1.__sub__(a.__mul__(a)))))));
    }
}

final class Arcsin extends UnaryFunction {
    String docString() {return "arcsin(a [,r]) returns arcsin(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.asin(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(cnj.__mul__(Umath.log.__call__(cpj.__mul__(a).__add__(
	          Umath.sqrt.__call__(cp1.__sub__(a.__mul__(a)))))));
    }
}

final class Arcsinh extends UnaryFunction {
    String docString() {return "arcsinh(a [,r]) returns arcsinh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    Array.setDouble(a.data, i, -Math.log(Math.sqrt(1+d*d)-d));
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(Umath.log.__call__(Umath.sqrt.__call__(
	          cp1.__add__(a.__mul__(a))).__sub__(a)).__neg__());
    }
}

final class Arctan extends UnaryFunction {
    String docString() {return "arctan(a [,r]) returns arctan(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.atan(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(cpj_2.__mul__(Umath.log.__call__(cpj.__add__(a).__div__(cpj.__sub__(a)))));
    }
}

final class Arctanh extends UnaryFunction {
    String docString() {return "arctanh(a [,r]) returns arctanh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    Array.setDouble(a.data, i, 0.5*Math.log((1.+d)/(1.-d)));
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(cp1_2.__mul__(Umath.log.__call__(cp1.__add__(a).__div__(cp1.__sub__(a)))));
    }
}

final class Ceil extends UnaryFunction {
    String docString() {return "ceil(a [,r]) returns floor(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.ceil(Array.getDouble(a.data,i)));
	return a;
    }
}

final class Conjugate extends UnaryFunction {
    String docString() {return "conjugate(a [,r]) returns conjugate(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2)
	    Array.setDouble(a.data, i+1, -Array.getDouble(a.data,i+1));
	return a;
    }
}

final class Cos extends UnaryFunction {
    String docString() {return "cos(a [,r]) returns cos(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.cos(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    double eim = Math.exp(im), cosre = Math.cos(re), sinre = Math.sin(re);
	    Array.setDouble(a.data, i,    0.5*cosre*(eim+1./eim));
	    Array.setDouble(a.data, i+1, -0.5*sinre*(eim-1./eim));
	}
	return a;
    }
}

final class Cosh extends UnaryFunction {
    String docString() {return "cosh(a [,r]) returns cosh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    double ed = Math.exp(d); 
	    Array.setDouble(a.data, i, 0.5*ed+0.5/ed);
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	PyObject ea = Umath.exp.__call__(a);
	return PyMultiarray.asarray(cp1_2.__mul__(ea).__add__(cp1_2.__div__(ea)));
    }
}

final class Exp extends UnaryFunction {
    String docString() {return "exp(a [,r]) returns exp(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.exp(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    double ere = Math.exp(re), cosim = Math.cos(im), sinim = Math.sin(im);
	    Array.setDouble(a.data, i,   ere*cosim);
	    Array.setDouble(a.data, i+1, ere*sinim);
	}
	return a;
    }
}

final class Floor extends UnaryFunction {
    String docString() {return "floor(a [,r]) returns floor(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.floor(Array.getDouble(a.data,i)));
	return a;
    }
}

final class Imaginary extends UnaryFunction {
    String docString() {return "imaginary(a [,r]) returns imaginary(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	return PyMultiarray.zeros(a.dimensions, 'd');
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return a.getImag();
    }
}

final class Log extends UnaryFunction {
    String docString() {return "log(a [,r]) returns log(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.log(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    Array.setDouble(a.data, i,   Math.log(im*im+re*re) / 2.);
	    Array.setDouble(a.data, i+1, Math.atan2(im, re));
	}
	return a;
    }
}

final class Log10 extends UnaryFunction {
    String docString() {return "log10(a [,r]) returns log10(a) and stores the result in r if supplied.\n";}
    final double log10 = Math.log(10);
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.log(Array.getDouble(a.data,i))/log10);
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    Array.setDouble(a.data, i,   Math.log(Math.sqrt(im*im+re*re))/log10);
	    Array.setDouble(a.data, i+1, Math.atan2(im, re)/log10);
	}
	return a;
    }
}

final class LogicalNot extends UnaryFunction {
    String docString() {return "logical_not(a [,r]) returns the logical inverse of a and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	return PyMultiarray.asarray(a.__eq(Py.Zero));
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return PyMultiarray.asarray(a.__eq(Py.Zero));
    }
}

final class Real extends UnaryFunction {
    String docString() {return "real(a [,r]) returns real(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	return a.getReal();
    }
}

final class Sin extends UnaryFunction {
    String docString() {return "sin(a [,r]) returns sin(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.sin(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    double eim = Math.exp(im), cosre = Math.cos(re), sinre = Math.sin(re);
	    Array.setDouble(a.data, i,   0.5*sinre*(eim+1./eim));
	    Array.setDouble(a.data, i+1, 0.5*cosre*(eim-1./eim));
	}
	return a;
    }
}

final class Sinh extends UnaryFunction {
    String docString() {return "sinh(a [,r]) returns sinh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    double ed = Math.exp(d); 
	    Array.setDouble(a.data, i, 0.5*ed-0.5/ed);
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	PyObject ea = Umath.exp.__call__(a);
	return PyMultiarray.asarray(cp1_2.__mul__(ea).__sub__(cp1_2.__div__(ea)));
    }
}

final class Sqrt extends UnaryFunction {
    String docString() {return "aqrt(a [,r]) returns sqrt(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.sqrt(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i+=2) {
	    double re = Array.getDouble(a.data,i), im = Array.getDouble(a.data,i+1);
	    double mag = Math.pow(re*re+im*im, 0.25), phi = Math.atan2(im, re)/2.;
	    Array.setDouble(a.data, i, mag*Math.cos(phi));
	    Array.setDouble(a.data, i+1, mag*Math.sin(phi));
	}
	return a;
    }
}

final class Tan extends UnaryFunction {
    String docString() {return "tan(a [,r]) returns tan(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++)
	    Array.setDouble(a.data, i, Math.tan(Array.getDouble(a.data,i)));
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	PyMultiarray sina = Umath.sin.function.ComplexDouble(PyMultiarray.array(a));
	return PyMultiarray.asarray(sina.__div__(Umath.cos.function.ComplexDouble(a)));
    }
}

final class Tanh extends UnaryFunction {
    String docString() {return "tanh(a [,r]) returns tanh(a) and stores the result in r if supplied.\n";}
    public PyMultiarray Double(PyMultiarray a) {
	for (int i = 0; i < Array.getLength(a.data); i++) {
	    double d = Array.getDouble(a.data,i);
	    double e2d = Math.exp(2*d); 
	    Array.setDouble(a.data, i, (e2d-1)/(e2d+1));
	}
	return a;
    }
    public PyMultiarray ComplexDouble(PyMultiarray a) {
	PyObject e2a = Umath.exp.__call__(cp2.__mul__(a));
	return PyMultiarray.asarray(e2a.__sub__(cp1).__div__(e2a.__add__(cp1)));
    }
}
