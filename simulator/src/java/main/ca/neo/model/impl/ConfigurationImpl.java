/*
 * Created on 3-Dec-07
 */
package ca.neo.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.neo.config.MainHandler;
import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;

/**
 * Base implementation of Configuration. A Configurable would normally have 
 * an associated implementation of <code>setValue(String, Object)</code> that 
 * maps to the Configurable's native setters.
 * 
 * @author Bryan Tripp
 */
public class ConfigurationImpl implements Configuration {

	private Configurable myConfigurable;
	private Map<String, Property> myProperties;
	
	/**
	 * @param configurable The Configurable to which this Configuration belongs
	 */
	public ConfigurationImpl(Configurable configurable) {
		myConfigurable = configurable;
		myProperties = new HashMap<String, Property>(20);
	}
	
	/**
	 * @see ca.neo.model.Configuration#getConfigurable()
	 */
	public Configurable getConfigurable() {
		return myConfigurable;
	}

	/**
	 * To be called by the associated Configurable, immediately after construction (once 
	 * per property). 
	 *   
	 * @param property The new Property 
	 */
	public void defineProperty(Property property) {
		myProperties.put(property.getName(), property);
	}
	
	public SingleValuedProperty defineSingleValuedProperty(String name, Class c, boolean mutable) {		
		SingleValuedProperty property = new SingleValuedProperty(this, name, c, mutable);
		myProperties.put(name, property);
		return property;
	}
	
	public TemplateProperty defineTemplateProperty(String name, Class c, boolean multiValued, boolean fixedCardinality) {
		TemplateProperty property = new TemplateProperty(this, name, c, multiValued, fixedCardinality);
		myProperties.put(name, property);
		return property;
	}
	
	public TemplateProperty defineTemplateProperty(String name, Class c, Object defaultValue) {
		TemplateProperty property = new TemplateProperty(this, name, c, defaultValue);
		myProperties.put(name, property);
		return property;
	}
	
	/**
	 * @see ca.neo.model.Configuration#getPropertyNames()
	 */
	public List<String> getPropertyNames() {
		return new ArrayList<String>(myProperties.keySet());
	}

	/**
	 * @see ca.neo.model.Configuration#getProperty(java.lang.String)
	 */
	public Property getProperty(String name) throws StructuralException {
		if (myProperties.containsKey(name)) {
			return myProperties.get(name);
		} else {
			throw new StructuralException("The property " + name + " is unknown");
		}
	}

	private static abstract class BaseProperty implements Property {
		
		private Configuration myConfiguration;
		private String myName;
		private Class myClass;
		private boolean myMutable;
		
