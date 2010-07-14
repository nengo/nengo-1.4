package ca.nengo.util.impl;

import ca.nengo.model.Node;
import ca.nengo.model.Units;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.LIFSpikeGenerator;
import ca.nengo.model.neuron.impl.SpikingNeuron;

public class GPUThread extends NodeThread {
	private GPUNodeThreadPool myGPUNodeThreadPool;
	private int myNumGPUNodes;
	private Node[] myGPUNodes;

	GPUThread(GPUNodeThreadPool GPUNodeThreadPool) {
		super(GPUNodeThreadPool);
		myGPUNodeThreadPool = GPUNodeThreadPool;
		myGPUNodes = myGPUNodeThreadPool.myGPUNodes;
		myNumGPUNodes = myGPUNodes.length;
	}

	static native void nativeSetupRun(float[][][][] terminationTransforms, float[][] terminationTau, float[][][] encoders, float[][][][] decoders, float[][] neuronData, boolean[] collectSpikes, int[][] GPUData, float maxTimeStep);
	
	// if using the first GPU implementation, uncomment this line and comment out the line above
	//static native void nativeSetupRun(float[][][][] terminationTransforms, float[][] terminationTau, float[][][] encoders, float[][][][] decoders, float[][] neuronData, boolean[] collectSpikes);
	
	static native void nativeStep(float[][][] representedInput, float[][][] representedOutput, float[][] spikes, float startTime, float endTime);

	static native void nativeKill();

