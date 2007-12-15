/*
 * Created on 6-Dec-07
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.List;

import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.ConfigurationImpl.FixedCardinalityProperty;
import ca.neo.model.impl.ConfigurationImpl.MultiValuedProperty;
import ca.neo.model.impl.ConfigurationImpl.SingleValuedProperty;

/**
 * A dummy Configurable class for testing purposes.
 * 
 * TODO: should go to underlying variable in Parameter.getValue() -- synchronization bugs will be confusing
 * TODO: do we really need listeners? can the UI check periodically? maybe not. refresh on select? listeners optional? listeners separate?
 * TODO: can we get rid of list methods if we don't need listeners?  
 * 
 * @author Bryan Tripp
 */
public class MockConfigurable implements Configurable {
	private int myIntField;
	private float myFloatField;
	private boolean myBooleanField;
	private String myStringField;
	private float[] myFloatArrayField;
	private float[][] myFloatArrayArrayField;
	private SimulationMode mySimulationModeField;
	private Units myUnitsField;
	private Configurable myConfigurableField;
	
	private final String myImmutableField;
	private final List<String> myMultiValuedField;
	private final String[] myFixedCardinalityField;
	
	private SingleValuedProperty myIntProperty;
	private SingleValuedProperty myFloatProperty;
	private SingleValuedProperty myBooleanProperty;
	private SingleValuedProperty myStringProperty;
	private SingleValuedProperty myFloatArrayProperty;
	private SingleValuedProperty myFloatArrayArrayProperty;
	private SingleValuedProperty mySimulationModeProperty;
	private SingleValuedProperty myUnitsProperty;
	private SingleValuedProperty myConfigurableProperty;
	private MultiValuedProperty myMultiValuedProperty;
	private FixedCardinalityProperty myFixedCardinalityProperty;
	
	private ConfigurationImpl myConfiguration;
	
