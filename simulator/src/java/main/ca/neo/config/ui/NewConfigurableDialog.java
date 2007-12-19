/*
 * Created on 14-Dec-07
 */
package ca.neo.config.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.tree.DefaultTreeModel;

import ca.neo.config.ClassRegistry;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.impl.MockConfigurable.MockLittleConfigurable;

public class NewConfigurableDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String CANCEL_ACTION_COMMAND = "cancel";
	
	private static Configurable myResult;	
	private static NewConfigurableDialog myDialog;
	
	private Class myType;
	private Configuration myConfiguration;
	private JTree myConfigurationTree;
	private ConfigurationTreePopupListener myPopupListener;
	private JButton myOKButton;
	
	public static Configurable showDialog(Component comp, Class type, Class currentType) {
		myResult = null;
		myDialog = new NewConfigurableDialog(comp, type, currentType);
		myDialog.setVisible(true);
		return myResult;
	}
	
	private NewConfigurableDialog(Component comp, Class type, Class currentType) {
		super(JOptionPane.getFrameForComponent(comp), "New " + type.getSimpleName(), true);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCEL_ACTION_COMMAND);
		cancelButton.addActionListener(this);
		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewConfigurableDialog.this.setResult();
			}
		});
		myOKButton = new JButton("OK");
		myOKButton.addActionListener(this);
		myOKButton.setEnabled(false);
		getRootPane().setDefaultButton(myOKButton);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setAlignmentX(FlowLayout.RIGHT);
		buttonPanel.add(cancelButton);
		buttonPanel.add(createButton);
		buttonPanel.add(myOKButton);
		
		myConfigurationTree = new JTree(new Object[0]);
		myConfigurationTree.setEditable(true); 
		myConfigurationTree.setRootVisible(true);
		myConfigurationTree.setCellEditor(new ConfigurationTreeCellEditor(myConfigurationTree));
		ConfigurationTreeCellRenderer cellRenderer = new ConfigurationTreeCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof Value && ((Value) value).getObject() instanceof ConstructionProperties 
						&& result instanceof JLabel) {
					((JLabel) result).setText("Construction properties");
				}
				return result;
			}
			
		};
		myConfigurationTree.setCellRenderer(cellRenderer);
		
		JScrollPane treeScroll = new JScrollPane(myConfigurationTree);
		
		List<Class> types = ClassRegistry.getInstance().getImplementations(type);
		if (currentType != null && !types.contains(currentType)) {
			types.add(0, currentType);
		}
		final JComboBox typeBox = new JComboBox(types.toArray());
		typeBox.setRenderer(new BasicComboBoxRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				((JLabel) result).setText(((Class) value).getSimpleName());
				return result;
			}
		});
		typeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewConfigurableDialog.this.setSelectedType((Class) typeBox.getSelectedItem());
			}
		});
		if (currentType != null) {
			typeBox.setSelectedItem(currentType);
		} else {
			typeBox.setSelectedIndex(0);
		}

		treeScroll.setPreferredSize(new Dimension(typeBox.getPreferredSize().width, 200));
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(typeBox, BorderLayout.NORTH);
		contentPane.add(treeScroll, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(comp);
	}
	
	private void setSelectedType(Class type) {
		myType = type;
		
		Method templateMethod = null;
		Method[] methods = type.getMethods();
		for (int i = 0; i < methods.length && templateMethod == null; i++) {
			if (methods[i].getName().equals("getConstructionTemplate") 
					&& methods[i].getParameterTypes().length == 0
					&& Configuration.class.isAssignableFrom(methods[i].getReturnType())) {
				templateMethod = methods[i];
			}
		}
		
		if (myPopupListener != null) {
			myConfigurationTree.removeMouseListener(myPopupListener);
		}
		
		
		if (templateMethod != null) {
			try {
				myConfiguration = (Configuration) templateMethod.invoke(type, new Object[0]);
				ConfigurationTreeModel model = new ConfigurationTreeModel(new ConstructionProperties(myConfiguration)); 
				myConfigurationTree.setModel(model);
				myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
				myConfigurationTree.addMouseListener(myPopupListener);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
//			myConfiguration = new ConfigurationImpl(null);
			myConfiguration = null;
			myConfigurationTree.setModel(new DefaultTreeModel(null));
		}
		
		myOKButton.setEnabled(false);
	}
	
	private void setResult() {
		Constructor<?>[] constructors = myType.getConstructors();
		Constructor zeroArgConstructor = null;
		Constructor templateConstructor = null;
		for (int i = 0; i < constructors.length; i++) {
			Class[] paramTypes = constructors[i].getParameterTypes();
			if (paramTypes.length == 0) {
				zeroArgConstructor = constructors[i];
			} else if (paramTypes.length == 1 && Configuration.class.isAssignableFrom(paramTypes[0])) {
				templateConstructor = constructors[i];
			}
		}
		
		try {
			if (templateConstructor != null) {
				myResult = (Configurable) templateConstructor.newInstance(new Object[]{myConfiguration});
			} else if (zeroArgConstructor != null) {
				myResult = (Configurable) zeroArgConstructor.newInstance(new Object[0]);
			} else {
				throw new RuntimeException(myType.getName() + " doesn't have template-arg or zero-arg constructor");
			}
			
			if (myPopupListener != null) {
				myConfigurationTree.removeMouseListener(myPopupListener);
			}
			ConfigurationTreeModel model = new ConfigurationTreeModel(myResult); 
			myConfigurationTree.setModel(model);
			myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
			myConfigurationTree.addMouseListener(myPopupListener);

			myOKButton.setEnabled(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 		
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (CANCEL_ACTION_COMMAND.equals(e.getActionCommand())) {
			myResult = null;
		}
		myDialog.setVisible(false);
	}

	private static class ConstructionProperties implements Configurable {

		private Configuration myConfiguration;
		
		private ConstructionProperties(Configuration configuration) {
			myConfiguration = configuration;
		}
		
		public Configuration getConfiguration() {
			return myConfiguration;
		}
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Tree Test");
		final JButton button = new JButton("New");
		button.setPreferredSize(new Dimension(200, 50));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Configurable result = showDialog(button, Configurable.class, MockLittleConfigurable.class);
				System.out.println("Result: " + result);
			}
		});
		frame.getContentPane().add(button);
		
		frame.pack();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
	}

}
