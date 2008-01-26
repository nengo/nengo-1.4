/*
 * Created on 3-Jun-07
 */
package ca.neo.model.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Noise;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.Units;

/**
 * A generic implementation of Origin. Nodes that contain an Origin of this type should call one 
 * of the setValues() methods with every Node.run(...).   
 * 
 * @author Bryan Tripp
 */
public class BasicOrigin implements Origin, Noise.Noisy {

	private static final long serialVersionUID = 1L;

	private Node myNode;
	private String myName;
	private int myDimension;
	private Units myUnits;
	private InstantaneousOutput myValues;
	private Noise myNoise;

	/**
	 * @param node The parent Node
	 * @param dimension Dimension of output of this Origin
	 * @param units The output units  
	 */
	public BasicOrigin(Node node, String name, int dimension, Units units) {
		myNode = node; 
		myName = name;
		myDimension = dimension;
		myUnits = units;
		myValues = new RealOutputImpl(new float[dimension], units, 0);
	}
	
	/**
	 * This method is normally called by the Node that contains this Origin, to set the input that is 
	 * read by other nodes from getValues(). If the Noise model has been set, noise is applied to the 
	 * given values. 
	 *    
	 * @param startTime Start time of step for which outputs are being defined
	 * @param endTime End time of step for which outputs are being defined
	 * @param values Values underlying RealOutput that is to be output by this Origin in subsequent 
	 * 		calls to getValues() 
	 */
	public void setValues(float startTime, float endTime, float[] values) {
		assert values.length == myDimension;

		float[] v = values;
		if (myNoise != null) {
			v = myNoise.getValues(startTime, endTime, values);
		}
		
		myValues = new RealOutputImpl(v, myUnits, endTime);
	}
	
	/**
	 * This method is normally called by the Node that contains this Origin, to set the input that is 
	 * read by other nodes from getValues(). No noise is applied to the given values. 
	 *  
	 * @param values Values to be output by this Origin in subsequent calls to getValues() 
	 */
	public void setValues(InstantaneousOutput values) {
		assert values.getDimension() == myDimension;
		
		myValues = values;
	}

	/**
	 * @see ca.neo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return myDimension;
	}
	
	public void setDimensions(int dim) {
		myDimension = dim;
	}

	/**
	 * @see ca.neo.model.Origin#getName()
	 */
	public String getName() {
		return myName;
	}
	
	public void setName(String name) {
		myName = name;
	}
	
	public Units getUnits() {
		return myUnits;
	}
	
	public void setUnits(Units units) {
		myUnits = units;
	}

	/**
	 * @see ca.neo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		return myValues;
	}

	/**
	 * @see ca.neo.model.Noise.Noisy#getNoise()
	 */
	public Noise getNoise() {
		return myNoise;
	}

	/**
	 * Note that noise is only applied to RealOutput. 
	 * 
	 * @see ca.neo.model.Noise.Noisy#setNoise(ca.neo.model.Noise)
	 */
	public void setNoise(Noise noise) {
		myNoise = noise;
	}

	/**
	 * @see ca.neo.model.Origin#getNode()
	 */
	public Node getNode() {
		return myNode;
	}
	
	public void setNode(Node node) {
		myNode = node;
	}
	
}