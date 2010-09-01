package ca.nengo.util.impl;

import ca.nengo.model.Node;
//import ca.nengo.model.impl.NetworkImpl;

//import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * A pool of threads for running nodes in. All interaction with the threads
 * is done through this class
 * 
 * @author Eric Crawford
 */
public class NodeThreadPool{
	protected static final int defaultNumThreads = 0;
	
	// numThreads can change throughout a simulation run. Therefore, it should not be used during a run,
	// only at the beginning of a run to create the threads. During a run, use myThreads.length.
	protected static int myNumThreads = defaultNumThreads;
	protected NodeThread[] myNodeThreads;
	protected Object myLock;
	protected LinkedBlockingQueue<Node> myNodes;
	protected Node[] myNodeArray;
	protected int myNumNodesRequired;
	protected int myNumNodesProcessed;
	protected volatile float myStartTime;
	protected volatile float myEndTime;
	protected volatile boolean isSleeping;
	protected volatile boolean myKill = false;
	
	public static int getNumThreads(){
		return myNumThreads;
	}
	
	public static void setNumThreads(int value){
		myNumThreads = value;
	}
	
	public static boolean isMultithreading(){
		return myNumThreads != 0;
	}
	
	// to turn it back on, call setNumThreads with a positive value
	public static void turnOffMultithreading(){
		myNumThreads = 0;
	}

	public float getStartTime(){
		return myStartTime;
	}
	
	public float getEndTime(){
		return myEndTime;
	}
	
	public boolean finishedRun(){
		synchronized(myLock){
			return myNumNodesProcessed == myNumNodesRequired;
		}
	}

	// Dummy default constructor. Use at your own risk.
	protected NodeThreadPool(){
	}
	
	public NodeThreadPool(Node[] nodes){
		initialize(nodes);
	}
	
	protected void initialize(Node[] nodes){
		myNodes = new LinkedBlockingQueue<Node>();
		myLock = new Object();

		myNodeArray = nodes;
		
		myNumNodesProcessed = 0;
		myNumNodesRequired = myNodeArray.length;
		
		myNodeThreads = new NodeThread[myNumThreads];
		
		for(int i = 0; i < myNumThreads; i++){
			myNodeThreads[i] = new NodeThread(this);
			myNodeThreads[i].setPriority(Thread.MAX_PRIORITY - 1);
			myNodeThreads[i].start();
		}
		
		Thread.yield();	
	}
	
	// Execute the run method of an array of nodes
	public void step(float startTime, float endTime){
		int oldPriority = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		myStartTime = startTime;
		myEndTime = endTime;
		myNumNodesProcessed = 0;

		for(int i = 0; i < myNodeArray.length; i++){
			myNodes.offer(myNodeArray[i]);
		}
		
		waitForThreads();
		Thread.currentThread().setPriority(oldPriority);
	}
	
	
	// Called from within the node pool. Waits until all the nodes in the run have been executed.
	protected void waitForThreads(){
		synchronized(myLock){
			if(!finishedRun()){
				try{
					isSleeping = true;
					myLock.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}

	// Kill the threads by interrupting them. 
	// Each thread will handle it accordingly by ending its run method.
	public void kill(){
		myKill = true;
		for(int i = 0; i < myNodeThreads.length; i++){
			myNodeThreads[i].interrupt();
		}
	}
	
	// To be called by the threads of this node pool. Give returns the next node to be processed.
	// myNodes.take will cause the thread to wait until there is a node to be processed.
	public Node getNextNode(){
		try{
			return myNodes.take();
		}catch(InterruptedException e){
			return null;
		}
	}
	
	// Called by the threads of this node pool to signal that they have completed a node.
	public void finishedANode(){
		synchronized(myLock){
			myNumNodesProcessed++;
			if(finishedRun() && isSleeping){
				myLock.notify();
			}
		}
	}
}
