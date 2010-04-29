package ca.nengo.util.impl;

import java.util.concurrent.locks.*;
import java.util.ArrayList;

import ca.nengo.model.Node;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;

// A GPUNodeThreadPool is like a NodeThreadPool except that it has an extra thread that does no
// node processing itself, but handles interaction with native code so that GPU nodes and normal nodes
// can be executed in parallel
public class GPUNodeThreadPool extends NodeThreadPool {
	public static boolean myUseGPU = true;
	protected GPUThread myNativeThread;
	protected Lock myGPUThreadLock;
	protected Condition myGPUThreadCondition;
	protected boolean myGPUThreadSleeping;
	protected Node[] myGPUNodes;
	protected int myNumGPUNodes;
	protected int oldNumThreads;
	
	// used by a thread about to sleep to check whether the signal has happened already or not
	// if it has, it shouldn't sleep, because it will never be woken up
	protected int mySignal;
	
	static{
		System.loadLibrary("NengoGPU");
	}
	
	
	public static void setUseGPU(boolean value){
		myUseGPU = value;
	}
	
	public static boolean getUseGPU(){
		return myUseGPU;
	}
	
	public GPUNodeThreadPool(Node[] nodes){
		Initialize(nodes);
	}
	
	public void Sleep(){
		myGPUThreadLock.lock();
		
		if(mySignal == 0)
		{
			try
			{
				myGPUThreadCondition.await();
			}catch(InterruptedException e){}
		}
		
		mySignal--;
		
		myGPUThreadLock.unlock();
	}
	
	protected void Initialize(Node[] nodes){
		myGPUThreadLock = new ReentrantLock();
		myGPUThreadCondition = myGPUThreadLock.newCondition();
		myNumGPUNodes = 0;
		mySignal = 0;
		
		ArrayList<Node> GPUNodeList = new ArrayList<Node>();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		
		for(int i = 0; i < nodes.length; i++){
			if(nodes[i] instanceof NEFEnsembleImpl && ((NEFEnsembleImpl)nodes[i]).isGPUNode())
			{
				myNumGPUNodes++;
				GPUNodeList.add(nodes[i]);
			}else{
				nodeList.add(nodes[i]); 	
			} 
		}
		
		myGPUNodes = new Node[0];
		myGPUNodes = GPUNodeList.toArray(myGPUNodes);
		
		nodes = new Node[0];
		nodes = nodeList.toArray(nodes);
		
		myNativeThread = new GPUThread(this);
		myNativeThread.setPriority(Thread.MAX_PRIORITY - 1);
		myNativeThread.start();
		
		// Here we set myMyNumThreads to one if it was zero before so that we have at least one thread to process the non-GPU nodes.
		// We save that value and set it back after the run is finished.
		if(myNumThreads == 0)
		{
			oldNumThreads = 0;
			myNumThreads = 1;
		}
		
		super.Initialize(nodeList.toArray(nodes));
		myNumNodesRequired += myNumGPUNodes;
		
		Thread.yield();
	}
	
	public void step(float startTime, float endTime){
		int oldPriority = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		myStartTime = startTime;
		myEndTime = endTime;
		myNumNodesProcessed = 0;
		
		myGPUThreadLock.lock();
		mySignal = 1;
		myGPUThreadCondition.signalAll();
		myGPUThreadLock.unlock();
		
		for(int i = 0; i < myNodeArray.length; i++){
				myNodes.offer(myNodeArray[i]);
		}
		
		waitForThreads();
		
		Thread.currentThread().setPriority(oldPriority);
	}
	
	// Kill the threads by interrupting them. 
	// Each thread will handle it accordingly by ending its run method.
	public void kill(){
		super.kill();
		myNativeThread.interrupt();
	}
	
	// Called by the threads of this node pool to signal that they have completed nodes
	public void finishedGPUNodes(){
		synchronized(myLock){
			myNumNodesProcessed += myNumGPUNodes;
			if(finishedRun()){
				myLock.notify();
			}
		}
	}
}