/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "DecodableEnsembleImpl.java". Description: 
"Default implementation of DecodableEnsemble"

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
 * Created on 20-Feb-07
 */
package ca.nengo.model.nef.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.Function;
import ca.nengo.math.LinearApproximator;
import ca.nengo.math.impl.ConstantFunction;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.EnsembleImpl;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.nef.DecodableEnsemble;
import ca.nengo.util.MU;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.impl.TimeSeriesImpl;

/**
 * Default implementation of DecodableEnsemble. 
 * 
 * @author Bryan Tripp
 */
public class DecodableEnsembleImpl extends EnsembleImpl implements DecodableEnsemble {

	private static final long serialVersionUID = 1L;
	
	private Map<String, DecodedOrigin> myDecodedOrigins;
	private ApproximatorFactory myApproximatorFactory;
	private Map<String, LinearApproximator> myApproximators;
	private float myTime; //used to support Probeable	

	/**
	 * @param name Name of the Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @param factory Source of LinearApproximators to use in decoding output
	 * @throws StructuralException 
	 */
	public DecodableEnsembleImpl(String name, Node[] nodes, ApproximatorFactory factory) throws StructuralException {
		super(name, nodes);
		
		myDecodedOrigins = new HashMap<String, DecodedOrigin>(10);
		myApproximatorFactory = factory;
		myApproximators = new HashMap<String, LinearApproximator>(10);
		myTime = 0;
	}

