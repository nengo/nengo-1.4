/*
 * Created on 9-Dec-07
 */
package ca.neo.config.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configurable;
import ca.neo.config.Configuration;
import ca.neo.config.ListProperty;
import ca.neo.config.MainHandler;
import ca.neo.config.NamedValueProperty;
import ca.neo.config.Property;
import ca.neo.config.SingleValuedProperty;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.EnsembleImpl;
import ca.neo.model.impl.MockConfigurable;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.model.nef.impl.NEFEnsembleImpl;
import ca.neo.model.neuron.impl.LIFSpikeGenerator;
import ca.neo.model.neuron.impl.LinearSynapticIntegrator;
import ca.neo.model.neuron.impl.PlasticExpandableSpikingNeuron;
import ca.neo.model.neuron.impl.SpikingNeuron;
import ca.neo.util.MU;

/**
 * Data model underlying JTree user interface for a Configurable.
 * 
 * DONE (tuesday) add, remove, set for map parameters; test against plastic 
 * DONE (tuesday): handle array values in ConfigurationTreeModel & NewConfigurableDialog; default to empty array
 * DONE (wed) expand Plastic interface to allow configuration
 * DONE (wed): support float[] and float[][] size changes
 * DONE (wed): constructor arg names and docs from source
 * DONE (thurs): support primitives in ListProperty or limit Property use to objects 
 * TODO (thurs): clean up configuration code  
 * TODO (friday): remove Configurable 
 * TODO (friday): augment model classes with getters
 * 
 * @author Bryan Tripp
 */
public class ConfigurationTreeModel implements TreeModel {

	private Value myRoot;
	private List<TreeModelListener> myListeners;
		
	public ConfigurationTreeModel(Object configurable) {
		myRoot = new Value(0, configurable);
		myListeners = new ArrayList<TreeModelListener>(5);
	}
	
	public void addValue(Object source, TreePath parentPath, Object value, String name) {
		try {
			Object parent = parentPath.getLastPathComponent();
			if (parent instanceof ListProperty) {
				ListProperty property = (ListProperty) parent; 
				property.addValue(value);
				
				TreeModelEvent event = new TreeModelEvent(source, parentPath, new int[]{property.getNumValues()-1}, new Object[]{value});
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeNodesInserted(event);
				}
			} else if (parent instanceof NamedValueProperty) {
				NamedValueProperty property = (NamedValueProperty) parent;
				property.setValue(name, value);
				refresh(source, parentPath);
			} else {
				throw new RuntimeException("Can't add child to a " + parent.getClass().getName());
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}		
	}
	
	public void refresh(Object source, TreePath path) {
		TreeModelEvent event = new TreeModelEvent(source, path);
		for (int i = 0; i <myListeners.size(); i++) {
			myListeners.get(i).treeStructureChanged(event);						
		}
	}
	
