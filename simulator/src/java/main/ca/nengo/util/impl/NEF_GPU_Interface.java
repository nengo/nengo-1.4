package ca.nengo.util.impl;

import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.LIFSpikeGenerator;
import ca.nengo.model.neuron.impl.SpikingNeuron;

public class NEF_GPU_Interface {
	public static boolean myUseGPU = false;
	
	protected Node[] myGPUNodes;
	protected Projection[] myGPUProjections;
	
	protected Node[] myNodes;
	protected Projection[] myProjections;
	
	protected float myStartTime;
	protected float myEndTime;
	
	float[][][] representedInputValues;
	float[][][] representedOutputValues;
	float[][] spikeOutput;
	int[][] terminationToProjectionPointer;
	
	
	// load the shared library that contains the native functions
	static{
		try {
			System.loadLibrary("NengoGPU");
		} catch (java.lang.UnsatisfiedLinkError e) {
			myUseGPU=false;
			System.out.print(e.toString());
		}
	}
	
	
	static native void nativeSetupRun(float[][][][] terminationTransforms,
			int[][] isDecodedTermination, float[][] terminationTau,
			float[][][] encoders, float[][][][] decoders, float[][] neuronData,
			int[][] projections, int[][] GPUData, int[] projectionOnGPU,
			float maxTimeStep);

	static native void nativeStep(float[][][] representedInput,
			float[][][] representedOutput, float[][] spikes, float startTime,
			float endTime);

	static native void nativeKill();
	
	
	// set whether or not to use the GPU
	public static void setUseGPU(boolean value){
		myUseGPU = value;
	}
	
	// get whether or not to use the GPU
	public static boolean getUseGPU(){
		return myUseGPU;
	}
	
	public NEF_GPU_Interface(Node[] nodes, Projection[] projections){
		initialize(nodes, projections);
	}
	
