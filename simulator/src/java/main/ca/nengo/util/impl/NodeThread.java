package ca.nengo.util.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;

public class NodeThread extends Thread {
	
	private NodeThreadPool myNodeThreadPool;

	private Node[] myNodes;
	private Projection[] myProjections;
	private float startTime;
	private float endTime;
	
	public NodeThread(NodeThreadPool nodePool, Node[] nodes, Projection[] projections){
		myNodeThreadPool = nodePool;
		myNodes = nodes;
		myProjections = projections;
	}
	
	public void waitForPool(){
		try{
			myNodeThreadPool.threadWait();
		}catch(Exception e){
		}
	}
	
	public void finished()
	{
		try{
			myNodeThreadPool.threadFinished();
		}catch(Exception e){
		}
	}
	
	public void run(){
		try{
			int i;
			float startTime, endTime;
			
			waitForPool();
			
			while(true) {
				startTime = myNodeThreadPool.getStartTime();
				endTime = myNodeThreadPool.getEndTime();
				
				for (i = 0; i < myProjections.length; i++) {
					InstantaneousOutput values = myProjections[i].getOrigin().getValues();
					myProjections[i].getTermination().setValues(values);
				}
				
				finished();
				
				for (i = 0; i < myNodes.length; i++) {
					myNodes[i].run(startTime, endTime);
				}
				
				finished();
				
				// This is the means of getting out of the loop. The pool will interrupt
				// this thread at the appropriate time.
				if(Thread.currentThread().isInterrupted()){
					return;
				}
			}
		}catch(SimulationException e){
		}
	}	
		
}