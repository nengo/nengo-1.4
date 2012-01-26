package ca.nengo.util.impl;

//import ca.nengo.model.InstantaneousOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.util.TaskSpawner;
import ca.nengo.util.ThreadTask;

/**
 * A pool of threads for running nodes in. All interaction with the threads
 * is done through this class
 *
 * @author Eric Crawford
 */
public class NodeThreadPool {
	protected static final int maxNumJavaThreads = 100;
	protected static final int defaultNumJavaThreads = 8;


	// numThreads can change throughout a simulation run. Therefore, it should not be used during a run,
	// only at the beginning of a run to create the threads.
	protected static int myNumJavaThreads = defaultNumJavaThreads;
	protected int myNumThreads;
	protected NodeThread[] myThreads;
	protected Object myLock;

	protected Node[] myNodes;
	protected Projection[] myProjections;
    protected ThreadTask[] myTasks;

	protected volatile int numThreadsComplete;

	protected volatile boolean threadsRunning;
	protected volatile boolean runFinished;
	protected float myStartTime;
	protected float myEndTime;

	public static int getNumJavaThreads(){
		return myNumJavaThreads;
	}

	public static void setNumJavaThreads(int value){
		myNumJavaThreads = value;
	}
	
	public static int getMaxNumJavaThreads(){
		return maxNumJavaThreads;
	}
	

	public static boolean isMultithreading(){
		return myNumJavaThreads != 0;
	}