	//path to node to insert before
	public void insertValue(Object source, TreePath path, Object value) {
		try {
			Object parent = path.getParentPath().getLastPathComponent();
			if (parent instanceof ListProperty && path.getLastPathComponent() instanceof Value) {
				ListProperty property = (ListProperty) parent;
				Value toInsertBefore = (Value) path.getLastPathComponent();
				property.insert(toInsertBefore.getIndex(), value);
				
				Value node = new Value(toInsertBefore.getIndex(), value);
				TreeModelEvent insertEvent = new TreeModelEvent(source, path.getParentPath(), 
						new int[]{toInsertBefore.getIndex()}, new Object[]{node});
				TreeModelEvent changeEvent = getIndexUpdateEvent(source, path.getParentPath(), 
						toInsertBefore.getIndex()+1, property.getNumValues());
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeNodesInserted(insertEvent);
					myListeners.get(i).treeNodesChanged(changeEvent);
				}
				
			} else {
				throw new RuntimeException("Can't set value on child of " + parent.getClass().getName());
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}	

	//creates an event to update a range of child indices with given parent    
	private TreeModelEvent getIndexUpdateEvent(Object source, TreePath parentPath, int fromIndex, int toIndex) {
		Object parent = parentPath.getLastPathComponent();
		int[] changedIndices = new int[toIndex-fromIndex];
		Object[] changedValues = new Object[changedIndices.length];
		for (int i = 0; i < changedIndices.length; i++) {
			changedIndices[i] = fromIndex + i;
			changedValues[i] = getChild(parent, changedIndices[i]); //creates new Value object with correct index
		}
		return new TreeModelEvent(source, parentPath, changedIndices, changedValues);
	}
	
	public void setValue(Object source, TreePath path, Object value) throws StructuralException {
		Object parent = path.getParentPath().getLastPathComponent();
		if (parent instanceof Property && path.getLastPathComponent() instanceof Value) {
			int index = ((Value) path.getLastPathComponent()).getIndex();
			
			if (parent instanceof SingleValuedProperty) {
				((SingleValuedProperty) parent).setValue(value);
			} else if (parent instanceof ListProperty) {
				((ListProperty) parent).setValue(index, value);				
			} else if (parent instanceof NamedValueProperty) {
				String name = ((Value) path.getLastPathComponent()).getName();
				((NamedValueProperty) parent).setValue(name, value);
			}
			
			Value child = (Value) path.getLastPathComponent();
			child.setObject(value);
			
			if (child.getObject() instanceof Configurable) {
				TreeModelEvent event = new TreeModelEvent(source, path);
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeStructureChanged(event);						
				}
			} else {
				TreePath shortPath = new TreePath(new Object[]{parent, child});
				TreeModelEvent event = new TreeModelEvent(source, shortPath, new int[]{index}, new Object[]{child});
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeNodesChanged(event);
				}
			}
		} else {
			throw new RuntimeException("Can't set value on child of " 
					+ parent.getClass().getName() + " (this is probably a bug).");
		}
	}
	
	public void removeValue(Object source, TreePath path) {
		try {
			Object parent = path.getParentPath().getLastPathComponent();
			if (path.getLastPathComponent() instanceof Value && (parent instanceof ListProperty || parent instanceof NamedValueProperty)) {
				Value toRemove = (Value) path.getLastPathComponent();
				int numValues = 0;
				
				if (parent instanceof ListProperty) {
					ListProperty property = (ListProperty) parent;
					property.remove(toRemove.getIndex());
					numValues = property.getNumValues();
				} else if (parent instanceof NamedValueProperty) {
					NamedValueProperty property = (NamedValueProperty) parent;
					property.removeValue(toRemove.getName());
					numValues = property.getValueNames().size();
				}
				
				TreeModelEvent removeEvent = new TreeModelEvent(source, path.getParentPath(), 
						new int[]{toRemove.getIndex()}, new Object[]{toRemove});
				TreeModelEvent changeEvent = getIndexUpdateEvent(source, path.getParentPath(), 
						toRemove.getIndex(), numValues);
				for (int i = 0; i < myListeners.size(); i++) {
					myListeners.get(i).treeNodesRemoved(removeEvent);
					myListeners.get(i).treeNodesChanged(changeEvent);					
				}	
			} else {
				throw new RuntimeException("Can't remove child of " 
						+ parent.getClass().getName() + " (this is probably a bug)");
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
//		System.out.println("called getChild() with " + parent.getClass().getName());
		Object result = null;
		try {
			if (parent instanceof Value) {
				Configuration c = ((Value) parent).getConfiguration();
				if (c != null) {
					List<String> propertyNames = c.getPropertyNames();
					Collections.sort(propertyNames);
					result = c.getProperty(propertyNames.get(index));
				}
			} else if (parent instanceof ListProperty) {
				ListProperty p = (ListProperty) parent;
				Object o = p.getValue(index);
				result = new Value(index, o); 
			} else if (parent instanceof NamedValueProperty) {
				NamedValueProperty p = (NamedValueProperty) parent;
				String name = p.getValueNames().get(index);
				Object o = p.getValue(name);
				result = new Value(index, o);
				((Value) result).setName(name);
			} else if (parent instanceof SingleValuedProperty) {
				if (index == 0) {
					Object o = ((SingleValuedProperty) parent).getValue();
					result = new Value(index, o);
				} else {
					ConfigExceptionHandler.handle(
							new StructuralException("SingleValuedProperty doesn't have child " + index), 
							ConfigExceptionHandler.DEFAULT_BUG_MESSAGE, null);
				}
			}
		} catch (StructuralException e) {
			ConfigExceptionHandler.handle(e, ConfigExceptionHandler.DEFAULT_BUG_MESSAGE, null);
		}
		
//		System.out.println("Child " + index + " of " + parent.toString() + ": " + result);
		return result;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {		
		int result = 0;
		
		if (parent instanceof Value) {
			Configuration configuration = ((Value) parent).getConfiguration(); 
			if (configuration != null) result = configuration.getPropertyNames().size();
//			System.out.println("Child count for " + ((Value) parent).getObject().getClass().getName() + ": " + result);
		} else if (parent instanceof SingleValuedProperty) {
			result = 1; 
		} else if (parent instanceof ListProperty) {
			result = ((ListProperty) parent).getNumValues();
		} else if (parent instanceof NamedValueProperty) {
			result = ((NamedValueProperty) parent).getValueNames().size();
		}
		
		return result;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		int index = -1;
		
		try {
			if (child instanceof Property) {
				Configuration c = ((Value) parent).getConfiguration();
				Property p = (Property) child;
				List<String> propertyNames = c.getPropertyNames();
				Collections.sort(propertyNames);
				index = propertyNames.indexOf(p.getName());
			} else if (parent instanceof SingleValuedProperty) {
				SingleValuedProperty p = (SingleValuedProperty) parent;
				Value v = (Value) child;
				if (p.getValue() != null && p.getValue().equals(v.getObject())) {
					index = 0;
				}
			} else if (parent instanceof ListProperty) {
				ListProperty p = (ListProperty) parent;
				Value v = (Value) child;
				for (int i = 0; i < p.getNumValues() && index == -1; i++) {
					if (p.getValue(i) != null && p.getValue(i).equals(v.getObject())) index = i;
				}
			} else if (parent instanceof NamedValueProperty) {
				NamedValueProperty p = (NamedValueProperty) parent;
				String name = ((Value) child).getName();				
				for (int i = 0; i < p.getValueNames().size() && index == -1; i++) {
					System.out.println(i + " of " + p.getValueNames().size() + " name " + name + ": " + p.getValueNames().get(i));
					if (p.getValueNames().get(i).equals(name)) index = i;
				}				
			}
		} catch (StructuralException e) {
			ConfigExceptionHandler.handle(e, ConfigExceptionHandler.DEFAULT_BUG_MESSAGE, null);
		}
		
		return index;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return myRoot;
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object o) {
		return ( !(o instanceof Value && ((Value) o).getConfiguration() != null) && !(o instanceof Property) );
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		myListeners.add(listener);
	}

	/**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener listener) {
		myListeners.remove(listener);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		if (newValue instanceof Value) {
			TreeModelEvent event = new TreeModelEvent(null, path.getParentPath(), 
					new int[]{((Value) newValue).getIndex()}, new Object[]{newValue});
			for (int i = 0; i < myListeners.size(); i++) {
				myListeners.get(i).treeNodesChanged(event);
			}
		}
	}
	
	public static class Value {
		
		private int myIndex;
		private Object myObject;
		private String myName;
		private Configuration myConfiguration;
		
		public Value(int index, Object object) {
			myIndex = index;
			myObject = (object == null) ? new NullValue() : object;
			
			if (object != null && !MainHandler.getInstance().canHandle(object.getClass())) {
				myConfiguration = ConfigUtil.getConfiguration(object);
			}
		}
		
		public int getIndex() {
			return myIndex;
		}
		
		public void setIndex(int index) {
			myIndex = index;
		}
		
		public void setObject(Object o) {
			myObject = o;
		}
		
		public Object getObject() {
			return myObject;
		}
		
		public String getName() {
			return myName;
		}
		
		public void setName(String name) {
			myName = name;
		}
		
		public Configuration getConfiguration() {
			return myConfiguration;
		}
	}
	
	public static class NullValue {
		public String toString() {
			return "NULL";
		}
	}
	
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame("Tree Test"); 
//			MockConfigurable configurable = new MockConfigurable(MockConfigurable.getConstructionTemplate());
//			configurable.addMultiValuedField("test1");
//			configurable.addMultiValuedField("test2");
//			Object configurable = new SpikingNeuron(new LinearSynapticIntegrator(.001f, Units.ACU), 
//					new LIFSpikeGenerator(.001f, .02f, .001f), 15, 0, "neuron");
			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
			NEFEnsembleImpl configurable = (NEFEnsembleImpl) ef.make("test", 100, 2);
			configurable.addDecodedTermination("decoded", MU.I(2), .005f, false);
			configurable.addTermination("composite", MU.zero(100, 1), .005f, false);
//			EnsembleImpl configurable = new EnsembleImpl("test", new Node[]{
//					new PlasticExpandableSpikingNeuron(new LinearSynapticIntegrator(), new LIFSpikeGenerator(), 1, 0, "foo")
//			});
//			configurable.addTermination("term", new float[][]{new float[]{1}}, .005f, false);
			
			ConfigurationTreeModel model = new ConfigurationTreeModel(configurable); 
			final JTree tree = new JTree(model);
			tree.setEditable(true); 
			tree.setCellEditor(new ConfigurationTreeCellEditor(tree));
			tree.addMouseListener(new ConfigurationTreePopupListener(tree, model));
			tree.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					Object selected = (tree.getSelectionPath() == null) ? null : tree.getSelectionPath().getLastPathComponent();
					if (e.getKeyCode() == 112 && selected instanceof Property) {
						String documentation = ((Property) selected).getDocumentation(); 
						if (documentation != null) ConfigUtil.showHelp(documentation);
					}
				}
			});
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
