/*
 * Created on 10-Dec-07
 */
package ca.neo.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ca.neo.config.ConfigurationTreeModel.LeafNode;
import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
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
			Object o = myTree.getPathForLocation(me.getX(), me.getY()).getLastPathComponent();
			if ( !(o instanceof Configurable) && !(o instanceof Configuration.Property)) {
				result = true;
			}
		}
		
		return result;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component result = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		
		final TreePath path = tree.getPathForRow(row);
		Object parent = tree.getPathForRow(row).getParentPath().getLastPathComponent();		
		if (path.getParentPath().getLastPathComponent() instanceof Property && value instanceof LeafNode) {
			final ConfigurationTreeModel model = (ConfigurationTreeModel) tree.getModel();
			Property property = (Property) path.getParentPath().getLastPathComponent();
			LeafNode node = (LeafNode) value;
			if (Integer.class.isAssignableFrom(property.getType())) {
				final JTextField tf = new JTextField(ConfigurationConfiguration.getInstance().getDisplayText(node.getObject()));
				tf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							model.setValue(this, path, new Integer(tf.getText()));
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						}
						ConfigurationTreeCellEditor.this.stopCellEditing();
					}
				});
				result = tf;
			}
		}
		
		return result;
	}
	
	

}
