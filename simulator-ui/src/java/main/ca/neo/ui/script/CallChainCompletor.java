/*
 * Created on 9-Nov-07
 */
package ca.neo.ui.script;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.python.core.PyFunction;
import org.python.core.PyInstance;
import org.python.core.PyJavaInstance;
import org.python.core.PyList;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PyTableCode;
import org.python.util.PythonInterpreter;

/**
 * A CommandCompletor that suggests completions based on Python variable names and 
 * methods/fields of Python objects.
 *    
 * @author Bryan Tripp
 */
public class CallChainCompletor extends CommandCompletor {
	
	private PythonInterpreter myInterpreter;
	
	/**
	 * @param interpreter The Python interpreter from which variables are gleaned  
	 */
	public CallChainCompletor(PythonInterpreter interpreter) {
		myInterpreter = interpreter;
	}
	
	/**
	 * Rebuilds the completion options list from a "base" call chain.
	 *  
	 * @param callChain A partial call chain, eg "x.getY().get", from which we would extract
	 * 		the base call chain "x.getY()". (In this case the new options list might include 
	 * 		"x.getY().toString()", "x.getY().wait()", etc.)
	 */
	public void setBase(String callChain) {
		List<String> options = null;
		if (callChain.contains(".")) { //the root variable is specified
			String base = callChain.substring(0, callChain.lastIndexOf('.'));
			System.out.println(base);
			options = getMembers(base);
		} else {
			options = getVariables();
		}
		getOptions().clear();
		getOptions().addAll(options);
		resetIndex();
	}
	
	/**
	 * @return List of variable names known to the interpreter. 
	 */
	public List<String> getVariables() {
		PyStringMap map = (PyStringMap) myInterpreter.getLocals();
		PyList keys = (PyList) map.keys();
		PyObject iter = keys.__iter__();

		List<String> result = new ArrayList<String>(50);
		System.out.println("VARIABLES:");
		for (PyObject item; (item = iter.__iternext__()) != null; ) {
			result.add(item.toString());
			System.out.println(" " + item.toString());
		}
		
		return result;
	}
	
	/**
	 * @param base A variable name in the interpreter. For variables that wrap 
	 * 		Java objects, this arg can consist of a call chain, eg x.getY().getZ()
	 * 		For native python variables, the return type of a call isn't known, 
	 * 		so this method can't return anything given a call chain.   
	 * @return A list of completion options including methods (with args) 
	 * 		and fields on the named variable.  
	 */
	public List<String> getMembers(String base) {
		PyStringMap map = (PyStringMap) myInterpreter.getLocals();

		StringTokenizer varTok = new StringTokenizer(base, ".", false);
		String rootVariable = varTok.nextToken();

		PyObject po = getObject(map, rootVariable);
		List<String> result = new ArrayList<String>(20);
		if (po instanceof PyJavaInstance) {
			String rootClassName = ((PyJavaInstance) po).instclass.__name__;
			try {
				Class c = getReturnClass(rootClassName, base);

				Field[] fields = c.getFields();
				for (int i = 0; i < fields.length; i++) {
					result.add(fields[i].getName());
				}
				
				Method[] methods = c.getMethods();
				for (int i = 0; i < methods.length; i++) {
					StringBuffer buf = new StringBuffer(base + ".");
					buf.append(methods[i].getName());
					buf.append('(');
					Class[] paramTypes = methods[i].getParameterTypes();
					for (int j = 0; j < paramTypes.length; j++) {
						buf.append(paramTypes[j].getSimpleName());
						if (j < paramTypes.length - 1) buf.append(", ");
					}
					buf.append(')');
					result.add(buf.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else if (po instanceof PyInstance) {
			if ( !varTok.hasMoreTokens() ) {
				PyObject iter = ((PyList) ((PyInstance) po).__dir__()).__iter__();
				
				for (PyString item; (item = (PyString) iter.__iternext__()) != null; ) {
					PyObject attr = po.__findattr__(item);
					StringBuffer buf = new StringBuffer(base + ".");					
					buf.append(item.toString());
					if (attr instanceof PyMethod) {						
						buf.append("(");
						try {
							String[] varnames = ((PyTableCode) ((PyFunction) ((PyMethod) attr).im_func).func_code).co_varnames;
							for (int i = 1; i < varnames.length; i++) { //skip 'self' arg
								buf.append(varnames[i]);
								if (i < varnames.length - 1) buf.append(", ");
							}
						} catch (ClassCastException e) {
							e.printStackTrace();
						}
						buf.append(")");
					}
					result.add(buf.toString());
				}
			}
		}
		
		return result;		
	}
	
	private PyObject getObject(PyStringMap map, String key) {
		PyObject result = null;
		
		PyList keys = (PyList) map.keys();
		PyObject iter = keys.__iter__();
		for (PyObject item; (item = iter.__iternext__()) != null && result == null; ) {
			if (item.toString().equals(key)) {
				result = map.get(item);
			}
		}
		
		return result;
	}
	
	/**
	 * We only match methods by name and number of parameters, not by parameter type. 
	 *   
	 * @param rootClassName Name of class of root variable
	 * @param callChain Full call chain, eg x.getY().getZ(foo)
	 * @return The class of the final return value
	 */
	private Class getReturnClass(String rootClassName, String callChain) throws Exception {
		Class result = Class.forName(rootClassName);				

		StringTokenizer varTok = new StringTokenizer(callChain, ".", false);
		varTok.nextToken(); //skip root	
		
		while (varTok.hasMoreTokens()) {
			String member = varTok.nextToken();
			if (member.contains("(")) { //a method
				StringTokenizer memberTok = new StringTokenizer(member, "(, )", false);						
				int numParams = memberTok.countTokens() - 1;
				member = memberTok.nextToken();
				
				Method[] methods = result.getMethods();
				Method firstMatchingMethod = null;
				for (int i = 0; i < methods.length && firstMatchingMethod == null; i++) {
					if (methods[i].getName().equals(member) && methods[i].getGenericParameterTypes().length == numParams) {
						firstMatchingMethod = methods[i];
					}
				}
				
				if (firstMatchingMethod == null) {
					throw new NoSuchMethodException("No method named " + member + " with " + numParams + " parameters");
				}
				
				result = firstMatchingMethod.getReturnType();
			} else { //a field
				result = result.getField(member).getClass();
			}
		}

		return result;
	}
	
}
