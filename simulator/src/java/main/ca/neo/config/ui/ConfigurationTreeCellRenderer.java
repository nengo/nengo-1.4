/*
 * Created on 10-Dec-07
 */
package ca.neo.config.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import ca.neo.config.IconRegistry;
import ca.neo.config.MainHandler;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.Configurable;
import ca.neo.model.Configuration;

/**
 * Renderer for cells in a configuration tree. 
 * 
 * @author Bryan Tripp
 */
public class ConfigurationTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component result = this;
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		Icon icon = getCustomIcon(value);
		if (icon != null) setIcon(icon);
		
		if (value instanceof Value && ((Value) value).getObject() instanceof Configurable) {
			setText(((Value) value).getObject().getClass().getSimpleName());
			setToolTipText(((Value) value).getObject().getClass().getCanonicalName());
		} else if (value instanceof Configuration.Property) {
			Configuration.Property property = (Configuration.Property) value;
			
			StringBuffer text = new StringBuffer(property.getName());
			text.append(" (");
			text.append(property.getType().getSimpleName());
			text.append(")");
			setText(text.toString());
			
			setToolTipText(null);
		} else if (value instanceof Value) {
			Object o = ((Value) value).getObject();
			Component customRenderer = MainHandler.getInstance().getRenderer(o);
			if (customRenderer == null) {
				setText(o.toString()); //ConfigurationConfiguration.getInstance().getDisplayText(o));
				setToolTipText(null);							
			} else {
				customRenderer.setBackground(sel ? new Color(.1f, .4f, .7f, .2f) : Color.WHITE);
				result = customRenderer;
			}
		} else {
			setToolTipText(value.getClass().getCanonicalName());
		}
		
		return result;
	}
	
	private Icon getCustomIcon(Object node) {
		Object value = (node instanceof Value) ? ((Value) node).getObject() : node;
		return IconRegistry.getInstance().getIcon(value);
	}
	
}