	protected void initialize(Node[] nodes, Projection[] projections){
		
		ArrayList<Node> GPUNodeList = new ArrayList<Node>();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		
		// Sort out the GPU nodes from the CPU nodes
		for(int i = 0; i < nodes.length; i++){
			if(nodes[i] instanceof NEFEnsembleImpl && ((NEFEnsembleImpl)nodes[i]).isGPUNode())
			{
				GPUNodeList.add(nodes[i]);
			}else{
				nodeList.add(nodes[i]); 	
			} 
		}
		
		// Sort out the GPU projections from the CPU projections
		ArrayList<Projection> GPU_projections_list = new ArrayList<Projection>();
		ArrayList<Projection> projections_list = new ArrayList<Projection>();
		
		for(int i = 0; i < projections.length; i++)
		{
			Node originNode = projections[i].getOrigin().getNode();
			Node terminationNode = projections[i].getTermination().getNode();
			
			if(originNode instanceof NEFEnsembleImpl && 
				((NEFEnsembleImpl) originNode).isGPUNode() &&
				terminationNode instanceof NEFEnsembleImpl &&
				((NEFEnsembleImpl) terminationNode).isGPUNode() &&
				projections[i].getTermination() instanceof DecodedTermination)
			{
				GPU_projections_list.add(projections[i]);
			}	
			else
			{
				projections_list.add(projections[i]);
			}
		}
		
		myGPUNodes = GPUNodeList.toArray(new Node[0]);
		myGPUProjections = GPU_projections_list.toArray(new Projection[0]);
		
		myNodes = nodeList.toArray(new Node[0]);
		myProjections = projections_list.toArray(new Projection[0]);

		if (myGPUNodes.length == 0)
			return;

		// Put the data in a format useful for passing to the GPU. 
		// Most of this function is devoted to this task.
		
		int i = 0, j = 0, k = 0, numEnsemblesCollectingSpikes = 0;
		NEFEnsembleImpl workingNode;
		Termination[] terminations;
		DecodedOrigin[] origins;
		Origin[] NDorigins;

		float[][][][] terminationTransforms = new float[myGPUNodes.length][][][];
		int[][] isDecodedTermination = new int[myGPUNodes.length][];
		float[][] terminationTau = new float[myGPUNodes.length][];
		float[][][] encoders = new float[myGPUNodes.length][][];
		float[][][][] decoders = new float[myGPUNodes.length][][][];
		float[][] neuronData = new float[myGPUNodes.length][];
		GPUData gpuData = new GPUData();
		int[][] gpuDataArray = new int[myGPUNodes.length][];
		boolean[] collectSpikes = new boolean[myGPUNodes.length];
		float maxTimeStep = ((LIFSpikeGenerator) ((SpikingNeuron) ((NEFEnsembleImpl) myGPUNodes[0])
				.getNodes()[0]).getGenerator()).getMaxTimeStep();

		// We put the list of projections in terms of the GPU nodes
		// For each projections we record 4 numbers: the number of the origin
		// ensemble, the number of the origin in its ensemble, the number of
		// the termination ensemble and the number of the termination in its
		// ensemble
		int[][] adjustedProjections = new int[myGPUProjections.length][6];
		terminationToProjectionPointer = new int[myGPUNodes.length][];

		// prepare the data to pass in to the native setup call
		for (i = 0; i < myGPUNodes.length; i++) {

			workingNode = (NEFEnsembleImpl) myGPUNodes[i];

			gpuData.reset();

			gpuData.dimension = workingNode.getDimension();
			gpuData.numNeurons = workingNode.getNeurons();

			terminations = workingNode.getTerminations();

			int terminationDim = 0;
			gpuData.maxTransformDimension = 0;

			terminationTransforms[i] = new float[terminations.length][][];
			terminationTau[i] = new float[terminations.length];
			isDecodedTermination[i] = new int[terminations.length];
			terminationToProjectionPointer[i] = new int[terminations.length];

			for (j = 0; j < terminations.length; j++) {

				if (terminations[j] instanceof DecodedTermination) {
					terminationTransforms[i][j] = ((DecodedTermination) terminations[j])
							.getTransform();
					terminationTau[i][j] = terminations[j].getTau();

					terminationDim = terminations[j].getDimensions();
					gpuData.totalInputSize += terminationDim;

					if (terminationDim > gpuData.maxTransformDimension) {
						gpuData.maxTransformDimension = terminationDim;
					}

					isDecodedTermination[i][j] = 1;
					
					gpuData.numDecodedTerminations++;
				} else if (terminations[j] instanceof PlasticEnsembleTermination) {
					terminationTransforms[i][j] = new float[1][1];
					float[][] tempTransform = ((PlasticEnsembleTermination) terminations[j])
							.getTransform();
					terminationTransforms[i][j][0][0] = tempTransform[0][0];
					terminationTau[i][j] = terminations[j].getTau();
					isDecodedTermination[i][j] = 0;

					terminationDim = 1;
					gpuData.totalInputSize += 1;
					gpuData.numNonDecodedTerminations++;
				}

				k = 0;
				while (k < myGPUProjections.length
						&& myGPUProjections[k].getTermination() != terminations[j]) {
					k++;
				}

				if (k < myGPUProjections.length) {
					adjustedProjections[k][2] = i;
					adjustedProjections[k][3] = j;
					adjustedProjections[k][4] = terminationDim;
					adjustedProjections[k][5] = -1;

					terminationToProjectionPointer[i][j] = k;
				} else {
					terminationToProjectionPointer[i][j] = -1;
				}
			}

			encoders[i] = workingNode.getEncoders();
			float[] radii = workingNode.getRadii();
			for (j = 0; j < encoders[i].length; j++) {
				for (k = 0; k < encoders[i][j].length; k++)
					encoders[i][j][k] = encoders[i][j][k] / radii[k];
			}

			origins = workingNode.getDecodedOrigins();

			gpuData.numOrigins = origins.length;
			gpuData.maxDecoderDimension = 0;

			decoders[i] = new float[origins.length][][];
			int originDim;
			for (j = 0; j < origins.length; j++) {
				decoders[i][j] = origins[j].getDecoders();
				originDim = origins[j].getDimensions();

				gpuData.totalOutputSize += originDim;

				if (originDim > gpuData.maxDecoderDimension) {
					gpuData.maxDecoderDimension = originDim;
				}

				for (k = 0; k < myGPUProjections.length; k++) {
					if (myGPUProjections[k].getOrigin() == origins[j]) {
						adjustedProjections[k][0] = i;
						adjustedProjections[k][1] = j;
					}
				}
			}

			neuronData[i] = workingNode.getStaticNeuronData();

			collectSpikes[i] = workingNode.isCollectingSpikes();
			numEnsemblesCollectingSpikes++;

			gpuDataArray[i] = gpuData.getAsArray();
		}

		int[] projectionOnGPU = new int[adjustedProjections.length];
		
		nativeSetupRun(terminationTransforms, isDecodedTermination,
				terminationTau, encoders, decoders, neuronData,
				adjustedProjections, gpuDataArray, projectionOnGPU, maxTimeStep);

		// Set up the data structures that we pass in and out of the native step call.
		// They do not change in size from step to step so we can re-use them.
		representedInputValues = new float[myGPUNodes.length][][];
		representedOutputValues = new float[myGPUNodes.length][][];
		spikeOutput = new float [myGPUNodes.length][];
		
		int count, pointer;
		
		ArrayList<Projection> nonGPUProjections = new ArrayList<Projection>();
		ArrayList<Projection> GPUProjections = new ArrayList<Projection>();
		
		for(i = 0; i < myGPUProjections.length; i++){
			if(projectionOnGPU[i] == 0)
			{
				nonGPUProjections.add(myGPUProjections[i]);
			}
			else
			{
				GPUProjections.add(myGPUProjections[i]);
			}
		}
		
		if(nonGPUProjections.size() > 0)
		{
			myGPUProjections = GPUProjections.toArray(new Projection[0]);
			adjustProjections(nonGPUProjections);
		}

		
		for (i = 0; i < myGPUNodes.length; i++) {

			terminations = ((NEFEnsembleImpl) myGPUNodes[i]).getTerminations();
			count = terminations.length;

			representedInputValues[i] = new float[count][];
			
			// set projection pointer array so that projections that could've been on the GPU
			// but are not (because they would cross multiple GPUs) are marked as not on the GPU
			for(j = 0; j < terminations.length; j++)
			{
				pointer = terminationToProjectionPointer[i][j];
				if(pointer >= 0 && projectionOnGPU[pointer] == 0)
				{
					terminationToProjectionPointer[i][j] = -1;	
				}
			}
		}

		for (i = 0; i < myGPUNodes.length; i++) {
			origins = ((NEFEnsembleImpl) myGPUNodes[i]).getDecodedOrigins();
			count = origins.length;

			representedOutputValues[i] = new float[count][];

			for (j = 0; j < count; j++) {
				representedOutputValues[i][j] = new float[origins[j].getDimensions()];
			}
		}

		int spikeIndex = 0;
		for (i = 0; i < myGPUNodes.length; i++) {
			spikeOutput[spikeIndex++] = new float[((NEFEnsembleImpl) myGPUNodes[i]).getNeurons()];
		}
	}
	
	
	// after we've figured out which projections are actually run on the 
	// GPU by calling nativeSetupRun we have to give back the ones that aren't
	public void adjustProjections(ArrayList<Projection> nonGPUProjections)
	{
		ArrayList<Projection> projections = new ArrayList<Projection>(Arrays.asList(myProjections));
		projections.addAll(nonGPUProjections);
		myProjections = projections.toArray(new Projection[0]);
	}
	
	
	public void step(float startTime, float endTime){
		
		myStartTime = startTime;
		myEndTime = endTime;
		
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
		System.out.println("After CPU processing: " + new Date().getTime());
		
		
		try {
			
			int terminationPointer, count, i, j;
			float[] inputRow = new float[0];
			Termination[] terminations;
				
			// get the input data from the terminations
			for (i = 0; i < myGPUNodes.length; i++) {
				terminations = ((NEFEnsembleImpl) myGPUNodes[i]).getTerminations();
				count = terminations.length;
				
				for (j = 0; j < count; j++) {
					terminationPointer = terminationToProjectionPointer[i][j];

					// we only get input for non-GPU terminations
					if (terminationPointer < 0) {
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
					((NEFEnsembleImpl) myGPUNodes[i]).
						setSpikePattern(spikeOutput[spikeIndex], endTime);
					
				}

				((NEFEnsembleImpl) myGPUNodes[i]).setTime(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void kill()
	{
		nativeKill();
	}
	

	private class GPUData {
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