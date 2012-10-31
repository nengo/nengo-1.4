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

// XXX migrate various pieces of PyMultiarray to here.
public class GeneralUfunc extends PyObject  {
	private static final long serialVersionUID = 1L;
  public GeneralUfunc() {
    super(PyType.fromClass(GeneralUfunc.class)) ;
    this.javaProxy = this ;
  }

}

