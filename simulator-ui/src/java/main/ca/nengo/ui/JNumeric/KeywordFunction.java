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

abstract public class KeywordFunction extends PyObject {
    protected String docString;
    protected String [] argNames;
    protected PyObject [] defaultArgs;

    abstract PyObject _call(PyObject args[]);

    public PyObject __call__(PyObject args[], String keywords[]) {
	return _call(processArgs(args, keywords));
    }

    protected PyObject [] processArgs(PyObject args[], String keywords[]) {
	// Note that that all nulls in defaultArgs must occur at beginning.
	if (args.length > argNames.length) {
	    throw Py.ValueError("too many arguments");
	}
	PyObject [] allArgs = new PyObject[argNames.length];
	int nPosArgs = args.length - keywords.length;
	for (int i = 0; i < nPosArgs; i++) {
	    allArgs[i] = args[i];
	}
	for (int i = 0; i < keywords.length; i++) {
	    int j;
	    for (j = 0; j < argNames.length; j++)
		if (keywords[i] == argNames[j]) 
		    break;
	    if (j == argNames.length)
		throw Py.TypeError("unexpected keyword parameter: " + keywords[i]);
	    if (allArgs[j] != null)
		throw Py.TypeError("keyword parameter redefined");
	    allArgs[j] = args[i + nPosArgs];
	}
	int nNulls = 0;
	for (int i = 0; i < defaultArgs.length; i++)
	if (allArgs[i] == null) {
	    if (defaultArgs[i] == null)
		nNulls++;
	    else
		allArgs[i] = defaultArgs[i];
	}
	if (nNulls != 0)
	throw Py.TypeError("not enough arguments; expected " + (nNulls+nPosArgs) + ", got " + nPosArgs);
	return allArgs;
    }

    public PyObject __findattr__(String name) {
	if (name == "__doc__") return new PyString(docString);
	return super.__findattr__(name);
    }
}
