/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "NetworkImpl.java". Description:
"Default implementation of Network"

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
 * Created on 23-May-2006
 */
package ca.nengo.model.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.nengo.math.Function;
import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Probeable;
import ca.nengo.model.Projection;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StepListener;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.impl.DecodableEnsembleImpl;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.sim.Simulator;
import ca.nengo.sim.impl.LocalSimulator;
import ca.nengo.util.MU;
import ca.nengo.util.Probe;
import ca.nengo.util.ScriptGenException;
import ca.nengo.util.TaskSpawner;
import ca.nengo.util.ThreadTask;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;
import ca.nengo.util.impl.ProbeTask;
import ca.nengo.util.impl.ScriptGenerator;

/**
 * Default implementation of Network.
 *
 * @author Bryan Tripp
 */
public class NetworkArrayImpl extends NetworkImpl {

	/**
	 * Default name for a Network
	 */
	private static final long serialVersionUID = 1L;
	// ?? private static Logger ourLogger = Logger.getLogger(NetworkImpl.class);
	
	private final int myDimension;
	
	private final NEFEnsemble[] myNodes;
	private Map<String, Origin> myOrigins;
	private int myNeurons;

	/**
	 * Create a network holding an array of nodes.  An 'X' Origin
	 * is automatically created which concatenates the values of each
	 * internal element's 'X' Origin.
	 *  
	 * This object is meant to be created using :func:`nef.Network.make_array()`, allowing for the
	 * efficient creation of neural groups that can represent large vectors.  For example, the
	 * following code creates a NetworkArray consisting of 50 ensembles of 1000 neurons, each of 
	 * which represents 10 dimensions, resulting in a total of 500 dimensions represented::
	 *  
	 *   net=nef.Network('Example Array')
	 *   A=net.make_array('A',neurons=1000,length=50,dimensions=10,quick=True)
	 *    
	 * The resulting NetworkArray object can be treated like a normal ensemble, except for the
	 * fact that when computing nonlinear functions, you cannot use values from different
	 * ensembles in the computation, as per NEF theory.
	 *  
	 * @param name The name of the NetworkArray to create
	 * @param nodes The ca.nengo.model.nef.NEFEnsemble nodes to combine together
	 * @throws StructuralException
	 */
	public NetworkArrayImpl(String name, NEFEnsemble[] nodes) throws StructuralException {
		super();
		
		this.setName(name);
		myDimension = nodes.length * nodes[0].getDimension();
		
		myNodes = nodes.clone();
		myNeurons = 0;
		
		myOrigins = new HashMap<String, Origin>(10);
		
		for(int i = 0; i < nodes.length; i++) {
			this.addNode(nodes[i]);
			myNeurons += nodes[i].getNodeCount();
		}
		
		this.setUseGPU(true);
	}

	
	/** 
	 * Create an Origin that concatenates the values of internal Origins.
     *
     * @param name The name of the Origin to create.  Each internal node must already have an Origin 
     * with that name.
     * @throws StructuralException
	 */
	private void createEnsembleOrigin(String name) throws StructuralException {
		// Create array origin too.
		this.exposeOrigin(this.myOrigins.get(name), name);
	}
	
	public int getNeurons() {
		return myNeurons;
	}
	
	public class ArrayOrigin extends BasicOrigin {

		private static final long serialVersionUID = 1L;
		
		private String myName;
		private NetworkArrayImpl myParent;
		private DecodedOrigin[] myOrigins;
		private int myDimensions;

		public ArrayOrigin(NetworkArrayImpl parent, String name, DecodedOrigin[] origins) {
			myParent = parent;
			myName = name;
			myOrigins = origins;
			myDimensions = 0;
			for(int i=0; i < myOrigins.length; i++)
				myDimensions += myOrigins[i].getDimensions();
		}
		
		public String getName() {
			return myName;
		}
		
		public int getDimensions() {
			return myDimensions;
		}
		
		public void setValues(RealOutput values) {
			float time = values.getTime();
			Units units = values.getUnits();
			float[] vals = ((RealOutput)values).getValues();
			
			int d=0;
			for(int i=0; i < myOrigins.length; i++) {
				float[] ovals = new float[myOrigins[i].getDimensions()];
				for(int j=0; j < ovals.length; j++)
					ovals[j] = vals[d+j];
				d += myOrigins[i].getDimensions();
				
				myOrigins[i].setValues(new RealOutputImpl(ovals, units, time));
			}
			
		}