	// to turn it back on, call setNumThreads with a positive value
	public static void turnOffMultithreading(){
		myNumJavaThreads = 0;
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
	
	public NodeThreadPool(Network network){
		initialize(network);
	}
	
	protected void initialize(Network network){
		
		myLock = new Object();
		
		//not breaking down network arrays
		Node[] nodes = network.getNodes();
		Projection[] projections = network.getProjections();
		
		myNodes = collectNodes(nodes, false);
		myProjections = collectProjections(nodes, projections);
		myTasks = collectTasks(nodes);
		
		// set the run configuration for the current run.
		NEFGPUInterface.setRequestedNumDevices(network.getCountGPU());
		setNumJavaThreads(network.getCountJavaThreads());
		
		threadsRunning = false;
		runFinished = false;
		
		if(NEFGPUInterface.getUseGPU()){  
			myNumThreads = myNumJavaThreads + 1;
	    }else{
	    	myNumThreads = myNumJavaThreads;
	    }
		
		myThreads = new NodeThread[myNumThreads];
		
		if(NEFGPUInterface.getUseGPU()){  
			GPUThread gpuThread = new GPUThread(this);
			
			// The NEFGPUInterface removes from myNodes ensembles that are to be run on the GPU and returns the rest.
			myNodes = gpuThread.getNEFGPUInterface().takeGPUNodes(myNodes);
			
			// The NEFGPUInterface removes from myProjections projections that are to be run on the GPU and returns the rest.
			myProjections = gpuThread.getNEFGPUInterface().takeGPUProjections(myProjections);
			
			gpuThread.getNEFGPUInterface().initialize();
			
			myThreads[myNumJavaThreads] = gpuThread;
			
			gpuThread.setPriority(Thread.MAX_PRIORITY);
			gpuThread.start();
		}
		
		//In the remaining nodes, do break down the NetworkArrays, we don't want to be running nodes which are members of
		// classes which derive from the NetworkImpl class since NetworkImpls create their own LocalSimulator when run.
		myNodes = collectNodes(myNodes, true);

		int nodesPerJavaThread = (int) Math.ceil((float) myNodes.length / (float) myNumJavaThreads);
		int projectionsPerJavaThread = (int) Math.ceil((float) myProjections.length / (float) myNumJavaThreads);
        int tasksPerJavaThread = (int) Math.ceil((float) myTasks.length / (float) myNumJavaThreads);

		int nodeOffset = 0, projectionOffset = 0, taskOffset = 0;
		int nodeStartIndex, nodeEndIndex, projectionStartIndex, projectionEndIndex, taskStartIndex, taskEndIndex;

		for(int i = 0; i < myNumJavaThreads; i++){

			nodeStartIndex = nodeOffset;
			nodeEndIndex = myNodes.length - nodeOffset >= nodesPerJavaThread ?
					nodeOffset + nodesPerJavaThread : myNodes.length;

			nodeOffset += nodesPerJavaThread;

			projectionStartIndex = projectionOffset;
			projectionEndIndex = myProjections.length - projectionOffset >= projectionsPerJavaThread ?
					projectionOffset + projectionsPerJavaThread : myProjections.length;

			projectionOffset += projectionsPerJavaThread;

			taskStartIndex = taskOffset;
			taskEndIndex = myTasks.length - taskOffset >= tasksPerJavaThread ?
					taskOffset + tasksPerJavaThread : myTasks.length;

			taskOffset += tasksPerJavaThread;

			myThreads[i] = new NodeThread(this, myNodes, nodeStartIndex,
					nodeEndIndex, myProjections, projectionStartIndex,
					projectionEndIndex, myTasks, taskStartIndex, taskEndIndex);

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

			// start the task processing
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
			while(!threadsRunning) {
                myLock.wait();
            }
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
	

	
	
    /**
     * Bring all nodes to the top level so we can run them all at once.
     * Retrieves nodes depth-first. Used for multithreaded and GPU-enabled runs.
     * Lets the caller choose whether to break down network arrays.
     */
    public static Node[] collectNodes(Node[] startingNodes, boolean breakDownNetworkArrays){

        ArrayList<Node> nodes = new ArrayList<Node>();

        List<Node> nodesToProcess = new LinkedList<Node>();
        nodesToProcess.addAll(Arrays.asList(startingNodes));

        Node workingNode;

        boolean isNetwork = false;
        while(nodesToProcess.size() != 0)
        {
            workingNode = nodesToProcess.remove(0);
            
            //Decide whether to break the node into its subnodes
            if((workingNode.getClass().getCanonicalName().contains("CCMModelNetwork"))){
            	isNetwork = false;
            }
            else if(workingNode.getClass().getCanonicalName() == "org.python.proxies.nef.array$NetworkArray$6")
            {
            	if(breakDownNetworkArrays){
            		isNetwork = true;
            	}else{
            		isNetwork = false;
            	}
            }
            else if(workingNode instanceof Network){
            	isNetwork = true;
            }
            else{
                isNetwork = false;
            }
            
            
            if(isNetwork){
            	List<Node> nodeList = new LinkedList<Node>(Arrays.asList(((Network) workingNode).getNodes()));

                nodeList.addAll(nodesToProcess);
                nodesToProcess = nodeList;
            }
            else{
            	nodes.add(workingNode);
            } 
        }

        return nodes.toArray(new Node[0]);
    }

    
    
    public static ThreadTask[] collectTasks(Node[] startingNodes){

        ArrayList<ThreadTask> tasks = new ArrayList<ThreadTask>();

        List<Node> nodesToProcess = new LinkedList<Node>();
        nodesToProcess.addAll(Arrays.asList(startingNodes));

        Node workingNode;

        while(nodesToProcess.size() != 0)
        {
            workingNode = nodesToProcess.remove(0);

            if(workingNode instanceof Network && !(workingNode.getClass().getCanonicalName().contains("CCMModelNetwork")))
            {
                List<Node> nodeList = new LinkedList<Node>(Arrays.asList(((Network) workingNode).getNodes()));

                nodeList.addAll(nodesToProcess);
                nodesToProcess = nodeList;
            }
            else if(workingNode instanceof TaskSpawner)
            {
                tasks.addAll(Arrays.asList(((TaskSpawner) workingNode).getTasks()));
            }
        }

        return tasks.toArray(new ThreadTask[0]);
    }

    // I want this function to give me networkArrays and NEFEnsembles that are set to run on the GPU.
    public static Node[] collectNetworkArraysForGPU(Node[] startingNodes){
        ArrayList<Node> nodes = new ArrayList<Node>();

        List<Node> nodesToProcess = new LinkedList<Node>();
        nodesToProcess.addAll(Arrays.asList(startingNodes));

        Node workingNode;

        while(nodesToProcess.size() != 0)
        {
            workingNode = nodesToProcess.remove(0);

            // The purpose of this function is to have a list of NetworkArrays. On the GPU, all NEF
            // ensembles are considered to be part of a NetworkArray. NEF ensembles that are not
            // ostensibly part of a NetworkArray are considered by the GPU to be part of a NetworkArray
            // that contains only one nodes. In this way, we do not have to make special considerations
            // for nodes that are part of the NetworkArray. Processing a network array that contains only
            // one node is effectively the same as processing that one node on its own, so nothing is lost.
            boolean isNEFEnsemble = workingNode instanceof NEFEnsembleImpl;
            boolean isNetworkArray = workingNode.getClass().getCanonicalName() == "org.python.proxies.nef.array$NetworkArray$6";
            boolean isNetwork = workingNode instanceof NetworkImpl;
           

            if(isNEFEnsemble){
            	if( ((NEFEnsembleImpl) workingNode).getUseGPU() )
            		nodes.add(workingNode);
            }
            else if(isNetworkArray){
            	
            	if( ((NetworkImpl) workingNode).getUseGPU() )
            		nodes.add(workingNode);
            }
            else if(isNetwork){
            	// notice we don't dive into networkArrays. We only want to run a network array on the GPU if all its ensembles run on the GPU.
                List<Node> nodeList = new LinkedList<Node>(Arrays.asList(((Network) workingNode).getNodes()));

                nodeList.addAll(nodesToProcess);
                nodesToProcess = nodeList;

                //nodesToProcess = Arrays.asList(((Network) workingNode).getNodes()).addAll(nodesToProcess);
                //nodesToProcess.addAll(Arrays.asList(((Network) workingNode).getNodes()));
            }
        }

        return nodes.toArray(new Node[0]);
    }


    /**
     * Bring all projections to the top level so we can run them all at once.
     * Used for multithreaded and GPU-enabled runs.
     */
    public static Projection[] collectProjections(Node[] startingNodes, Projection[] startingProjections){

        ArrayList<Projection> projections = new ArrayList<Projection>(Arrays.asList(startingProjections));

        List<Node> nodesToProcess = new LinkedList<Node>();
        nodesToProcess.addAll(Arrays.asList(startingNodes));

        Node workingNode;

        while(nodesToProcess.size() != 0)
        {
            workingNode = nodesToProcess.remove(0);

            if(workingNode instanceof Network) {
                List<Node> nodeList = new LinkedList<Node>(Arrays.asList(((Network) workingNode).getNodes()));

                nodeList.addAll(nodesToProcess);
                nodesToProcess = nodeList;

                projections.addAll(Arrays.asList(((Network) workingNode).getProjections()));
            }
        }

        return projections.toArray(new Projection[0]);
    }
}
