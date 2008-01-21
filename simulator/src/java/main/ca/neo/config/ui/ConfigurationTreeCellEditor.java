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

import ca.neo.config.Configurable;
import ca.neo.config.MainHandler;
import ca.neo.config.Property;
import ca.neo.config.ui.ConfigurationTreeModel.NullValue;
import ca.neo.config.ui.ConfigurationTreeModel.Value;

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
				if (path.getLastPathComponent() instanceof Value) {
					Object o = ((Value) path.getLastPathComponent()).getObject();
					Class c = o.getClass();
					
					Object parent = null;
					if (path.getPath().length > 1) {
						parent = path.getParentPath().getLastPathComponent();
					}
					
					if (o instanceof NullValue && parent != null && parent instanceof Property) {
						c = ((Property) parent).getType();
					}
					
					if (MainHandler.getInstance().canHandle(c)) {						
						if (parent instanceof Property && ((Property) parent).isMutable()) {
							result = true;					
						}						
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
			Value node = (Value) value;
			Object o = node.getObject();
			
			if (o instanceof NullValue) {
				Class type = ((Property) path.getParentPath().getLastPathComponent()).getType();
				o = MainHandler.getInstance().getDefaultValue(type);
			}
			
			ConfigurationChangeListener listener = new ConfigurationChangeListener(tree, path);
			Component customEditor = MainHandler.getInstance().getEditor(o, listener);
			if (customEditor != null) result = customEditor;
		}
		
		return result;
	}

}
