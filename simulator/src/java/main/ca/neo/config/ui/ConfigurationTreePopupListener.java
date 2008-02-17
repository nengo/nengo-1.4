/*
 * Created on 10-Dec-07
 */
package ca.neo.config.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ca.neo.config.ConfigUtil;
import ca.neo.config.ListProperty;
import ca.neo.config.MainHandler;
import ca.neo.config.NamedValueProperty;
import ca.neo.config.Property;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.StructuralException;

/**
 * Creates a popup menu for configuration tree nodes, to allow refreshing, adding/setting/removing, etc. 
 * as appropriate. 
 * 
 * @author Bryan Tripp
 */
public class ConfigurationTreePopupListener extends MouseAdapter {
	
	private JTree myTree;
	private final ConfigurationTreeModel myModel;
	
	/**
	 * @param tree A tree that displays a Configuration
	 * @param model TreeModel underlying the above tree
	 */
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
		final TreePath path = myTree.getPathForLocation(event.getX(), event.getY());		
		if (event.isPopupTrigger() && event.getComponent().equals(myTree) && path != null) {
			myTree.setSelectionPath(path);
			
			JPopupMenu popup = new JPopupMenu();
			if (path.getLastPathComponent() instanceof ListProperty) {
				final ListProperty p = (ListProperty) path.getLastPathComponent();
				if (!p.isFixedCardinality()) {
					JMenuItem addValueItem = new JMenuItem("Add");
					addValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							myModel.addValue(path, getValue(p.getType()), null);
						}
					});
					popup.add(addValueItem);				
				}
			} else if (path.getLastPathComponent() instanceof NamedValueProperty) {
				final NamedValueProperty p = (NamedValueProperty) path.getLastPathComponent();
				if (!p.isFixedCardinality()) {
					JMenuItem addValueItem = new JMenuItem("Add");
					addValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent E) {
							String name = JOptionPane.showInputDialog(myTree, "Value Name");
							myModel.addValue(path, getValue(p.getType()), name);
						}
					});
					popup.add(addValueItem);
				}
			} else if (path.getParentPath() != null && path.getParentPath().getLastPathComponent() instanceof Property) {
				final Property p = (Property) path.getParentPath().getLastPathComponent();
				if (p.isMutable() && !MainHandler.getInstance().canHandle(p.getType())) {
					final JMenuItem replaceValueItem = new JMenuItem("Replace");
					replaceValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Class currentType = ((Value) path.getLastPathComponent()).getObject().getClass();
							Object o = NewConfigurableDialog.showDialog(replaceValueItem, p.getType(), currentType);
							if (o != null) {
								try {
									myModel.setValue(path, o);
								} catch (StructuralException ex) {
									ConfigExceptionHandler.handle(ex, ex.getMessage(), event.getComponent());
								}
							}
						}
					});
					popup.add(replaceValueItem);									
				}
				
				if (!p.isFixedCardinality()) {
					if (p instanceof ListProperty) {
						JMenuItem insertValueItem = new JMenuItem("Insert");
						insertValueItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								myModel.insertValue(path, getValue(p.getType()));
							}
						}); 
						popup.add(insertValueItem);						
					}
					JMenuItem removeValueItem = new JMenuItem("Remove");
					removeValueItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							myModel.removeValue(path);
						}
					});
					popup.add(removeValueItem);				
				}
				
			}
			
			JMenuItem refreshItem = new JMenuItem("Refresh");
			refreshItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					myModel.refresh(path);
				}
			});
			popup.add(refreshItem);
			
			//property help on either property or property value ... 
			Property property = null;
			if (path.getParentPath() != null && path.getParentPath().getLastPathComponent() instanceof Property) {
				property = (Property) path.getParentPath().getLastPathComponent();
			} else if (path.getLastPathComponent() instanceof Property) {
				property = (Property) path.getLastPathComponent();
			}			
			if (property != null) {
				JMenuItem helpItem = new JMenuItem("Help");
				final Property p = property;
				helpItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String documentation = p.getDocumentation();
						if (documentation != null)
							ConfigUtil.showHelp(documentation);
					}				
				});
				popup.add(helpItem);
			}
			
//			if (path.getLastPathComponent() instanceof Value && ((Value) path.getLastPathComponent()).getObject() instanceof Function) {
//				final Function function = (Function) ((Value) path.getLastPathComponent()).getObject();
//				JMenuItem plotValueItem = new JMenuItem("Plot");
//				plotValueItem.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						Plotter.plot(function, -1, .001f, 1, "Function");
//					}
//				});
//				popup.add(plotValueItem);
//			}
			
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}
	
	private Object getValue(Class type) {
		Object result = ConfigUtil.getDefaultValue(type);
		if (result == null) {
			result = NewConfigurableDialog.showDialog(myTree, type, null);
		}
		return result;
	}
	
}
