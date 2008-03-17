package ca.neo.ui.configurable.descriptors.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;

/**
 * Function instances are created through reflection.
 * 
 * @author Shu Wu
 */
public class FnReflective extends AbstractFn {
	private PropertyDescriptor[] myProperties;

	public FnReflective(Class<? extends Function> functionClass, String typeName, PropertyDescriptor[] propStruct) {
		super(typeName, functionClass);

		this.myProperties = propStruct;
	}

	@Override
	protected Function createFunction(PropertySet props) throws ConfigException {
		PropertyDescriptor[] metaProperties = getConfigSchema();

		/*
		 * Create function using Java reflection, function parameter are
		 * configured via the IConfigurable interface
		 */
		Class<?> partypes[] = new Class[metaProperties.length];
		for (int i = 0; i < metaProperties.length; i++) {

			partypes[i] = metaProperties[i].getTypeClass();

		}
		Constructor<?> ct = null;
		try {
			ct = getFunctionType().getConstructor(partypes);

			Object arglist[] = new Object[metaProperties.length];
			for (int i = 0; i < metaProperties.length; i++) {
				arglist[i] = props.getProperty(metaProperties[i].getName());
			}
			Object retobj = null;
			try {
				retobj = ct.newInstance(arglist);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			return (Function) retobj;

		} catch (SecurityException e) {
			e.printStackTrace();
			throw new ConfigException("Could not configure function: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ConfigException("Could not configure function, no suitable constructor found");
		}
	}

	public PropertyDescriptor[] getConfigSchema() {
		return myProperties;
	}

}
