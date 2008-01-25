package ca.neo.config.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.neo.config.Configuration;
import ca.neo.config.JavaSourceParser;
import ca.neo.config.SingleValuedProperty;
import ca.neo.model.StructuralException;

/**
 * Default implementation of single-valued Properties.
 * 
 * @author Bryan Tripp
 */
public class SingleValuedPropertyImpl extends AbstractProperty implements SingleValuedProperty {
	
	public SingleValuedPropertyImpl(Configuration configuration, String name, Class c, boolean mutable) {
		super(configuration, name, c, mutable);
	}

	/**
	 * @see ca.neo.config.Property#getValue()
	 */
	public Object getValue() {
		Object result = null;
		
		Object configurable = getConfiguration().getConfigurable();
		Method getter = getGetter();
		if (getter != null) {
			try {
				result = getter.invoke(configurable, new Object[0]);
			} catch (Exception e) {
				throw new RuntimeException("Can't get property", e);
			}
		}
		
		return result;
//		Object result = null;
//		
//		Object o = getConfiguration().getConfigurable();
//		String methodName = "get" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
//		
//		try {
//			Method method = o.getClass().getMethod(methodName, new Class[0]);
//			result = method.invoke(o, new Object[0]);
//		} catch (Exception e) {
//			throw new RuntimeException("Can't get property", e);
//		}
//		
//		return result; 
	}
	
	private Method getGetter() {
		Method result = null;
		
		Object o = getConfiguration().getConfigurable();
		String methodName = "get" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
		
		try {
			result = o.getClass().getMethod(methodName, new Class[0]);
		} catch (Exception e) {
			throw new RuntimeException("Can't get property", e);
		}

		return result;
	}

	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return true;
	}

	/**
	 * By default, attempts to call method setX(y) on Configurable, where X is the name of the property (with 
	 * first letter capitalized) and y is the value (changed to a primitive if it's a primitive wrapper).
	 * A Configurable that needs different behaviour should override this method.   
	 *  
	 * @see ca.neo.config.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws StructuralException {
		if (getType().isAssignableFrom(value.getClass())) {
			Object o = getConfiguration().getConfigurable();
			String methodName = "set" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
			Class argClass = getType();
			//TODO: use handlers? support other primitives?
			if (argClass.equals(Integer.class)) {
				argClass = Integer.TYPE;
			} else if (argClass.equals(Float.class)) {
				argClass = Float.TYPE;					
			} else if (argClass.equals(Boolean.class)) {
				argClass = Boolean.TYPE;					
			}
			
			try {
				Method method = o.getClass().getMethod(methodName, new Class[]{argClass});
				method.invoke(o, value);
			} catch (Exception e) {
				Throwable t = e;
				if (t instanceof InvocationTargetException) {
					t = ((InvocationTargetException) t).getCause();
				}
				throw new StructuralException("Can't change property: " + t.getMessage(), t);
			}
		} else {
			throw new StructuralException("Value must be of class " + getType().getName());
		}
	}

	@Override
	public String getDocumentation() {
		String result = super.getDocumentation();
		
		if (result == null) {
			StringBuffer buf = new StringBuffer("<h1>API methods underlying property <i>" + getName() + "</i></h1>");
			appendDocs(buf, getGetter());
			result = buf.toString();
		}
			
		return result;
	}
	
	private static void appendDocs(StringBuffer buf, Method method) {
		if (method != null) {
			buf.append("<h2>" + JavaSourceParser.getSignature(method) + "</h2>");
			String docs = JavaSourceParser.getDocs(method); 
			if (docs != null) buf.append(docs);			
		}
	}
	
}