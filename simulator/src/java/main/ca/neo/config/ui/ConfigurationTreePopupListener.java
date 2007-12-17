/*
 * Created on 10-Dec-07
 */
package ca.neo.config.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

//import ca.neo.config.ConfigurationTreeModel.PropertyNode;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.Configurable;
import ca.neo.model.StructuralException;
import ca.neo.model.Configuration.Property;

public class ConfigurationTreePopupListener extends MouseAdapter {
	
	private JTree myTree;
	private final ConfigurationTreeModel myModel;
	
	public ConfigurationTreePopupListener(JTree tree, ConfigurationTreeModel model) {
		myTree = tree;
		myModel = model;			
	}

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger() && e.getComponent().equals(myTree)) {
			final TreePath path = myTree.getPathForLocation(e.getX(), e.getY());
			myTree.setSelectionPath(path);
			
			JPopupMenu popup = new JPopupMenu();
			if (path.getLastPathComponent() instanceof Property) {
				final Property p = (Property) path.getLastPathComponent();
				if (p.isMultiValued() && !p.isFixedCardinality()) {
					JMenuItem addValueItem = new JMenuItem("Add");
					addValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							//TODO: how to construct / replace non-configurables? clone?
							myModel.addValue(this, path, p.getDefaultValue());
						}
					});
					popup.add(addValueItem);				
				}
			} else if (path.getParentPath() != null && path.getParentPath().getLastPathComponent() instanceof Property) {
				final Property p = (Property) path.getParentPath().getLastPathComponent();
				if (Configurable.class.isAssignableFrom(p.getType())) {
					final JMenuItem replaceValueItem = new JMenuItem("Replace");
					replaceValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Class currentType = ((Value) path.getLastPathComponent()).getObject().getClass();
							Configurable c = NewConfigurableDialog.showDialog(replaceValueItem, p.getType(), currentType);
							if (c != null) myModel.setValue(this, path, c);
						}
					});
					popup.add(replaceValueItem);									
				}
				
				if (p.isMultiValued() && !p.isFixedCardinality()) {
					JMenuItem insertValueItem = new JMenuItem("Insert");
					insertValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							myModel.insertValue(this, path, p.getDefaultValue());
						}
					}); 
					popup.add(insertValueItem);				
					JMenuItem removeValueItem = new JMenuItem("Remove");
					removeValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							myModel.removeValue(this, path);
						}
					});
					popup.add(removeValueItem);				
				}
				
			}
			JMenuItem refreshItem = new JMenuItem("Refresh");
			refreshItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myModel.refresh(this, path);
				}
			});
			popup.add(refreshItem);
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	
}
