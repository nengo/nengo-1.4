/*
 * Created on 22-Dec-07
 */
package ca.neo.config;

import java.lang.reflect.Method;

import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.model.StructuralException;

/**
 * Configuration-related utility methods. 
 * 
 * @author Bryan Tripp
 */
public class ConfigUtil {
	
	/**
	 * @param properties Configuration from which to extract a property
	 * @param name Name of property to extact
	 * @param c Class to which property value must belong 
	 * @return Value
	 * @throws StructuralException If value doesn't belong to specified class
	 */
	public static Object get(Configuration properties, String name, Class c) throws StructuralException {
		Object o = ((SingleValuedProperty) properties.getProperty(name)).getValue();		
		if ( !c.isAssignableFrom(o.getClass()) ) {
			throw new StructuralException("Property " + name 
					+ " must be of class " + c.getName() + " (was " + o.getClass().getName() + ")");
		}		
		return o;
	}

	//TODO: pass methods to properties
	//TODO: finish indexed property
	//TODO: add Configuration to Value in tree model; use getConfiguration() if available
	//TODO: test against non-Configurable objects
	//TODO: rewrite property as hierarchy and handle map properties (check against Ensemble & Plastic)
	//TODO: write map property handler here 
	//TODO: support vector and matrix size changes in editor components? maybe not (setLength() might be better)
	public static ConfigurationImpl defaultConfiguration(Object configurable) {
		ConfigurationImpl result = new ConfigurationImpl(configurable);
		
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Class returnType = methods[i].getReturnType();
			if (isGetter(methods[i]) 
					&& !methods[i].getName().equals("getClass")
					&& !methods[i].getName().equals("getConstructor")) {
				
				Class returnTypeWrapped = getPrimitiveWrapperClass(returnType);
				//TODO: remove Configurable check here; need above wrapped?
				if (MainHandler.getInstance().canHandle(returnTypeWrapped) || Configurable.class.isAssignableFrom(returnType)) {
					String propName = Character.toLowerCase(methods[i].getName().charAt(3))
						+ methods[i].getName().substring(4);
					boolean mutable = hasSetter(configurable, methods[i].getName(), returnType);
					result.defineSingleValuedProperty(propName, returnTypeWrapped, mutable);
				}
			} else if (isIndexGetter(methods[i])) {
				
			}
		}
		return result;
	}
	
	private static boolean isGetter(Method m) {
		if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean hasSetter(Object o, String getterName, Class type) {
		boolean has = false;
		Method[] methods = o.getClass().getMethods();
		for (int i = 0; i < methods.length && !has; i++) {
			if (methods[i].getName().equals("s" + getterName.substring(1))
					&& methods[i].getParameterTypes().length == 1
					&& methods[i].getParameterTypes()[0].isAssignableFrom(type)) {
				has = true;
			}
		}
		return has;
	}

	private static boolean isIndexGetter(Method m) {
		Class[] paramTypes = m.getParameterTypes();
		if (m.getName().startsWith("get") && paramTypes.length == 1 && paramTypes[0].equals(Integer.TYPE)) {
			return true;
		} else {
			return false;
		}
	}
	
	//TODO: document name pattern
	private static Method getIndexCounter(Object o, String getterName) {
		Method result = null;
		
		String[] counterNames = new String[]{
				"getNum" + getterName.substring(3),
				"getNum" + getterName.substring(3) + "s"};

		Method[] methods = o.getClass().getMethods();
		for (int i = 0; i < methods.length && result == null; i++) {
			if (methods[i].getParameterTypes().length == 0) {
				for (int j = 0; j < counterNames.length && result == null; j++) {
					if (methods[i].getName().equals(counterNames[j])) {
						result = methods[i];
					}
				}
			}
		}
		
		return result;
	}
	
	private static Method getIndexSetter(Object o, String getterName, Class type) {
		Method result = null;
		
		Method[] methods = o.getClass().getMethods();
		for (int i = 0; i < methods.length && result == null; i++) {
			if (methods[i].getName().equals("s" + getterName.substring(1))
					&& methods[i].getParameterTypes().length == 1
					&& methods[i].getParameterTypes()[0].isAssignableFrom(Integer.TYPE)
					&& methods[i].getParameterTypes()[1].isAssignableFrom(type)) {
				result = methods[i];
			}
		}
		
		return result;
	}
	
	/**
	 * @param c Any class 
	 * @return Either c or if c is a primitive class (eg Integer.TYPE), the corresponding wrapper class 
	 */
	public static Class getPrimitiveWrapperClass(Class c) {
		if (Integer.TYPE.isAssignableFrom(c)) {
			c = Integer.class;
		} else if (Float.TYPE.isAssignableFrom(c)) {
			c = Float.class;
		} else if (Boolean.TYPE.isAssignableFrom(c)) {
			c = Boolean.class;
		} else if (Long.TYPE.isAssignableFrom(c)) {
			c = Long.class;
		} else if (Double.TYPE.isAssignableFrom(c)) {
			c = Double.class;
		} else if (Character.TYPE.isAssignableFrom(c)) {
			c = Character.class;
		} else if (Byte.TYPE.isAssignableFrom(c)) {
			c = Byte.class;
		} else if (Short.TYPE.isAssignableFrom(c)) {
			c = Short.class;
		}		
		
		return c;
	}
}
