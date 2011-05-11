package ca.nengo.util.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Projection;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.PlasticEnsembleTermination;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.impl.NetworkImpl.OriginWrapper;
import ca.nengo.model.impl.NetworkImpl.TerminationWrapper;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.LIFSpikeGenerator;
import ca.nengo.model.neuron.impl.SpikingNeuron;

public class NEFGPUInterface {
	private static boolean myUseGPU = true;
	private static boolean canUseGPU;
	private static boolean showTiming = false;
	public static int myNumDevices = 1;
	
	protected Node[] myGPUNodes;
	protected Projection[] myGPUProjections;
	
	protected Node[] myNodes;
	protected Projection[] myProjections;
	
	protected float myStartTime;
	protected float myEndTime;
	
	float[][][] representedInputValues;
	float[][][] representedOutputValues;
	float[][] spikeOutput;
	boolean[][] inputOnGPU;
	
	
	// load the shared library that contains the native functions

	static{
		try {
			System.loadLibrary("NengoGPU");
			canUseGPU = true;

			if(!hasGPU())
			{
				System.out.println("No CUDA-enabled GPU detected.");
				canUseGPU = false;
			}
			
		} catch (java.lang.UnsatisfiedLinkError e) {
			System.out.println("Couldn't load native library NengoUtilsGPU. " +
				"Unable to use GPU for class NEFGPUInterface.");
			canUseGPU = false;
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}

	static native boolean hasGPU();

	static native void nativeSetupRun(float[][][][] terminationTransforms,
			int[][] isDecodedTermination, float[][] terminationTau,
			float[][][] encoders, float[][][][] decoders, float[][] neuronData,
			int[][] projections, int[][] GPUData, int[][] adjacencyMatrix,
			float maxTimeStep, int numDevicesRequested);

	static native void nativeStep(float[][][] representedInput,
			float[][][] representedOutput, float[][] spikes, float startTime,
			float endTime);

	static native void nativeKill();
	
	// set whether or not to use the GPU
	public static void setRequestedNumDevices(int value){
		myNumDevices = value;
	}
	
	// get whether or not to use the GPU
	public static int getRequestedNumDevices(){
		return myNumDevices;
	}
	
	// set whether or not to use the GPU
	public static void setUseGPU(boolean value){
		myUseGPU = value;
	}
	
	// get whether or not to use the GPU
	public static boolean getUseGPU(){
		return canUseGPU && myUseGPU;
	}
	
	public static void showGPUTiming(){
		showTiming = true;
	}
	
	public static void hideGPUTiming(){
		showTiming = false;
	}
	
	public NEFGPUInterface(Node[] nodes, Projection[] projections){
		initialize(nodes, projections);
	}
	
	protected void initialize(Node[] nodes, Projection[] projections){
		
		ArrayList<Node> GPUNodeList = new ArrayList<Node>();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		
		// Sort out the GPU nodes from the CPU nodes
		for(int i = 0; i < nodes.length; i++){
			
			boolean useGPU = 
				nodes[i] instanceof NEFEnsembleImpl && ((NEFEnsembleImpl) nodes[i]).getUseGPU();
			
			if(useGPU)
			{		
				GPUNodeList.add(nodes[i]);
			}else{
				nodeList.add(nodes[i]); 	
			} 
		}
		
		// Sort out the GPU projections from the CPU projections
		ArrayList<Projection> GPUProjectionsList = new ArrayList<Projection>();
		ArrayList<Projection> projectionsList = new ArrayList<Projection>();
		
		for(int i = 0; i < projections.length; i++)
		{
			Node originNode = projections[i].getOrigin().getNode();
			Node terminationNode = projections[i].getTermination().getNode();
			
			boolean originNodeOnGPU = 
				originNode instanceof NEFEnsembleImpl && ((NEFEnsembleImpl) originNode).getUseGPU();
			
			boolean terminationNodeOnGPU = 
				terminationNode instanceof NEFEnsembleImpl && ((NEFEnsembleImpl) terminationNode).getUseGPU();
			
			if(originNodeOnGPU && terminationNodeOnGPU)
			{
				GPUProjectionsList.add(projections[i]);
			}
			else
			{
				projectionsList.add(projections[i]);
			}
		}
		
		myGPUNodes = GPUNodeList.toArray(new Node[0]);
		myGPUProjections = GPUProjectionsList.toArray(new Projection[0]);
		
		myNodes = nodeList.toArray(new Node[0]);
		myProjections = projectionsList.toArray(new Projection[0]);

		if (myGPUNodes.length == 0)
			return;

		// Put the data in a format appropriate for passing to the GPU. 
		// Most of this function is devoted to this task.
		int i = 0, j = 0, k = 0, numEnsemblesCollectingSpikes = 0;
		NEFEnsembleImpl workingNode;
		Termination[] terminations;
		DecodedOrigin[] origins;

		float[][][][] terminationTransforms = new float[myGPUNodes.length][][][];
		int[][] isDecodedTermination = new int[myGPUNodes.length][];
		float[][] terminationTau = new float[myGPUNodes.length][];
		float[][][] encoders = new float[myGPUNodes.length][][];
		float[][][][] decoders = new float[myGPUNodes.length][][][];
		float[][] neuronData = new float[myGPUNodes.length][];
		EnsembleData ensembleData = new EnsembleData();
		int[][] gpuDataArray = new int[myGPUNodes.length][];
		boolean[] collectSpikes = new boolean[myGPUNodes.length];
		float maxTimeStep = ((LIFSpikeGenerator) ((SpikingNeuron) ((NEFEnsembleImpl) myGPUNodes[0])
				.getNodes()[0]).getGenerator()).getMaxTimeStep();

		// We put the list of projections in terms of the GPU nodes
		// For each projection we record 4 numbers: the number of the origin
		// ensemble, the number of the origin in its ensemble, the number of
		// the termination ensemble and the number of the termination in its ensemble
		int[][] adjustedProjections = new int[myGPUProjections.length][6];
		inputOnGPU = new boolean[myGPUNodes.length][];
		
		
		// prepare the data to pass in to the native setup call
		for (i = 0; i < myGPUNodes.length; i++) {
			
			workingNode = (NEFEnsembleImpl) myGPUNodes[i];
			
			ensembleData.reset();

			ensembleData.dimension = workingNode.getDimension();
			ensembleData.numNeurons = workingNode.getNeurons();

			terminations = workingNode.getTerminations();

			int terminationDim = 0;
			ensembleData.maxTransformDimension = 0;

			terminationTransforms[i] = new float[terminations.length][][];
			terminationTau[i] = new float[terminations.length];
			isDecodedTermination[i] = new int[terminations.length];
			inputOnGPU[i] = new boolean[terminations.length];

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
				} else if (terminations[j] instanceof PlasticEnsembleTermination) {
					terminationTransforms[i][j] = new float[1][1];
					float[][] tempTransform = ((PlasticEnsembleTermination) terminations[j])
							.getTransform();
					terminationTransforms[i][j][0][0] = tempTransform[0][0];
					terminationTau[i][j] = terminations[j].getTau();
					isDecodedTermination[i][j] = 0;

					terminationDim = 1;
					ensembleData.totalInputSize += 1;
					ensembleData.numNonDecodedTerminations++;
				}
				
				k = 0;
				Termination termination;
				boolean terminationWrapped, projectionMatches = false;
				
				while(!projectionMatches && k < myGPUProjections.length){
					termination = myGPUProjections[k].getTermination();
					terminationWrapped = termination instanceof TerminationWrapper;
					if(terminationWrapped)
						termination = ((TerminationWrapper) termination).getWrappedTermination();
					
					projectionMatches = termination == terminations[j];
					
					if(projectionMatches)
						break;
					
					k++;
				}

				if (projectionMatches) {
					adjustedProjections[k][2] = i;
					adjustedProjections[k][3] = j;
					adjustedProjections[k][4] = terminationDim;
					adjustedProjections[k][5] = -1;

					inputOnGPU[i][j] = true;
				} else {
					inputOnGPU[i][j] = false;
				}
			}

			encoders[i] = workingNode.getEncoders();
			float[] radii = workingNode.getRadii();
			for (j = 0; j < encoders[i].length; j++) {
				for (k = 0; k < encoders[i][j].length; k++)
					encoders[i][j][k] = encoders[i][j][k] / radii[k];
			}

			origins = workingNode.getDecodedOrigins();

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

				Origin origin;
				boolean originWrapped;
				
				for (k = 0; k < myGPUProjections.length; k++) {
					origin = myGPUProjections[k].getOrigin();
					originWrapped = origin instanceof OriginWrapper;
					
					if(originWrapped)
						origin = ((OriginWrapper) origin).getWrappedOrigin();
					
					if (origin == origins[j]) {
						adjustedProjections[k][0] = i;
						adjustedProjections[k][1] = j;
					}
				}
			}

