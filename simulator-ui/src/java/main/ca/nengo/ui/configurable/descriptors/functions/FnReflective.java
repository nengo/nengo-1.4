package ca.nengo.ui.configurable.descriptors.functions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ca.nengo.math.Function;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchema;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;

/**
 * Function instances are created through reflection.
 * 
 * @author Shu Wu
 */
public class FnReflective extends AbstractFn {
	private Property[] myProperties;

	public FnReflective(Class<? extends Function> functionClass, String typeName,
			Property[] propStruct) {
		super(typeName, functionClass);

		this.myProperties = propStruct;
	}

	@Override
	protected Function createFunction(ConfigResult props) throws ConfigException {
		List<Property> metaProperties = getSchema().getProperties();

		/*
		 * Create function using Java reflection, function parameter are
		 * configured via the IConfigurable interface
		 */
		Class<?> partypes[] = new Class[metaProperties.size()];
		for (int i = 0; i < metaProperties.size(); i++) {
			partypes[i] = metaProperties.get(i).getTypeClass();

		}
		Constructor<?> ct = null;
		try {
			ct = getFunctionType().getConstructor(partypes);

			Object arglist[] = new Object[metaProperties.size()];
			for (int i = 0; i < metaProperties.size(); i++) {
				arglist[i] = props.getValue(metaProperties.get(i).getName());
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

	public ConfigSchema getSchema() {
		return new ConfigSchemaImpl(myProperties);
	}

}
