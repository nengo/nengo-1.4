/*
 * Created on 10-Dec-07
 */
package ca.neo.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import ca.neo.config.ConfigurationTreeModel.LeafNode;
import ca.neo.model.Configuration.Property;

public class ConfigurationTreeCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	
	private JTree myTree;
	
	public ConfigurationTreeCellEditor(JTree tree) {
		super(new JTextField());
		myTree = tree;
	}

	@Override
	public boolean isCellEditable(EventObject e) {
		boolean result = false;
		
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			TreePath path = myTree.getPathForLocation(me.getX(), me.getY()); 
			if (path.getLastPathComponent() instanceof LeafNode) {
				Object o = path.getParentPath().getLastPathComponent();
				if (o instanceof Property && ((Property) o).isMutable()) {
					result = true;					
				}
			}
		}
		
		return result;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component result = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		
		TreePath path = tree.getPathForRow(row);
		if (path.getParentPath().getLastPathComponent() instanceof Property && value instanceof LeafNode) {
			Property property = (Property) path.getParentPath().getLastPathComponent();
			LeafNode node = (LeafNode) value;
			if (result instanceof JTextComponent) {
				((JTextComponent) result).setText(ConfigurationConfiguration.getInstance().getDisplayText(node.getObject()));
			}
			Component customEditor = ConfigurationConfiguration.getInstance().getEditor(property.getType(), node.getObject(), tree, path);
			if (customEditor != null) result = customEditor;
		}
		
		return result;
	}
	
	public static abstract class ConfigurationChangeListener implements ActionListener {
		
		private ConfigurationTreeModel myModel;
		private TreeCellEditor myEditor;
		private TreePath myPath;
		
		public ConfigurationChangeListener(JTree tree, TreePath path) {
			myModel = (ConfigurationTreeModel) tree.getModel();
			myEditor = tree.getCellEditor();
			myPath = path;
		}
		
		public abstract Object getValue() throws Exception;

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				myModel.setValue(this, myPath, getValue());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			myEditor.stopCellEditing();
		}
		
	}

}