	public void run() {
		if (myGPUNodes.length==0) return;

		int i = 0, j = 0, numEnsemblesCollectingSpikes = 0;
		NEFEnsembleImpl workingNode;
		DecodedTermination[] terminations;
		DecodedOrigin[] origins;

		float[][][][] terminationTransforms = new float[myGPUNodes.length][][][];
		float[][] terminationTau = new float[myGPUNodes.length][];
		float[][][] encoders = new float[myGPUNodes.length][][];
		float[][][][] decoders = new float[myGPUNodes.length][][][];
		float[][] neuronData = new float[myGPUNodes.length][];
		GPUData gpuData = new GPUData();
		int[][] gpuDataArray = new int[myGPUNodes.length][];
		boolean[] collectSpikes = new boolean[myGPUNodes.length];
		float maxTimeStep = ((LIFSpikeGenerator) ((SpikingNeuron)  ((NEFEnsembleImpl) myGPUNodes[0]).getNodes()[0]).getGenerator()).getMaxTimeStep();

		// prepare the data to pass in to the native setup call
		for (; i < myNumGPUNodes; i++) {

			workingNode = (NEFEnsembleImpl) myGPUNodes[i];

			gpuData.reset();
			
			gpuData.dimension = workingNode.getDimension();
			gpuData.numNeurons = workingNode.getNeurons();

			terminations = workingNode.getDecodedTerminations();

			int terminationDim;
			gpuData.numTerminations = terminations.length;
			gpuData.maxTransformDimension = 0;

			terminationTransforms[i] = new float[terminations.length][][];
			terminationTau[i] = new float[terminations.length];

			for (j = 0; j < terminations.length; j++) {
				terminationTransforms[i][j] = terminations[j].getTransform();
				terminationTau[i][j] = terminations[j].getTau();

				terminationDim = terminations[j].getDimensions();
				gpuData.totalInputSize += terminationDim;
			
				if(terminationDim > gpuData.maxTransformDimension){
					gpuData.maxTransformDimension = terminationDim;
				}
			}

			encoders[i] = workingNode.getEncoders();
			float[] radii=workingNode.getRadii();
			for (j=0; j<encoders[i].length; j++) {
				for (int k=0; k<encoders[i][j].length; k++)
					encoders[i][j][k]=encoders[i][j][k]/radii[k];
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
							
				if(originDim > gpuData.maxDecoderDimension){
					gpuData.maxDecoderDimension = originDim;
				}
				
			}

			neuronData[i] = workingNode.getStaticNeuronData();
			
			gpuDataArray[i] = gpuData.getAsArray();
			
			collectSpikes[i] = workingNode.isCollectingSpikes();
			numEnsemblesCollectingSpikes++;
		}

		nativeSetupRun(terminationTransforms, terminationTau, encoders, decoders, neuronData, collectSpikes, gpuDataArray, maxTimeStep);

		// if using the first GPU implementation, uncomment this line and comment out the line above
		//nativeSetupRun(terminationTransforms, terminationTau, encoders, decoders, neuronData, collectSpikes);

		// Set up the data structures that we pass in and out of the native step call. They do not change in size from step to step, so we can re-use them.
		float[][][] representedInputValues = new float[myNumGPUNodes][][];
		float[][][] representedOutputValues = new float[myNumGPUNodes][][];
		int count;

		for (i = 0; i < myNumGPUNodes; i++) {
			terminations = ((NEFEnsembleImpl) myGPUNodes[i])
					.getDecodedTerminations();
			count = terminations.length;

			representedInputValues[i] = new float[count][];
		}

		for (i = 0; i < myNumGPUNodes; i++) {
			origins = ((NEFEnsembleImpl) myGPUNodes[i]).getDecodedOrigins();
			count = origins.length;

			representedOutputValues[i] = new float[count][];

			for (j = 0; j < count; j++) {
				representedOutputValues[i][j] = new float[origins[j].getDimensions()];
			}
		}

		float[][] spikeOutput = new float[numEnsemblesCollectingSpikes][];
	
		int spikeIndex = 0;
		for (i = 0; i < myNumGPUNodes; i++) {
			if(((NEFEnsembleImpl) myGPUNodes[i]).isCollectingSpikes())
			{
				spikeOutput[spikeIndex++] = new float[ ((NEFEnsembleImpl) myGPUNodes[i]).getNeurons() ];
			}
		}
		

		// Now wait until we get the signal to step from the main thread
		myGPUNodeThreadPool.Sleep();
		
		// int step = 0;
		while (!myGPUNodeThreadPool.myKill) {
			try {

				// get the input data from the terminations
				for (i = 0; i < myNumGPUNodes; i++) {
					terminations = ((NEFEnsembleImpl) myGPUNodes[i])
							.getDecodedTerminations();
					count = terminations.length;

					for (j = 0; j < count; j++) {
						representedInputValues[i][j] = terminations[j].getInput().getValues();
					}
				}

				// take a step
				nativeStep(representedInputValues, representedOutputValues, spikeOutput, myGPUNodeThreadPool.getStartTime(), myGPUNodeThreadPool.getEndTime());

				float endTime = myGPUNodeThreadPool.getEndTime();
				
				// Put data computed by GPU in the origins
				spikeIndex = 0;
				for (i = 0; i < myNumGPUNodes; i++) {

					origins = ((NEFEnsembleImpl) myGPUNodes[i]).getDecodedOrigins();
					count = origins.length;

					for (j = 0; j < count; j++) {
						origins[j].setValues(new RealOutputImpl(
								representedOutputValues[i][j].clone(),
								Units.UNK, endTime));
					}
					
					if(((NEFEnsembleImpl) myGPUNodes[i]).isCollectingSpikes()){
						((NEFEnsembleImpl) myGPUNodes[i]).setSpikePattern(spikeOutput[spikeIndex], endTime);
						spikeIndex++;
					}
					
					((NEFEnsembleImpl) myGPUNodes[i]).setTime(endTime);
				}

				// tell main thread that we've finished processing all GPU nodes for this step
				myGPUNodeThreadPool.finishedGPUNodes();

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// sleep until we are told by the main thread to take another step (or to end the run)
			myGPUNodeThreadPool.Sleep();
		}

		nativeKill();
	}
	
	private class GPUData {
		int numEntries = 8;
		
		public int dimension;
		public int numTerminations;
		public int numNeurons;
		public int numOrigins;
		
		public int totalInputSize;
		public int totalOutputSize;
		
		public int maxTransformDimension;
		public int maxDecoderDimension;
		
		public void reset()
		{
			dimension = 0;
			numTerminations = 0;
			numNeurons = 0;
			numOrigins = 0;
			
			totalInputSize = 0;
			totalOutputSize = 0;
			
			maxTransformDimension = 0;
			maxDecoderDimension = 0;	
		}
		
		public int[] getAsArray()
		{
			int[] array = new int[numEntries];
			
			int i = 0;
			array[i++] = dimension;
			array[i++] = numTerminations;
			array[i++] = numNeurons;
			array[i++] = numOrigins;
			
			array[i++] = totalInputSize;
			array[i++] = totalOutputSize;
			
			array[i++] = maxTransformDimension;
			array[i++] = maxDecoderDimension;

			return array;
		}
	}
}
