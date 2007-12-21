/*
 * Created on 9-Dec-07
 */
package ca.neo.config.ui;

import java.awt.BorderLayout;
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

import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.Configuration.Property;
import ca.neo.model.impl.MockConfigurable;

/**
 * Data model underlying JTree user interface for a Configurable.
 * 
 * @author Bryan Tripp
 */
public class ConfigurationTreeModel implements TreeModel {

	private Value myRoot;
	private List<TreeModelListener> myListeners;
		
	public ConfigurationTreeModel(Configurable configurable) {
		myRoot = new Value(0, configurable);
		myListeners = new ArrayList<TreeModelListener>(5);
	}
	
	public void addValue(Object source, TreePath parentPath, Object value) {
		try {
			Object parent = parentPath.getLastPathComponent();
			if (parent instanceof Property) {
				Property property = (Property) parent; 
				property.addValue(value);
				
				TreeModelEvent event = new TreeModelEvent(source, parentPath, new int[]{property.getNumValues()-1}, new Object[]{value});
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeNodesInserted(event);
				}
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
			if (parent instanceof Property && path.getLastPathComponent() instanceof Value) {
				Property property = (Property) parent;
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
	
	public void setValue(Object source, TreePath path, Object value) {
		try {
			Object parent = path.getParentPath().getLastPathComponent();
			if (parent instanceof Property && path.getLastPathComponent() instanceof Value) {
				Property property = (Property) parent;
				int index = ((Value) path.getLastPathComponent()).getIndex();
				property.setValue(index, value);
				
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
				throw new RuntimeException("Can't set value on child of " + parent.getClass().getName());
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}		
	}
	
	public void removeValue(Object source, TreePath path) {
		try {
			Object parent = path.getParentPath().getLastPathComponent();
			if (parent instanceof Property && path.getLastPathComponent() instanceof Value) {
				Property property = (Property) parent;
				Value toRemove = (Value) path.getLastPathComponent();
				property.remove(toRemove.getIndex());
				
				TreeModelEvent removeEvent = new TreeModelEvent(source, path.getParentPath(), 
						new int[]{toRemove.getIndex()}, new Object[]{toRemove});
				TreeModelEvent changeEvent = getIndexUpdateEvent(source, path.getParentPath(), 
						toRemove.getIndex(), property.getNumValues());
				for (int i = 0; i < myListeners.size(); i++) {
					myListeners.get(i).treeNodesRemoved(removeEvent);
					myListeners.get(i).treeNodesChanged(changeEvent);					
				}	

			} else {
				throw new RuntimeException("Can't set value on child of " + parent.getClass().getName());
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		Object result = null;
		try {
			if (parent instanceof Value && ((Value) parent).getObject() instanceof Configurable) {
				Configuration c = ((Configurable) ((Value) parent).getObject()).getConfiguration();
				List<String> propertyNames = c.getPropertyNames();
				Collections.sort(propertyNames);
				result = c.getProperty(propertyNames.get(index));
			} else if (parent instanceof Property) {
				Property p = (Property) parent;
				Object o = p.getValue(index);
				result = new Value(index, o); //TODO: adjust indices with insertions and removals
			}			
		} catch (StructuralException e) {
			ConfigExceptionHandler.handle(e, ConfigExceptionHandler.DEFAULT_BUG_MESSAGE, null);
		}
		
		return result;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		int result = 0;
		
		if (parent instanceof Value && ((Value) parent).getObject() instanceof Configurable) {
			result = ((Configurable) ((Value) parent).getObject()).getConfiguration().getPropertyNames().size();
		} else if (parent instanceof Property) {
			result = ((Property) parent).getNumValues();
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
				Configuration c = ((Configurable) ((Value) parent).getObject()).getConfiguration();
				Property p = (Property) child;
				List<String> propertyNames = c.getPropertyNames();
				Collections.sort(propertyNames);
				index = propertyNames.indexOf(p.getName());
			} else if (parent instanceof Property) {
				Property p = (Property) parent;
				for (int i = 0; i < p.getNumValues() && index == -1; i++) {
					if (p.getValue(i).equals(child)) index = i;
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
		return ( !(o instanceof Value && ((Value) o).getObject() instanceof Configurable) && !(o instanceof Property) );
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
		
		public Value(int index, Object object) {
			myIndex = index;
			myObject = object;
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
	}
	
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame("Tree Test");
			MockConfigurable configurable = new MockConfigurable(MockConfigurable.getConstructionTemplate());
			configurable.addMultiValuedField("test1");
			configurable.addMultiValuedField("test2");
			
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

		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

}
