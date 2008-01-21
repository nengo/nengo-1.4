/*
 * Created on 10-Dec-07
 */
package ca.neo.config.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ca.neo.config.Configurable;
import ca.neo.config.ListProperty;
import ca.neo.config.MainHandler;
import ca.neo.config.Property;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.StructuralException;

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
	
	private void maybeShowPopup(final MouseEvent event) {
		if (event.isPopupTrigger() && event.getComponent().equals(myTree)) {
			final TreePath path = myTree.getPathForLocation(event.getX(), event.getY());
			myTree.setSelectionPath(path);
			
			JPopupMenu popup = new JPopupMenu();
			if (path.getLastPathComponent() instanceof ListProperty) {
				final ListProperty p = (ListProperty) path.getLastPathComponent();
				if (!p.isFixedCardinality()) {
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
				if (p.isMutable() /**&& Configurable.class.isAssignableFrom(p.getType())**/) {
					final JMenuItem replaceValueItem = new JMenuItem("Replace");
					replaceValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Class currentType = ((Value) path.getLastPathComponent()).getObject().getClass();
							Object o = NewConfigurableDialog.showDialog(replaceValueItem, p.getType(), currentType);
							if (o != null) {
								try {
									myModel.setValue(this, path, o);
								} catch (StructuralException ex) {
									ConfigExceptionHandler.handle(ex, ex.getMessage(), event.getComponent());
								}
							}
						}
					});
					popup.add(replaceValueItem);									
				}
				
				if (p instanceof ListProperty && !p.isFixedCardinality()) {
					JMenuItem insertValueItem = new JMenuItem("Insert");
					insertValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							myModel.insertValue(this, path, ((ListProperty) p).getDefaultValue());
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
			
			if (path.getLastPathComponent() instanceof Value) {
				final Value v = (Value) path.getLastPathComponent();
				JMenuItem displayItem = new JMenuItem("Display");
				displayItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println("index: " + v.getIndex());
					}
				});
				popup.add(displayItem);
			}
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}
	
	
}
