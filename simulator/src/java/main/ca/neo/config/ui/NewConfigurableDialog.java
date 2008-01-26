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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
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
import ca.neo.config.ConfigUtil;
import ca.neo.config.Configurable;
import ca.neo.config.Configuration;
import ca.neo.config.JavaSourceParser;
import ca.neo.config.ListProperty;
import ca.neo.config.MainHandler;
import ca.neo.config.Property;
import ca.neo.config.SingleValuedProperty;
import ca.neo.config.impl.AbstractProperty;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.TemplateArrayProperty;
import ca.neo.config.impl.TemplateProperty;
import ca.neo.config.ui.ConfigurationTreeModel.NullValue;
import ca.neo.config.ui.ConfigurationTreeModel.Value;
import ca.neo.math.Function;
import ca.neo.math.impl.FourierFunction;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.impl.NEFEnsembleImpl;
import ca.neo.model.neuron.impl.SpikingNeuron;

public class NewConfigurableDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String CANCEL_ACTION_COMMAND = "cancel";
	
	private static Object myResult;	
	private static NewConfigurableDialog myDialog;
	
//	private Class myType;
	private Configuration myConfiguration;
	private JTree myConfigurationTree;
	private ConfigurationTreePopupListener myPopupListener;
	private JButton myPreviousButton;
	private JButton myNextButton;
	private JButton myOKButton;
	private Constructor[] myConstructors;
	private int myConstructorIndex;
	
	public static Object showDialog(Component comp, Class type, Class currentType) {
		myResult = null;
		
		List<Class> types = ClassRegistry.getInstance().getImplementations(type);
		if (currentType != null && !NullValue.class.isAssignableFrom(currentType) && !types.contains(currentType)) {
			types.add(0, currentType);
		}
		
		if (types.size() > 0) {
			myDialog = new NewConfigurableDialog(comp, type, types);
			myDialog.setVisible(true);			
		} else {
			String errorMessage = "There are no registered implementations of type " + type.getName();
			ConfigExceptionHandler.handle(new RuntimeException(errorMessage), errorMessage, comp);
		}
		
		return myResult;
	}
	
	private NewConfigurableDialog(Component comp, final Class type, List<Class> types) {
		super(JOptionPane.getFrameForComponent(comp), "New " + type.getSimpleName(), true);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCEL_ACTION_COMMAND);
		cancelButton.addActionListener(this);
		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					NewConfigurableDialog.this.setResult();
				} catch (StructuralException ex) {
					ConfigExceptionHandler.handle(ex, 
							"A programming bug was encountered while trying to create the new " + type.getSimpleName() 
							+ ". The error log may contain more information.", NewConfigurableDialog.this);
				}
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
					String text = "Constructor Arguments";
					if (((ConstructionProperties) ((Value) value).getObject()).getConfiguration().getPropertyNames().size() == 0) {
						text = "Zero-Argument Constructor";
					}
					((JLabel) result).setText(text);
				}
				return result;
			}
			
		};
		myConfigurationTree.setCellRenderer(cellRenderer);
		
		JScrollPane treeScroll = new JScrollPane(myConfigurationTree);
		
		JPanel typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
		
