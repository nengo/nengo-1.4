package ca.neo.config.impl;

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
	
	private Method myGetter;
	private Method mySetter;

	/**
	 * @param configuration Configuration to which this Property belongs
	 * @param name Parameter name
	 * @param c Parameter type
	 * @return Property or null if the necessary methods don't exist on the underlying class  
	 */
	public static SingleValuedProperty getSingleValuedProperty(Configuration configuration, String name, Class type) {
		SingleValuedPropertyImpl result = null;
		Class targetClass = configuration.getConfigurable().getClass();
		
		String uname = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		String[] getterNames = new String[]{"get"+uname};
		String[] setterNames = new String[]{"set"+uname}; 

		Method getter = ListPropertyImpl.getMethod(targetClass, getterNames, new Class[0], type);
		Method setter = ListPropertyImpl.getMethod(targetClass, setterNames, new Class[]{type}, null);

		if (getter != null && setter != null) {
			result = new SingleValuedPropertyImpl(configuration, name, type, getter, setter);
		} else if (getter != null) {
			result = new SingleValuedPropertyImpl(configuration, name, type, getter);
		}
		
		return result;
	}
	
	/**
	 * Constructor for immutable single-valued properties. 
	 * 
	 * @param configuration Configuration to which this Property belongs
	 * @param name Parameter name
	 * @param c Parameter type
	 * @param getter Zero-arg getter method
	 */
	public SingleValuedPropertyImpl(Configuration configuration, String name, Class c, Method getter) {
		super(configuration, name, c, false);
		myGetter = getter;
	}

	/**
	 * Constructor for mutable single-valued properties. 
	 * 
	 * @param configuration Configuration to which this Property belongs
	 * @param name Parameter name
	 * @param c Parameter type
	 * @param getter Zero-arg getter method
	 * @param setter Single-arg setter method
	 */
	public SingleValuedPropertyImpl(Configuration configuration, String name, Class c, Method getter, Method setter) {
		super(configuration, name, c, true);
		myGetter = getter;
		mySetter = setter;
	}
	
	/**
	 * @see ca.neo.config.Property#getValue()
	 */
	public Object getValue() {
		Object result = null;
		
		Object configurable = getConfiguration().getConfigurable();
		try {
			result = myGetter.invoke(configurable, new Object[0]);
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
		Object configurable = getConfiguration().getConfigurable();
		
		try {
			mySetter.invoke(configurable, new Object[]{value});
		} catch (Exception e) {
			throw new RuntimeException("Can't set property", e);
		}
	}

	@Override
	public String getDocumentation() {
		String result = super.getDocumentation();
		
		if (result == null) {
			StringBuffer buf = new StringBuffer("<h1>API methods underlying property <i>" + getName() + "</i></h1>");
			appendDocs(buf, myGetter);
			appendDocs(buf, mySetter);
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