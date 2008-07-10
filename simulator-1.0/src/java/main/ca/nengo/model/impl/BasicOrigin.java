/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "BasicOrigin.java". Description: 
"A generic implementation of Origin"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 3-Jun-07
 */
package ca.nengo.model.impl;

import org.apache.log4j.Logger;

import ca.nengo.config.ConfigUtil;
import ca.nengo.config.Configurable;
import ca.nengo.config.Configuration;
import ca.nengo.config.Property;
import ca.nengo.config.impl.ConfigurationImpl;
import ca.nengo.config.impl.SingleValuedPropertyImpl;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Noise;
import ca.nengo.model.Origin;
import ca.nengo.model.Resettable;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Units;

/**
 * A generic implementation of Origin. Nodes that contain an Origin of this type should call one 
 * of the setValues() methods with every Node.run(...).   
 * 
 * @author Bryan Tripp
 */
public class BasicOrigin implements Origin, Noise.Noisy, Resettable, Configurable {

	private static final long serialVersionUID = 1L;
	
	private static Logger ourLogger = Logger.getLogger(BasicOrigin.class);

	private Node myNode;
	private String myName;
	private int myDimension;
	private Units myUnits;
	private InstantaneousOutput myValues;
	private Noise myNoise;
	private Noise[] myNoises; //per output
	private transient ConfigurationImpl myConfiguration;

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
	
	private void initConfiguration() {
		myConfiguration = ConfigUtil.defaultConfiguration(this);
		myConfiguration.removeProperty("dimensions");
		try {
			Property p = new SingleValuedPropertyImpl(myConfiguration, "dimensions", Integer.TYPE, 
					this.getClass().getMethod("getDimensions", new Class[0])); 
			myConfiguration.defineProperty(p);		
		} catch (Exception e) {
			ourLogger.warn("Can't define property 'dimensions'", e);
		}		
	}
	
	/**
	 * @see ca.nengo.config.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		if (myConfiguration == null) {
			initConfiguration();
		}
		return myConfiguration;
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
			v = new float[myDimension];
			System.arraycopy(values, 0, v, 0, values.length);
			for (int i = 0; i < myDimension; i++) {
				v[i] = myNoises[i].getValue(startTime, endTime, values[i]);
			}
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
	 * @see ca.nengo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return myDimension;
	}
	
	public void setDimensions(int dim) {
		myDimension = dim;
		if (myNoise != null) {
			setNoise(myNoise);
		}
		reset(false);
	}

	/**
	 * @see ca.nengo.model.Origin#getName()
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
	 * @see ca.nengo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		return myValues;
	}

	/**
	 * @see ca.nengo.model.Noise.Noisy#getNoise()
	 */
	public Noise getNoise() {
		return myNoise;
	}

	/**
	 * Note that noise is only applied to RealOutput. 
	 * 
	 * @see ca.nengo.model.Noise.Noisy#setNoise(ca.nengo.model.Noise)
	 */
	public void setNoise(Noise noise) {
		myNoise = noise;
		myNoises = new Noise[myDimension];
		for (int i = 0; i < myDimension; i++) {
			myNoises[i] = myNoise.clone();
		}
	}

	/**
	 * @see ca.nengo.model.Origin#getNode()
	 */
	public Node getNode() {
		return myNode;
	}
	
	public void setNode(Node node) {
		myNode = node;
	}

	@Override
	public Origin clone() throws CloneNotSupportedException {
		BasicOrigin result = (BasicOrigin) super.clone();
		if (myNoise != null) result.setNoise(myNoise.clone());
		result.setValues(myValues.clone());
		return result;
	}

	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		if (myNoise != null) myNoise.reset(randomize);
		if (myNoises != null) {
			for (int i = 0; i < myNoises.length; i++) {
				myNoises[i].reset(randomize);
			}
		}
		myValues = new RealOutputImpl(new float[myDimension], myUnits, 0);		
	}
	
}