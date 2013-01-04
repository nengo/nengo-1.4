/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "EnsembleOrigin.java". Description: 
"An Origin that is composed of the Origins of multiple Nodes"

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
 * Created on 31-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.PreciseSpikeOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;

/**
 * An Origin that is composed of the Origins of multiple Nodes. The dimension 
 * of this Origin equals the number of Nodes. All the Nodes must produce the 
 * same type of output (RealOutput or SpikeOutput) with the same Unit at the same 
 * time (these things can change in subsequent time steps, but they must change 
 * together for all Nodes). 
 *   
 * @author Bryan Tripp
 */
public class EnsembleOrigin implements Origin {

	private static final long serialVersionUID = 1L;
	
	private Node myNode;
	private Origin[] myNodeOrigins;
	private String myName;
	private boolean myRequiredOnCPU;
	
	/**
	 * @param node The parent Node
	 * @param name Name of this Origin 
	 * @param nodeOrigins Origins on individual Nodes that are combined to make this 
	 * 		Origin. Each of these is expected to have dimension 1, but this is not enforced. 
	 * 		Other dimensions are ignored. 
	 */
	public EnsembleOrigin(Node node, String name, Origin[] nodeOrigins) {
		myNode = node;
		myNodeOrigins = nodeOrigins;
		myName = name;
	}

	/**
	 * @see ca.nengo.model.Origin#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.nengo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return myNodeOrigins.length;
	}

	/**
	 * @return A composite of the first-dimensional outputs of all the Node Origins
	 * 		that make up the EnsembleOrigin. Node Origins should normally have 
	 * 		dimension 1, but this isn't enforced here. All Node Origins must have 
	 * 		the same units, and must output the same type of InstantaneousOuput (ie 
	 * 		either SpikeOutput or RealOutput), otherwise an exception is thrown.   
	 * @see ca.nengo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		InstantaneousOutput result = null;
		
		Units units = myNodeOrigins[0].getValues().getUnits(); //must be same for all
		
		if (myNodeOrigins[0].getValues() instanceof RealOutput) {
			result = composeRealOutput(myNodeOrigins, units);
		} else if (myNodeOrigins[0].getValues() instanceof PreciseSpikeOutput) {
			result = composePreciseSpikeOutput(myNodeOrigins, units);			
		} else if (myNodeOrigins[0].getValues() instanceof SpikeOutput) {
			result = composeSpikeOutput(myNodeOrigins, units);			
		}
		
		return result;
	}
	
	public void setValues(InstantaneousOutput values) {
		for(Origin origin : myNodeOrigins){
			origin.setValues(values);
		}
	}
	
	private static RealOutput composeRealOutput(Origin[] origins, Units units) throws SimulationException {
		float[] values = new float[origins.length];
		
		for (int i = 0; i < origins.length; i++) {
			InstantaneousOutput o = origins[i].getValues();
			if ( !(o instanceof RealOutput) ) {
				throw new SimulationException("Some of the Node Origins are not producing real-valued output");
			}
			if ( !o.getUnits().equals(units) ) {
				throw new SimulationException("Some of the Node Origins are producing outputs with non-matching units");
			}
			
			values[i] = ((RealOutput) o).getValues()[0];
		}
		
		return new RealOutputImpl(values, units, origins[0].getValues().getTime());
	}
	
	private static SpikeOutput composeSpikeOutput(Origin[] origins, Units units) throws SimulationException {
		boolean[] values = new boolean[origins.length];
		
		for (int i = 0; i < origins.length; i++) {
			InstantaneousOutput o = origins[i].getValues();
			if ( !(o instanceof SpikeOutput) ) {
				throw new SimulationException("Some of the Node Origins are not producing spiking output");
			}
			if ( !o.getUnits().equals(units) ) {
				throw new SimulationException("Some of the Node Origins are producing outputs with non-matching units");
			}
			
			values[i] = ((SpikeOutput) o).getValues()[0];
		}
		
		return new SpikeOutputImpl(values, units, origins[0].getValues().getTime());
	}

	private static PreciseSpikeOutput composePreciseSpikeOutput(Origin[] origins, Units units) throws SimulationException {
		float[] values = new float[origins.length];
		
		for (int i = 0; i < origins.length; i++) {
			InstantaneousOutput o = origins[i].getValues();
			if ( !(o instanceof PreciseSpikeOutput) ) {
				throw new SimulationException("Some of the Node Origins are not producing precise spiking output");
			}
			if ( !o.getUnits().equals(units) ) {
				throw new SimulationException("Some of the Node Origins are producing outputs with non-matching units");
			}
			
			values[i] = ((PreciseSpikeOutput) o).getSpikeTimes()[0];
		}
		
		return new PreciseSpikeOutputImpl(values, units, origins[0].getValues().getTime());
	}
	
	public void setRequiredOnCPU(boolean val){
        myRequiredOnCPU = val;
    }
    
    public boolean getRequiredOnCPU(){
        return myRequiredOnCPU;
    }
	
	
	/**
	 * @see ca.nengo.model.Origin#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

	/**
	 * Note: the clone references the same copies of the underlying node origins. This 
	 * will work if the intent is to duplicate an EnsembleOrigin on the same Ensemble. 
	 * More work is needed if this clone is part of an Ensemble clone, since the cloned
	 * EnsembleOrigin should then reference the new node origins, which we don't have 
	 * access to here.   
	 */
	@Override
	public EnsembleOrigin clone() throws CloneNotSupportedException {
		return new EnsembleOrigin(myNode, myName, myNodeOrigins);
	}
	
	public EnsembleOrigin clone(Ensemble ensemble) throws CloneNotSupportedException {
		EnsembleOrigin result = new EnsembleOrigin(myNode, myName, new Origin[myNodeOrigins.length]);
		
		// get origins for nodes in new ensemble
		try {
			for (int i = 0; i < myNodeOrigins.length; i++)
				result.myNodeOrigins[i] = ensemble.getNodes()[i].getOrigin(myNodeOrigins[i].getName());
		} catch (StructuralException e) {
			throw new CloneNotSupportedException("Error cloning EnsembleOrigin: " + e.getMessage());
		}
		return result;
	}

}