		public InstantaneousOutput getValues() throws SimulationException {
			InstantaneousOutput v0 = myOrigins[0].getValues();
			
			Units unit = v0.getUnits();
			float time = v0.getTime();
			
			if(v0 instanceof PreciseSpikeOutputImpl) {
				float[] vals = new float[myDimensions];
				int d=0;
				for(int i=0; i < myOrigins.length; i++) {
					float[] ovals = ((PreciseSpikeOutputImpl)myOrigins[i].getValues()).getSpikeTimes();
					for(int j=0; j < ovals.length; j++)
						vals[d++] = ovals[j];
				}
				
				return new PreciseSpikeOutputImpl(vals, unit, time);
			} else if(v0 instanceof RealOutputImpl) {
				float[] vals = new float[myDimensions];
				int d=0;
				for(int i=0; i < myOrigins.length; i++) {
					float[] ovals = ((RealOutputImpl)myOrigins[i].getValues()).getValues();
					for(int j=0; j < ovals.length; j++)
						vals[d++] = ovals[j];
				}
				
				return new RealOutputImpl(vals, unit, time);
			} else if(v0 instanceof SpikeOutputImpl) {
				boolean[] vals = new boolean[myDimensions];
				int d=0;
				for(int i=0; i < myOrigins.length; i++) {
					boolean[] ovals = ((SpikeOutputImpl)myOrigins[i].getValues()).getValues();
					for(int j=0; j < ovals.length; j++)
						vals[d++] = ovals[j];
				}
				
				return new SpikeOutputImpl(vals, unit, time);
			} else {
				System.err.println("Unknown type in ArrayOrigin.getValues()");
				return null;
			}
		}
		
		public Node getNode() {
			return myParent;
		}
		
		public boolean getRequiredOnCPU() {
			for(int i=0; i < myOrigins.length; i++)
				if(myOrigins[i].getRequiredOnCPU())
					return true;
			return false;
		}
		
		public void setRequiredOnCPU(boolean req) {
			for(int i=0; i < myOrigins.length; i++)
				myOrigins[i].setRequiredOnCPU(req);
		}
		
		public Origin clone() {
			//this is how it was implemented in networkarray, but I don't think it will work (myOrigins needs to be updated to the cloned origins)
			return new ArrayOrigin(myParent, myName, myOrigins);
		}

		public float[][] getDecoders() {
			int neurons = myParent.getNeurons();
			float[][] decoders = new float[neurons*myOrigins.length][myDimensions];
			for(int i=0; i < myOrigins.length; i++) {
				MU.copyInto(myOrigins[i].getDecoders(), decoders, i*neurons, i*myOrigins[i].getDimensions(), neurons);
			}
			return decoders;
		}

	}
	
	/**
	 * Create a new Origin.  A new origin is created on each of the 
     * ensembles, and these are grouped together to create an output.
     *  
     * This method uses the same signature as ca.nengo.model.nef.NEFEnsemble.addDecodedOrigin()
     * 
	 * @param name The name of the newly created origin
	 * @param functions A list of ca.nengo.math.Function objects to approximate at this origin
	 * @param nodeOrigin Name of the base Origin to use to build this function approximation
       (this will always be 'AXON' for spike-based synapses)
	 * @return Origin that encapsulates all of the internal node origins
	 * @throws StructuralException
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException {
		Origin[] origins = new Origin[myNodes.length];
		for(int i = 0; i < myNodes.length; i++) {
			origins[i] = myNodes[i].addDecodedOrigin(name,  functions,  nodeOrigin);
		}
		this.createEnsembleOrigin(name);
		return this.getOrigin(name);
	}
	
	/**
	 * Create a new termination.  A new termination is created on each
     * of the ensembles, which are then grouped together.  This termination
     * does not use NEF-style encoders; instead, the matrix is the actual connection
     * weight matrix.  Often used for adding an inhibitory connection that can turn
     * off the whole array (by setting *matrix* to be all -10, for example). 
     *   
	 * @param name The name of the newly created origin
	 * @param weights Synaptic connection weight matrix (NxM where M is the total number of neurons in the NetworkArray)
	 * @param tauPSC Post-synaptic time constant
	 * @param modulatory Boolean value that is False for normal connections, True for modulatory connections 
	 * (which adjust neural properties rather than the input current)
	 * @return Termination that encapsulates all of the internal node terminations
	 * @throws StructuralException
	 */
	public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException {
		
	}
}
