/*
 * Created on May 17, 2006
 */
package ca.neo.model;

import java.io.Serializable;

/**
 * <p>An output from an Origin at an instant in time. This is the medium we use to pass 
 * information around a neural circuit.</p> 
 * 
 * <p>Note that an Ensemble or Neuron may have multiple Origins and can therefore produce 
 * multiple outputs simultaneously. For example, one Origin of an Ensemble might produce  
 * spiking outputs, another the decoded estimates of variables it represents, and others 
 * decoded functions of these variables.</p> 
 * 
 * <p>Note that the methods for getting output values from an InstantaneousOuput are not
 * defined here, but on subinterfaces.</p> 
 * 
 * @author Bryan Tripp
 */
public interface InstantaneousOutput extends Serializable, Cloneable {

	/**
	 * @return Units in which output is expressed. 
	 */
	public Units getUnits();
	
	/**
	 * @return Dimension of output  
	 */
	public int getDimension();
	
	/**
	 * @return Time at which output is produced. 
	 */
	public float getTime();

	public InstantaneousOutput clone() throws CloneNotSupportedException;
	
}