	/**
	 * @see ca.nengo.model.nef.DecodableEnsemble#addDecodedOrigin(java.lang.String, ca.nengo.math.Function[], java.lang.String, ca.nengo.model.Network, ca.nengo.util.Probe, float, float)
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, 
			Probe probe, float startTime, float endTime) throws StructuralException, SimulationException {

		probe.reset();
		environment.run(startTime, endTime);
		float[] times = probe.getData().getTimes();
		float[][] evalPoints = new float[times.length][];
		for (int i = 0; i < times.length; i++) {
			evalPoints[i] = new float[]{times[i]};
		}
		float[][] values = probe.getData().getValues();
		float[][] valuesT = MU.transpose(values);
		
		LinearApproximator approximator = myApproximatorFactory.getApproximator(evalPoints, valuesT);
		DecodedOrigin result = new DecodedOrigin(this, name, getNodes(), nodeOrigin, functions, approximator);
		result.setMode(getMode());

		addDecodedOrigin(name, result);
		return result;
	}

	/**
	 * @see ca.nengo.model.nef.DecodableEnsemble#addDecodedOrigin(java.lang.String, ca.nengo.math.Function[], java.lang.String, ca.nengo.model.Network, ca.nengo.util.Probe, ca.nengo.model.Termination, float[][], float)
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, 
			Probe probe, Termination termination, float[][] evalPoints, float transientTime) throws StructuralException, SimulationException {

		float[][] values = new float[evalPoints.length][];
		for (int i = 0; i < evalPoints.length; i++) {
			Function[] f = new Function[evalPoints[i].length];
			for (int j = 0; j < f.length; j++) {
				f[j] = new ConstantFunction(1, evalPoints[i][j]);
			}
			FunctionInput fi = new FunctionInput("DECODING SIMULATION INPUT", f, Units.UNK);
			environment.addNode(fi);
			environment.addProjection(fi.getOrigin(FunctionInput.ORIGIN_NAME), termination);
			probe.reset();
			environment.run(0, transientTime);
			TimeSeries result = probe.getData();
			environment.removeProjection(termination);
			environment.removeNode(fi.getName());
			
			values[i] = new float[result.getDimension()];
			int samples = (int) Math.ceil( (double) result.getValues().length / 10d ); //use only last ~10% of run in the average to avoid transient
			for (int j = 0; j < result.getDimension(); j++) {
				values[i][j] = 0;
				for (int k = result.getValues().length - samples; k < result.getValues().length; k++) {
					values[i][j] += result.getValues()[j][k];
				}
				values[i][j] = values[i][j] / (float) samples;
			}
		}
		
		LinearApproximator approximator = myApproximatorFactory.getApproximator(evalPoints, values);
		DecodedOrigin result = new DecodedOrigin(this, name, getNodes(), nodeOrigin, functions, approximator);
		result.setMode(getMode());
		addDecodedOrigin(name, result);
		return result;		
	}
	
	/**
	 * @param name The name of a new DecodedOrigin
	 * @param origin The new Origin
	 */
	protected void addDecodedOrigin(String name, DecodedOrigin origin) {
		myDecodedOrigins.put(name, origin);
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.nef.DecodableEnsemble#doneOrigins()
	 */
	public void doneOrigins() {
		myApproximators.clear();
	}

	/**
	 * @see ca.nengo.model.nef.DecodableEnsemble#removeDecodedOrigin(java.lang.String)
	 */
	public void removeDecodedOrigin(String name) {
		myDecodedOrigins.remove(name);
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return myDecodedOrigins.containsKey(name) ? myDecodedOrigins.get(name) : super.getOrigin(name);
	}

	/**
	 * @see ca.nengo.model.Ensemble#getOrigins()
	 */
	public Origin[] getOrigins() {		
		Origin[] decoded = (myDecodedOrigins == null) ? new Origin[0] : myDecodedOrigins.values().toArray(new Origin[0]);
		Origin[] composites = super.getOrigins();
		
		Origin[] all = new Origin[decoded.length + composites.length];
		System.arraycopy(decoded, 0, all, 0, decoded.length);
		System.arraycopy(composites, 0, all, decoded.length, composites.length);
		
		return all;
	}

	/**
	 * @see ca.nengo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		super.run(startTime, endTime);
		
		Iterator<DecodedOrigin> it = myDecodedOrigins.values().iterator();
		while (it.hasNext()) {
			it.next().run(null, startTime, endTime);
		}

		setTime(endTime);
	}
	
	/**
	 * Allows subclasses to set the simulation time, which is used to support Probeable. 
	 * This is normally set in the run() method. Subclasses that override run() without 
	 * calling it should set the time. 
	 * 
	 * @param time Simulation time
	 */
	protected void setTime(float time) {
		myTime = time;
	}

	/**
	 * @return The source of LinearApproximators for this ensemble (used to find linear decoding vectors). 
	 */
	public ApproximatorFactory getApproximatorFactory() {
		return myApproximatorFactory;
	}

	/**
	 * @see ca.nengo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		
		Origin origin = (Origin) myDecodedOrigins.get(stateName);
		
		if (origin != null) {
			float[] vals = ((RealOutput) origin.getValues()).getValues();
			Units[] units = new Units[vals.length];
			for (int i = 0; i < vals.length; i++) {
				units[i] = origin.getValues().getUnits();
			}
			result = new TimeSeriesImpl(new float[]{myTime}, new float[][]{vals}, units);
		} else {
			return super.getHistory(stateName);
		}
		
		return result;
	}

	/**
	 * @see ca.nengo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = super.listStates();
		
		Iterator it = myDecodedOrigins.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			result.setProperty(name, "Function of NEFEnsemble state"); //TODO: could put function.toString() here
		}
		
		return result;
	}

	@Override
	public DecodableEnsemble clone() throws CloneNotSupportedException {
		DecodableEnsembleImpl result = (DecodableEnsembleImpl) super.clone();
		
		result.myApproximatorFactory = myApproximatorFactory.clone();
		result.myApproximators = new HashMap<String, LinearApproximator>(5);
		
		result.myDecodedOrigins = new HashMap<String, DecodedOrigin>(5);
		for (DecodedOrigin oldOrigin : myDecodedOrigins.values()) {
			Function[] oldFunctions = oldOrigin.getFunctions();
			Function[] newFunctions = new Function[oldFunctions.length];
			for (int i = 0; i < newFunctions.length; i++) {
				newFunctions[i] = oldFunctions[i].clone(); 
			}
			
			try {
				DecodedOrigin newOrigin = new DecodedOrigin(
						result, 
						oldOrigin.getName(), 
						result.getNodes(), 
						oldOrigin.getNodeOrigin(), 
						newFunctions, 
						MU.clone(oldOrigin.getDecoders()));
				if (oldOrigin.getNoise() != null) newOrigin.setNoise(oldOrigin.getNoise());
				newOrigin.setMode(oldOrigin.getMode());
				result.addDecodedOrigin(newOrigin.getName(), newOrigin);				
				newOrigin.reset(false);
			} catch (StructuralException e) {
				throw new CloneNotSupportedException("Problem cloneing DecodedOrigin: " + e.getMessage());
			}
		}
		
		return result;
	}

}
