package ca.nengo.util.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.nengo.math.impl.MultiLevelKLNetworkPartitioner;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.Projection;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.EnsembleTermination;
import ca.nengo.model.impl.NetworkArrayImpl;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.impl.NetworkImpl.OriginWrapper;
import ca.nengo.model.impl.NetworkImpl.TerminationWrapper;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.LIFSpikeGenerator;
import ca.nengo.model.neuron.impl.SpikingNeuron;

/**
 * Allows running NEFEnsembles on the GPU. 
 * Passes the ensemble data to the GPU through a native function. 
 * Passes input to the GPU each step and stores the output from the GPU in the appropriate locations.
 *
 * @author Eric Crawford
 */
public class NEFGPUInterface {
	private static boolean myUseGPU = false;
	private static int myNumDevices = 0;
	private static int myNumAvailableDevices = 0;
	private static String myErrorMessage;
	
	private static boolean showTiming = false;
	private boolean myShowTiming;
	private long averageTimeSpentInGPU;
	private long averageTimeSpentInCPU;
	private long totalRunTime;
	private int numSteps;
	
	protected static boolean myRequireAllOutputsOnCPU;
	
	
	protected NEFEnsembleImpl[] myGPUEnsembles;
	protected Projection[] myGPUProjections;
	protected Projection[] nonGPUProjections;
	protected Node[] myGPUNetworkArrays;
	
	protected Node[] myNodes;
	protected Projection[] myProjections;
	
	protected float myStartTime;
	protected float myEndTime;
	
	float[][][] representedInputValues;
	float[][][] representedOutputValues;
	float[][] spikeOutput;
	boolean[][] inputOnGPU;
	
	/**	Load the shared library that contains the native functions.
	 * This is called just once, when this class is initially loaded.
	 * 
	 */
	static{
		try {
			myErrorMessage = "";
			System.loadLibrary("NengoGPU");
			myNumAvailableDevices = nativeGetNumDevices();
			
			if(myNumAvailableDevices < 1)
			{
				myErrorMessage = "No CUDA-enabled GPU detected.";
				System.out.println(myErrorMessage);
			}
			
		} catch (java.lang.UnsatisfiedLinkError e) {
			myNumAvailableDevices = 0;
			myErrorMessage = "Couldn't load native library NengoGPU - Linker error:";
			System.out.println(myErrorMessage);
			System.out.println(e);
		} catch (Exception e) {
			myNumAvailableDevices = 0;
			myErrorMessage = "Couldn't load native library NengoGPU - General exception:";
			myErrorMessage = e.getMessage();
			System.out.println(myErrorMessage);
			System.out.println(e.getStackTrace());
		}
	}

	static native int nativeGetNumDevices();

	static native void nativeSetupRun(float[][][][] terminationTransforms,
			int[][] isDecodedTermination, float[][] terminationTau,
			float[][][] encoders, float[][][][] decoders, float[][] neuronData,
			int[][] projections, int[][] networkArrayData, int[][] ensembleData, 
			int[] isSpikingEnsemble, int[] collectSpikes, int[][] outputRequiredOnCPU, float maxTimeStep, 
			int[] deviceForNetworkArrays, int numDevicesRequested);

	static native void nativeStep(float[][][] representedInput,
			float[][][] representedOutput, float[][] spikes, float startTime,
			float endTime);

	static native void nativeKill();
	
	public NEFGPUInterface(){
    }

	public static int getNumAvailableDevices(){
		return myNumAvailableDevices;
	}
	
	public static void setRequestedNumDevices(int value){
		myNumDevices = Math.min(Math.max(value, 0), myNumAvailableDevices);
	}
	
	public static int getRequestedNumDevices(){
		return myNumDevices;
	}
	
	// get whether or not to use the GPU. set whether or not to use the GPU by using setRequestedNumDevices
	public static boolean getUseGPU(){
		return myNumDevices > 0;
	}
	
