package ca.nengo.util.impl;

import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;

public class NodeThread extends Thread {
	
	private NodeThreadPool myNodeThreadPool;
	
	public NodeThread(NodeThreadPool nodePool){
		myNodeThreadPool = nodePool;
	}
	
	private Node getNextNode(){
		return myNodeThreadPool.getNextNode();
	}
	
	public void run(){
		Node workingNode;

		workingNode = myNodeThreadPool.getNextNode();
			
		while(workingNode != null)
		{
			try {
				workingNode.run(myNodeThreadPool.getStartTime(), myNodeThreadPool.getEndTime());
				
				myNodeThreadPool.finishedANode();

				Thread.yield();
			} catch (SimulationException e) {
				e.printStackTrace();
			}
			
			if(Thread.currentThread().isInterrupted()){
				return;
			}
			
			if((workingNode = getNextNode()) == null){
				return;
			}
		}
	}
}