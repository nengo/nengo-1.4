/*
 * Created on 22-Dec-07
 */
package ca.neo.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.ListPropertyImpl;
import ca.neo.config.impl.NamedValuePropertyImpl;
import ca.neo.config.impl.SingleValuedPropertyImpl;
import ca.neo.config.ui.ConfigurationTreeCellEditor;
import ca.neo.config.ui.ConfigurationTreeCellRenderer;
import ca.neo.config.ui.ConfigurationTreeModel;
import ca.neo.config.ui.ConfigurationTreePopupListener;
import ca.neo.model.StructuralException;

/**
 * Configuration-related utility methods. 
 * 
 * @author Bryan Tripp
 */
public class ConfigUtil {
	
	/**
	 * Shows a tree in which object properties can be edited.
	 * 
	 * @param o
	 *            The Object to configure
	 */
	public static void configure(Frame owner, Object o) {
		JScrollPane configuruationPane = createConfigurationPane(o);

		final JDialog dialog = new JDialog(owner, o.getClass().getSimpleName() + " Configuration");

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(configuruationPane, BorderLayout.CENTER);

		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(doneButton);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Shows a tree in which object properties can be edited.
	 * 
	 * @param o
	 *            The Object to configure
	 * @return A Scroll Pane containing the configuration properties
	 */
	public static JScrollPane createConfigurationPane(Object o) {
		ConfigurationTreeModel model = new ConfigurationTreeModel(o);

		final JTree tree = new JTree(model);
		tree.setPreferredSize(new Dimension(300, 300));
		tree.setEditable(true);
		tree.setCellEditor(new ConfigurationTreeCellEditor(tree));

		tree.addMouseListener(new ConfigurationTreePopupListener(tree, model));

		// shows help when F1 is pressed
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Object selected = (tree.getSelectionPath() == null) ? null : tree
						.getSelectionPath().getLastPathComponent();
				if (e.getKeyCode() == 112 && selected instanceof Property) {
					String documentation = ((Property) selected).getDocumentation();
					if (documentation != null)
						ConfigUtil.showHelp(documentation);
				}
			}
		});
		ConfigurationTreeCellRenderer cellRenderer = new ConfigurationTreeCellRenderer();

		tree.setCellRenderer(cellRenderer);

		ToolTipManager.sharedInstance().registerComponent(tree);

		return new JScrollPane(tree);
	}
	
	/**
	 * TODO: remove this method 
	 * 
	 * @param properties Configuration from which to extract a property
	 * @param name Name of property to extact
	 * @param c Class to which property value must belong 
	 * @return Value
	 * @throws StructuralException If value doesn't belong to specified class
	 */
	public static Object get(Configuration properties, String name, Class c) throws StructuralException {
		Object o = ((SingleValuedProperty) properties.getProperty(name)).getValue();		
		if ( !c.isAssignableFrom(o.getClass()) ) {
			throw new StructuralException("Property " + name 
					+ " must be of class " + c.getName() + " (was " + o.getClass().getName() + ")");
		}		
		return o;
	}
	
	/**
	 * @param configurable An object
	 * @return configurable.getConfiguration() : Configuration if such a method is defined for configurable, 
	 * 		otherwise ConfigUtil.defaultConfiguration(configurable).  
	 */
	public static Configuration getConfiguration(Object configurable) {
		Configuration result = null;
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length && result == null; i++) {
			if (methods[i].getName().equals("getConfiguration")
					&& methods[i].getParameterTypes().length == 0
					&& Configuration.class.isAssignableFrom(methods[i].getReturnType())) {
				
				try {
					result = (Configuration) methods[i].invoke(configurable, new Object[0]);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		if (result == null) {
			result = defaultConfiguration(configurable);
		}
		
		return result;
	}
	
	/**
	 * @param configurable An Object
	 * @return A default Configuration with properties of the object, based on reflection of the 
	 * 		object's getters and setters. 
	 */
	public static ConfigurationImpl defaultConfiguration(Object configurable) {
		ConfigurationImpl result = new ConfigurationImpl(configurable);
		
		Method[] methods = configurable.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Class returnType = methods[i].getReturnType();
			String propName = getPropertyName(methods[i]);

			if (isSingleValueGetter(methods[i]) 
					&& !methods[i].getName().equals("getClass")
					&& !methods[i].getName().equals("getConfiguration")
					&& !isCounter(methods[i])) {
				
				result.defineSingleValuedProperty(propName, returnType, false);
			} else if (isIndexedGetter(methods[i]) && !result.getPropertyNames().contains(propName)) {
				Property p = ListPropertyImpl.getListProperty(result, propName, returnType);
				if (p != null) result.defineProperty(p);					
			} else if (isNamedGetter(methods[i]) && !result.getPropertyNames().contains(propName)) {
				Property p = NamedValuePropertyImpl.getNamedValueProperty(result, propName, returnType);
				if (p != null) result.defineProperty(p);									
			}
		}
		
		//look for additional array, list, and map getters 
		for (int i = 0; i < methods.length; i++) {
			Type returnType = methods[i].getGenericReturnType();
			String propName = getPropertyName(methods[i]);
			
			if (isGetter(methods[i]) && !isNamesGetter(methods[i]) && !result.getPropertyNames().contains(propName)
					&& !result.getPropertyNames().contains(stripSuffix(propName, "s"))
					&& !result.getPropertyNames().contains(stripSuffix(propName, "es"))) {
				
				Property p = null;
				if (returnType instanceof Class && MainHandler.getInstance().canHandle((Class) returnType)) {
					p = SingleValuedPropertyImpl.getSingleValuedProperty(result, propName, (Class) returnType);
				} else if (returnType instanceof Class && ((Class) returnType).isArray()) {
					p = ListPropertyImpl.getListProperty(result, propName, ((Class) returnType).getComponentType());
				} else if (returnType instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) returnType).getRawType();
					Type[] typeArgs = ((ParameterizedType) returnType).getActualTypeArguments();
					if (rawType instanceof Class && List.class.isAssignableFrom((Class) rawType) 
							&& typeArgs[0] instanceof Class) {
						p = ListPropertyImpl.getListProperty(result, propName, (Class) typeArgs[0]);
					} else if (rawType instanceof Class && Map.class.isAssignableFrom((Class) rawType)
							&& typeArgs[0] instanceof Class && typeArgs[1] instanceof Class) {
						p = NamedValuePropertyImpl.getNamedValueProperty(result, propName, (Class) typeArgs[1]);						
					}
				}
				if (p != null) result.defineProperty(p);
			}
		}
		
		return result;
	}
	
	private static boolean isCounter(Method method) {
		String name = method.getName(); 
		if (method.getReturnType().equals(Integer.TYPE) 
				&& (name.matches("getNum.+") || name.matches("get.+Count")) ) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isNamesGetter(Method method) {
		String name = method.getName();
		
		boolean returnsStringArray = method.getReturnType().isArray() 
			&& String.class.isAssignableFrom(method.getReturnType().getComponentType());
		boolean returnsStringList = List.class.isAssignableFrom(method.getReturnType()) 
			&& (method.getGenericReturnType() instanceof ParameterizedType) 
			&& ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0] instanceof Class
			&& String.class.isAssignableFrom((Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
		
		if (name.matches("get.+Names") && (returnsStringArray || returnsStringList)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static String getPropertyName(Method method) {
		String result = method.getName();

		result = stripPrefix(result, "get");
		result = stripPrefix(result, "All");
		result = stripSuffix(result, "Array");
		result = stripSuffix(result, "List");
				
		return result.length() > 0 ? Character.toLowerCase(result.charAt(0)) + result.substring(1) : "";
	}
	
	/**
	 * @param s A String
	 * @param suffix Something that the string might end with
	 * @return The string with the given suffix removed (if it was there)
	 */
	public static String stripSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
	
	private static String stripPrefix(String s, String prefix) {
		if (s.startsWith(prefix)) {
			return s.substring(prefix.length());
		} else {
			return s;
		}
	}
	
	private static boolean isSingleValueGetter(Method m) {
		if (m.getName().startsWith("get") 
				&& m.getParameterTypes().length == 0
				&& !Collection.class.isAssignableFrom(m.getReturnType()) 
				&& !Map.class.isAssignableFrom(m.getReturnType()) 
				&& !m.getReturnType().isArray()) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isGetter(Method m) {
		if (m.getName().startsWith("get") 
				&& m.getParameterTypes().length == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isIndexedGetter(Method m) {
		Class[] paramTypes = m.getParameterTypes();
		if (m.getName().startsWith("get") && paramTypes.length == 1 && paramTypes[0].equals(Integer.TYPE)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isNamedGetter(Method m) {
		Class[] paramTypes = m.getParameterTypes();
		if (m.getName().startsWith("get") && paramTypes.length == 1 && paramTypes[0].equals(String.class)) {
			return true;
		} else {
			return false;
		}		
	}	
	
	/**
	 * @param c Any class 
	 * @return Either c or if c is a primitive class (eg Integer.TYPE), the corresponding wrapper class 
	 */
	public static Class getPrimitiveWrapperClass(Class c) {
		if (Integer.TYPE.isAssignableFrom(c)) {
			c = Integer.class;
		} else if (Float.TYPE.isAssignableFrom(c)) {
			c = Float.class;
		} else if (Boolean.TYPE.isAssignableFrom(c)) {
			c = Boolean.class;
		} else if (Long.TYPE.isAssignableFrom(c)) {
			c = Long.class;
		} else if (Double.TYPE.isAssignableFrom(c)) {
			c = Double.class;
		} else if (Character.TYPE.isAssignableFrom(c)) {
			c = Character.class;
		} else if (Byte.TYPE.isAssignableFrom(c)) {
			c = Byte.class;
		} else if (Short.TYPE.isAssignableFrom(c)) {
			c = Short.class;
		}		
		
		return c;
	}
	
	/**
	 * @param type A class
	 * @return If there is a ConfigurationHandler for the class, then getDefaultValue() from that 
	 * 		handler, otherwise if there is a zero-arg constructor then the result of that 
	 * 		constructor, otherwise null.  
	 */
	public static Object getDefaultValue(Class type) {
		Object result = null;
		
		if (MainHandler.getInstance().canHandle(type)) {
			result = MainHandler.getInstance().getDefaultValue(type);
		}
		
		if (result == null) {
			Constructor<?>[] constructors = type.getConstructors();
			Constructor zeroArgConstructor = null;
			for (int i = 0; i < constructors.length && zeroArgConstructor == null; i++) {
				if (constructors[i].getParameterTypes().length == 0) {
					zeroArgConstructor = constructors[i];
				}
			}
			if (zeroArgConstructor != null) {
				try {
					result = zeroArgConstructor.newInstance(new Object[0]);
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
		}
		
		return result; 
	}
	
	/**
	 * Displays given text in a help window. 
	 * 
	 * @param text Help text (html body)
	 */
	public static void showHelp(String text) {
		String document = "<html><head></head><body>" + text + "</body></html>";
		JEditorPane pane = new JEditorPane("text/html", document);
		pane.setEditable(false);
		
		JFrame frame = new JFrame("Help"); 
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(pane), BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	//functional test code
//	public static void main(String[] args) {
//		Object foo = new Object() {
//			public int getFooCount() {
//				return 0;
//			}
//			public int getNumFoo() {
//				return 0;
//			}
//			public int[] getAllFoo() {
//				return new int[0];
//			}
//			public int[] getFooArray() {
//				return new int[0];
//			}
//			public int[] getFooList() {
//				return new int[0];
//			}
//		};
//		
//		try {
//			System.out.println(isCounter(foo.getClass().getMethod("toString", new Class[0])));
//			System.out.println(isCounter(foo.getClass().getMethod("getFooCount", new Class[0])));
//			System.out.println(isCounter(foo.getClass().getMethod("getNumFoo", new Class[0])));
//			
//			System.out.println(getPropertyName(foo.getClass().getMethod("getAllFoo", new Class[0])));
//			System.out.println(getPropertyName(foo.getClass().getMethod("getFooArray", new Class[0])));
//			System.out.println(getPropertyName(foo.getClass().getMethod("getFooList", new Class[0])));
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//	}
}