//		List<Class> types = ClassRegistry.getInstance().getImplementations(type);
//		if (currentType != null && !NullValue.class.isAssignableFrom(currentType) && !types.contains(currentType)) {
//			types.add(0, currentType);
//		}
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
		typePanel.add(typeBox);
		
		myPreviousButton = new JButton("<");
		myPreviousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewConfigurableDialog.this.changeConstructor(-1);
			}
		});
		myNextButton = new JButton(">");
		myNextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewConfigurableDialog.this.changeConstructor(1);
			}
		});
		typePanel.add(myPreviousButton);
		typePanel.add(myNextButton);

		typeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewConfigurableDialog.this.setSelectedType((Class) typeBox.getSelectedItem());
			}
		});
		
		treeScroll.setPreferredSize(new Dimension(typeBox.getPreferredSize().width, 200));
		typeBox.setSelectedIndex(0);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(typePanel, BorderLayout.NORTH);
		contentPane.add(treeScroll, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(comp);
	}
	
	private void setSelectedType(Class type) {
//		myType = type;				
		myConstructors = type.getConstructors();		
		setConstructor(0);
		System.out.println("setting constructors " + myConstructors.length);
		
//		Method templateMethod = null;
//		Method[] methods = type.getDeclaredMethods();
//		for (int i = 0; i < methods.length; i++) {
//			if ((methods[i].getName().equals("getUserConstructionTemplate") 
//						|| (templateMethod == null && methods[i].getName().equals("getConstructionTemplate")))  
//					&& methods[i].getParameterTypes().length == 0
//					&& Configuration.class.isAssignableFrom(methods[i].getReturnType())) {
//				templateMethod = methods[i];
//			}
//		}
//		
//		if (myPopupListener != null) {
//			myConfigurationTree.removeMouseListener(myPopupListener);
//		}
//		
//		
//		if (templateMethod != null) {
//			try {
//				myConfiguration = (Configuration) templateMethod.invoke(type, new Object[0]);
//				ConfigurationTreeModel model = new ConfigurationTreeModel(new ConstructionProperties(myConfiguration)); 
//				myConfigurationTree.setModel(model);
//				myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
//				myConfigurationTree.addMouseListener(myPopupListener);
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		} else {
//			myConfiguration = null;
//			myConfigurationTree.setModel(new DefaultTreeModel(null));
//		}
		
		myOKButton.setEnabled(false);
	}
	
	private void changeConstructor(int increment) {
		int newIndex = myConstructorIndex + increment;
		if (newIndex >= 0 && newIndex < myConstructors.length) {
			setConstructor(newIndex);			
		}
	}
	
	private void setConstructor(int index) {
		myConstructorIndex = index;
		Constructor constructor = myConstructors[index];
		
		if (myConstructorIndex == 0) {
			myPreviousButton.setEnabled(false);
		} else {
			myPreviousButton.setEnabled(true);
		}
		
		if (myConstructorIndex == myConstructors.length - 1) {
			myNextButton.setEnabled(false);
		} else {
			myNextButton.setEnabled(true);
		}
		
		if (myPopupListener != null) {
			myConfigurationTree.removeMouseListener(myPopupListener);
		}		
		
//		if (constructor.getParameterTypes().length > 0) {
			myConfiguration = makeTemplate(constructor);
			ConfigurationTreeModel model = new ConfigurationTreeModel(new ConstructionProperties(myConfiguration)); 
			myConfigurationTree.setModel(model);
			myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
			myConfigurationTree.addMouseListener(myPopupListener);
//		} else {
//			myConfiguration = null;
//			myConfigurationTree.setModel(new DefaultTreeModel(null));
//		}		
	}
	
	private static Configuration makeTemplate(Constructor constructor) {
		ConfigurationImpl result = new ConfigurationImpl(null);
		Class[] types = constructor.getParameterTypes();
		String[] names = JavaSourceParser.getArgNames(constructor);
		for (int i = 0; i < types.length; i++) {
			if (types[i].isPrimitive()) types[i] = ConfigUtil.getPrimitiveWrapperClass(types[i]);
			AbstractProperty p = null;
			if (types[i].isArray() && !MainHandler.getInstance().canHandle(types[i])) {
				p = new TemplateArrayProperty(result, names[i], types[i].getComponentType());
			} else {
				p = new TemplateProperty(result, names[i], types[i], ConfigUtil.getDefaultValue(types[i]));				
			}
			p.setDocumentation(JavaSourceParser.getArgDocs(constructor, i));
			result.defineProperty(p);
		}
		return result;
	}
	