		public BaseProperty(Configuration configuration, String name, Class c, boolean mutable) {
			if (!Configurable.class.isAssignableFrom(c) && !MainHandler.getInstance().canHandle(c)) {
				throw new IllegalArgumentException("No handler for property type " + c.getName());
			}
				
			myConfiguration = configuration;
			myName = name;
			myClass = c;	
			myMutable = mutable;
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#getName()
		 */
		public String getName() {
			return myName;
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#getType()
		 */
		public Class getType() {
			return myClass;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#isMutable()
		 */
		public boolean isMutable() {
			return myMutable;
		}
		
		protected Configuration getConfiguration() {
			return myConfiguration;
		}

		/**
		 * Returns getValue() be default. 
		 */
		public Object getDefaultValue() {
			return getValue();
		}		
		

	}
	
	
	
	public static class TemplateProperty extends BaseProperty {

		private boolean myMultiValued;
		private boolean myFixedCardinality;
		private List<Object> myValues;
		
		public TemplateProperty(Configuration configuration, String name, Class c, boolean multiValued, boolean fixedCardinality) {
			super(configuration, name, c, true);
			
			myMultiValued = multiValued;
			myFixedCardinality = fixedCardinality;
			myValues = new ArrayList<Object>(10);
		}
		
		/**
		 * A shortcut for creating a single-valued template property with a default value. 
		 *     
		 * @param configuration
		 * @param name
		 * @param c
		 * @param defaultValue
		 */
		public TemplateProperty(Configuration configuration, String name, Class c, Object defaultValue) {
			super(configuration, name, c, true);
			
			myMultiValued = false;
			myFixedCardinality = true;
			myValues = new ArrayList<Object>(10);
			myValues.add(defaultValue);
		}

		/**
		 * @see ca.neo.model.Configuration.Property#addValue(java.lang.Object)
		 */
		public void addValue(Object value) throws StructuralException {
			if (myMultiValued && !myFixedCardinality) {
				checkClass(value);
				myValues.add(value);
			}
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getNumValues()
		 */
		public int getNumValues() {
			return myValues.size();
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue()
		 */
		public Object getValue() {
			return (myValues.size() > 0) ? myValues.get(0) : null;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue(int)
		 */
		public Object getValue(int index) throws StructuralException {
			try {
				return myValues.get(index);
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value " + index + " doesn't exist", e);
			}
		}

		/**
		 * @see ca.neo.model.Configuration.Property#insert(int, java.lang.Object)
		 */
		public void insert(int index, Object value) throws StructuralException {
			if (myMultiValued && !myFixedCardinality) {
				checkClass(value);
				try {
					myValues.add(value);					
				} catch (IndexOutOfBoundsException e) {
					throw new StructuralException("Value " + index + " doesn't exist", e);
				}
			}			
		}

		/**
		 * @see ca.neo.model.Configuration.Property#isFixedCardinality()
		 */
		public boolean isFixedCardinality() {
			return myFixedCardinality;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#isMultiValued()
		 */
		public boolean isMultiValued() {
			return myMultiValued;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#remove(int)
		 */
		public void remove(int index) throws StructuralException {
			if (myMultiValued && !myFixedCardinality) {
				try {
					myValues.remove(index);					
				} catch (IndexOutOfBoundsException e) {
					throw new StructuralException("Value " + index + " doesn't exist", e);
				}
			}			
		}

		/**
		 * @see ca.neo.model.Configuration.Property#setValue(java.lang.Object)
		 */
		public void setValue(Object value) throws StructuralException {
			checkClass(value);
			myValues.set(0, value);
		}

		/**
		 * @see ca.neo.model.Configuration.Property#setValue(int, java.lang.Object)
		 */
		public void setValue(int index, Object value) throws StructuralException {
			checkClass(value);
			try {
				myValues.set(index, value);					
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value " + index + " doesn't exist", e);
			}
		}
		
		private void checkClass(Object value) throws StructuralException {
			if (!getType().isAssignableFrom(value.getClass())) {
				throw new StructuralException("Value must be of type " + getType() 
						+ " (was " + value.getClass().getName() + ")");
			}
		}
		
	}
	
	public static abstract class FixedCardinalityProperty extends MultiValuedProperty implements Property {

		public FixedCardinalityProperty(Configuration configuration, String name, Class c, boolean mutable) {
			super(configuration, name, c, mutable);
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#isFixedCardinality()
		 */
		public boolean isFixedCardinality() {
			return true;
		}


		@Override
		public void addValue(Object value) throws StructuralException {
			throw new StructuralException("Can't add a value after construction; "
					+ getName() + " has exactly " + getNumValues() + " values");
		}

		@Override
		public void doInsert(int index, Object value) throws StructuralException {
			throw new StructuralException("Can't insert a value after construction; "
					+ getName() + " has exactly " + getNumValues() + " values");
		}

		@Override
		public void doRemove(int index) throws StructuralException {
			throw new StructuralException("Can't remove a value after construction; "
					+ getName() + " has exactly " + getNumValues() + " values");
		}

	}
	
	public static abstract class MultiValuedProperty extends BaseProperty implements Property {
		
//		private List<Object> myValues;

		public MultiValuedProperty(Configuration configuration, String name, Class c, boolean mutable) {
			super(configuration, name, c, mutable);
//			myValues = new ArrayList<Object>(10);
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getNumValues()
		 */
		public abstract int getNumValues();
//		public int getNumValues() {
//			return myValues.size();
//		}

		/**
		 * @see ca.neo.model.Configuration.Property#isMultiValued()
		 */
		public boolean isMultiValued() {
			return true;
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#isFixedCardinality()
		 */
		public boolean isFixedCardinality() {
			return false;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue()
		 */
		public Object getValue() {
			try {
				return (getNumValues() > 0) ? getValue(0) : null;
			} catch (StructuralException e) {
				throw new RuntimeException("There are " + getNumValues() 
						+ " values but there was a problem returning the first (this may be a bug)", e);
			}
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue(int)
		 */
		public Object getValue(int index) throws StructuralException {
			try {
//				return myValues.get(index);			
				return doGetValue(index);			 
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value #" + index + " doesn't exist", e);
			}
		}
		
		public abstract Object doGetValue(int index) throws StructuralException;

		/**
		 * @see ca.neo.model.Configuration.Property#addValue(java.lang.Object)
		 */
		public abstract void addValue(Object value) throws StructuralException;
		
//		public void addValueCompleted(Object value) {
//			myValues.add(value);
//			Event event = new EventImpl(getConfiguration().getConfigurable(), this, Type.ADD, myValues.size());
//			getConfiguration().notifyListeners(event);			
//		}

		/**
		 * @see ca.neo.model.Configuration.Property#insert(int, java.lang.Object)
		 */
		public void insert(int index, Object value) throws StructuralException {
			try {
				doInsert(index, value);				
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value " + index + " doesn't exist", e);
			}
		}
		
		public abstract void doInsert(int index, Object value) throws IndexOutOfBoundsException, StructuralException;
		
//		public void insertCompleted(int index, Object value) {
//			myValues.add(index, value);
//			Event event = new EventImpl(getConfiguration().getConfigurable(), this, Type.INSERT, index);
//			getConfiguration().notifyListeners(event);
//		}

		/**
		 * @see ca.neo.model.Configuration.Property#remove(int)
		 */
		public void remove(int index) throws StructuralException {
			try {
				doRemove(index);				
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value " + index + " doesn't exist", e);
			}
		}
		
		public abstract void doRemove(int index) throws IndexOutOfBoundsException, StructuralException;
		
//		public void removeCompleted(int index) {
//			myValues.remove(index);
//			Event event = new EventImpl(getConfiguration().getConfigurable(), this, Type.REMOVE, myValues.size());
//			getConfiguration().notifyListeners(event);			
//		}

		/**
		 * @see ca.neo.model.Configuration.Property#setValue(java.lang.Object)
		 */
		public void setValue(Object value) throws StructuralException {
			setValue(0, value);
		}

		/**
		 * Calls doSetValue(...) and wraps any resulting {@link IndexOutOfBoundsException} in 
		 * a {@link StructuralException}. 
		 *  
		 * @see ca.neo.model.impl.ConfigurationImpl.MultiValuedProperty#setValue(int, java.lang.Object)
		 */
		public void setValue(int index, Object value) throws StructuralException {
			try {
				doSetValue(index, value);
			} catch (IndexOutOfBoundsException e) {
				throw new StructuralException("Value " + index + " doesn't exist", e);
			}
		}
		
		public abstract void doSetValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException;
		
		/**
		 * To be called by Configurable after a value has been set. Updates local field and 
		 * notifies listeners. 
		 * 
		 * @param value Value that has been set
		 */
//		public void setValueCompleted(int index, Object value) {
//			myValues.set(index, value);
//			Event event = new EventImpl(getConfiguration().getConfigurable(), this, Type.CHANGE, index);
//			getConfiguration().notifyListeners(event);
//		}
	}
	
	/**
	 * Default implementation of single-valued Properties. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class SingleValuedProperty extends BaseProperty implements Property {
		
		public SingleValuedProperty(Configuration configuration, String name, Class c, boolean mutable) {
			super(configuration, name, c, mutable);
		}

		/**
		 * @see ca.neo.model.Configuration.Property#addValue(java.lang.Object)
		 */
		public void addValue(Object value) throws StructuralException {
			throw new StructuralException("Can't add a value to the single-valued property " + getName());
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#getNumValues()
		 */
		public int getNumValues() {
			return 1;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue()
		 */
		public Object getValue() {
			Object result = null;
			
			Configurable c = getConfiguration().getConfigurable();
			String methodName = "get" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
			
			try {
				Method method = c.getClass().getMethod(methodName, new Class[0]);
				result = method.invoke(c, new Object[0]);
			} catch (Exception e) {
				throw new RuntimeException("Can't get property", e);
			}
			
			return result; 
		}

		/**
		 * @see ca.neo.model.Configuration.Property#getValue(int)
		 */
		public Object getValue(int index) throws StructuralException {
			if (index == 0) {
				return getValue();
			} else {
				throw new StructuralException("Value " + index + " of the single-valued property " + getName() + " does not exist");
			}
		}

		/**
		 * @see ca.neo.model.Configuration.Property#insert(int, java.lang.Object)
		 */
		public void insert(int index, Object value) throws StructuralException {
			throw new StructuralException("Can't add a value to the single-valued property " + getName());
		}

		/**
		 * @see ca.neo.model.Configuration.Property#isMultiValued()
		 */
		public boolean isMultiValued() {
			return false;
		}
		
		/**
		 * @see ca.neo.model.Configuration.Property#isFixedCardinality()
		 */
		public boolean isFixedCardinality() {
			return true;
		}

		/**
		 * @see ca.neo.model.Configuration.Property#remove(int)
		 */
		public void remove(int index) throws StructuralException {
			throw new StructuralException("Can't remove a value from the single-valued property " + getName());
		}

		/**
		 * By default, attempts to call method setX(y) on Configurable, where X is the name of the property (with 
		 * first letter capitalized) and y is the value (changed to a primitive if it's a primitive wrapper).
		 * A Configurable that needs different behaviour should override this method.   
		 *  
		 * @see ca.neo.model.Configuration.Property#setValue(java.lang.Object)
		 */
		public void setValue(Object value) throws StructuralException {
			if (getType().isAssignableFrom(value.getClass())) {
				Configurable c = getConfiguration().getConfigurable();
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
					Method method = c.getClass().getMethod(methodName, new Class[]{argClass});
					method.invoke(c, value);
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
		
		/**
		 * To be called by Configurable after a value has been set. Updates local field and 
		 * notifies listeners. 
		 * 
		 * @param value Value that has been set
		 */
//		public void setValueCompleted(Object value) {
//			myValue = value;
//			Event event = new EventImpl(getConfiguration().getConfigurable(), this, Type.CHANGE, 0);
//			getConfiguration().notifyListeners(event);
//		}

		/**
		 * @see ca.neo.model.Configuration.Property#setValue(int, java.lang.Object)
		 */
		public void setValue(int index, Object value) throws StructuralException {
			if (index == 0) {
				setValue(value);
			} else {
				throw new StructuralException("Can't set value " + index + " of the single-valued property " + getName());
			}
		}
		
	}
	
	/**
	 * Default implementation of Event. 
	 * 
	 * @author Bryan Tripp
	 */
//	private static class EventImpl implements Event {
//
//		private Configurable myConfigurable;
//		private Property myProperty;
//		private Type myType;
//		private int myLocation;
//		
//		public EventImpl(Configurable configurable, Property property, Type type, int location) {
//			myConfigurable = configurable;
//			myProperty = property;
//			myType = type;
//			myLocation = location;
//		}
//		
//		/**
//		 * @see ca.neo.model.Configuration.Event#getConfigurable()
//		 */
//		public Configurable getConfigurable() {
//			return myConfigurable;
//		}
//
//		/**
//		 * @see ca.neo.model.Configuration.Event#getEventType()
//		 */
//		public Type getEventType() {
//			return myType;
//		}
//
//		/**
//		 * @see ca.neo.model.Configuration.Event#getLocationOfChange()
//		 */
//		public int getLocationOfChange() {
//			return myLocation;
//		}
//
//		/**
//		 * @see ca.neo.model.Configuration.Event#getProperty()
//		 */
//		public Property getProperty() {
//			return myProperty;
//		}
//		
//	}

}
