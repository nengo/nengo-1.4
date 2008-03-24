/*
 * Created on 24-Jan-08
 */
package ca.nengo.util;

import java.lang.reflect.Array;

/**
 * Class-related utility methods. 
 *  
 * @author Bryan Tripp
 */
public class ClassUtils {

	/**
	 * As Class.forName(String) but arrays and primitives are also handled. 
	 * 
	 * @param name Name of a Class
	 * @return Named Class 
	 * @throws ClassNotFoundException
	 */
	public static Class forName(String name) throws ClassNotFoundException {
		Class result = null;
		
		if (name.endsWith("[]")) {
			Class baseClass = forName(name.substring(0, name.length()-2));
			result = Array.newInstance(baseClass, 0).getClass();
		} else if (name.equals("byte")) {
			result = Byte.TYPE;
		} else if (name.equals("short")) {
			result = Short.TYPE;
		} else if (name.equals("int")) {
			result = Integer.TYPE;
		} else if (name.equals("long")) {
			result = Long.TYPE;
		} else if (name.equals("float")) {
			result = Float.TYPE;
		} else if (name.equals("double")) {
			result = Double.TYPE;
		} else if (name.equals("boolean")) {
			result = Boolean.TYPE;
		} else if (name.equals("char")) {
			result = Character.TYPE;
		} else {
			result = Class.forName(name);
		}
		
		return result;
	}
	
	/**
	 * @param c A Class
	 * @return The class name, with arrays identified with trailing "[]"
	 */
	public static String getName(Class c) {
		if (c.isArray()) {
			return getName(c.getComponentType()) + "[]";
		} else {
			return c.getName();
		}
	}
}
