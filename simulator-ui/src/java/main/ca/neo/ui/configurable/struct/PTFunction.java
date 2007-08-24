package ca.neo.ui.configurable.struct;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.IConfigurable;

public class PTFunction extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	private static final ConfigurableFunction constantFunction = new ConfigurableFunction(
			ConstantFunction.class, "Constant Function",
			new ConfigParamDescriptor[] { new PTInt("Dimension"),
					new PTFloat("Value") });

	private static final ConfigurableFunction fourierFunction = new ConfigurableFunction(
			FourierFunction.class, "Fourier Function",
			new ConfigParamDescriptor[] { new PTFloat("Fundamental"),
					new PTFloat("Cutoff"), new PTFloat("RMS"),
					new PTLong("Seed") });

	private static final ConfigurableFunction gaussianPDF = new ConfigurableFunction(
			GaussianPDF.class, "Guassiand PDF", new ConfigParamDescriptor[] {
					new PTFloat("Mean"), new PTFloat("Variance"),
					new PTFloat("Peak") });

	public static final ConfigurableFunction[] functions = new ConfigurableFunction[] {
			constantFunction, fourierFunction, gaussianPDF };

	public PTFunction(String name) {
		super(name);
	}

	public PTFunction(String name, Object defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public FunctionPanel createInputPanel() {
		return new FunctionPanel(this);
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

/**
 * 
 * Functions are configured through the Configuration Manager. Function
 * instances are created through reflection.
 * 
 * @author Shu Wu
 * 
 */
class ConfigurableFunction implements IConfigurable {

	private Function function;

	@SuppressWarnings("unchecked")
	private Class functionClass;

	private ConfigParamDescriptor[] propStruct;

	private String typeName;

	public ConfigurableFunction(Class<?> functionClass, String typeName,
			ConfigParamDescriptor[] propStruct) {
		super();

		this.functionClass = functionClass;
		this.propStruct = propStruct;
		this.typeName = typeName;
	}

	@SuppressWarnings("unchecked")
	public void completeConfiguration(ConfigParam props) throws ConfigException {

		ConfigParamDescriptor[] metaProperties = getConfigSchema();

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
			ct = getFunctionClass().getConstructor(partypes);

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

			setFunction((Function) retobj);

		} catch (SecurityException e) {
			e.printStackTrace();
			throw new ConfigException("Could not configure function: "
					+ e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ConfigException(
					"Could not configure function, no suitable constructor found");
		}

	}

	public ConfigParamDescriptor[] getConfigSchema() {
		return propStruct;
	}

	public Function getFunction() {
		return function;
	}

	/**
	 * @return Type of Function to create
	 */

	@SuppressWarnings("unchecked")
	public Class getFunctionClass() {
		return functionClass;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

	protected void setFunction(Function function) {
		this.function = function;
	}

}