	public static void showGPUTiming(){
		showTiming = true;
	}
	
	public static void hideGPUTiming(){
		showTiming = false;
	}
	
	public static void requireAllOutputsOnCPU(boolean require){
		myRequireAllOutputsOnCPU = require;
	}
	
	public static String getErrorMessage(){
		return myErrorMessage;
	}
	
	/**
	 * Gets all the necessary data from the nodes and projections which are assigned to run on GPUss
	 * and puts it in a form appropriate for passing to the native setup function. The native setup function
	 * will create a thread for each GPU in use, process the data further until its in a form suitable
	 * for running on the GPU, and finally move all the data to the GPU. The GPU threads will be waiting
	 * for a call to nativeStep which will tell them to take a step.
	 * 
	 * @author Eric Crawford
	 */
	public void initialize(){
		int[] nodeAssignments = findOptimalNodeAssignments(myGPUNetworkArrays, myGPUProjections, myNumDevices);

		boolean requireAllOutputsOnCPU = myRequireAllOutputsOnCPU;
		
		myShowTiming = showTiming;
		if(myShowTiming){
			averageTimeSpentInGPU = 0;
			averageTimeSpentInCPU = 0;
			numSteps = 0;
			totalRunTime = 0;
		}
		
		ArrayList<Node> GPUNodeList = new ArrayList<Node>();
		
		for(Node currentNode : myGPUNetworkArrays){
			// all the nodes in myGPUNetworkArrays are going to run on the GPU. 
			if(currentNode instanceof NetworkArrayImpl){
				List<Node> nodeList = Arrays.asList(((NetworkImpl) currentNode).getNodes());
				GPUNodeList.addAll(nodeList);
			}
			else{
				GPUNodeList.add(currentNode);
			}
		}
		
		myGPUEnsembles = GPUNodeList.toArray(new NEFEnsembleImpl[0]);

		if (myGPUEnsembles.length == 0)
			return;

		// Put the data in a format appropriate for passing to the GPU. 
		// Most of this function is devoted to this task.
		int i = 0, j = 0, k = 0, numEnsemblesCollectingSpikes = 0;
		NEFEnsemble workingNode;
		Termination[] terminations;
		DecodedOrigin[] origins;

		float[][][][] terminationTransforms = new float[myGPUEnsembles.length][][][];
		int[][] isDecodedTermination = new int[myGPUEnsembles.length][];
		float[][] terminationTau = new float[myGPUEnsembles.length][];
		float[][][] encoders = new float[myGPUEnsembles.length][][];
		float[][][][] decoders = new float[myGPUEnsembles.length][][][];
		float[][] neuronData = new float[myGPUEnsembles.length][];
		EnsembleData ensembleData = new EnsembleData();
		int[][] ensembleDataArray = new int[myGPUEnsembles.length][];
		int[] collectSpikes = new int[myGPUEnsembles.length];
		int[][] outputRequiredOnCPU = new int[myGPUNetworkArrays.length][];
		int[] isSpikingEnsemble = new int[myGPUEnsembles.length];
		float maxTimeStep = ((LIFSpikeGenerator) ((SpikingNeuron) ((NEFEnsembleImpl) myGPUEnsembles[0])
				.getNodes()[0]).getGenerator()).getMaxTimeStep();
		
		

		
		// We put the list of projections in terms of the GPU nodes
		// For each projection we record 4 numbers: the index of the origin
		// ensemble, the index of the origin in its ensemble, the index of
		// the termination ensemble and the index of the termination in its ensemble
		int[][] adjustedProjections = new int[myGPUProjections.length][6];
		
		
		inputOnGPU = new boolean[myGPUNetworkArrays.length][];
		
		Node workingArray;
		int networkArrayOffset = 0;

		NetworkArrayData networkArrayData = new NetworkArrayData();
		int[][] networkArrayDataArray = new int[myGPUNetworkArrays.length][];

		int totalInputSize = 0;
		
		// store networkArray data
		for(i = 0; i < myGPUNetworkArrays.length; i++){
			
			networkArrayData.reset();
			workingArray = myGPUNetworkArrays[i];
			
			networkArrayData.indexOfFirstNode = networkArrayOffset;
			
			if(workingArray instanceof NEFEnsembleImpl){
				networkArrayOffset++;
			}else{
				networkArrayOffset += ((NetworkImpl) workingArray).getNodes().length;
			}
			
			networkArrayData.endIndex = networkArrayOffset;
				
			Termination[] networkArrayTerminations = workingArray.getTerminations();
			networkArrayData.numTerminations = networkArrayTerminations.length;
			
			
			for(j = 0; j < networkArrayTerminations.length; j++){
				networkArrayData.totalInputSize += networkArrayTerminations[j].getDimensions();
			}
			
			totalInputSize += networkArrayData.totalInputSize;
			
			Origin[] networkArrayOrigins;
			if(workingArray instanceof NEFEnsembleImpl)
			{
				networkArrayOrigins = ((NEFEnsembleImpl) workingArray).getDecodedOrigins();
			}else{
				networkArrayOrigins = workingArray.getOrigins();
			}
			networkArrayData.numOrigins = networkArrayOrigins.length;
			
			for(j = 0; j < networkArrayOrigins.length; j++){
				networkArrayData.totalOutputSize += networkArrayOrigins[j].getDimensions();
			}
			
			if(workingArray instanceof NEFEnsembleImpl){
				networkArrayData.numNeurons = ((NEFEnsembleImpl) workingArray).getNeurons();
			}else{
				Node[] subNodes = ((NetworkImpl) workingArray).getNodes();
				for(j = 0; j < subNodes.length; j++){
					networkArrayData.numNeurons += ((NEFEnsembleImpl) subNodes[j]).getNeurons();
				}
			}

			networkArrayDataArray[i] = networkArrayData.getAsArray();
			
			inputOnGPU[i] = new boolean[networkArrayTerminations.length];
			outputRequiredOnCPU[i] = new int[networkArrayOrigins.length];
			
			for(j = 0; j < networkArrayTerminations.length; j++){
				Termination termination = networkArrayTerminations[j];
				boolean terminationWrapped = termination instanceof TerminationWrapper;
				if(terminationWrapped)
					termination = ((TerminationWrapper) termination).getBaseTermination();
				
				k = 0;
				boolean projectionMatches = false;
				
				while(!projectionMatches && k < myGPUProjections.length){
					Termination projectionTermination = myGPUProjections[k].getTermination();
					boolean projectionTerminationWrapped = projectionTermination instanceof TerminationWrapper;
					if(projectionTerminationWrapped)
						projectionTermination = ((TerminationWrapper) projectionTermination).getBaseTermination();
					
					projectionMatches = termination == projectionTermination;
					
					if(projectionMatches)
						break;
					
					k++;
				}
	
				if (projectionMatches) {
					adjustedProjections[k][2] = i;
					adjustedProjections[k][3] = j;
					adjustedProjections[k][4] = termination.getDimensions();
					adjustedProjections[k][5] = -1;
	
					inputOnGPU[i][j] = true;
				} else {
					inputOnGPU[i][j] = false;
				}
			}
			
			for (j = 0; j < networkArrayOrigins.length; j++) {
				Origin origin = networkArrayOrigins[j];
				boolean originWrapped = origin instanceof OriginWrapper;
				if(originWrapped)
					origin = ((OriginWrapper) origin).getWrappedOrigin();
				
				for (k = 0; k < myGPUProjections.length; k++) {
					Origin projectionOrigin = myGPUProjections[k].getOrigin();
					boolean projectionOriginWrapped = projectionOrigin instanceof OriginWrapper;
					
					if(projectionOriginWrapped)
						projectionOrigin = ((OriginWrapper) projectionOrigin).getWrappedOrigin();
					
					if (origin == projectionOrigin) {
						adjustedProjections[k][0] = i;
						adjustedProjections[k][1] = j;
					}
				}
				
				outputRequiredOnCPU[i][j] = (origin.getRequiredOnCPU() || requireAllOutputsOnCPU) ? 1 : 0;
				
				// even if its not explicitly required on the CPU, it might be implicitly
				// if it is attached to a projection whose termination is on the CPU
				if(outputRequiredOnCPU[i][j] == 0){
    				for (k = 0; k < nonGPUProjections.length; k++) {
    					Origin projectionOrigin = nonGPUProjections[k].getOrigin();
                        boolean projectionOriginWrapped = projectionOrigin instanceof OriginWrapper;
                        
                        if(projectionOriginWrapped)
                            projectionOrigin = ((OriginWrapper) projectionOrigin).getWrappedOrigin();
                    
                        if (origin == projectionOrigin){
                            outputRequiredOnCPU[i][j] = 1;
                        }
    				}
				}
			}
		}
		
		nonGPUProjections = null;
		
		// store NEFEnsemble data
		for (i = 0; i < myGPUEnsembles.length; i++) {
			
			workingNode = myGPUEnsembles[i];
			
			ensembleData.reset();

			ensembleData.dimension = workingNode.getDimension();
			ensembleData.numNeurons = workingNode.getNodeCount();
			
			isSpikingEnsemble[i] = (workingNode.getMode() == SimulationMode.DEFAULT) ? 1 : 0;

			terminations = workingNode.getTerminations();

			int terminationDim = 0;
			ensembleData.maxTransformDimension = 0;

			terminationTransforms[i] = new float[terminations.length][][];
			terminationTau[i] = new float[terminations.length];
			isDecodedTermination[i] = new int[terminations.length];
			
			for (j = 0; j < terminations.length; j++) {

				if (terminations[j] instanceof DecodedTermination) {
					terminationTransforms[i][j] = ((DecodedTermination) terminations[j])
							.getTransform();
					terminationTau[i][j] = terminations[j].getTau();

					terminationDim = terminations[j].getDimensions();
					ensembleData.totalInputSize += terminationDim;

					if (terminationDim > ensembleData.maxTransformDimension) {
						ensembleData.maxTransformDimension = terminationDim;
					}

					isDecodedTermination[i][j] = 1;
					
					ensembleData.numDecodedTerminations++;
				} else if (terminations[j] instanceof EnsembleTermination) {
					terminationTransforms[i][j] = new float[1][1];
					
					
					// when we do learning, this will have to be changed, as well as some code in the NengoGPU library.
					// currently it assumes all neurons in the ensemble have the same weight for each non-decoded termination
					// (mainly just to support gates which have uniform negative weights).
					// When we do learning, will have to extract the whole weight matrix.
					Termination[] neuronTerminations = ((EnsembleTermination) terminations[j]).getNodeTerminations();
					terminationTransforms[i][j][0] = ((PlasticNodeTermination) neuronTerminations[0]).getWeights();
					terminationTau[i][j] = terminations[j].getTau();
					isDecodedTermination[i][j] = 0;

					terminationDim = terminations[j].getDimensions();
					
					ensembleData.totalInputSize += terminationDim;
					ensembleData.nonDecodedTransformSize += terminationDim;
					ensembleData.numNonDecodedTerminations++;
				}
			}

			encoders[i] = workingNode.getEncoders();
			float[] radii = workingNode.getRadii();
			for (j = 0; j < encoders[i].length; j++) {
				for (k = 0; k < encoders[i][j].length; k++)
					encoders[i][j][k] = encoders[i][j][k] / radii[k];
			}

			origins = ((NEFEnsembleImpl) workingNode).getDecodedOrigins();

			ensembleData.numOrigins = origins.length;
			ensembleData.maxDecoderDimension = 0;

			decoders[i] = new float[origins.length][][];
			int originDim;
			for (j = 0; j < origins.length; j++) {
				decoders[i][j] = origins[j].getDecoders();
				originDim = origins[j].getDimensions();

				ensembleData.totalOutputSize += originDim;

				if (originDim > ensembleData.maxDecoderDimension) {
					ensembleData.maxDecoderDimension = originDim;
				}
			}

			neuronData[i] = ((NEFEnsembleImpl) workingNode).getStaticNeuronData();

			collectSpikes[i] = workingNode.isCollectingSpikes() ? 1 : 0;
			numEnsemblesCollectingSpikes++;

			ensembleDataArray[i] = ensembleData.getAsArray();
		}
		
		nativeSetupRun(terminationTransforms, isDecodedTermination,
				terminationTau, encoders, decoders, neuronData,
				adjustedProjections, networkArrayDataArray, ensembleDataArray,
				isSpikingEnsemble, collectSpikes, outputRequiredOnCPU, maxTimeStep, nodeAssignments, myNumDevices);

		// Set up the data structures that we pass in and out of the native step call.
		// They do not change in size from step to step so we can re-use them.
		representedInputValues = new float[myGPUNetworkArrays.length][][];
		representedOutputValues = new float[myGPUNetworkArrays.length][][];
		spikeOutput = new float [myGPUEnsembles.length][];
		
		for (i = 0; i < myGPUNetworkArrays.length; i++) {
			terminations = myGPUNetworkArrays[i].getTerminations();
			representedInputValues[i] = new float[terminations.length][];
		}

		for (i = 0; i < myGPUNetworkArrays.length; i++) {
			Origin[] networkArrayOrigins;
			if(myGPUNetworkArrays[i] instanceof NEFEnsembleImpl)
			{
				networkArrayOrigins = ((NEFEnsembleImpl) myGPUNetworkArrays[i]).getDecodedOrigins();
			}else{
				networkArrayOrigins = myGPUNetworkArrays[i].getOrigins();
			}

			representedOutputValues[i] = new float[networkArrayOrigins.length][];

			for (j = 0; j < networkArrayOrigins.length; j++) {
				if(outputRequiredOnCPU[i][j] != 0){
					representedOutputValues[i][j] = new float[networkArrayOrigins[j].getDimensions()];
				}else{
					representedOutputValues[i][j] = null;
				}
			}
		}
		
		for (i = 0; i < myGPUEnsembles.length; i++) {
			if(collectSpikes[i] != 0){
				spikeOutput[i] = new float[myGPUEnsembles[i].getNeurons()];
			}else{
				spikeOutput[i] = null;
			}
		}
	}
	
