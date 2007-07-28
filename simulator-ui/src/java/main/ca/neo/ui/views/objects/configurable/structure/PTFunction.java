package ca.neo.ui.views.objects.configurable.struct;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.PropertyInputPanel;
import ca.neo.ui.views.objects.configurable.UIConfigManager;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.shu.ui.lib.util.Util;

public class PTFunction extends PropertyStructure {

	private static final long serialVersionUID = 1L;

	public PTFunction(String name) {
		super(name);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new FunctionInputPanel(this);
	}

	@Override
	public Class<Function> getTypeClass() {
		return Function.class;
	}

	@Override
	public String getTypeName() {
		return "Function";
	}

}

/*
 * Schema for describing a function
 */
class FnSchema {
	@SuppressWarnings("unchecked")
	Class functionClass;

	PropertyStructure[] metaProperties;

	@SuppressWarnings("unchecked")
	public FnSchema(Class functionClass, PropertyStructure[] metaProperties) {
		super();
		this.functionClass = functionClass;
		this.metaProperties = metaProperties;
	}

	@SuppressWarnings("unchecked")
	public Class getFunctionClass() {
		return functionClass;
	}

	public PropertyStructure[] getMetaProperties() {
		return metaProperties;
	}

	@Override
	public String toString() {
		return functionClass.getSimpleName();
	}

}

class FunctionConfiguration implements IConfigurable {
	Function function;

	@SuppressWarnings("unchecked")
	Class functionType;

	FunctionInputPanel inputPanel;

	PropertyStructure[] metaProperties;

	String name;

	SimpleAttributeSet properties;

	@SuppressWarnings("unchecked")
	public FunctionConfiguration(FunctionInputPanel inputPanel,
			Class functionType, PropertyStructure[] propertyTypes) {
		super();
		this.metaProperties = propertyTypes;
		properties = new SimpleAttributeSet();

		this.inputPanel = inputPanel;
		this.functionType = functionType;
		this.name = functionType.getSimpleName();

	}

	public void cancelConfiguration() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public void completeConfiguration() {

		/*
		 * Create function using Java reflection
		 */
		Class partypes[] = new Class[metaProperties.length];
		for (int i = 0; i < metaProperties.length; i++) {

			partypes[i] = metaProperties[i].getTypeClass();

		}
		Constructor ct = null;
		try {
			ct = functionType.getConstructor(partypes);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ct == null) {
			return;
		}

		Object arglist[] = new Object[metaProperties.length];
		for (int i = 0; i < metaProperties.length; i++) {
			arglist[i] = getProperty(metaProperties[i].getName());
		}
		Object retobj = null;
		try {
			retobj = ct.newInstance(arglist);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (retobj != null) {
			inputPanel.setValue(retobj);
		}
	}

	public void deletePropretiesFile(String fileName) {
		Util.deleteProperty(functionType, fileName);
	}

	public Function getFunction() {
		return function;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public PropertyStructure[] getPropertiesSchema() {
		return metaProperties;
	}

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	public String[] getPropertyFiles() {
		return Util.getPropertyFiles(functionType);
	}

	@SuppressWarnings("unchecked")
	public Class getType() {
		return functionType;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Function";
	}

	public void loadPropertiesFromFile(String fileName) {

		SimpleAttributeSet prop = (SimpleAttributeSet) Util.loadProperty(
				functionType, fileName);
		if (prop != null) {
			properties = prop;
		}

	}

	public void savePropertiesToFile(String fileName) {
		Util.saveProperty(functionType, properties, fileName);

	}

	public void setProperty(String name, Object value) {

		properties.addAttribute(name, value);

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

}

class FunctionInputPanel extends PropertyInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * TODO: make meta properties static to save memory
	 * 
	 */
	static final FnSchema[] functions = new FnSchema[] {

			new FnSchema(ConstantFunction.class, new PropertyStructure[] {
					new PTInt("Dimension"), new PTFloat("Value") }),
			new FnSchema(FourierFunction.class, new PropertyStructure[] {
					new PTFloat("Fundamental"), new PTFloat("Cutoff"),
					new PTFloat("RMS") }),

			new FnSchema(GaussianPDF.class, new PropertyStructure[] {
					new PTFloat("Mean"), new PTFloat("Variance"),
					new PTFloat("Peak") }), };

	JComboBox comboBox;

	Function function = null;

	public FunctionInputPanel(PropertyStructure property) {
		super(property);
		// TODO Auto-generated constructor stub
		setValue(null);
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return function;
	}

	@Override
	public void init(JPanel panel) {
		comboBox = new JComboBox(functions);
		JButton configureFunction = new JButton(new SetParametersAction());

		panel.add(comboBox);
		panel.add(configureFunction);
	}

	@Override
	public boolean isValueSet() {
		if (function != null)
			return true;
		else
			return false;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Function) {
			function = (Function) value;
			setStatusMsg("");

		} else {
			setStatusMsg("function parameters not set");
		}

	}

	protected void setParameters() {
		FnSchema fnDescriptor = (FnSchema) comboBox.getSelectedItem();

		if (fnDescriptor == null)
			return;

		FunctionConfiguration functionProxy = new FunctionConfiguration(this,
				fnDescriptor.getFunctionClass(), fnDescriptor
						.getMetaProperties());

		/*
		 * get the JDialog parent 
		 */		
		Container parent = getParent();
		while (parent != null) {
			if (parent instanceof JDialog)
				break;
			parent = parent.getParent();
		}
		
		if (parent != null && parent instanceof JDialog) {
			
			/*
			 * Configure the function
			 */
			IConfigurationManager configManager = new UIConfigManager(
					(JDialog) parent);

			configManager.configure(functionProxy);
		} else {
			Util.Error("Could not attach properties dialog");
		}

	}

	class SetParametersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SetParametersAction() {
			super("Set Parameters");
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent e) {
			setParameters();

		}

	}

}
