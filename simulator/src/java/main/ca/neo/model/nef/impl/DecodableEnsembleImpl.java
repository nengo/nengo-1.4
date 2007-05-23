/*
 * Created on 20-Feb-07
 */
package ca.neo.model.nef.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.AbstractEnsemble;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.DecodableEnsemble;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.Probe;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Default implementation of DecodableEnsemble. 
 * 
 * @author Bryan Tripp
 */
public class DecodableEnsembleImpl extends AbstractEnsemble implements DecodableEnsemble {

	private static final long serialVersionUID = 1L;
	
	private Map<String, DecodedOrigin> myDecodedOrigins;
	private ApproximatorFactory myApproximatorFactory;
	private Map<String, LinearApproximator> myApproximators;
	private float myTime; //used to support Probeable	

	/**
	 * @param name Name of the Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @param factory Source of LinearApproximators to use in decoding output
	 */
	public DecodableEnsembleImpl(String name, Node[] nodes, ApproximatorFactory factory) {
		super(name, nodes);
		
		myDecodedOrigins = new HashMap<String, DecodedOrigin>(10);
		myApproximatorFactory = factory;
		myApproximators = new HashMap<String, LinearApproximator>(10);
		myTime = 0;
	}

	/**
	 * @see ca.neo.model.nef.DecodableEnsemble#addDecodedOrigin(java.lang.String, ca.neo.math.Function[], java.lang.String, ca.neo.model.Network, ca.neo.util.Probe, float, float)
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
		DecodedOrigin result = new DecodedOrigin(name, getNodes(), nodeOrigin, functions, approximator);

		addDecodedOrigin(name, result);
		return result;
	}

	/**
	 * @see ca.neo.model.nef.DecodableEnsemble#addDecodedOrigin(java.lang.String, ca.neo.math.Function[], java.lang.String, ca.neo.model.Network, ca.neo.util.Probe, ca.neo.model.Termination, float[][], float)
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
		DecodedOrigin result = new DecodedOrigin(name, getNodes(), nodeOrigin, functions, approximator);
		addDecodedOrigin(name, result);
		return result;		
	}
	
	/**
	 * @param name The name of a new DecodedOrigin
	 * @param origin The new Origin
	 */
	protected void addDecodedOrigin(String name, DecodedOrigin origin) {
		myDecodedOrigins.put(name, origin);
	}

	/**
	 * @see ca.neo.model.nef.DecodableEnsemble#doneOrigins()
	 */
	public void doneOrigins() {
		myApproximators.clear();
	}

	/**
	 * @see ca.neo.model.nef.DecodableEnsemble#removeDecodedOrigin(java.lang.String)
	 */
	public void removeDecodedOrigin(String name) {
		myDecodedOrigins.remove(name);
	}

	/**
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return myDecodedOrigins.containsKey(name) ? myDecodedOrigins.get(name) : super.getOrigin(name);
	}

	/**
	 * @see ca.neo.model.Ensemble#getOrigins()
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
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		super.run(startTime, endTime);
		
		Iterator<DecodedOrigin> it = myDecodedOrigins.values().iterator();
		while (it.hasNext()) {
			it.next().run(null, endTime - startTime);;
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
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
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
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		
		Iterator it = myDecodedOrigins.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			result.setProperty(name, "Function of NEFEnsemble state"); //TODO: could put function.toString() here
		}
		
		return result;
	}

}
