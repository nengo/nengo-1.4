package ca.nengo.util.impl;

import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;

public class NodeThread extends Thread {
	
	private NodeThreadPool myNodePool;
	
	public NodeThread(NodeThreadPool nodePool){
		myNodePool = nodePool;
	}
	
	public void run(){
		Node workingNode;

		workingNode = myNodePool.getNextNode();
			
		while(workingNode != null)
		{
			try {
				workingNode.run(myNodePool.getStartTime(), myNodePool.getEndTime());
				
				myNodePool.finishedANode();

				Thread.yield();
			} catch (SimulationException e) {
				e.printStackTrace();
			}
			
			if(Thread.currentThread().isInterrupted()){
				return;
			}
			
			if((workingNode = myNodePool.getNextNode()) == null){
				return;
			}
		}
	}
}