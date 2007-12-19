/*
 * Created on 10-Dec-07
 */
package ca.neo.config.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ca.neo.config.MainHandler;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.Configurable;
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
			if (me.getClickCount() > 1) {
				TreePath path = myTree.getPathForLocation(me.getX(), me.getY()); 
				if (path.getLastPathComponent() instanceof Value 
						&& !(((Value) path.getLastPathComponent()).getObject() instanceof Configurable) ) {
					Object o = path.getParentPath().getLastPathComponent();
					if (o instanceof Property && ((Property) o).isMutable()) {
						result = true;					
					}
				}				
			}
		}
		
		return result;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component result = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		
		TreePath path = tree.getPathForRow(row);
		if (path.getParentPath().getLastPathComponent() instanceof Property && value instanceof Value) {
//			Property property = (Property) path.getParentPath().getLastPathComponent();
			Value node = (Value) value;
//			if (result instanceof JTextComponent) {
//				((JTextComponent) result).setText(ConfigurationConfiguration.getInstance().getDisplayText(node.getObject()));
//			}
			
			ConfigurationChangeListener listener = new ConfigurationChangeListener(tree, path);;
			Component customEditor = MainHandler.getInstance().getEditor(node.getObject(), listener);
			if (customEditor != null) result = customEditor;
		}
		
		return result;
	}

}
