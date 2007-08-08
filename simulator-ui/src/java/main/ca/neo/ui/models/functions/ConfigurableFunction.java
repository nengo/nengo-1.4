package ca.neo.ui.models.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.views.objects.configurable.AbstractConfigurable;
import ca.neo.ui.views.objects.configurable.managers.UserConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

/**
 * 
 * Functions are configured through the Configuration Manager. Function
 * instances are created through reflection.
 * 
 * @author Shu Wu
 * 
 */
public abstract class ConfigurableFunction extends AbstractConfigurable {

	public ConfigurableFunction() {
		super();

	}

	public void cancelConfiguration() {

	}

	@SuppressWarnings("unchecked")
	public void completeConfiguration(PropertySet props) {

		PropDescriptor[] metaProperties = getConfigSchema();

		/*
		 * Create function using Java reflection, function parameter are
		 * configured via the IConfigurable interface
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
			arglist[i] = props.getProperty(metaProperties[i].getName());
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

		setFunction((Function) retobj);

	}

	Function function;

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	/**
	 * @return Type of Function to create
	 */
	@SuppressWarnings("unchecked")
	public abstract Class getFunctionClass();

}