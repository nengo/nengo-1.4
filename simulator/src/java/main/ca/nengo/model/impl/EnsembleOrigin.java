/*
 * Created on 31-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.PreciseSpikeOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SpikeOutput;
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
	public Origin clone() throws CloneNotSupportedException {
		return new EnsembleOrigin(myNode, myName, myNodeOrigins);
	}

}
