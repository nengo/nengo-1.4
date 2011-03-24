#ifdef __cplusplus
extern "C"{
#endif

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <cuda_runtime.h>

#include "NengoGPUData.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPU.h"

extern FILE* fp;


///////////////////////////////////////////////////////
// intArray and floatArray allocating and freeing
///////////////////////////////////////////////////////
intArray* newIntArray(int size, const char* name)
{
  intArray* new = (intArray*)malloc(sizeof(intArray));
  if(!new)
  {
    printf("Failed to allocate memory for intArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  new->array = (int*)malloc(size * sizeof(int));
  if(!new->array)
  {
    printf("Failed to allocate memory for intArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }
  
  new->size = size;
  new->name = strdup(name);
  new->onDevice = 0;

  return new;
}

void freeIntArray(intArray* a)
{
  a->onDevice ? cudaFree(a->array) : free(a->array);
  free(a->name);

  free(a);
}

intArray* newIntArrayOnDevice(int size, const char* name)
{
  intArray* new = (intArray*)malloc(sizeof(intArray));

  new->array = allocateCudaIntArray(size);
  new->size = size;
  new->name = strdup(name);
  new->onDevice = 1;

  return new;
}

floatArray* newFloatArray(int size, const char* name)
{
  floatArray* new = (floatArray*)malloc(sizeof(floatArray));
  if(!new)
  {
    printf("Failed to allocate memory for floatArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  new->array = (float*)malloc(size * sizeof(float));
  if(!new->array)
  {
    printf("Failed to allocate memory for floatArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  new->size = size;
  new->name = strdup(name);
  new->onDevice = 0;

  return new;
}

void freeFloatArray(floatArray* a)
{
  a->onDevice ? cudaFree(a->array) : free(a->array);
  free(a->name);
  free(a);
}

floatArray* newFloatArrayOnDevice(int size, const char* name)
{
  floatArray* new = (floatArray*)malloc(sizeof(floatArray));

  new->array = allocateCudaFloatArray(size);
  new->size = size;
  new->name = strdup(name);
  new->onDevice = 1;

  return new;
}
  

///////////////////////////////////////////////////////
// intArray and floatArray safe getters and setters
///////////////////////////////////////////////////////
void intArraySetElement(intArray* a, int index, int value)
{
  if(index >= a->size || index < 0)
  {
    printf("Setting intArray out of bounds, name: %s, size: %d, index:%d\n", a->name, a->size, index);
    exit(EXIT_FAILURE);
  }
  else
  {
    a->array[index] = value;
  }
}

void floatArraySetElement(floatArray* a, int index, float value)
{
  if(index >= a->size || index < 0)
  {
    printf("Setting floatArray out of bounds, name: %s, size: %d, index:%d\n", a->name, a->size, index);
    exit(EXIT_FAILURE);
  }
  else
  {
    a->array[index] = value;
  }
}

int intArrayGetElement(intArray* a, int index)
{
  if(index >= a->size || index < 0)
  {
    printf("Getting intArray out of bounds, name: %s, size: %d, index:%d\n", a->name, a->size, index);
    exit(EXIT_FAILURE);
  }
  else
  {
    return a->array[index];
  }
}

float floatArrayGetElement(floatArray* a, int index)
{
  if(index >= a->size || index < 0)
  {
    printf("Getting floatArray out of bounds, name: %s, size: %d, index:%d\n", a->name, a->size, index);
    exit(EXIT_FAILURE);
  }
  else
  {
    return a->array[index];
  }
}

void intArraySetData(intArray* a, int* data, int dataSize)
{
  if(dataSize > a->size)
  {
    printf("Warning: calling intArraySetData with a data set that is too large; truncating data. name: %s, size: %d, dataSize: %d", a->name, a->size, dataSize);
  }
  
  memcpy(a->array, data, dataSize * sizeof(int));
}

void floatArraySetData(floatArray* a, float* data, int dataSize)
{
  if(dataSize > a->size)
  {
    printf("Warning: calling floatArraySetData with a data set that is too large; truncating data. name: %s, size: %d, dataSize: %d", a->name, a->size, dataSize);
  }
  
  memcpy(a->array, data, dataSize * sizeof(float));
}

///////////////////////////////////////////////////////
// projection storing and printing
///////////////////////////////////////////////////////
void storeProjection(projection* proj, int* data)
{
  proj->sourceEnsemble = data[0];
  proj->sourceOrigin = data[1];
  proj->destinationEnsemble = data[2];
  proj->destinationTermination = data[3];
  proj->size = data[4];
  proj->device = data[5];
}

void printProjection(projection* proj)
{
  printf("%d %d %d %d %d %d\n", proj->sourceEnsemble, proj->sourceOrigin, proj->destinationEnsemble, proj->destinationTermination, proj->size, proj->device);
}


///////////////////////////////////////////////////////
// NengoGPUData functions
///////////////////////////////////////////////////////

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
  new-> numDecodedTerminations = 0;
  new-> numNDterminations = 0;
  new-> numOrigins = 0;

  new->totalInputSize = 0;
  new->totalTransformSize = 0;
  new->totalNumTransformRows = 0;
  new->totalEnsembleDimension = 0;
  new->totalEncoderSize = 0;
  new->totalDecoderSize = 0;
  new->totalOutputSize = 0;

  new->maxDecodedTerminationDimension = 0;
  new->maxNumDecodedTerminations = 0;
  new->maxDimension = 0;
  new->maxNumNeurons = 0;
  new->maxOriginDimension = 0;
  
  new->numNDterminations = 0;

  new->input = NULL;
  new->inputHost = NULL;
  new->inputOffset = NULL;

  new->terminationTransforms = NULL;
  new->transformRowToInputIndexor = NULL;
  new->terminationTau = NULL;
  new->inputDimensions = NULL;
  new->terminationOutput = NULL;
  new->terminationOutputIndexor = NULL;

  new->blockSizeForEncode = 0;
  new->numBlocksForEncode = 0;

  new->blockSizeForDecode = 0;
  new->numBlocksForDecode = 0;

  new->ensembleSums = NULL;
  new->encoders = NULL;
  new->decoders = NULL;
  new->spikes = NULL;
  new->spikesHost = NULL;
  new->output = NULL;

  new->neuronVoltage = NULL;
  new->neuronReftime = NULL;
  new->neuronBias = NULL;
  new->neuronScale = NULL;
  new->ensembleTauRC = NULL;
  new->ensembleTauRef = NULL;

  new->ensembleNumTerminations = NULL;
  new->ensembleDimension = NULL;
  new->ensembleNumNeurons = NULL;
  new->ensembleOutputSize = NULL;
  new->ensembleNumOrigins = NULL;

  new->blockToEnsembleMapForEncode = NULL;
  new->blockToEnsembleMapForDecode = NULL;

  new->ensembleIndexOfFirstBlockForEncode = NULL;
  new->ensembleIndexOfFirstBlockForDecode = NULL;

  new->ensembleOffsetInDimensions = NULL;
  new->ensembleOffsetInNeurons = NULL;
  new->ensembleOffsetInEncoders = NULL;
  new->ensembleOffsetInDecoders = NULL;
  new->ensembleOffsetInOutput = NULL;

  new->encoderStride = NULL;
  new->decoderStride = NULL;

  new->ensembleOrderInEncoders = NULL;
  new->ensembleOrderInDecoders = NULL;

  new->output = NULL;
  new->outputHost = NULL;
  
  new->originDimension = NULL;
  new->outputOffset = NULL;
  new->GPUTerminationToOriginMap = NULL;

  new->ensembleIndexInJavaArray = NULL;
  
  new->NDterminationInputIndexor = NULL;
  new->NDterminationCurrents = NULL;
  new->NDterminationWeights = NULL;
  new->NDterminationEnsembleOffset = NULL;
  new->NDterminationEnsembleSums = NULL;

  return new;
}


// Should only be called once the NengoGPUData's numerical values have been set. This function allocates memory of the approprate size for each pointer.
// Memory is allocated on the host. The idea is to call this before we load the data in from the JNI structures, so we have somewhere to put that data. Later, we will move most of the data to the device.
void initializeNengoGPUData(NengoGPUData* new)
{
  if(new == NULL)
  {
     return;
  }

  const char* name;
  new->blockSizeForEncode = 128;
  new->blockSizeForDecode = 8;
  
  
  name = "inputOffset";
  new->inputOffset = newIntArray(new->numTerminations, name);

  name = "terminationTranforms";
  new->terminationTransforms = newFloatArray(new->totalTransformSize, name);
  memset(new->terminationTransforms->array, '\0', new->terminationTransforms->size * sizeof(float));

  name = "transformRowToInputIndexor";
  new->transformRowToInputIndexor = newIntArray(new->totalNumTransformRows, name);
  name = "terminationTau";
  new->terminationTau = newFloatArray(new->numTerminations, name);
  name = "inputDimensions";
  new->inputDimensions = newIntArray(new->numTerminations, name);
  name = "terminationOutputIndexor";
  new->terminationOutputIndexor = newIntArray(new->totalNumTransformRows, name);
 
  name = "encoders";
  new->encoders = newFloatArray(new->totalEncoderSize, name);
  name = "decoders";
  new->decoders = newFloatArray(new->totalDecoderSize, name);

  name = "neuronBias";
  new->neuronBias = newFloatArray(new->numNeurons, name);
  name = "neuronScale";
  new->neuronScale = newFloatArray(new->numNeurons, name);
  name = "ensembleTauRC";
  new->ensembleTauRC = newFloatArray(new->numEnsembles, name);
  name = "ensembleTauRef";
  new->ensembleTauRef = newFloatArray(new->numEnsembles, name);

  name = "ensembleNumTerminations";
  new->ensembleNumTerminations = newIntArray(new->numEnsembles, name);
  name = "ensembleDimension";
  new->ensembleDimension = newIntArray(new->numEnsembles, name);
  name = "ensembleNumNeurons";
  new->ensembleNumNeurons = newIntArray(new->numEnsembles, name);
  name = "ensembleOutputSize";
  new->ensembleOutputSize = newIntArray(new->numEnsembles, name);
  name = "ensembleNumOrigins";
  new->ensembleNumOrigins = newIntArray(new->numEnsembles, name);


  name = "ensembleOffsetInDimensions";
  new->ensembleOffsetInDimensions = newIntArray(new->numEnsembles, name);
  name = "ensembleOffsetInNeurons";
  new->ensembleOffsetInNeurons = newIntArray(new->numEnsembles, name);
  name = "ensembleOffsetInEncoders";
  new->ensembleOffsetInEncoders = newIntArray(new->numEnsembles, name);
  name = "ensembleOffsetInDecoders";
  new->ensembleOffsetInDecoders = newIntArray(new->numEnsembles, name);
  name = "ensembleOffsetInOutput";
  new->ensembleOffsetInOutput = newIntArray(new->numEnsembles, name);
  name = "neuronToEnsembleIndexor";
  new->neuronToEnsembleIndexor = newIntArray(new->numNeurons, name);

  name = "ensembleIndexOfFirstBlockForEncode";
  new->ensembleIndexOfFirstBlockForEncode = newIntArray(new->numEnsembles, name);
  name = "ensembleIndexOfFirstBlockForDecode";
  new->ensembleIndexOfFirstBlockForDecode = newIntArray(new->numEnsembles, name);

  name = "encoderStride";
  new->encoderStride = newIntArray(new->maxDimension, name);
  name = "decoderStride";
  new->decoderStride = newIntArray(new->maxNumNeurons, name);

  //new->ensembleOrderInEncoders = (int*)malloc(new->numEnsembles * sizeof(int));
  //new->ensembleOrderInDecoders = (int*)malloc(new->numEnsembles * sizeof(int));

  name = "originDimension";
  new->originDimension = newIntArray(new->numOrigins, name);
  name = "outputOffset";
  new->outputOffset = newIntArray(new->numOrigins, name);

  name = "ensembleIndexInJavaArray";
  new->ensembleIndexInJavaArray = newIntArray(new->numEnsembles, name);

  name = "spikesHost";
  new->spikesHost = newFloatArray(new->numNeurons, name);

  name = "outputHost";
  new->outputHost = newFloatArray(new->totalOutputSize, name);


  name = "NDterminationInputIndexor";
  new->NDterminationInputIndexor = newIntArray(new->numNDterminations, name);
  name = "NDterminationWeights";
  new->NDterminationWeights = newFloatArray(new->numNDterminations, name);
  name = "NDterminationEnsembleOffset";
  new->NDterminationEnsembleOffset = newIntArray(new->numEnsembles, name);
}


// Called at the end of initializeNengoGPUData to determine whether any of the mallocs failed.
void checkNengoGPUData(NengoGPUData* currentData)
{
  int status = 0;

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
    // this function is in NengoGPU_CUDA.cu
    initializeDeviceInputAndOutput(currentData);

    moveToDeviceIntArray(currentData->inputOffset);
    moveToDeviceFloatArray(currentData->terminationTransforms);
    moveToDeviceFloatArray(currentData->terminationTau);
    moveToDeviceIntArray(currentData->inputDimensions);
    moveToDeviceIntArray(currentData->transformRowToInputIndexor);
    moveToDeviceIntArray(currentData->terminationOutputIndexor);

    moveToDeviceFloatArray(currentData->encoders);
    moveToDeviceFloatArray(currentData->decoders);

    moveToDeviceFloatArray(currentData->neuronBias);
    moveToDeviceFloatArray(currentData->neuronScale);
    moveToDeviceFloatArray(currentData->ensembleTauRC);
    moveToDeviceFloatArray(currentData->ensembleTauRef);
    
    moveToDeviceIntArray(currentData->ensembleNumTerminations);
    moveToDeviceIntArray(currentData->ensembleDimension);
    moveToDeviceIntArray(currentData->ensembleNumNeurons);
    moveToDeviceIntArray(currentData->ensembleOutputSize);
    moveToDeviceIntArray(currentData->ensembleNumOrigins);

    moveToDeviceIntArray(currentData->ensembleOffsetInDimensions);
    moveToDeviceIntArray(currentData->ensembleOffsetInNeurons);
    moveToDeviceIntArray(currentData->ensembleOffsetInEncoders);
    moveToDeviceIntArray(currentData->ensembleOffsetInDecoders);
    moveToDeviceIntArray(currentData->ensembleOffsetInOutput);
    moveToDeviceIntArray(currentData->neuronToEnsembleIndexor);

    moveToDeviceIntArray(currentData->blockToEnsembleMapForEncode);
    moveToDeviceIntArray(currentData->blockToEnsembleMapForDecode);
    moveToDeviceIntArray(currentData->ensembleIndexOfFirstBlockForEncode);
    moveToDeviceIntArray(currentData->ensembleIndexOfFirstBlockForDecode);
    moveToDeviceIntArray(currentData->encoderStride);
    moveToDeviceIntArray(currentData->decoderStride);

    moveToDeviceIntArray(currentData->outputOffset);
    moveToDeviceIntArray(currentData->GPUTerminationToOriginMap);

    moveToDeviceIntArray(currentData->NDterminationInputIndexor);
    moveToDeviceFloatArray(currentData->NDterminationWeights);
    moveToDeviceIntArray(currentData->NDterminationEnsembleOffset);

    currentData->onDevice = 1;
  }
}

// Free the NengoGPUData. Makes certain assumptions about where each array is (device or host).
void freeNengoGPUData(NengoGPUData* currentData)
{
  freeFloatArray(currentData->input);
  freeFloatArray(currentData->inputHost); 
  freeIntArray(currentData->inputOffset);

  freeFloatArray(currentData->terminationTransforms);
  freeIntArray(currentData->transformRowToInputIndexor);
  freeFloatArray(currentData->terminationTau);
  freeIntArray(currentData->inputDimensions);
  freeFloatArray(currentData->terminationOutput);
  freeIntArray(currentData->terminationOutputIndexor);

  freeFloatArray(currentData->ensembleSums);
  freeFloatArray(currentData->encoders);
  freeFloatArray(currentData->decoders);
  freeFloatArray(currentData->encodeResult);

  freeFloatArray(currentData->neuronVoltage);
  freeFloatArray(currentData->neuronReftime);
  freeFloatArray(currentData->neuronBias);
  freeFloatArray(currentData->neuronScale);
  freeFloatArray(currentData->ensembleTauRC);
  freeFloatArray(currentData->ensembleTauRef);

  freeIntArray(currentData->ensembleNumTerminations);
  freeIntArray(currentData->ensembleDimension);
  freeIntArray(currentData->ensembleNumNeurons);
  freeIntArray(currentData->ensembleOutputSize);
  freeIntArray(currentData->ensembleNumOrigins);

  freeIntArray(currentData->ensembleOffsetInDimensions);
  freeIntArray(currentData->ensembleOffsetInNeurons);
  freeIntArray(currentData->ensembleOffsetInEncoders);
  freeIntArray(currentData->ensembleOffsetInDecoders);
  freeIntArray(currentData->ensembleOffsetInOutput);
  freeIntArray(currentData->neuronToEnsembleIndexor);

  freeIntArray(currentData->ensembleIndexOfFirstBlockForEncode);
  freeIntArray(currentData->ensembleIndexOfFirstBlockForDecode);

  freeIntArray(currentData->encoderStride);
  freeIntArray(currentData->decoderStride);

  freeIntArray(currentData->ensembleOrderInEncoders);
  freeIntArray(currentData->ensembleOrderInDecoders);

  freeFloatArray(currentData->spikes);
  freeFloatArray(currentData->spikesHost);

  freeFloatArray(currentData->output);
  freeFloatArray(currentData->outputHost);

  freeIntArray(currentData->originDimension);
  freeIntArray(currentData->ensembleIndexInJavaArray);
  freeIntArray(currentData->outputOffset);
  freeIntArray(currentData->GPUTerminationToOriginMap);

  freeIntArray(currentData->NDterminationInputIndexor);
  freeFloatArray(currentData->NDterminationCurrents);
  freeFloatArray(currentData->NDterminationWeights);
  freeIntArray(currentData->NDterminationEnsembleOffset);
  freeFloatArray(currentData->NDterminationEnsembleSums);
  
  free(currentData);
};

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
  printf("totalEnsembleDimension: %d\n", currentData->totalEnsembleDimension);
  printf("totalEncoderSize: %d\n", currentData->totalEncoderSize);
  printf("totalDecoderSize: %d\n", currentData->totalDecoderSize);
  printf("totalOutputSize: %d\n", currentData->totalOutputSize);
  
  printf("maxDecodedTerminationDimension: %d\n", currentData->maxDecodedTerminationDimension);
  printf("maxNumDecodedTerminations: %d\n", currentData->maxNumDecodedTerminations);
  printf("maxDimension: %d\n", currentData->maxDimension);
  printf("maxNumNeurons: %d\n", currentData->maxNumNeurons);
  printf("maxOriginDimension: %d\n", currentData->maxOriginDimension);
  printf("GPUinputSize: %d\n", currentData->GPUInputSize);
  printf("CPUinputSize: %d\n", currentData->CPUInputSize);


  printf("inputOffset:\n");
  printIntArrayFromDevice(NULL, currentData->inputOffset, currentData->numTerminations, 1);

  printf("terminationTransforms:\n");
  printFloatArrayFromDevice(NULL, currentData->terminationTransforms, currentData->totalNumTransformRows, currentData->maxDecodedTerminationDimension);
  
  printf("terminationTau:\n");
  printFloatArrayFromDevice(NULL, currentData->terminationTau, currentData->numTerminations, 1); 
  
  printf("inputDimensions:\n");
  printIntArrayFromDevice(NULL, currentData->inputDimensions, currentData->numTerminations, 1);
  
  
  printf("terminationOutputIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->terminationOutputIndexor, currentData->totalNumTransformRows, 1);

  printf("transformRowToInputIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->transformRowToInputIndexor, currentData->totalNumTransformRows, 1);

  printf("ensembleSums:\n");
  printFloatArrayFromDevice(NULL, currentData->ensembleSums, currentData->totalEnsembleDimension, 1);
  
  printf("encoders:\n");
//  printFloatArrayFromDevice(NULL, currentData->encoders, currentData->totalEncoderSize, 1); 

  printf("decoders:\n");
  //printFloatArrayFromDevice(NULL, currentData->decoders, currentData->totalDecoderSize, 1);


  //printf("neuronBias:\n");
  //printFloatArrayFromDevice(NULL, currentData->neuronBias, currentData->numNeurons, 1);

  //printf("neuronScale:\n");
  //printFloatArrayFromDevice(NULL, currentData->neuronScale, currentData->numNeurons, 1);

  printf("ensembleTauRC:\n");
  printFloatArrayFromDevice(NULL, currentData->ensembleTauRC, currentData->numEnsembles, 1);

  printf("ensembleTauRef:\n");
  printFloatArrayFromDevice(NULL, currentData->ensembleTauRef, currentData->numEnsembles, 1);
  
  printf("ensembleNumTerminations:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleNumTerminations, currentData->numEnsembles, 1);

  printf("ensembleDimension:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleDimension, currentData->numEnsembles, 1);

  printf("ensembleNumNeurons:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleNumNeurons, currentData->numEnsembles, 1);

  printf("ensembleOutputSize:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOutputSize, currentData->numEnsembles, 1);

  printf("ensembleNumOrigins:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleNumOrigins, currentData->numEnsembles, 1);

  printf("ensembleOffsetInDimensions:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOffsetInDimensions, currentData->numEnsembles, 1);

  printf("ensembleOffsetInNeurons:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOffsetInNeurons, currentData->numEnsembles, 1);

  printf("ensembleOffsetInEncoders:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOffsetInEncoders, currentData->numEnsembles, 1);

  printf("ensembleOffsetInDecoders:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOffsetInDecoders, currentData->numEnsembles, 1);

  printf("ensembleOffsetInOutput:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleOffsetInOutput, currentData->numEnsembles, 1);

  printf("neuronToEnsembleIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->neuronToEnsembleIndexor, currentData->numNeurons, 1);

  printf("blockToEnsembleMapForEncode:\n");
  printIntArrayFromDevice(NULL, currentData->blockToEnsembleMapForEncode, currentData->numBlocksForEncode, 1);

  printf("blockToEnsembleMapForDecode:\n");
  printIntArrayFromDevice(NULL, currentData->blockToEnsembleMapForDecode, currentData->numBlocksForDecode, 1);

  printf("ensembleIndexOfFirstBlockForEncode:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleIndexOfFirstBlockForEncode, currentData->numEnsembles, 1);

  printf("ensembleIndexOfFirstBlockForDecode:\n");
  printIntArrayFromDevice(NULL, currentData->ensembleIndexOfFirstBlockForDecode, currentData->numEnsembles, 1);


  printf("encoderStride:\n");
  printIntArrayFromDevice(NULL, currentData->encoderStride, currentData->maxDimension, 1);

  printf("decoderStride:\n");
  printIntArrayFromDevice(NULL, currentData->decoderStride, currentData->maxNumNeurons, 1);


  printf("originDimension:\n");
  printIntArray(currentData->originDimension, currentData->numOrigins, 1);

  printf("outputOffset:\n");
  printIntArrayFromDevice(NULL, currentData->outputOffset, currentData->numOrigins, 1);

  printf("GPUTerminationToOriginMap:\n");
  printIntArrayFromDevice(NULL, currentData->GPUTerminationToOriginMap, currentData->GPUInputSize, 1);
  
  printf("numNDterminations: %d\n", currentData->numNDterminations);

  printf("NDterminationInputIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationInputIndexor, currentData->numNDterminations, 1);
  printf("NDterminationWeights:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationWeights, currentData->numNDterminations, 1);
  printf("NDterminationEnsembleOffset:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationEnsembleOffset, currentData->numEnsembles, 1);

//  int bytesOnGPU = sizeof(float) * (8 * currentData->numEnsembles + currentData->totalInputSize + currentData->totalTransformSize + 2 * currentData->totalNumTransformRows + 2 * currentData->numTerminations + currentData->maxNumDecodedTerminations * currentData->totalEnsembleDimension + currentData->maxDimension * currentData->numNeurons + currentData->totalEnsembleDimension + currentData->numNeurons * 6 + currentData->maxNumNeurons * currentData->totalOutputSize + 2 * currentData->totalOutputSize);
 // printf("bytes on GPU: %d\n", bytesOnGPU);
 
}

void printDynamicNengoGPUData(NengoGPUData* currentData)
{
  
  
  printf("input:\n");
  printFloatArrayFromDevice(NULL, currentData->input, currentData->totalInputSize, 1);

  printf("inputOffset:\n");
  printIntArrayFromDevice(NULL, currentData->inputOffset, currentData->numTerminations, 1);

  printf("terminationTau:\n");
  printFloatArrayFromDevice(NULL, currentData->terminationTau, currentData->numTerminations, 1); 
  /*

  printf("terminationOutput:\n");
  printFloatArrayFromDevice(NULL, currentData->terminationOutput, currentData->totalEnsembleDimension * currentData->maxNumDecodedTerminations, 1);

  printf("ensembleSums:\n");
  printFloatArrayFromDevice(NULL, currentData->ensembleSums, currentData->totalEnsembleDimension, 1);

  printf("encodeResult:\n");
  printFloatArrayFromDevice(NULL, currentData->encodeResult, currentData->numNeurons, 1);

  //printf("neuronVoltage:\n");
  //printFloatArrayFromDevice(NULL, currentData->neuronVoltage, currentData->numNeurons, 1);
  //printFloatArrayFromDevice(NULL, currentData->neuronVoltage, currentData->maxNumNeurons, currentData->numEnsembles);

  //printf("neuronReftime:\n");
  //printFloatArrayFromDevice(NULL, currentData->neuronReftime, currentData->numNeurons, 1);


  //printf("spikes:\n");
  //printFloatArrayFromDevice(NULL, currentData->spikes, currentData->numNeurons, 1);
  //printFloatArrayFromDevice(NULL, currentData->spikes, currentData->maxNumNeurons, currentData->numEnsembles);


  //printf("spikesHost:\n");
  //printFloatArray(currentData->spikesHost, currentData->numNeurons, 1);
 

  printf("output:\n");
  printFloatArrayFromDevice(NULL, currentData->output, currentData->totalOutputSize, 1);

  printf("outputHost:\n");
  printFloatArray(currentData->outputHost, currentData->totalOutputSize, 1);

*/

  printf("NDterminationInputIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationInputIndexor, currentData->numNDterminations, 1);

  printf("NDterminationWeights:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationWeights, currentData->numNDterminations, 1);

  printf("NDterminationEnsembleOffset:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationEnsembleOffset, currentData->numEnsembles, 1);

  printf("NDterminationCurrents:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationCurrents, currentData->numNDterminations, 1);

  printf("NDterminationEnsembleSums:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationEnsembleSums, currentData->numEnsembles, 1);
}


void printIntArray(intArray* a, int n, int m)
{
  if(a->onDevice)
  {
    printIntArrayFromDevice(NULL, a, m, n);
    return;
  }

  int i, j;
  for(i = 0; i < m; i++)
  {
    for(j = 0; j < n; j++)
    {
      printf("%d ", a->array[i * n + j]);
    }
    printf("\n");
  }

  printf("\n");
}

void printFloatArray(floatArray* a, int n, int m)
{
  if(a->onDevice)
  {
    printFloatArrayFromDevice(NULL, a, m, n);
    return;
  }

  int i, j;

  for(i = 0; i < m; i++)
  {
    for(j = 0; j < n; j++)
    {
      printf("%f ", a->array[i * n + j]);
    }
    printf("\n");
  }

  printf("\n");
}

void moveToDeviceIntArray(intArray* a)
{
  if(a->onDevice)
    return;

  int* result;
  cudaError_t err;

  int size = a->size;

  err = cudaMalloc((void**)&result, size * sizeof(int));
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  err = cudaMemcpy(result, a->array, size * sizeof(int), cudaMemcpyHostToDevice);
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  free(a->array);
  a->array = result;
  a->onDevice = 1;
}
  
void moveToDeviceFloatArray(floatArray* a)
{
  if(a->onDevice)
    return;

  float* result;
  cudaError_t err;

  int size = a->size;

  err = cudaMalloc((void**)&result, size * sizeof(float));
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  err = cudaMemcpy(result, a->array, size * sizeof(float), cudaMemcpyHostToDevice);
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  free(a->array);
  a->array = result;
  a->onDevice = 1;
}
  
  
void moveToHostFloatArray(floatArray* a)
{
  if(!a->onDevice)
    return;

  float* result;
  int size = a->size;
  result = (float*)malloc(size * sizeof(float));
  cudaMemcpy(result, a->array, size * sizeof(float), cudaMemcpyDeviceToHost);
  cudaFree(a->array);
  a->array = result;
  a->onDevice = 0;
}

void moveToHostIntArray(intArray* a)
{
  if(!a->onDevice)
    return;

  int* result;
  int size = a->size;
  result = (int*)malloc(size * sizeof(int));
  cudaMemcpy(result, a->array, size * sizeof(int), cudaMemcpyDeviceToHost);
  cudaFree(a->array);
  a->array = result;
  a->onDevice = 0;
}

#ifdef __cplusplus
}
#endif

