/*
 * Created on 16-Jan-08
 */
package ca.neo.config.impl;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import ca.neo.config.ListProperty;
import ca.neo.config.ui.ConfigurationTreeCellEditor;
import ca.neo.config.ui.ConfigurationTreeCellRenderer;
import ca.neo.config.ui.ConfigurationTreeModel;
import ca.neo.config.ui.ConfigurationTreePopupListener;
import ca.neo.model.StructuralException;

import junit.framework.TestCase;

public class ListPropertyImplTest extends TestCase {

	private MockObject myObject;
	private ConfigurationImpl myConfiguration;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		myObject = new MockObject();
		myConfiguration = new ConfigurationImpl(myObject);
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "A", String.class));
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "B", String.class));
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "C", String.class));
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "D", String.class));
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "E", String.class));
		myConfiguration.defineProperty(ListPropertyImpl.getListProperty(myConfiguration, "F", String.class));
		assertTrue(ListPropertyImpl.getListProperty(myConfiguration, "G", String.class) == null);
	}

	private ListProperty getProperty(String name) throws StructuralException {
		return (ListProperty) myConfiguration.getProperty(name);
	}

	public void testGetNumValues() throws StructuralException {
		assertEquals(1, getProperty("A").getNumValues());
		assertEquals(1, getProperty("B").getNumValues());
		assertEquals(1, getProperty("C").getNumValues());
		assertEquals(2, getProperty("D").getNumValues());
		assertEquals(1, getProperty("E").getNumValues());
		assertEquals(1, getProperty("F").getNumValues());
	}

	public void testGetValue() throws StructuralException {
		assertEquals("1", getProperty("A").getValue(0));
		assertEquals("1", getProperty("B").getValue(0));
		assertEquals("1", getProperty("C").getValue(0));
		assertEquals("1", getProperty("D").getValue(0));
		assertEquals("2", getProperty("D").getValue(1));
		assertEquals("1", getProperty("E").getValue(0));
		assertEquals("1", getProperty("F").getValue(0));
	}

	public void testSetValue() throws StructuralException {
		getProperty("A").setValue(0, "1a");
		getProperty("B").setValue(0, "1a");
		getProperty("E").setValue(0, "1a");
		getProperty("F").setValue(0, "1a");
		
		assertEquals("1a", getProperty("A").getValue(0));
		assertEquals("1a", getProperty("B").getValue(0));
		assertEquals("1a", getProperty("E").getValue(0));
		assertEquals("1a", getProperty("F").getValue(0));
		
		try {
			getProperty("C").setValue(0, "1a");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("D").setValue(0, "1a");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("D").setValue(1, "2a");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
	}

	public void testAddValue() throws StructuralException {
		try {
			getProperty("A").addValue("2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		getProperty("B").addValue("2");
		assertEquals(2, getProperty("B").getNumValues());
		assertEquals("2", getProperty("B").getValue(1));
		
		try {
			getProperty("C").addValue("2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("D").addValue("2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("E").addValue("2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
	
		getProperty("F").addValue("2");
		assertEquals(2, getProperty("F").getNumValues());
		assertEquals("2", getProperty("F").getValue(1));
	}

	public void testInsert() throws StructuralException {
		try {
			getProperty("A").insert(0, "2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		getProperty("B").insert(0, "2");
		assertEquals(2, getProperty("B").getNumValues());
		assertEquals("2", getProperty("B").getValue(0));
		assertEquals("1", getProperty("B").getValue(1));
		
		try {
			getProperty("C").insert(0, "2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("D").insert(0, "2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("E").insert(0, "2");
			fail("Should have throw exception");
		} catch (StructuralException e) {}
	
		getProperty("F").insert(0, "2");
		assertEquals(2, getProperty("F").getNumValues());
		assertEquals("2", getProperty("F").getValue(0));
		assertEquals("1", getProperty("F").getValue(1));
	}

	public void testRemove() throws StructuralException {
		try {
			getProperty("A").remove(0);
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		getProperty("B").remove(0);
		assertEquals(0, getProperty("B").getNumValues());
		
		try {
			getProperty("C").remove(0);
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("D").remove(0);
			fail("Should have throw exception");
		} catch (StructuralException e) {}
		
		try {
			getProperty("E").remove(0);
			fail("Should have throw exception");
		} catch (StructuralException e) {}
	
		getProperty("F").remove(0);
		assertEquals(0, getProperty("F").getNumValues());
	}

	public void testIsFixedCardinality() throws StructuralException {
		assertEquals(true, getProperty("A").isFixedCardinality());
		assertEquals(false, getProperty("B").isFixedCardinality());
		assertEquals(true, getProperty("C").isFixedCardinality());
		assertEquals(true, getProperty("D").isFixedCardinality());
		assertEquals(true, getProperty("E").isFixedCardinality());
		assertEquals(false, getProperty("F").isFixedCardinality());
	}
	
	public void testIsMutable() throws StructuralException {
		assertEquals(true, getProperty("A").isMutable());
		assertEquals(true, getProperty("B").isMutable());
		assertEquals(false, getProperty("C").isMutable());
		assertEquals(false, getProperty("D").isMutable());
		assertEquals(true, getProperty("E").isMutable());
		assertEquals(true, getProperty("F").isMutable());		
	}
	
	private static class MockObject {
		
		private List<String> myA; //fixed cardinality
		private List<String> myB; //not fixed cardinality
		private List<String> myC; //immutable
		private String[] myD; //immutable array
		private String[] myE; //mutable array
		private List<String> myF; //exposed list 
		
		public MockObject() {
			myA = new ArrayList<String>(10);
			myA.add("1");
			
			myB = new ArrayList<String>(10);
			myB.add("1");
			
			myC = new ArrayList<String>(10);
			myC.add("1");

			myD = new String[]{"1", "2"};
			myE = new String[]{"1"};
			
			myF = new ArrayList<String>(10);
			myF.add("1");
		}
		
		public String getA(int index) {
			return myA.get(index);
		}
		
		public void setA(int index, String val) {
			myA.set(index, val);
		}
		
		public int getNumA() {
			return myA.size();
		}
		
		public String getB(int index) {
			return myB.get(index);
		}
		
		public void setB(int index, String val) {
			myB.set(index, val);
		}
		
		public int getNumB() {
			return myB.size();
		}
		
		public void addB(String val) {
			myB.add(val);
		}
		
		public void removeB(int index) {
			myB.remove(index);
		}
		
		public void insertB(int index, String val) {
			myB.add(index, val);
		}
		
		public String getC(int index) {
			return myC.get(index);
		}
		
		public int getNumC() {
			return myC.size();
		}
		
		public String getD(int index) {
			return myD[index];
		}
		
		public String[] getAllD() {
			return myD;
		}
		
//		public String getE(int index) {
//			return myE[index];
//		}
		
		public String[] getEs() {
			return myE;
		}
		
		public void setEs(String[] vals) {
			myE = vals;
		}
		
		public List<String> getF() {
			return myF;
		}
		
	}

	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame("Tree Test");
			Object configurable = new MockObject();
			
			ConfigurationTreeModel model = new ConfigurationTreeModel(configurable); 
			JTree tree = new JTree(model);
			tree.setEditable(true); 
			tree.setCellEditor(new ConfigurationTreeCellEditor(tree));
			tree.addMouseListener(new ConfigurationTreePopupListener(tree, model));
			ConfigurationTreeCellRenderer cellRenderer = new ConfigurationTreeCellRenderer();
			tree.setCellRenderer(cellRenderer);
			
			ToolTipManager.sharedInstance().registerComponent(tree);
			
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
			
			frame.pack();
			frame.setVisible(true);
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent arg0) {
					System.exit(0);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