//	private static Object getDefaultValue(Class type) {
//		Object result = null;
//		
//		if (MainHandler.getInstance().canHandle(type)) {
//			result = MainHandler.getInstance().getDefaultValue(type);
//		}
//		
//		if (result == null) {
//			Constructor<?>[] constructors = type.getConstructors();
//			Constructor zeroArgConstructor = null;
//			for (int i = 0; i < constructors.length && zeroArgConstructor == null; i++) {
//				if (constructors[i].getParameterTypes().length == 0) {
//					zeroArgConstructor = constructors[i];
//				}
//			}
//			if (zeroArgConstructor != null) {
//				try {
//					result = zeroArgConstructor.newInstance(new Object[0]);
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				}
//			}			
//		}
//		
//		return result; 
//	}
	
	private void setResult() throws StructuralException {
		List<Object> args = new ArrayList<Object>(myConfiguration.getPropertyNames().size());
		for (Iterator<String> iter = myConfiguration.getPropertyNames().iterator(); iter.hasNext(); ) {
			Property p = myConfiguration.getProperty(iter.next());
			if (p instanceof SingleValuedProperty) {
				args.add(((SingleValuedProperty) p).getValue());
			} else if (p instanceof ListProperty) {
				ListProperty lp = (ListProperty) p;
				Object array = Array.newInstance(p.getType(), lp.getNumValues());
				for (int i = 0; i < lp.getNumValues(); i++) {
					Array.set(array, i, lp.getValue(i));
				}
				args.add(array);
			}
		}
		
		String errorMessage = "Can't create new " 
			+ myConstructors[myConstructorIndex].getDeclaringClass().getName() + ". See error log for further detail. ";
		
		try {
			myResult = myConstructors[myConstructorIndex].newInstance(args.toArray(new Object[0]));
			
			if (myPopupListener != null) {
				myConfigurationTree.removeMouseListener(myPopupListener);
			}
			ConfigurationTreeModel model = new ConfigurationTreeModel(myResult); 
			myConfigurationTree.setModel(model);
			myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
			myConfigurationTree.addMouseListener(myPopupListener);

			myOKButton.setEnabled(true);			
		} catch (IllegalArgumentException e) {
			ConfigExceptionHandler.handle(e, errorMessage, myConfigurationTree);
		} catch (InstantiationException e) {
			ConfigExceptionHandler.handle(e, errorMessage, myConfigurationTree);
		} catch (IllegalAccessException e) {
			ConfigExceptionHandler.handle(e, errorMessage, myConfigurationTree);
		} catch (InvocationTargetException e) {
			ConfigExceptionHandler.handle(e, errorMessage + " (" + e.getCause().getClass().getName() + ")", 
					myConfigurationTree);
		}
		
//		Constructor<?>[] constructors = myType.getConstructors();
//		Constructor zeroArgConstructor = null;
//		Constructor templateConstructor = null;
//		for (int i = 0; i < constructors.length; i++) {
//			Class[] paramTypes = constructors[i].getParameterTypes();
//			if (paramTypes.length == 0) {
//				zeroArgConstructor = constructors[i];
//			} else if (paramTypes.length == 1 && Configuration.class.isAssignableFrom(paramTypes[0])) {
//				templateConstructor = constructors[i];
//			}
//		}
//		
//		try {
//			if (templateConstructor != null) {
//				myResult = (Configurable) templateConstructor.newInstance(new Object[]{myConfiguration});
//			} else if (zeroArgConstructor != null) {
//				myResult = (Configurable) zeroArgConstructor.newInstance(new Object[0]);
//			} else {
//				throw new StructuralException(myType.getName() + " doesn't have template-arg or zero-arg constructor");
//			}
//			
//			if (myPopupListener != null) {
//				myConfigurationTree.removeMouseListener(myPopupListener);
//			}
//			ConfigurationTreeModel model = new ConfigurationTreeModel(myResult); 
//			myConfigurationTree.setModel(model);
//			myPopupListener = new ConfigurationTreePopupListener(myConfigurationTree, model);
//			myConfigurationTree.addMouseListener(myPopupListener);
//
//			myOKButton.setEnabled(true);
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} 		
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

	public static class ConstructionProperties {

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
				Object result = showDialog(button, Node.class, NEFEnsembleImpl.class);
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
