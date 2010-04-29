#ifdef __cplusplus
extern "C"{
#endif

#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <cuda_runtime.h>

#include "NengoGPUData.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPU.h"

extern FILE* fp;

// return a fresh NengoGPUData object with all numerical values zeroed out and all pointers set to null
NengoGPUData* getNewNengoGPUData()
{
  NengoGPUData* new = (NengoGPUData*)malloc(sizeof(NengoGPUData));

  new-> onDevice = 0;
  new-> device = 0;
  new-> maxTimeStep = 0;
   
  new-> numNeurons = 0;
  new-> numEnsembles = 0;
  new-> numTerminations = 0;
  new-> numOrigins = 0;

  new->totalInputSize = 0;
  new->totalTransformSize = 0;
  new->totalNumTransformRows = 0;
  new->totalDimension = 0;
  new->totalEncoderSize = 0;
  new->totalDecoderSize = 0;
  new->totalNumDecoderRows = 0;
  new->totalOutputSize = 0;

  new->maxTransformDimension = 0;
  new->maxDimension = 0;
  new->maxNumNeurons = 0;
  new->maxDecoderDimension = 0;

  new->input = NULL;
  new->inputIndices = NULL;

  new->terminationTransforms = NULL;
  new->terminationTransformIndices = NULL;
  new->terminationTau = NULL;
  new->terminationDimensions = NULL;
  new->terminationDimensionsHost = NULL;
  new->terminationValues = NULL;
  new->terminationRowToTerminationIndexor = NULL;

  new->ensembleNeurons = NULL;
  new->ensembleNeuronsHost = NULL;
  new->ensembleDimensions = NULL;
  new->ensembleTerminations = NULL;
  new->dimensionToEnsembleIndexor = NULL;
  new->ensemblePositionsInTerminationValues = NULL;

  new->ensembleSums = NULL;
  new->dimensionIndexInEnsemble = NULL;
  new->sumIndices = NULL;
  new->sumHelper = NULL;

  new->encoders = NULL;
  new->encoderIndices = NULL;

  new->neuronVoltage = NULL;
  new->neuronReftime = NULL;
  new->neuronBias = NULL;
  new->neuronScale = NULL;
  new->neuronToEnsembleIndexor = NULL;
  new->ensembleTauRC = NULL;
  new->ensembleTauRef = NULL;

  new->spikes = NULL;
  new->spikesHost = NULL;
  new->spikeHelper = NULL;
  new->spikeIndices = NULL;
  new->spikeIndicesHost = NULL;
  new->collectSpikes = NULL;

  new->decoders = NULL;
  new->decoderIndices = NULL;
  new->decoderDimension = NULL;
  new->decoderRowToEnsembleIndexor = NULL;
  
  new->output = NULL;

  return new;
}

// Should only be called once the NengoGPUData's numerical values have been set. This function allocates memory of the approprate size for each pointer.
// Memory is allocated on the host. The idea is to call this before we load the data in from the JNI structures, so we have somewhere to put that data. Later, we will move most of the data to the device.
void initializeNengoGPUData(NengoGPUData* currentData)
{
  if(currentData == NULL)
  {
     return;
  }

    currentData->input = (float*) malloc(currentData->totalInputSize * sizeof(float));
    currentData->inputIndices = (int*) malloc(currentData->totalInputSize * sizeof(int));

    currentData->terminationTransforms = (float*) malloc(currentData->totalTransformSize * sizeof(float));
    currentData->terminationTransformIndices = (int*) malloc(currentData->totalTransformSize * sizeof(int));

    currentData->terminationTau = (float*) malloc(currentData->numTerminations * sizeof(float));
    currentData->terminationDimensionsHost = (int*) malloc(currentData->numTerminations * sizeof(int));
    currentData->terminationValues = (float*) malloc(currentData->totalNumTransformRows * sizeof(float));
    currentData->terminationRowToTerminationIndexor = (int*) malloc(currentData->totalNumTransformRows * sizeof(int));

    currentData->ensembleSums = (float*) malloc(currentData->totalDimension * sizeof(float));
    currentData->dimensionIndexInEnsemble = (int*) malloc(currentData->totalDimension * sizeof(int));
    currentData->sumHelper = (int*) malloc(currentData->totalDimension * sizeof(int));
    currentData->sumIndices = (int*) malloc(currentData->totalDimension * sizeof(int));
    currentData->dimensionToEnsembleIndexor = (int*) malloc(currentData->totalDimension * sizeof(int));

    currentData->ensembleDimensions = (int*) malloc(currentData->numEnsembles * sizeof(int));
    currentData->ensembleNeuronsHost = (int*) malloc(currentData->numEnsembles * sizeof(int));
    currentData->ensembleTerminations = (int*) malloc(currentData->numEnsembles * sizeof(int));
    currentData->ensembleTauRC = (float*) malloc(currentData->numEnsembles * sizeof(float));
    currentData->ensembleTauRef = (float*) malloc(currentData->numEnsembles * sizeof(float));
    currentData->ensemblePositionsInTerminationValues = (int*) malloc(currentData->numEnsembles * sizeof(int));

    currentData->encoders = (float*) malloc(currentData->totalEncoderSize * sizeof(float));
    currentData->encoderIndices = (int*) malloc(currentData->totalEncoderSize * sizeof(int));
    
    currentData->neuronVoltage = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->neuronReftime = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->neuronBias = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->neuronScale = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->neuronToEnsembleIndexor = (int*)malloc(currentData->numNeurons * sizeof(int));

    currentData->spikes = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->spikesHost = (float*)malloc(currentData->numNeurons * sizeof(float));
    currentData->spikeHelper = (int*)malloc(currentData->numNeurons * sizeof(int));
    currentData->spikeIndicesHost = (int*)malloc(currentData->numNeurons * sizeof(int));
    currentData->collectSpikes = (unsigned char*)malloc(currentData->numEnsembles * sizeof(unsigned char));

    currentData->decoders = (float*)malloc(currentData->totalDecoderSize * sizeof(float));
    currentData->decoderIndices = (int*)malloc(currentData->totalDecoderSize * sizeof(int));
    currentData->decoderDimension = (int*)malloc(currentData->numOrigins * sizeof(int));

    currentData->decoderRowToEnsembleIndexor = (int*) malloc(currentData->totalOutputSize * sizeof(float));

    currentData->output = (float*) malloc(currentData->totalOutputSize * sizeof(float));

    checkNengoGPUData(currentData);
}

// Called at the end of initializeNengoGPUData to determine whether any of the mallocs failed.
void checkNengoGPUData(NengoGPUData* currentData)
{
  int status = 0;

  if(!currentData->input) status = 1;
  if(!currentData->inputIndices) status = 1;
  if(!currentData->terminationTransforms) status = 1;
  if(!currentData->terminationTransformIndices) status = 1;
  if(!currentData->terminationTau) status = 1;
  if(!currentData->terminationDimensionsHost) status = 1;
  if(!currentData->terminationValues) status = 1;
  if(!currentData->terminationRowToTerminationIndexor) status = 1;
  if(!currentData->ensembleSums) status = 1;
  if(!currentData->dimensionIndexInEnsemble) status = 1;
  if(!currentData->sumHelper) status = 1;
  if(!currentData->sumIndices) status = 1;
  if(!currentData->dimensionToEnsembleIndexor) status = 1;
  if(!currentData->ensembleDimensions) status = 1;
  if(!currentData->ensembleNeuronsHost) status = 1;
  if(!currentData->ensembleTerminations) status = 1;
  if(!currentData->ensembleTauRC) status = 1;
  if(!currentData->ensembleTauRef) status = 1;
  if(!currentData->ensemblePositionsInTerminationValues) status = 1;
  if(!currentData->encoders) status = 1;
  if(!currentData->encoderIndices) status = 1;
  if(!currentData->neuronVoltage) status = 1;
  if(!currentData->neuronReftime) status = 1;
  if(!currentData->neuronBias) status = 1;
  if(!currentData->neuronScale) status = 1;
  if(!currentData->neuronToEnsembleIndexor) status = 1;
  if(!currentData->spikes) status = 1;
  if(!currentData->spikesHost) status = 1;
  if(!currentData->spikeHelper) status = 1;
  if(!currentData->spikeIndicesHost) status = 1;
  if(!currentData->collectSpikes) status = 1;
  if(!currentData->encoderIndices) status = 1;
  if(!currentData->decoders) status = 1;
  if(!currentData->decoderIndices) status = 1;
  if(!currentData->decoderDimension) status = 1;
  if(!currentData->decoderRowToEnsembleIndexor) status = 1;
  if(!currentData->output) status = 1;

  if(status)
  {
    printf("bad NengoGPUData\n");
    exit(EXIT_FAILURE);
  }
}

// Move data that has to be on the device to the device
void moveToDeviceNengoGPUData(NengoGPUData* currentData)
{
  if(!currentData->onDevice)
  {
    currentData->inputIndices = moveToDeviceIntArray(currentData->inputIndices, currentData->totalInputSize);

    currentData->terminationTransforms = moveToDeviceFloatArray(currentData->terminationTransforms, currentData->totalTransformSize);
    currentData->terminationTransformIndices = moveToDeviceIntArray(currentData->terminationTransformIndices, currentData->totalTransformSize);

    currentData->terminationTau = moveToDeviceFloatArray(currentData->terminationTau, currentData->numTerminations);

    cudaError_t err;
    err = cudaMalloc((void**)&currentData->terminationDimensions, currentData->numTerminations * sizeof(int));
    err = cudaMemcpy(currentData->terminationDimensions, currentData->terminationDimensionsHost, currentData->numTerminations * sizeof(int), cudaMemcpyHostToDevice);

    currentData->terminationValues = moveToDeviceFloatArray(currentData->terminationValues, currentData->totalNumTransformRows);
    currentData->terminationRowToTerminationIndexor = moveToDeviceIntArray(currentData->terminationRowToTerminationIndexor, currentData->totalNumTransformRows);

    currentData->ensembleSums = moveToDeviceFloatArray(currentData->ensembleSums, currentData->totalDimension);
    currentData->dimensionIndexInEnsemble = moveToDeviceIntArray(currentData->dimensionIndexInEnsemble, currentData->totalDimension);
    currentData->sumHelper = moveToDeviceIntArray(currentData->sumHelper, currentData->totalDimension);
    currentData->sumIndices = moveToDeviceIntArray(currentData->sumIndices, currentData->totalDimension);
    currentData->dimensionToEnsembleIndexor = moveToDeviceIntArray(currentData->dimensionToEnsembleIndexor, currentData->totalDimension);

    currentData->ensembleDimensions = moveToDeviceIntArray(currentData->ensembleDimensions, currentData->numEnsembles);

    err = cudaMalloc((void**)&currentData->ensembleNeurons, currentData->numEnsembles * sizeof(int));
    err = cudaMemcpy(currentData->ensembleNeurons, currentData->ensembleNeuronsHost, currentData->numEnsembles * sizeof(int), cudaMemcpyHostToDevice);

    currentData->ensembleTerminations = moveToDeviceIntArray(currentData->ensembleTerminations, currentData->numEnsembles);
    currentData->ensembleTauRC = moveToDeviceFloatArray(currentData->ensembleTauRC, currentData->numEnsembles); 
    currentData->ensembleTauRef = moveToDeviceFloatArray(currentData->ensembleTauRef, currentData->numEnsembles);
    currentData->ensemblePositionsInTerminationValues = moveToDeviceIntArray(currentData->ensemblePositionsInTerminationValues, currentData->numEnsembles); 

    currentData->encoders = moveToDeviceFloatArray(currentData->encoders, currentData->totalEncoderSize);
    currentData->encoderIndices = moveToDeviceIntArray(currentData->encoderIndices, currentData->totalEncoderSize);
    
    currentData->neuronVoltage = moveToDeviceFloatArray(currentData->neuronVoltage, currentData->numNeurons);
    currentData->neuronReftime = moveToDeviceFloatArray(currentData->neuronReftime, currentData->numNeurons);
    currentData->neuronBias = moveToDeviceFloatArray(currentData->neuronBias, currentData->numNeurons);
    currentData->neuronScale = moveToDeviceFloatArray(currentData->neuronScale, currentData->numNeurons);
    currentData->neuronToEnsembleIndexor = moveToDeviceIntArray(currentData->neuronToEnsembleIndexor, currentData->numNeurons);

    currentData->spikes = moveToDeviceFloatArray(currentData->spikes, currentData->numNeurons);
    currentData->spikeHelper = moveToDeviceIntArray(currentData->spikeHelper, currentData->numNeurons);

    err = cudaMalloc((void**)&currentData->spikeIndices, currentData->numNeurons * sizeof(int));
    err = cudaMemcpy(currentData->spikeIndices, currentData->spikeIndicesHost, currentData->numNeurons * sizeof(int), cudaMemcpyHostToDevice);

    currentData->decoders = moveToDeviceFloatArray(currentData->decoders, currentData->totalDecoderSize);
    currentData->decoderIndices = moveToDeviceIntArray(currentData->decoderIndices, currentData->totalDecoderSize);
    currentData->decoderDimension = moveToDeviceIntArray(currentData->decoderDimension, currentData->numOrigins);

    currentData->decoderRowToEnsembleIndexor = moveToDeviceIntArray(currentData->decoderRowToEnsembleIndexor, currentData->totalOutputSize);

    currentData->onDevice = 1;
  }
}

// Free the NengoGPUData. Makes certain assumptions about where each array is (device or host).
void freeNengoGPUData(NengoGPUData* currentData)
{
  free(currentData->input);
  free(currentData->output);

  cudaFree(currentData->inputIndices);

  cudaFree(currentData->terminationTransforms);
  cudaFree(currentData->terminationTransformIndices);
  cudaFree(currentData->terminationTau);
  free(currentData->terminationDimensionsHost);
  cudaFree(currentData->terminationDimensions);
  cudaFree(currentData->terminationValues);
  cudaFree(currentData->terminationRowToTerminationIndexor);

  cudaFree(currentData->ensembleNeurons);
  free(currentData->ensembleNeuronsHost);
  cudaFree(currentData->ensembleDimensions);
  cudaFree(currentData->ensembleTerminations);
  cudaFree(currentData->dimensionToEnsembleIndexor);
  cudaFree(currentData->ensemblePositionsInTerminationValues);

  cudaFree(currentData->ensembleSums);
  cudaFree(currentData->dimensionIndexInEnsemble);
  cudaFree(currentData->sumIndices);
  cudaFree(currentData->sumHelper);

  cudaFree(currentData->encoders);
  cudaFree(currentData->encoderIndices);
  
  cudaFree(currentData->neuronVoltage);
  cudaFree(currentData->neuronReftime);
  cudaFree(currentData->neuronBias);
  cudaFree(currentData->neuronScale);
  cudaFree(currentData->neuronToEnsembleIndexor);
  cudaFree(currentData->ensembleTauRC);
  cudaFree(currentData->ensembleTauRef);

  cudaFree(currentData->spikes);
  free(currentData->spikesHost);
  cudaFree(currentData->spikeHelper);
  cudaFree(currentData->spikeIndices);
  free(currentData->spikeIndicesHost);
  free(currentData->collectSpikes);

  cudaFree(currentData->decoders);
  cudaFree(currentData->decoderIndices);
  cudaFree(currentData->decoderDimension);
  cudaFree(currentData->decoderRowToEnsembleIndexor);
}

// print the NengoGPUData. Should only be called once the data has been set.
void printNengoGPUData(NengoGPUData* currentData)
{
  printf("printing NengoGPUData:\n");

  printf("onDevice: %d\n", currentData->onDevice);
  printf("device: %d\n", currentData->device);
  printf("maxTimeStep: %f\n", currentData->maxTimeStep);

  printf("numNeurons: %d\n", currentData->numNeurons);
  printf("numEnsembles: %d\n", currentData->numEnsembles);
  printf("numTerminations: %d\n", currentData->numTerminations);
  printf("numOrigins: %d\n", currentData->numOrigins);

  printf("totalInputSize: %d\n", currentData->totalInputSize);
  printf("totalTransformSize: %d\n", currentData->totalTransformSize);
  printf("totalNumTransformRows: %d\n", currentData->totalNumTransformRows);
  printf("totalDimension: %d\n", currentData->totalDimension);
  printf("totalEncoderSize: %d\n", currentData->totalEncoderSize);
  printf("totalDecoderSize: %d\n", currentData->totalDecoderSize);
  printf("totalNumDecoderRows: %d\n", currentData->totalNumDecoderRows);
  printf("totalOutputSize: %d\n", currentData->totalOutputSize);
  
  printf("maxTransformDimension: %d\n", currentData->maxTransformDimension);
  printf("maxDimension: %d\n", currentData->maxDimension);
  printf("maxNumNeurons: %d\n", currentData->maxNumNeurons);
  printf("maxDecoderDimension: %d\n", currentData->maxDecoderDimension);

  printf("input:\n");
  printFloatArray(currentData->input, currentData->totalInputSize, 0);
  printf("inputIndices:\n");
  printIntArray(currentData->inputIndices, currentData->totalInputSize, currentData->onDevice);

  printf("terminationTransforms:\n");
  printFloatArray(currentData->terminationTransforms, currentData->totalTransformSize, currentData->onDevice);
  printf("terminationTransformIndices:\n");
  printIntArray(currentData->terminationTransformIndices, currentData->totalTransformSize, currentData->onDevice);
  printf("terminationTau:\n");
  printFloatArray(currentData->terminationTau, currentData->numTerminations, currentData->onDevice);
  printf("terminationDimensions:\n");
  if(currentData->onDevice)
  {
  printIntArray(currentData->terminationDimensions, currentData->numTerminations, currentData->onDevice);
  }
  else
  {
  printIntArray(currentData->terminationDimensionsHost, currentData->numTerminations, currentData->onDevice);
  }

  printf("terminationValues:\n");
  printFloatArray(currentData->terminationValues, currentData->totalNumTransformRows, currentData->onDevice);
  printf("terminationRowToTerminationIndexor:\n");
  printIntArray(currentData->terminationRowToTerminationIndexor, currentData->totalNumTransformRows, currentData->onDevice);

  printf("ensembleNeurons:\n");
  printIntArray(currentData->ensembleNeurons, currentData->numEnsembles, currentData->onDevice);
  printf("ensembleDimensions:\n");
  printIntArray(currentData->ensembleDimensions, currentData->numEnsembles, currentData->onDevice);
  printf("ensembleTerminations:\n");
  printIntArray(currentData->ensembleTerminations, currentData->numEnsembles, currentData->onDevice);
  printf("dimensionToEnsembleIndexor:\n");
  printIntArray(currentData->dimensionToEnsembleIndexor, currentData->totalDimension, currentData->onDevice);
  printf("ensemblePositionsInTermination:\n");
  printIntArray(currentData->ensemblePositionsInTerminationValues, currentData->numEnsembles, currentData->onDevice);
  
  printf("ensembleSums:\n");
  printFloatArray(currentData->ensembleSums, currentData->totalDimension, currentData->onDevice);
  printf("dimensionIndexInEnsemble:\n");
  printIntArray(currentData->dimensionIndexInEnsemble, currentData->totalDimension, currentData->onDevice);
  printf("sumIndices:\n");
  printIntArray(currentData->sumIndices, currentData->totalDimension, currentData->onDevice);
  printf("sumHelper:\n");
  printIntArray(currentData->sumHelper, currentData->totalDimension, currentData->onDevice);
  
  printf("encoders:\n");
  printFloatArray(currentData->encoders, currentData->totalEncoderSize, currentData->onDevice);
  printf("encoderIndices:\n");
  printIntArray(currentData->encoderIndices, currentData->totalEncoderSize, currentData->onDevice);

  printf("neuronVoltage:\n");
  printFloatArray(currentData->neuronVoltage, currentData->numNeurons, currentData->onDevice);
  printf("neuronReftime:\n");
  printFloatArray(currentData->neuronReftime, currentData->numNeurons, currentData->onDevice);
  printf("neuronBias:\n");
  printFloatArray(currentData->neuronBias, currentData->numNeurons, currentData->onDevice);
  printf("neuronScale:\n");
  printFloatArray(currentData->neuronScale, currentData->numNeurons, currentData->onDevice);
  printf("neuronToEnsembleIndexor:\n");
  printIntArray(currentData->neuronToEnsembleIndexor, currentData->numNeurons, currentData->onDevice);
  printf("ensembleTauRC:\n");
  printFloatArray(currentData->ensembleTauRC, currentData->numEnsembles, currentData->onDevice);
  printf("ensembleTauRef:\n");
  printFloatArray(currentData->ensembleTauRef, currentData->numEnsembles, currentData->onDevice);
  
  printf("spikes:\n");
  printFloatArray(currentData->spikes, currentData->numNeurons, currentData->onDevice);
  printf("spikeHelper:\n");
  printIntArray(currentData->spikeHelper, currentData->numNeurons, currentData->onDevice);
  printf("spikeIndices:\n");
  printIntArray(currentData->spikeIndices, currentData->numNeurons, currentData->onDevice);

  printf("decoder:\n");
  printFloatArray(currentData->decoders, currentData->totalDecoderSize, currentData->onDevice);
  printf("decoderIndices:\n");
  printIntArray(currentData->decoderIndices, currentData->totalDecoderSize, currentData->onDevice);
  printf("decoderDimension:\n");
  printIntArray(currentData->decoderDimension, currentData->numOrigins, currentData->onDevice);
  printf("decoderRowToEnsembleIndexor:\n");
  printIntArray(currentData->decoderRowToEnsembleIndexor, currentData->totalNumDecoderRows, currentData->onDevice);

  int bytesUseOnGPU = 4 * (2 * currentData->totalInputSize + 2 * currentData->totalTransformSize + 2 * currentData->numTerminations + 2 * currentData->totalNumTransformRows + 5 * currentData->totalDimension + 6 * currentData->numEnsembles + 2 * currentData->totalEncoderSize + 8 * currentData->numNeurons + 2 * currentData->totalDecoderSize + currentData->numOrigins + 2 * currentData->totalOutputSize);
  printf("bytes on GPU: %d\n", bytesUseOnGPU);
}

void printIntArray(int* array, int m, int onDevice)
{
  if(onDevice)
  {
    printIntArrayFromDevice(NULL, array, m);
    return;
  }

  int i = 0;

  for(; i < m; i++)
  {
    printf("%d ", array[i]);
  }

  printf("\n");
}

void printFloatArray(float* array, int m, int onDevice)
{
  if(onDevice)
  {
    printFloatArrayFromDevice(NULL, array, m);
    return;
  }

  int i = 0;

  for(; i < m; i++)
  {
    printf("%f ", array[i]);
  }

  printf("\n");
}

int* moveToDeviceIntArray(int* array, int size)
{
  int* result;
  cudaError_t err;
  err = cudaMalloc((void**)&result, size * sizeof(int));
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  err = cudaMemcpy(result, array, size * sizeof(int), cudaMemcpyHostToDevice);
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  free(array);
  return result;
}
  
float* moveToDeviceFloatArray(float* array, int size)
{
  float* result;
  cudaError_t err;

  err = cudaMalloc((void**)&result, size * sizeof(float));
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  err = cudaMemcpy(result, array, size * sizeof(float), cudaMemcpyHostToDevice);
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  free(array);
  return result;
}
  
float* moveToHostFloatArray(float* array, int size)
{
  float* result;
  result = (float*)malloc(size * sizeof(float));
  cudaMemcpy(result, array, size * sizeof(float), cudaMemcpyDeviceToHost);
  cudaFree(array);
  return result;
}

int* moveToHostIntArray(int* array, int size)
{
  int* result;
  result = (int*)malloc(size * sizeof(int));
  cudaMemcpy(result, array, size * sizeof(int), cudaMemcpyDeviceToHost);
  cudaFree(array);
  return result;
}

#ifdef __cplusplus
}
#endif

