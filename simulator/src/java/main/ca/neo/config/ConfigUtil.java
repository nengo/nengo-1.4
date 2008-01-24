/*
 * Created on 22-Dec-07
 */
package ca.neo.config;

import java.awt.BorderLayout;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.ListPropertyImpl;
import ca.neo.config.impl.NamedValuePropertyImpl;
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
	
	public static Configuration getConfiguration(Object configurable) {
		Configuration result = null;
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length && result == null; i++) {
			if (methods[i].getName().equals("getConfiguration")
					&& methods[i].getParameterTypes().length == 0
					&& Configuration.class.isAssignableFrom(methods[i].getReturnType())) {
				
				try {
					result = (Configuration) methods[i].invoke(configurable, new Object[0]);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		if (result == null) {
			result = defaultConfiguration(configurable);
		}
		
		return result;
	}
	
	public static ConfigurationImpl defaultConfiguration(Object configurable) {
		ConfigurationImpl result = new ConfigurationImpl(configurable);
		
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Class returnType = methods[i].getReturnType();
			String propName = getPropertyName(methods[i]);

			if (isSingleValueGetter(methods[i]) 
					&& !methods[i].getName().equals("getClass")
					&& !methods[i].getName().equals("getConfiguration")
					&& !isCounter(methods[i])) {
				
				Class returnTypeWrapped = getPrimitiveWrapperClass(returnType); 
				boolean mutable = hasSetter(configurable, methods[i].getName(), returnType);
				result.defineSingleValuedProperty(propName, returnTypeWrapped, mutable);				
			} else if (isIndexedGetter(methods[i]) && !result.getPropertyNames().contains(propName)) {
				Property p = ListPropertyImpl.getListProperty(result, propName, returnType);
				if (p != null) result.defineProperty(p);					
			} else if (isNamedGetter(methods[i]) && !result.getPropertyNames().contains(propName)) {
				Property p = NamedValuePropertyImpl.getNamedValueProperty(result, propName, returnType);
				if (p != null) result.defineProperty(p);									
			}
		}
		
		//look for additional array, list, and map getters 
		for (int i = 0; i < methods.length; i++) {
			Type returnType = methods[i].getGenericReturnType();
			String propName = getPropertyName(methods[i]);
			
			if (isGetter(methods[i]) && !isNamesGetter(methods[i]) && !result.getPropertyNames().contains(propName)
					&& !result.getPropertyNames().contains(stripSuffix(propName, "s"))
					&& !result.getPropertyNames().contains(stripSuffix(propName, "es"))) {
				
				Property p = null;
//				System.out.println(returnType instanceof Class);
//				System.out.println(returnType instanceof Class && ((Class) returnType).isArray());
				if (returnType instanceof Class && ((Class) returnType).isArray()) {
					p = ListPropertyImpl.getListProperty(result, propName, ((Class) returnType).getComponentType());
				} else if (returnType instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) returnType).getRawType();
					Type[] typeArgs = ((ParameterizedType) returnType).getActualTypeArguments();
					if (rawType instanceof Class && List.class.isAssignableFrom((Class) rawType) 
							&& typeArgs[0] instanceof Class) {
						p = ListPropertyImpl.getListProperty(result, propName, (Class) typeArgs[0]);
					} else if (rawType instanceof Class && Map.class.isAssignableFrom((Class) rawType)
							&& typeArgs[0] instanceof Class && typeArgs[1] instanceof Class) {
						p = NamedValuePropertyImpl.getNamedValueProperty(result, propName, (Class) typeArgs[1]);						
					}
				}
				if (p != null) result.defineProperty(p);
			}
		}
		
		//TODO: might be nice to build properties from lone map, list, or array, but 
		//  1) not sure how to check generic type in map/list
		
		return result;
	}
	
	private static boolean isCounter(Method method) {
		String name = method.getName(); 
		if (method.getReturnType().equals(Integer.TYPE) 
				&& (name.matches("getNum.+") || name.matches("get.+Count")) ) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isNamesGetter(Method method) {
		String name = method.getName();
		
		boolean returnsStringArray = method.getReturnType().isArray() 
			&& String.class.isAssignableFrom(method.getReturnType().getComponentType());
		boolean returnsStringList = List.class.isAssignableFrom(method.getReturnType()) 
			&& (method.getGenericReturnType() instanceof ParameterizedType) 
			&& ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0] instanceof Class
			&& String.class.isAssignableFrom((Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
		
		if (name.matches("get.+Names") && (returnsStringArray || returnsStringList)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static String getPropertyName(Method method) {
		String result = method.getName();

		result = stripPrefix(result, "get");
		result = stripPrefix(result, "All");
		result = stripSuffix(result, "Array");
		result = stripSuffix(result, "List");
				
		return result.length() > 0 ? Character.toLowerCase(result.charAt(0)) + result.substring(1) : "";
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
	
	private static String stripPrefix(String s, String prefix) {
		if (s.startsWith(prefix)) {
			return s.substring(prefix.length());
		} else {
			return s;
		}
	}
	
	private static boolean isSingleValueGetter(Method m) {
		if (m.getName().startsWith("get") 
				&& m.getParameterTypes().length == 0
				&& !Collection.class.isAssignableFrom(m.getReturnType()) 
				&& !Map.class.isAssignableFrom(m.getReturnType()) 
				&& !m.getReturnType().isArray()) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isGetter(Method m) {
		if (m.getName().startsWith("get") 
				&& m.getParameterTypes().length == 0) {
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

	private static boolean isIndexedGetter(Method m) {
		Class[] paramTypes = m.getParameterTypes();
		if (m.getName().startsWith("get") && paramTypes.length == 1 && paramTypes[0].equals(Integer.TYPE)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isNamedGetter(Method m) {
		Class[] paramTypes = m.getParameterTypes();
		if (m.getName().startsWith("get") && paramTypes.length == 1 && paramTypes[0].equals(String.class)) {
			return true;
		} else {
			return false;
		}		
	}
	
//	private static boolean isArrayGetter(Method m) {
//		if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 
//				&& m.getReturnType().isArray()) {
//			return true;
//		} else {
//			return false;
//		}		
//	}
//	
//	private static boolean isListGetter(Method m) {
//		if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 
//				&& List.class.isAssignableFrom(m.getReturnType())) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	private static boolean isMapGetter(Method m) {
//		if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 
//				&& Map.class.isAssignableFrom(m.getReturnType())) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	
//	//TODO: document name pattern
//	private static Method getIndexCounter(Object o, String getterName) {
//		Method result = null;
//		
//		String[] counterNames = new String[]{
//				"getNum" + getterName.substring(3),
//				"getNum" + getterName.substring(3) + "s"};
//
//		Method[] methods = o.getClass().getMethods();
//		for (int i = 0; i < methods.length && result == null; i++) {
//			if (methods[i].getParameterTypes().length == 0) {
//				for (int j = 0; j < counterNames.length && result == null; j++) {
//					if (methods[i].getName().equals(counterNames[j])) {
//						result = methods[i];
//					}
//				}
//			}
//		}
//		
//		return result;
//	}
//	
//	private static Method getIndexSetter(Object o, String getterName, Class type) {
//		Method result = null;
//		
//		Method[] methods = o.getClass().getMethods();
//		for (int i = 0; i < methods.length && result == null; i++) {
//			if (methods[i].getName().equals("s" + getterName.substring(1))
//					&& methods[i].getParameterTypes().length == 1
//					&& methods[i].getParameterTypes()[0].isAssignableFrom(Integer.TYPE)
//					&& methods[i].getParameterTypes()[1].isAssignableFrom(type)) {
//				result = methods[i];
//			}
//		}
//		
//		return result;
//	}
	
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
	
	public static Object getDefaultValue(Class type) {
		Object result = null;
		
		if (MainHandler.getInstance().canHandle(type)) {
			result = MainHandler.getInstance().getDefaultValue(type);
		}
		
		if (result == null) {
			Constructor<?>[] constructors = type.getConstructors();
			Constructor zeroArgConstructor = null;
			for (int i = 0; i < constructors.length && zeroArgConstructor == null; i++) {
				if (constructors[i].getParameterTypes().length == 0) {
					zeroArgConstructor = constructors[i];
				}
			}
			if (zeroArgConstructor != null) {
				try {
					result = zeroArgConstructor.newInstance(new Object[0]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}			
		}
		
		return result; 
	}
	
	public static void showHelp(String text) {
		String document = "<html><head></head><body>" + text + "</body></html>";
		JEditorPane pane = new JEditorPane("text/html", document);
		pane.setEditable(false);
		
		JFrame frame = new JFrame("Help"); 
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(pane), BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Object foo = new Object() {
			public int getFooCount() {
				return 0;
			}
			public int getNumFoo() {
				return 0;
			}
			public int[] getAllFoo() {
				return new int[0];
			}
			public int[] getFooArray() {
				return new int[0];
			}
			public int[] getFooList() {
				return new int[0];
			}
		};
		
		try {
			System.out.println(isCounter(foo.getClass().getMethod("toString", new Class[0])));
			System.out.println(isCounter(foo.getClass().getMethod("getFooCount", new Class[0])));
			System.out.println(isCounter(foo.getClass().getMethod("getNumFoo", new Class[0])));
			
			System.out.println(getPropertyName(foo.getClass().getMethod("getAllFoo", new Class[0])));
			System.out.println(getPropertyName(foo.getClass().getMethod("getFooArray", new Class[0])));
			System.out.println(getPropertyName(foo.getClass().getMethod("getFooList", new Class[0])));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