	/**
	 * 1. Load data from terminations into "representedInputValues". 
	 * 2. Call nativeStep which will run the GPU's for one step and return the results in "representedOutputValues".
	 * 3. Put the data from "representedOutputValues" into the appropriate origins.
	 * 
	 * @author Eric Crawford
	 */
	public void step(float startTime, float endTime){
		
		myStartTime = startTime;
		myEndTime = endTime;

		
		if(myGPUEnsembles.length > 0){
		
			try {
				
				int count, i, j;
				float[] inputRow = new float[0];
				Termination[] terminations;
				
				// get the input data from the terminations
				for (i = 0; i < myGPUNetworkArrays.length; i++) {
					terminations = myGPUNetworkArrays[i].getTerminations();
					count = terminations.length;
					
					for (j = 0; j < count; j++) {
						// we only get input for non-GPU terminations
						if (!inputOnGPU[i][j]) {
							inputRow = ((RealOutput) terminations[j].getInput()).getValues();
								
							representedInputValues[i][j] = inputRow;
						}
					}
				}
				
	
				nativeStep(representedInputValues, representedOutputValues, spikeOutput, startTime, endTime);
	
				
				// Put data computed by GPU in the origins
				Origin[] origins;
				for (i = 0; i < myGPUNetworkArrays.length; i++) {
	
					if(myGPUNetworkArrays[i] instanceof NEFEnsembleImpl)
					{
						origins = ((NEFEnsembleImpl) myGPUNetworkArrays[i]).getDecodedOrigins();
					}else{
						origins = myGPUNetworkArrays[i].getOrigins();
					}
					
					count = origins.length;
	
					for (j = 0; j < count; j++) {
						float[] currentRepOutput = representedOutputValues[i][j];
						if(currentRepOutput != null){
							origins[j].setValues(new RealOutputImpl(
									currentRepOutput.clone(),
    								Units.UNK, endTime));
						}
					}
				}
				
				
				for(i = 0; i < myGPUEnsembles.length; i++){
				    NEFEnsembleImpl currentEnsemble = myGPUEnsembles[i];
				    
				    currentEnsemble.setTime(endTime);
				    
				    float[] currentSpikeOutput = spikeOutput[i];
				    if (currentSpikeOutput != null) {
				        currentEnsemble.setSpikePattern(currentSpikeOutput, endTime);
				    }
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	

	public void kill()
	{
		if (myGPUEnsembles.length == 0)
			return;
		
		nativeKill();
	}

	/**
	 * Used when there are multiple GPU's running a simulation. Finds a distribution of nodes to GPU's that minimizes 
	 * communication between GPU's while also ensuring the number of neurons running on each GPU is relatively balanced.
	 * Note that this problem (a variant of the min bisection problem) is NP-Complete, so a heuristic is employed.
	 * 
	 * @return an array of integers where the value in the i'th entry denotes the partition number of the i'th node ...
	 * in the "nodes" input array
	 * 
	 * @author Eric Crawford
	 */
	public static int[] findOptimalNodeAssignments(Node[] nodes, Projection[] projections, int numPartitions){
		
		if(numPartitions < 1)
		{
			return new int[0];
		}else if(numPartitions == 1){
			int[] nodeAssignments = new int[nodes.length];
			Arrays.fill(nodeAssignments, 0);
			return nodeAssignments;
		}
		
		MultiLevelKLNetworkPartitioner networkPartitioner = new MultiLevelKLNetworkPartitioner();
		networkPartitioner.initialize(nodes, projections, numPartitions);
		
		return networkPartitioner.getPartitionsAsIntArray();
	}
	
	/**
	 * Finds all nodes in the given array which are supposed to execute on the GPU. Stores
	 * those nodes in myGPUNetworkArrays and returns the rest.
	 * 
	 * @author Eric Crawford
	 */
	public Node[] takeGPUNodes(Node[] nodes){
		ArrayList<Node> gpuNodeList = new ArrayList<Node>();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		
		for(int i = 0; i < nodes.length; i++){
			Node workingNode = nodes[i];
			boolean NEFEnsembleUseGPU = 
				workingNode instanceof NEFEnsembleImpl && ((NEFEnsembleImpl) workingNode).getUseGPU();
			
			boolean NetworkArrayUseGPU = 
				workingNode instanceof NetworkArrayImpl &&
				((NetworkImpl) workingNode).getUseGPU();
		
			if(NEFEnsembleUseGPU || NetworkArrayUseGPU){
				gpuNodeList.add(workingNode);
			}
			else{
				nodeList.add(workingNode);
			}
		}
		
		myGPUNetworkArrays = gpuNodeList.toArray(new Node[0]);
		return nodeList.toArray(new Node[0]);
	}
	
	/**
	 * Finds all projections in the given array which are supposed to execute on the GPU. Stores
	 * those projections in myGPUProjections and returns the rest. takeGPUNodes should be called before
	 * this is called, since the nodes which run on the GPU determine which projections run on the GPU.
	 * (ie a projection runs on the GPU only if both its target and source run on the GPU).
	 * 
	 * @author Eric Crawford
	 */
	public Projection[] takeGPUProjections(Projection[] projections){
		// Sort out the GPU projections from the CPU projections
		ArrayList<Projection> gpuProjectionsList = new ArrayList<Projection>();
		ArrayList<Projection> projectionList = new ArrayList<Projection>();
		
		List<Node> GPUNetworkArrayList = Arrays.asList(myGPUNetworkArrays);
		
		for(int i = 0; i < projections.length; i++)
		{
			Node originNode = projections[i].getOrigin().getNode();
			Node terminationNode = projections[i].getTermination().getNode();

			boolean originNodeOnGPU = GPUNetworkArrayList.contains(originNode);
			boolean terminationNodeOnGPU = GPUNetworkArrayList.contains(terminationNode);
			
			if(originNodeOnGPU && terminationNodeOnGPU)
			{
				gpuProjectionsList.add(projections[i]);
			}
			else
			{
				projectionList.add(projections[i]);
			}
		}
		
		myGPUProjections = gpuProjectionsList.toArray(new Projection[0]);
		nonGPUProjections = projectionList.toArray(new Projection[0]);
		return nonGPUProjections;
		
	}

	/**
	 * Used to hold data about each network array to pass to native code. Allows
	 * the fields to be set by name and returned as an array which is the form 
	 * the native code expects the data to be in.
	 * 
	 * @author Eric Crawford
	 */
	private class NetworkArrayData {
		int numEntries = 7;
		
		public int indexOfFirstNode;
		public int endIndex;
		public int numTerminations;
		public int totalInputSize;
		public int numOrigins;
		public int totalOutputSize;
		public int numNeurons;
		
		public void reset(){
			indexOfFirstNode = 0;
			endIndex = 0;
			numTerminations = 0;
			totalInputSize = 0;
			numOrigins = 0;
			totalOutputSize = 0;
			numNeurons = 0;
		}
		
		public int[] getAsArray() {
			int[] array = new int[numEntries];

			int i = 0;
			array[i++] = indexOfFirstNode;
			array[i++] = endIndex;
			array[i++] = numTerminations;
			array[i++] = totalInputSize;
			array[i++] = numOrigins;
			array[i++] = totalOutputSize;
			array[i++] = numNeurons;
			
			return array;
		}
			
	}

	/**
	 * Used to hold data about each ensemble to pass to native code.. Allows
	 * the fields to be set by name, but returned as an array which is the form 
	 * the native code expects the data to be in.
	 * 
	 * @author Eric Crawford
	 */
	private class EnsembleData {
		int numEntries = 10;

		public int dimension;
		public int numNeurons;
		public int numOrigins;

		public int totalInputSize;
		public int totalOutputSize;

		public int maxTransformDimension;
		public int maxDecoderDimension;

		public int numDecodedTerminations;
		public int numNonDecodedTerminations;
		
		public int nonDecodedTransformSize;
		

		public void reset() {
			dimension = 0;
			numNeurons = 0;
			numOrigins = 0;

			totalInputSize = 0;
			totalOutputSize = 0;

			maxTransformDimension = 0;
			maxDecoderDimension = 0;
			
			numDecodedTerminations = 0;
			numNonDecodedTerminations = 0;
			nonDecodedTransformSize = 0;
			
		}

		public int[] getAsArray() {
			int[] array = new int[numEntries];

			int i = 0;
			array[i++] = dimension;
			array[i++] = numNeurons;
			array[i++] = numOrigins;

			array[i++] = totalInputSize;
			array[i++] = totalOutputSize;

			array[i++] = maxTransformDimension;
			array[i++] = maxDecoderDimension;
			
			array[i++] = numDecodedTerminations;
			array[i++] = numNonDecodedTerminations;
			
			array[i++] = nonDecodedTransformSize;
			
			return array;
		}
	}
}
