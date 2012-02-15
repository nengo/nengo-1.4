package ca.nengo.util.impl;

import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.util.ThreadTask;
import ca.nengo.util.impl.NEFGPUInterface;

public class GPUThread extends NodeThread {

	NEFGPUInterface myNEFGPUInterface;
	
	public GPUThread(NodeThreadPool nodePool) {
		super(nodePool, new Node[0], 0, -1, new Projection[0], 0, -1, new ThreadTask[0], 0, -1);
		
		// create NEFGPUInterface from nodes and projections.
		// have to have some way to communicate which nodes and projections it decides are going to run on the GPU
		// so that the rest of the threads can run the remaining nodes and projections
		myNEFGPUInterface = new NEFGPUInterface();
	}
	
	protected void runNodes(float startTime, float endTime) throws SimulationException{
		//System.out.println("in runNodes GPUThread");
		
		myNEFGPUInterface.step(startTime, endTime);
	}
	
	public NEFGPUInterface getNEFGPUInterface(){
		return myNEFGPUInterface;
	}
	
	protected void kill(){
		super.kill();
		myNEFGPUInterface.kill();
	}
}
