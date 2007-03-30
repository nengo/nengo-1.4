/*
 * Created on 30-Mar-07
 */
package ca.neo.dynamics.impl;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.model.Units;

/**
 * Base implementation of DynamicalSystem. 
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractDynamicalSystem implements DynamicalSystem {

	private static Logger ourLogger = Logger.getLogger(AbstractDynamicalSystem.class);
	
	private float[] myState;
	
	public AbstractDynamicalSystem(float[] state) {
		myState = state;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#f(float, float[])
	 */
	public abstract float[] f(float t, float[] u);

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#g(float, float[])
	 */
	public abstract float[] g(float t, float[] u);

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getInputDimension()
	 */
	public abstract int getInputDimension();

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getOutputDimension()
	 */
	public abstract int getOutputDimension();

	/**
	 * Returns Units.UNK by default. 
	 * 
	 * @see ca.neo.dynamics.DynamicalSystem#getOutputUnits(int)
	 */
	public Units getOutputUnits(int outputDimension) {
		return Units.UNK;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getState()
	 */
	public float[] getState() {
		return myState;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#setState(float[])
	 */
	public void setState(float[] state) {
		myState = state;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			return this.getClass().getConstructor(new Class[]{float[].class}).newInstance(new Object[]{myState});
		} catch (SecurityException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		} catch (NoSuchMethodException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		} catch (IllegalArgumentException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		} catch (InstantiationException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		} catch (InvocationTargetException e) {
			ourLogger.error("Default clone implementation failed.", e);
			throw new CloneNotSupportedException("Default clone implementation doesn't work with this DynamicalSystem: " 
					+ e.getMessage());
		}
	}
	
}
