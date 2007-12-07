package ca.neo.model;

import java.util.List;

/**
 * <p>Contains all the variable parameters of a Configurable object.</p> 
 * 
 * <p>Parameters can only be of the following classes:</p> 
 * 
 *  <ul>
 *  <li>Integer</li>
 *  <li>Float</li>
 *  <li>String</li>
 *  <li>Boolean</li>
 *  <li>float[]</li>
 *  <li>float[][]</li>
 *  <li>Configurable</li>
 *  <li>SimulationMode</li>
 *  <li>Units</li>
 *  </ul>
 *  
 * @author Bryan Tripp
 */
public interface Configuration {
	
	/**
	 * @return The Configurable to which this Configuration belongs
	 */
	public Configurable getConfigurable();

	/**
	 * @return Names of configuration properties 
	 */
	public List<String> getPropertyNames();
	
	/**
	 * @param name Name of a configuration property
	 * @return Parameter of the given name
	 * @throws StructuralException if the named property does not exist
	 */
	public Property getProperty(String name) throws StructuralException;
//	
//	/**
//	 * @param name Parameter name
//	 * @return Type of the named parameter 
//	 * @throws StructuralException
//	 */
//	public Class getType(String name) throws StructuralException;
//	
//	/**
//	 * @param name Parameter name
//	 * @return True iff the named parameter can be changed after the Configurable is first created
//	 * @throws StructuralException
//	 */
//	public boolean getMutable(String name) throws StructuralException;
//	
//	/**
//	 * @param name Parameter name
//	 * @return Value of the named parameter
//	 * @throws StructuralException
//	 */
//	public Object getValue(String name) throws StructuralException;
//	
//	/**
//	 * @param name Parameter name
//	 * @param value New value of named parameter
//	 * @throws StructuralException
//	 */
//	public void setValue(String name, Object value) throws StructuralException;
	
	/**
	 * @param listener Listener to add
	 */
	public void addListener(Configuration.Listener listener);

	/**
	 * @param listener Listener ro remove
	 */
	public void removeListener(Configuration.Listener listener);
	
	public void notifyListeners(Event event);
	
	
	
	/**
	 * A configuration property. Properties have restricted classes (see Configuration docs). 
	 * Properties can have multiple values. 
	 *  
	 * @author Bryan Tripp
	 */
	public static interface Property {

		/**
		 * @return Property name
		 */
		public String getName();
		
		/**
		 * @return Class to which values belong
		 */
		public Class getType();
		
		/**
		 * @return True if values can be changed after construction of the Configurable
		 */
		public boolean isMutable();
		
		/**
		 * @return True if the property can have multiple values   
		 */
		public boolean isMultiValued();
		
		/**
		 * @return True if the property has a fixed number of values 
		 */
		public boolean isFixedCardinality();
		
		/**
		 * @return Value (for single-valued properties) or first value (for multi-valued properties)
		 */
		public Object getValue();
		
		/**
		 * @param value New value (for single-valued properties) or first value (for multi-valued properties)
		 * @throws StructuralException if the given value is not one of the allowed classes, or if the 
		 * 		Configurable rejects it for any other reason (eg inconsistency with other properties)
		 */
		public void setValue(Object value) throws StructuralException;
		
		/**
		 * @param index Index of a certain single value of a multi-valued property 
		 * @return The value at the given index
		 * @throws StructuralException if the given index is out of range
		 */
		public Object getValue(int index) throws StructuralException;
		
		/**
		 * @param index Index of a certain single value of a multi-valued property 
		 * @param value New value to replace that at the given index 
		 * @throws StructuralException if the value is invalid (as in setValue) or the given index is 
		 * 		out of range 
		 */
		public void setValue(int index, Object value) throws StructuralException;

		/**
		 * @param value New value to be added to the end of the list 
		 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
		 * 		not multi-valued 
		 */
		public void addValue(Object value) throws StructuralException;
		
		/**
		 * @return Number of repeated values of this Property
		 */
		public int getNumValues();
		
		/**
		 * @param index Index at which new value is to be inserted  
		 * @param value New value
		 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
		 * 		not multi-valued or the index is out of range 
		 */
		public void insert(int index, Object value) throws StructuralException;
		
		/**
		 * @param index Index of a single value of a multi-valued property that is to be removed
		 * @throws StructuralException if the given index is out of range or the Property is immutable
		 */
		public void remove(int index) throws StructuralException;
		
	}

	
	
	/**
	 * A listener of changes to a Configuration. 
	 *  
	 * @author Bryan Tripp
	 */
	public static interface Listener {

		/**
		 * @param event A description of the change. 
		 */
		public void configurationChange(Event event);		
	}
	
	
	
	/**
	 * A Configuration change event. 
	 * 
	 * @author Bryan Tripp
	 */
	public static interface Event {
		
		public enum Type { CHANGE, ADD, INSERT, REMOVE };

		/**
		 * @return The Configurable that has been changed
		 */
		public Configurable getConfigurable();
		
//		/**
//		 * @return The name of the changed parameter
//		 */
//		public String getParameterName();
//		
//		/**
//		 * @return The new value of the changed parameter 
//		 */
//		public Object getParameterValue();

		/**
		 * @return The updated Property
		 */
		public Property getProperty();

		/**
		 * @return Type of operation performed
		 */
		public Type getEventType();
		
		/**
		 * @return Index of removed, added, inserted, or changed value (for multi-valued Properties)
		 */
		public int getLocationOfChange();
		
	}

}
