/*
 * Created on 6-Dec-07
 */
package ca.neo.model.impl;

import java.util.List;

import ca.neo.TestUtil;
import ca.neo.config.Configurable;
import ca.neo.config.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.MockConfigurable.MockChildConfigurable;
import junit.framework.TestCase;

/**
 * Unit tests for ConfigurationImpl. 
 * 
 * @author Bryan Tripp
 */
public class ConfigurationImplTest extends TestCase {

	private MockConfigurable myConfigurable;
	
	protected void setUp() throws Exception {
		super.setUp();
		myConfigurable = new MockConfigurable(MockConfigurable.getConstructionTemplate());
	}

	public void testGetConfigurable() {
		assertEquals(myConfigurable, myConfigurable.getConfiguration().getConfigurable());
	}

//	public void testListeners() {
//		MockListener listener = new MockListener();
//		
//		myConfigurable.setIntField(2);
//		assertEquals(0, listener.getValue());
//		
//		myConfigurable.getConfiguration().addListener(listener);
//		myConfigurable.setIntField(3);
//		assertEquals(3, listener.getValue());
//		
//		myConfigurable.getConfiguration().removeListener(listener);
//		myConfigurable.setIntField(4);
//		assertEquals(3, listener.getValue());
//	}

	public void testGetPropertyNames() {
		List<String> names = myConfigurable.getConfiguration().getPropertyNames();
		assertEquals(12, names.size());
		assertTrue(names.contains("intField"));
		assertTrue(names.contains("floatField"));
	}

	public void testIntProperty() throws StructuralException {
		assertEquals(myConfigurable.getIntField(), ((Integer) myConfigurable.getConfiguration().getProperty("intField").getValue()).intValue());
		myConfigurable.setIntField(2);
		assertEquals(2, ((Integer) myConfigurable.getConfiguration().getProperty("intField").getValue()).intValue());
		myConfigurable.getConfiguration().getProperty("intField").setValue(new Integer(3));
		assertEquals(3, myConfigurable.getIntField());
		
		try {
			myConfigurable.getConfiguration().getProperty("intField").setValue("wrong");
			fail("Should have thrown exception");
		} catch (StructuralException e) {}
		
		try {
			myConfigurable.getConfiguration().getProperty("intField").addValue(new Integer(1));
			fail("Should have thrown exception");
		} catch (StructuralException e) {}
	}
	
	public void testFloatProperty() throws StructuralException {
		TestUtil.assertClose(myConfigurable.getFloatField(), ((Float) myConfigurable.getConfiguration().getProperty("floatField").getValue()).floatValue(), .0001f);
		myConfigurable.setFloatField(2);
		TestUtil.assertClose(2, ((Float) myConfigurable.getConfiguration().getProperty("floatField").getValue()).floatValue(), .0001f);
		myConfigurable.getConfiguration().getProperty("floatField").setValue(new Float(3));
		TestUtil.assertClose(3, myConfigurable.getFloatField(), .0001f);
	}
	
	public void testBooleanProperty() throws StructuralException {
		assertEquals(myConfigurable.getBooleanField(), ((Boolean) myConfigurable.getConfiguration().getProperty("booleanField").getValue()).booleanValue());
		myConfigurable.setBooleanField(false);
		assertEquals(false, ((Boolean) myConfigurable.getConfiguration().getProperty("booleanField").getValue()).booleanValue());
		myConfigurable.getConfiguration().getProperty("booleanField").setValue(new Boolean(true));
		assertEquals(true, myConfigurable.getBooleanField());
	}
	
	//TODO: test remaining fields 
	
	public void testConstruction() throws StructuralException {
		Configuration template = MockConfigurable.getConstructionTemplate();
		template.getProperty("immutableField").setValue("custom");
		MockConfigurable c = new MockConfigurable(template);
		assertEquals("custom", c.getImmutableField());
	}
	
	public void testChild() throws StructuralException {
		MockChildConfigurable c = new MockChildConfigurable(MockChildConfigurable.getConstructionTemplate());
		assertEquals("foo", c.getImmutableField());
		
		c.getConfiguration().getProperty("field").setValue("foo");
		assertEquals("foo", c.getField());
	}

//	private static class MockListener implements Configuration.Listener {
//
//		private int myValue = 0;
//		
//		public void configurationChange(Event event) {
//			myValue = ((Integer) event.getProperty().getValue()).intValue();
//		}
//		
//		public int getValue() {
//			return myValue;
//		}
//	}
//
}
