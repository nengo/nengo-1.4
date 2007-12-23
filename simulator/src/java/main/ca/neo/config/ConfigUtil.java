/*
 * Created on 22-Dec-07
 */
package ca.neo.config;

import java.lang.reflect.Method;

import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.ConfigurationImpl;

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
		Object o = properties.getProperty(name).getValue();		
		if ( !c.isAssignableFrom(o.getClass()) ) {
			throw new StructuralException("Property " + name 
					+ " must be of class " + c.getName() + " (was " + o.getClass().getName() + ")");
		}		
		return o;
	}
	
	public static ConfigurationImpl defaultConfiguration(Configurable configurable) {
		ConfigurationImpl result = new ConfigurationImpl(configurable);
		
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (isGetter(methods[i]) 
					&& !methods[i].getName().equals("getClass")
					&& !methods[i].getName().equals("getConstructor")) {
				
				Class returnType = methods[i].getReturnType();
				if (MainHandler.getInstance().canHandle(returnType)) {
					String propName = Character.toLowerCase(methods[i].getName().charAt(3))
						+ methods[i].getName().substring(4);
					boolean mutable = hasSetter(configurable, methods[i].getName(), returnType);
					result.defineSingleValuedProperty(propName, returnType, mutable);
				}
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
	

}
