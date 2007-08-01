package ca.neo.ui.models.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.views.objects.configurable.AbstractConfigurable;
import ca.neo.ui.views.objects.configurable.UIConfigManager;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;

/*
 * Schema for describing a function
 */
public abstract class ConfigurableFunction extends AbstractConfigurable {

	// FunctionInputPanel inputPanel;


	public ConfigurableFunction() {
		super();
		// this.inputPanel = inputPanel;

	}

	public void cancelConfiguration() {

	}

	@SuppressWarnings("unchecked")
	public void completeConfiguration() {

		PropertyStructure[] metaProperties = getPropertiesSchema();

		/*
		 * Create function using Java reflection
		 */
		Class partypes[] = new Class[metaProperties.length];
		for (int i = 0; i < metaProperties.length; i++) {

			partypes[i] = metaProperties[i].getTypeClass();

		}
		Constructor ct = null;
		try {
			ct = getFunctionClass().getConstructor(partypes);

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

		// if (retobj != null) {
		// inputPanel.setValue(retobj);
		// }
		setFunction((Function) retobj);

	}

	/**
	 * 
	 * @param parent
	 *            Parent component to hold the dialog
	 */
	public void launchConfigDialog(JDialog parent) {
		setFunction(null);

		IConfigurationManager configManager = new UIConfigManager(
				(JDialog) parent);

		configManager.configure(this);

	}

	Function function;

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@SuppressWarnings("unchecked")
	public abstract Class getFunctionClass();

	// public void cancelConfiguration() {
	// // TODO Auto-generated method stub
	//		
	// }
	//
	// public void completeConfiguration() {
	// // TODO Auto-generated method stub
	//		
	// }

	
	@Override
	public String toString() {

		return getTypeName();
	}

}