	public MockConfigurable(Configuration immutableProperties) throws StructuralException {
		myImmutableField = immutableProperties.getProperty("immutableField").getValue().toString();
		
		myIntField = 1;
		myFloatField = 1;
		myBooleanField = true;
		myStringField = "test";
		myFloatArrayField = new float[]{1, 2};
		myFloatArrayArrayField = new float[][]{new float[]{1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new float[]{3, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
		mySimulationModeField = SimulationMode.DEFAULT;
		myUnitsField = Units.UNK;
		myConfigurableField = new MockLittleConfigurable(); 
		
		myMultiValuedField = new ArrayList<String>(10);
		myFixedCardinalityField = new String[]{"test1", "test2"};
		
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty("immutableField", String.class, false, myImmutableField);
		
		myIntProperty = myConfiguration.defineSingleValuedProperty("intField", Integer.class, true, new Integer(myIntField));
		myFloatProperty = myConfiguration.defineSingleValuedProperty("floatField", Float.class, true, new Float(myFloatField));
		myBooleanProperty = myConfiguration.defineSingleValuedProperty("booleanField", Boolean.class, true, new Boolean(myBooleanField));
		myStringProperty = myConfiguration.defineSingleValuedProperty("stringField", String.class, true, myStringField);
		myFloatArrayProperty = myConfiguration.defineSingleValuedProperty("floatArrayField", float[].class, true, myFloatArrayField);
		myFloatArrayArrayProperty = myConfiguration.defineSingleValuedProperty("floatArrayArrayField", float[][].class, true, myFloatArrayArrayField);
		mySimulationModeProperty = myConfiguration.defineSingleValuedProperty("simulationModeField", SimulationMode.class, true, mySimulationModeField);
		myUnitsProperty = myConfiguration.defineSingleValuedProperty("unitsField", Units.class, true, myUnitsField);
		myConfigurableProperty = myConfiguration.defineSingleValuedProperty("configurableField", Configurable.class, true, myConfigurableField);
		myConfiguration.defineSingleValuedProperty("immutableField", String.class, false, myImmutableField);
		
		myMultiValuedProperty = new ConfigurationImpl.MultiValuedProperty(myConfiguration, "multiValuedField", String.class, true) {
			@Override
			public void addValue(Object value) throws StructuralException {
				myMultiValuedField.add((String) value); 
				addValueCompleted(value);
			}

			@Override
			public void doInsert(int index, Object value) throws StructuralException {
				myMultiValuedField.add(index, (String) value);
				insertCompleted(index, value);
			}

			@Override
			public void doRemove(int index) throws StructuralException {
				myMultiValuedField.remove(index);
				removeCompleted(index);
			}

			@Override
			public void doSetValue(int index, Object value) throws StructuralException {
				myMultiValuedField.set(index, (String) value);
				setValueCompleted(index, value);
			}
		};
		myConfiguration.defineProperty(myMultiValuedProperty);
		
		myFixedCardinalityProperty = new ConfigurationImpl.FixedCardinalityProperty(myConfiguration, "fixedCardinalityField", String.class, true) {
			@Override
			public void doSetValue(int index, Object value) throws StructuralException {
				myFixedCardinalityField[index] = (String) value;
			}
		};
		myConfiguration.defineProperty(myFixedCardinalityProperty);
		
	}
	
	public static Configuration getConstructionTemplate() {
		ConfigurationImpl template = new ConfigurationImpl(null);
		template.defineTemplateProperty("immutableField", String.class, "immutable");
		return template;
	}

	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}
	
	public String getImmutableField() {
		return myImmutableField;
	}
	
	public void setIntField(int val) {
		myIntField = val;
		myIntProperty.setValueCompleted(val);
	}
	
	public int getIntField() {
		return myIntField;
	}
	
	public void setFloatField(float val) {
		myFloatField = val;
		myFloatProperty.setValueCompleted(val);
	}
	
	public float getFloatField() {
		return myFloatField;
	}
	
	public void setBooleanField(boolean val) {
		myBooleanField = val;
		myBooleanProperty.setValueCompleted(val);
	}
	
	public boolean getBooleanField() {
		return myBooleanField;
	}

	public void setStringField(String val) {
		myStringField = val;
		myStringProperty.setValueCompleted(val);
	}
	
	public String getStringField() {
		return myStringField;
	}

	public void setFloatArrayField(float[] val) {
		myFloatArrayField = val;
		myFloatArrayProperty.setValueCompleted(val);
	}
	
	public float[] getFloatArrayField() {
		return myFloatArrayField;
	}

	public void setFloatArrayArrayField(float[][] val) {
		myFloatArrayArrayField = val;
		myFloatArrayArrayProperty.setValueCompleted(val);
	}
	
	public float[][] getFloatArrayArrayField() {
		return myFloatArrayArrayField;
	}

	public void setSimulationModeField(SimulationMode val) {
		mySimulationModeField = val;
		mySimulationModeProperty.setValueCompleted(val);		
	}
	
	public SimulationMode getSimulationModeField() {
		return mySimulationModeField;
	}

	public void setUnitsField(Units val) {
		myUnitsField = val;
		myUnitsProperty.setValueCompleted(val);
	}
	
	public Units getUnitsField() {
		return myUnitsField;
	}

	public void setConfigurableField(Configurable val) {
		myConfigurableField = val;
		myConfigurableProperty.setValueCompleted(val);
	}
	
	public Configurable getConfigurableField() {
		return myConfigurableField;
	}

	public List<String> getMultiValuedField() {
		return new ArrayList<String>(myMultiValuedField);
	}
	
	public void setMultiValuedField(int index, String val) {
		myMultiValuedField.set(index, val);
		myMultiValuedProperty.setValueCompleted(index, val);		
	}
	
	public void addMultiValuedField(String val) {
		myMultiValuedField.add(val);
		myMultiValuedProperty.addValueCompleted(val);
	}
	
	public void addMultiValuedField(int index, String val) {
		myMultiValuedField.add(index, val);
		myMultiValuedProperty.insertCompleted(index, val);
	}
	
	public void removeMultiValuedField(int index) {
		myMultiValuedField.remove(index);
		myMultiValuedProperty.removeCompleted(index);
	}
	
	public String[] getFixedCardinalityField() {
		String[] result = new String[myFixedCardinalityField.length];
		System.arraycopy(myFixedCardinalityField, 0, result, 0, result.length);
		return result;
	}
	
	public void setFixedCardinalityField(int index, String val) {
		myFixedCardinalityField[index] = val;
		myFixedCardinalityProperty.setValueCompleted(index, val);
	}
	
	/**
	 * A simple dummy Configurable for nesting in MockConfigurable. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class MockLittleConfigurable implements Configurable {

		private String myField;
		private ConfigurationImpl myConfiguration;
		private SingleValuedProperty myProperty;
		
		public MockLittleConfigurable() {
			myField = "test";
			myConfiguration = new ConfigurationImpl(this);
			try {
				myProperty = myConfiguration.defineSingleValuedProperty("field", String.class, true, myField);
			} catch (StructuralException e) {
				throw new RuntimeException(e);
			}
			
		}
		
		public Configuration getConfiguration() {
			return myConfiguration;
		}
		
		public void setField(String value) {
			myField = value;
			myProperty.setValueCompleted(value);
		}
		
		public String getField() {
			return myField;
		}
		
	}

	/**
	 * A child of MockConfigurable. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class MockChildConfigurable extends MockConfigurable {

		private String myField;
		
		public MockChildConfigurable(Configuration immutableProperties) throws StructuralException {
			super(addParentProperties(immutableProperties));
			myField = "test";
			((ConfigurationImpl) getConfiguration()).defineSingleValuedProperty("field", String.class, true, myField);
		}
		
		private static Configuration addParentProperties(Configuration configuration) {
			((ConfigurationImpl) configuration).defineTemplateProperty("immutableField", String.class, "foo");
			return configuration;
		}
		
		public static Configuration getConstructionTemplate() {
			ConfigurationImpl template = new ConfigurationImpl(null);
			template.defineTemplateProperty("immutableFoo", String.class, "foo");
			return template;
		}
		
		public void setField(String val) {
			myField = val;			
		}
		
		public String getField() {
			return myField;
		}
		
	}
	
}