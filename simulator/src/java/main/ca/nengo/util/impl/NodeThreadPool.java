package ca.nengo.util.impl;

//import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
//import ca.nengo.model.SimulationException;

import java.lang.Math;
import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;



/**
 * A pool of threads for running nodes in. All interaction with the threads
 * is done through this class
 * 
 * @author Eric Crawford
 */
public class NodeThreadPool {
	protected static final int defaultNumThreads = 8;

	
	// numThreads can change throughout a simulation run. Therefore, it should not be used during a run,
	// only at the beginning of a run to create the threads.
	protected static int myNumThreads = defaultNumThreads;
	protected NodeThread[] myThreads;
	protected Object myLock;
	
	protected Node[] myNodes;
	protected Projection[] myProjections;
	
	protected volatile int numThreadsComplete;
	
	protected volatile boolean threadsRunning;
	protected volatile boolean runFinished;
	protected float myStartTime;
	protected float myEndTime;
	
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
	
	public boolean getRunFinished(){
		return runFinished;
	}
	
	// Dummy default constructor.
	protected NodeThreadPool(){
	}
	
	public NodeThreadPool(Node[] nodes, Projection[] projections){
		initialize(nodes, projections);
	}
	/*
	@SuppressWarnings("unchecked")
	public static <T> T[] copyOfRange(T[] original, int start, int end) {	
		if (original.length >= start && 0 <= start) {
			if (start <= end) {
				int length = end - start;
				int copyLength = Math.min(length, original.length - start);
				T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), length);
				System.arraycopy(original, start, copy, 0, copyLength);
				return copy;
			}
			throw new IllegalArgumentException();
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	*/
	
	protected void initialize(Node[] nodes, Projection[] projections){
		myLock = new Object();
		myNodes = nodes;
		myProjections = projections;
		
		myThreads = new NodeThread[myNumThreads];
		
		threadsRunning = false;
		runFinished = false;
		
		int nodesPerThread = (int) Math.ceil((float) myNodes.length / (float) myNumThreads);
		int projectionsPerThread = (int) Math.ceil((float) myProjections.length / (float) myNumThreads);
		
		int nodeOffset = 0, projectionOffset = 0;
		int nodeStartIndex, nodeEndIndex, projectionStartIndex, projectionEndIndex;
		
		for(int i = 0; i < myNumThreads; i++){
			
			nodeStartIndex = nodeOffset;
			nodeEndIndex = myNodes.length - nodeOffset >= nodesPerThread ?
					nodeOffset + nodesPerThread : myNodes.length;

			nodeOffset += nodesPerThread;
			
			projectionStartIndex = projectionOffset;
			projectionEndIndex = myProjections.length - projectionOffset >= projectionsPerThread ?
					projectionOffset + projectionsPerThread : myProjections.length;

			projectionOffset += projectionsPerThread;
			
			myThreads[i] = new NodeThread(this, myNodes, nodeStartIndex,
					nodeEndIndex, myProjections, projectionStartIndex, 
					projectionEndIndex);
			
			myThreads[i].setPriority(Thread.MAX_PRIORITY);
			myThreads[i].start();
		}
	}
	
	// Execute the run method of an array of nodes
	public void step(float startTime, float endTime){
		myStartTime = startTime;
		myEndTime = endTime;
		
		try
		{
			int oldPriority = Thread.currentThread().getPriority();
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			
			// start the projection processing
			startThreads();
			
			// start the node processing
			startThreads();
			
			Thread.currentThread().setPriority(oldPriority);
		}
		catch(Exception e)
		{}
	}
	
	private void startThreads() throws InterruptedException {
		synchronized(myLock){
			numThreadsComplete = 0;
			threadsRunning = true;
			
			myLock.notifyAll();
			myLock.wait();
		}
	}
	
	
	public void threadWait() throws InterruptedException{
		synchronized(myLock){
			while(!threadsRunning)
				myLock.wait();
		}
	}
	
	// Used by the threads of this pool to signal that they are done 
	// running their nodes for the current step
	public void threadFinished() throws InterruptedException{
		synchronized(myLock){
			numThreadsComplete++;
				
			if(numThreadsComplete == myThreads.length){
				threadsRunning = false;
				myLock.notifyAll();
			}	
			
			myLock.wait();
			
			threadWait();
		}
	}

	// Kill the threads by interrupting them. 
	// Each thread will handle it accordingly by ending its run method.
	public void kill(){
		synchronized(myLock)
		{
			threadsRunning = true;
			runFinished = true;
			
			for(int i = 0; i < myThreads.length; i++){
				myThreads[i].interrupt();
			}
			
			myLock.notifyAll();
		}
	}
}