			neuronData[i] = workingNode.getStaticNeuronData();

			collectSpikes[i] = workingNode.isCollectingSpikes();
			numEnsemblesCollectingSpikes++;

			gpuDataArray[i] = ensembleData.getAsArray();
		}
		
		int[][] adjacencyMatrix = findAdjacencyMatrix(myGPUNodes, myGPUProjections);
		

		nativeSetupRun(terminationTransforms, isDecodedTermination,
				terminationTau, encoders, decoders, neuronData,
				adjustedProjections, gpuDataArray, adjacencyMatrix, maxTimeStep, myNumDevices);

		// Set up the data structures that we pass in and out of the native step call.
		// They do not change in size from step to step so we can re-use them.
		representedInputValues = new float[myGPUNodes.length][][];
		representedOutputValues = new float[myGPUNodes.length][][];
		spikeOutput = new float [myGPUNodes.length][];
		
		for (i = 0; i < myGPUNodes.length; i++) {
			terminations = ((NEFEnsembleImpl) myGPUNodes[i]).getTerminations();

			representedInputValues[i] = new float[terminations.length][];
		}

		for (i = 0; i < myGPUNodes.length; i++) {
			origins = ((NEFEnsembleImpl) myGPUNodes[i]).getDecodedOrigins();

			representedOutputValues[i] = new float[origins.length][];

			for (j = 0; j < origins.length; j++) {
				representedOutputValues[i][j] = new float[origins[j].getDimensions()];
			}
		}

		int spikeIndex = 0;
		for (i = 0; i < myGPUNodes.length; i++) {
			spikeOutput[spikeIndex++] = new float[((NEFEnsembleImpl) myGPUNodes[i]).getNeurons()];
		}
	}
	
	public void step(float startTime, float endTime){
		
		myStartTime = startTime;
		myEndTime = endTime;
		
		if(showTiming)
			System.out.println("Before CPU processing: " + new Date().getTime());
		
		for (int i = 0; i < myProjections.length; i++) {
			try
			{
				InstantaneousOutput values = myProjections[i].getOrigin().getValues();
				myProjections[i].getTermination().setValues(values);
			}
			catch(SimulationException e)
			{
			}
		}

		for(int i = 0; i < myNodes.length; i++){
			try
			{
				myNodes[i].run(myStartTime, myEndTime);
			}
			catch(Exception e)
			{}
		}
		
		if(showTiming)
			System.out.println("After CPU processing, before GPU processing: " + new Date().getTime());
		
		if (myGPUNodes.length == 0)
			return;
		
		try {
			
			int count, i, j;
			float[] inputRow = new float[0];
			Termination[] terminations;
				
			// get the input data from the terminations
			for (i = 0; i < myGPUNodes.length; i++) {
				terminations = ((NEFEnsembleImpl) myGPUNodes[i]).getTerminations();
				count = terminations.length;
				
				for (j = 0; j < count; j++) {

					// we only get input for non-GPU terminations
					if (!inputOnGPU[i][j]) {
						if (terminations[j] instanceof DecodedTermination)
							inputRow = ((DecodedTermination) terminations[j]).getInput().getValues();
						else if (terminations[j] instanceof PlasticEnsembleTermination)
							inputRow = ((RealOutput) ((PlasticEnsembleTermination) terminations[j]).getInput()).getValues();
						else
							System.out.println("warning: using an unsupported termination type");
						
						representedInputValues[i][j] = inputRow;
					}
				}
			}


			nativeStep(representedInputValues, representedOutputValues, spikeOutput, startTime, endTime);

			
			// Put data computed by GPU in the origins
			int spikeIndex = 0;
			DecodedOrigin[] origins;
			for (i = 0; i < myGPUNodes.length; i++) {

				origins = ((NEFEnsembleImpl) myGPUNodes[i]).getDecodedOrigins();
				count = origins.length;

				for (j = 0; j < count; j++) {
					origins[j].setValues(new RealOutputImpl(
							representedOutputValues[i][j].clone(),
							Units.UNK, endTime));
				}

				if (((NEFEnsembleImpl) myGPUNodes[i]).isCollectingSpikes()) {
					((NEFEnsembleImpl) myGPUNodes[i]).setSpikePattern(spikeOutput[spikeIndex], endTime);
				}

				((NEFEnsembleImpl) myGPUNodes[i]).setTime(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(showTiming)
			System.out.println("After GPU processing: " + new Date().getTime());
	}
	

	public void kill()
	{
		if (myGPUNodes.length == 0)
			return;
		
		nativeKill();
	}
	
	// Converts a nengo network to an undirected graph stored as a lower triangular adjacency matrix.
	// If node A projects to node B with weight x and B projects to A with weight y,
	// we treat it as a single edge with weight (x + y). Self-loops (recurrent projections)
	// are deleted (this function is used for distributing ensembles to GPUs and self loops are
	// irrelevant in that task).
	public int[][] findAdjacencyMatrix(Node[] nodes, Projection[] projections) {
		
		HashMap <Node, Integer> nodeIndexes = new HashMap<Node, Integer>();
		
		int[][] adjacencyMatrix = new int[nodes.length][nodes.length];
		
		for(int i = 0; i < nodes.length; i++){
			for(int j = 0; j < nodes.length; j++){
				adjacencyMatrix[i][j] = 0;
			}
		}
		
		for(int i = 0; i < nodes.length; i++){
			nodeIndexes.put(nodes[i], i);
		}
		
		for(int i = 0; i < projections.length; i++){
			Origin origin = projections[i].getOrigin();
			Termination termination = projections[i].getTermination();
			
			boolean originWrapped = origin instanceof OriginWrapper;
			
			if(originWrapped)
				origin = ((OriginWrapper) origin).getWrappedOrigin();
			
			boolean terminationWrapped = termination instanceof TerminationWrapper;
			
			if(terminationWrapped)
				termination = ((TerminationWrapper) termination).getWrappedTermination();
			
			int originNodeIndex = nodeIndexes.get(origin.getNode());
			int termNodeIndex = nodeIndexes.get(termination.getNode());
			
			if(originNodeIndex > termNodeIndex)
				adjacencyMatrix[originNodeIndex][termNodeIndex] += termination.getDimensions();
			else if(termNodeIndex > originNodeIndex)
				adjacencyMatrix[termNodeIndex][originNodeIndex] += termination.getDimensions();
		}
		
		return adjacencyMatrix;
	}
	
	// Used to hold data about each ensemble to pass to native code.
	private class EnsembleData {
		int numEntries = 9;

		public int dimension;
		public int numNeurons;
		public int numOrigins;

		public int totalInputSize;
		public int totalOutputSize;

		public int maxTransformDimension;
		public int maxDecoderDimension;

		public int numDecodedTerminations;
		public int numNonDecodedTerminations;
		

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
			
			return array;
		}
	}
}
