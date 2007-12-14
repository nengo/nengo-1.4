/*
 * Created on 9-Dec-07
 */
package ca.neo.config;

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
 * TODO: tooltips stopped working -- did I mess with the listeners?  
 * 
 * @author Bryan Tripp
 */
public class ConfigurationTreeModel implements TreeModel {

	private Configurable myRoot;
	private List<TreeModelListener> myListeners;
		
	public ConfigurationTreeModel(Configurable configurable) {
		myRoot = configurable;
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
	
//	public void insertValue(Property parent, int index, Object value) {
//		
//	}
//	

	public void setValue(Object source, TreePath path, Object value) {
		try {
			Object parent = path.getParentPath().getLastPathComponent();
			if (parent instanceof Property 
					&& (path.getLastPathComponent() instanceof LeafNode || path.getLastPathComponent() instanceof Configurable)) {
				Property property = (Property) parent;
				int index = ((LeafNode) path.getLastPathComponent()).getIndex();
				property.setValue(index, value);
				
				LeafNode child = (LeafNode) path.getLastPathComponent();
				child.setObject(value);
				
				TreePath foo = new TreePath(new Object[]{parent, child});
				TreeModelEvent event = new TreeModelEvent(source, foo, new int[]{index}, new Object[]{child});
				for (int i = 0; i <myListeners.size(); i++) {
					myListeners.get(i).treeNodesChanged(event);
				}
			} else {
				throw new RuntimeException("Can't set value on child of " + parent.getClass().getName());
			}
		} catch (StructuralException e) {
			e.printStackTrace();
		}		
	}
//	
//	public void removeValue(Property parent, int index) {
//		
//	}
	
	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		Object result = null;
		try {
			if (parent instanceof Configurable) {
				Configuration c = ((Configurable) parent).getConfiguration();
				List<String> propertyNames = c.getPropertyNames();
				Collections.sort(propertyNames);
				result = c.getProperty(propertyNames.get(index));
			} else if (parent instanceof Property) {
				Property p = (Property) parent;
				Object o = p.getValue(index);
				if (o instanceof Configurable) {
					result = (Configurable) o;
				} else { 
					result = new LeafNode(index, o); //TODO: adjust indices with insertions and removals
				}	
			}			
		} catch (StructuralException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		int result = 0;
		
		if (parent instanceof Configurable) {
			result = ((Configurable) parent).getConfiguration().getPropertyNames().size();
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
			if (parent instanceof Configurable && child instanceof Property) {
				Configuration c = ((Configurable) parent).getConfiguration();
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
			throw new RuntimeException(e);
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
		return ( !(o instanceof Configurable) && !(o instanceof Property) );
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

	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
	}
	
	public static class LeafNode {
		
		private int myIndex;
		private Object myObject;
		
		public LeafNode(int index, Object object) {
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
