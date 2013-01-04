package ca.nengo.model.impl;

import ca.nengo.config.Configuration;
import ca.nengo.config.SingleValuedProperty;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.MockConfigurable.MockChildConfigurable;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class ConfigurationImplTest {

	private MockConfigurable myConfigurable = new MockConfigurable(MockConfigurable.getConstructionTemplate());
	
	public ConfigurationImplTest() throws StructuralException {
	}

	@Test
	public void testGetConfigurable() {
		assertEquals(myConfigurable, myConfigurable.getConfiguration().getConfigurable());
	}

	@Test
	public void testGetPropertyNames() {
		List<String> names = myConfigurable.getConfiguration().getPropertyNames();
		assertEquals(12, names.size());
		assertTrue(names.contains("intField"));
		assertTrue(names.contains("floatField"));
	}

	@Test
	public void testIntProperty() throws StructuralException {
		assertEquals(myConfigurable.getIntField(), ((Integer) getSVProperty("intField").getValue()).intValue());
		myConfigurable.setIntField(2);
		assertEquals(2, ((Integer) getSVProperty("intField").getValue()).intValue());
		getSVProperty("intField").setValue(Integer.valueOf(3));
		assertEquals(3, myConfigurable.getIntField());
		
		try {
			getSVProperty("intField").setValue("wrong");
			fail("Should have thrown exception");
		} catch (StructuralException e) {}
	}
	
	@Test
	public void testFloatProperty() throws StructuralException {
		assertEquals(myConfigurable.getFloatField(), ((Float) getSVProperty("floatField").getValue()).floatValue(), .0001f);
		myConfigurable.setFloatField(2);
		assertEquals(2, ((Float) getSVProperty("floatField").getValue()).floatValue(), .0001f);
		getSVProperty("floatField").setValue(new Float(3));
		assertEquals(3, myConfigurable.getFloatField(), .0001f);
	}
	
	@Test
	public void testBooleanProperty() throws StructuralException {
		assertEquals(myConfigurable.getBooleanField(), ((Boolean) getSVProperty("booleanField").getValue()).booleanValue());
		myConfigurable.setBooleanField(false);
		assertEquals(false, ((Boolean) getSVProperty("booleanField").getValue()).booleanValue());
		getSVProperty("booleanField").setValue(Boolean.valueOf(true));
		assertEquals(true, myConfigurable.getBooleanField());
	}
	
	//TODO: test remaining fields 
	
	@Test
	public void testConstruction() throws StructuralException {
		Configuration template = MockConfigurable.getConstructionTemplate();
		((SingleValuedProperty) template.getProperty("immutableField")).setValue("custom");
		MockConfigurable c = new MockConfigurable(template);
		assertEquals("custom", c.getImmutableField());
	}
	
	@Test
	public void testChild() throws StructuralException {
		MockChildConfigurable c = new MockChildConfigurable(MockChildConfigurable.getConstructionTemplate());
		assertEquals("foo", c.getImmutableField());
		
		((SingleValuedProperty) c.getConfiguration().getProperty("field")).setValue("foo");
		assertEquals("foo", c.getField());
	}
	
	private SingleValuedProperty getSVProperty(String name) throws StructuralException {
		return (SingleValuedProperty) myConfigurable.getConfiguration().getProperty(name);
	}
}
