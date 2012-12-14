#define _CRT_NONSTDC_NO_WARNINGS
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
intArray* newIntArray(jint size, const char* name)
{
  intArray* retVal = (intArray*)malloc(sizeof(intArray));
  if(!retVal)
  {
    printf("Failed to allocate memory for intArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  retVal->array = (jint*)malloc(size * sizeof(jint));
  if(!retVal->array)
  {
    printf("Failed to allocate memory for intArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }
  
  retVal->size = size;
  retVal->name = strdup(name);
  retVal->onDevice = 0;

  return retVal;
}

void freeIntArray(intArray* a)
{
  if(!a)
    return;

  if(a->array)
    a->onDevice ? cudaFree(a->array) : free(a->array);
  
  if(a->name)
    free(a->name);

  free(a);
}

intArray* newIntArrayOnDevice(jint size, const char* name)
{
  intArray* retVal = (intArray*)malloc(sizeof(intArray));

  retVal->array = allocateCudaIntArray(size);
  retVal->size = size;
  retVal->name = strdup(name);
  retVal->onDevice = 1;

  return retVal;
}

floatArray* newFloatArray(jint size, const char* name)
{
  floatArray* retVal = (floatArray*)malloc(sizeof(floatArray));
  if(!retVal)
  {
    printf("Failed to allocate memory for floatArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  retVal->array = (float*)malloc(size * sizeof(float));
  if(!retVal->array)
  {
    printf("Failed to allocate memory for floatArray. name: %s, attemped size: %d\n", name, size);
    exit(EXIT_FAILURE);
  }

  retVal->size = size;
  retVal->name = strdup(name);
  retVal->onDevice = 0;

  return retVal;
}

void freeFloatArray(floatArray* a)
{
  //printf("freeing %s, size: %d, onDevice: %d, address: %d\n", a->name, a->size, a->onDevice, (jint)a->array);

  if(!a)
    return;

  if(a->array)
    a->onDevice ? cudaFree(a->array) : free(a->array);
  
  if(a->name)
    free(a->name);

  free(a);
}

floatArray* newFloatArrayOnDevice(jint size, const char* name)
{
  floatArray* retVal = (floatArray*)malloc(sizeof(floatArray));

  retVal->array = allocateCudaFloatArray(size);
  retVal->size = size;
  retVal->name = strdup(name);
  retVal->onDevice = 1;

  return retVal;
}
  

///////////////////////////////////////////////////////
// intArray and floatArray safe getters and setters
///////////////////////////////////////////////////////

void checkBounds(char* verb, char* name, jint size, jint index)
{
  if(index >= size || index < 0)
  {
    printf("%s safe array out of bounds, name: %s, size: %d, index:%d\n", verb, name, size, index);
    exit(EXIT_FAILURE);
  }
}

void checkLocation(char* verb, char* name, jint onDevice, jint size, jint index)
{
  if(onDevice)
  {
    printf("%s safe array that is not on the host, name: %s, size: %d, index:%d\n", verb, name, size, index);
    exit(EXIT_FAILURE);
  }
}

void intArraySetElement(intArray* a, jint index, jint value)
{
  checkBounds("Setting", a->name, a->size, index);
  checkLocation("Setting", a->name, a->onDevice, a->size, index);

  a->array[index] = value;
}

void floatArraySetElement(floatArray* a, jint index, float value)
{
  checkBounds("Setting", a->name, a->size, index);
  checkLocation("Setting", a->name, a->onDevice, a->size, index);

  a->array[index] = value;
}

jint intArrayGetElement(intArray* a, jint index)
{
  checkBounds("Getting", a->name, a->size, index);
  checkLocation("Getting", a->name, a->onDevice, a->size, index);

  return a->array[index];
}

float floatArrayGetElement(floatArray* a, jint index)
{
  checkBounds("Getting", a->name, a->size, index);
  checkLocation("Getting", a->name, a->onDevice, a->size, index);

  return a->array[index];
}

void intArraySetData(intArray* a, jint* data, jint dataSize)
{
  if(dataSize > a->size)
  {
    printf("Warning: calling intArraySetData with a data set that is too large; truncating data. name: %s, size: %d, dataSize: %d", a->name, a->size, dataSize);
  }
  
  memcpy(a->array, data, dataSize * sizeof(jint));
}

void floatArraySetData(floatArray* a, float* data, jint dataSize)
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
void storeProjection(projection* proj, jint* data)
{
  proj->sourceNode = data[0];
  proj->sourceOrigin = data[1];
  proj->destinationNode = data[2];
  proj->destinationTermination = data[3];
  proj->size = data[4];
  proj->sourceDevice = 0;
  proj->destDevice = 0;
}

void printProjection(projection* proj)
{
  printf("%d %d %d %d %d %d %d\n", proj->sourceNode, proj->sourceOrigin, proj->destinationNode, proj->destinationTermination, proj->size, proj->sourceDevice, proj->destDevice);
}

///////////////////////////////////////////////////////
// int_list functions 
///////////////////////////////////////////////////////
int_list* cons_int_list(int_list* list, jint item)
{
  int_list* retVal = (int_list*)malloc(sizeof(int_list));
  retVal->first = item;
  retVal->next = list;
  return retVal;
}

void free_int_list(int_list* list)
{
  if(list)
  {
    int_list* temp = list->next;
    free(list);
    free_int_list(temp);
  }
}

///////////////////////////////////////////////////////
// int_queue functions
///////////////////////////////////////////////////////
int_queue* new_int_queue()
{
  int_queue* retVal = (int_queue*) malloc(sizeof(int_queue));

  retVal->size = 0;
  retVal->head = NULL;
  retVal->tail = NULL;

  return retVal;
}

jint pop_int_queue(int_queue* queue)
{
  if(queue )
  {
    jint val;
    switch(queue->size)
    {
      case 0:
        fprintf(stderr, "Error \"int_queue\": accessing empty queue\n");
        exit(EXIT_FAILURE);
        break;
      case 1:
        val = queue->head->first;
        free_int_list(queue->head);
        queue->head = NULL;
        queue->tail = NULL;
        queue->size--;
        return val;
        break;
      default:
        val = queue->head->first;
        int_list* temp = queue->head;
        queue->head = temp->next;
        temp->next = NULL;
        free_int_list(temp);
        queue->size--;
        return val;
    }
  }
  else
  {
    fprintf(stderr, "Error \"int_queue\": accessing null queue\n");
    exit(EXIT_FAILURE);
  }
}

void add_int_queue(int_queue* queue, jint val)
{
  if(queue)
  {
    queue->tail->next = cons_int_list(NULL, val);
    queue->tail = queue->tail->next;
    queue->size++;
  }
  else
  {
    fprintf(stderr, "Error \"int_queue\": accessing null queue\n");
    exit(EXIT_FAILURE);
  }
}

void free_int_queue(int_queue* queue)
{
  if(queue)
  {
    free_int_list(queue->head);
    free(queue);
  }
}

///////////////////////////////////////////////////////
// NengoGPUData functions
///////////////////////////////////////////////////////

// return a fresh NengoGPUData object with all numerical values zeroed out and all pointers set to null
NengoGPUData* getNewNengoGPUData()
{
  NengoGPUData* retVal = (NengoGPUData*)malloc(sizeof(NengoGPUData));

  retVal-> onDevice = 0;
  retVal-> device = 0;
  retVal-> maxTimeStep = 0;
   
  retVal-> numNetworkArrays = 0;

  retVal-> numNeurons = 0;
  retVal-> numInputs = 0;
  retVal-> numEnsembles = 0;
  retVal-> numTerminations = 0;
  retVal-> numDecodedTerminations = 0;
  retVal-> numNDterminations = 0;
  retVal-> numNetworkArrayOrigins = 0;
  retVal-> numOrigins = 0;

  retVal->totalInputSize = 0;
  retVal->totalTransformSize = 0;
  retVal->totalNumTransformRows = 0;
  retVal->totalEnsembleDimension = 0;
  retVal->totalEncoderSize = 0;
  retVal->totalDecoderSize = 0;
  retVal->totalOutputSize = 0;

  retVal->maxDecodedTerminationDimension = 0;
  retVal->maxNumDecodedTerminations = 0;
  retVal->maxDimension = 0;
  retVal->maxNumNeurons = 0;
  retVal->maxOriginDimension = 0;
  retVal->maxEnsembleNDTransformSize = 0;
  
  retVal->numNDterminations = 0;

  retVal->numSpikesToSendBack = 0;

  retVal->GPUOutputSize = 0;
  retVal->JavaOutputSize = 0;
  retVal->CPUOutputSize = 0;

  retVal->input = NULL;
  retVal->terminationOffsetInInput = NULL;
  retVal->networkArrayIndexInJavaArray = NULL;

  retVal->terminationTransforms = NULL;
  retVal->transformRowToInputIndexor = NULL;
  retVal->terminationTau = NULL;
  retVal->inputDimension = NULL;
  retVal->terminationOutput = NULL;
  retVal->terminationOutputIndexor = NULL;

  retVal->ensembleSums = NULL;
  retVal->encoders = NULL;
  retVal->decoders = NULL;
  retVal->encodeResult = NULL;

  retVal->neuronVoltage = NULL;
  retVal->neuronReftime = NULL;
  retVal->neuronBias = NULL;
  retVal->neuronScale = NULL;
  retVal->ensembleTauRC = NULL;
  retVal->ensembleTauRef = NULL;
  retVal->neuronToEnsembleIndexor = NULL;

  retVal->isSpikingEnsemble = NULL;

  retVal->ensembleNumTerminations = NULL;
  retVal->ensembleDimension = NULL;
  retVal->ensembleNumNeurons = NULL;
  retVal->ensembleNumOrigins = NULL;

  retVal->ensembleOffsetInDimensions = NULL;
  retVal->ensembleOffsetInNeurons = NULL;
  retVal->encoderRowToEnsembleIndexor = NULL;
  retVal->encoderRowToNeuronIndexor = NULL;
  retVal->decoderRowToEnsembleIndexor = NULL;

  retVal->encoderStride = NULL;
  retVal->decoderStride = NULL;

  retVal->ensembleOrderInEncoders = NULL;
  retVal->ensembleOrderInDecoders = NULL;

  retVal->spikes = NULL;

  retVal->ensembleOutput = NULL;
  retVal->output = NULL;
  retVal->outputHost = NULL;
  retVal->decoderRowToOutputIndexor = NULL;
  
  retVal->ensembleOriginDimension = NULL;

  retVal->ensembleOriginOffsetInOutput = NULL;
  retVal->networkArrayOriginOffsetInOutput = NULL; 
  retVal->networkArrayOriginDimension = NULL; 
  retVal->networkArrayNumOrigins = NULL;

  retVal->spikeMap = NULL;
  retVal->GPUTerminationToOriginMap = NULL;
  retVal->ensembleOutputToNetworkArrayOutputMap = NULL;
  retVal->ensembleIndexInJavaArray = NULL;
  
  retVal->NDterminationInputIndexor = NULL;
  retVal->NDterminationCurrents = NULL;
  retVal->NDterminationWeights = NULL;
  retVal->NDterminationEnsembleOffset = NULL;
  retVal->NDterminationEnsembleSums = NULL;

  retVal->sharedData_outputIndex = NULL;
  retVal->sharedData_sharedIndex = NULL;


  return retVal;
}


// Should only be called once the NengoGPUData's numerical values have been set. This function allocates memory of the approprate size for each pointer.
// Memory is allocated on the host. The idea is to call this before we load the data in from the JNI structures, so we have somewhere to put that data. Later, we will move most of the data to the device.
void initializeNengoGPUData(NengoGPUData* retVal)
{
  if(retVal == NULL)
  {
     return;
  }

  char* name; 

  name = "networkArrayIndexInJavaArray";
  retVal->networkArrayIndexInJavaArray = newIntArray(retVal->numNetworkArrays, name);

  name = "terminationOffsetInInput";
  retVal->terminationOffsetInInput = newIntArray(retVal->numInputs, name);

  name = "terminationTranforms";
  retVal->terminationTransforms = newFloatArray(retVal->totalTransformSize, name);
  memset(retVal->terminationTransforms->array, '\0', retVal->terminationTransforms->size * sizeof(float));

  name = "transformRowToInputIndexor";
  retVal->transformRowToInputIndexor = newIntArray(retVal->totalNumTransformRows, name);
  name = "terminationTau";
  retVal->terminationTau = newFloatArray(retVal->numInputs, name);
  name = "inputDimension";
  retVal->inputDimension = newIntArray(retVal->numInputs, name);
  name = "terminationOutputIndexor";
  retVal->terminationOutputIndexor = newIntArray(retVal->totalNumTransformRows, name);
 
  name = "encoders";
  retVal->encoders = newFloatArray(retVal->totalEncoderSize, name);
  name = "decoders";
  retVal->decoders = newFloatArray(retVal->totalDecoderSize, name);

  name = "neuronBias";
  retVal->neuronBias = newFloatArray(retVal->numNeurons, name);
  name = "neuronScale";
  retVal->neuronScale = newFloatArray(retVal->numNeurons, name);
  name = "ensembleTauRC";
  retVal->ensembleTauRC = newFloatArray(retVal->numEnsembles, name);
  name = "ensembleTauRef";
  retVal->ensembleTauRef = newFloatArray(retVal->numEnsembles, name);

  name = "isSpikingEnsemble";
  retVal->isSpikingEnsemble = newIntArray(retVal->numEnsembles, name);

  name = "ensembleNumTerminations";
  retVal->ensembleNumTerminations = newIntArray(retVal->numEnsembles, name);
  name = "ensembleDimension";
  retVal->ensembleDimension = newIntArray(retVal->numEnsembles, name);
  name = "ensembleNumNeurons";
  retVal->ensembleNumNeurons = newIntArray(retVal->numEnsembles, name);
  name = "ensembleNumOrigins";
  retVal->ensembleNumOrigins = newIntArray(retVal->numEnsembles, name);


  name = "ensembleOffsetInDimensions";
  retVal->ensembleOffsetInDimensions = newIntArray(retVal->numEnsembles, name);
  name = "ensembleOffsetInNeurons";
  retVal->ensembleOffsetInNeurons = newIntArray(retVal->numEnsembles, name);

  name = "encoderRowToEnsembleIndexor";
  retVal->encoderRowToEnsembleIndexor = newIntArray(retVal->numNeurons, name);
  name = "encoderRowToNeuronIndexor";
  retVal->encoderRowToNeuronIndexor = newIntArray(retVal->numNeurons, name);
  name = "decoderRowToEnsembleIndexor";
  retVal->decoderRowToEnsembleIndexor = newIntArray(retVal->totalOutputSize, name);
  name = "decoderRowToOutputIndexor";
  retVal->decoderRowToOutputIndexor = newIntArray(retVal->totalOutputSize, name);
  name = "neuronToEnsembleIndexor";
  retVal->neuronToEnsembleIndexor = newIntArray(retVal->numNeurons, name);

  name = "encoderStride";
  retVal->encoderStride = newIntArray(retVal->maxDimension, name);
  name = "decoderStride";
  retVal->decoderStride = newIntArray(retVal->maxNumNeurons, name);

  name = "ensembleOrderInEncoders";
  retVal->ensembleOrderInEncoders = newIntArray(retVal->numEnsembles, name);

  name = "ensembleOrderInDecoders";
  retVal->ensembleOrderInDecoders = newIntArray(retVal->numEnsembles, name); 

  name = "ensembleOriginDimension";
  retVal->ensembleOriginDimension = newIntArray(retVal->numOrigins, name);

  name = "ensembleOriginOffsetInOutput";
  retVal->ensembleOriginOffsetInOutput = newIntArray(retVal->numOrigins, name);

  name = "networkArrayOriginOffsetInOutput";
  retVal->networkArrayOriginOffsetInOutput = newIntArray(retVal->numNetworkArrayOrigins, name);

  name = "networkArrayOriginDimension";
  retVal->networkArrayOriginDimension = newIntArray(retVal->numNetworkArrayOrigins, name);

  name = "networkArrayNumOrigins";
  retVal->networkArrayNumOrigins = newIntArray(retVal->numNetworkArrays, name);

  name = "ensembleIndexInJavaArray";
  retVal->ensembleIndexInJavaArray = newIntArray(retVal->numEnsembles, name);

  name = "ensembleOutputToNetworkArrayOutputMap";
  retVal->ensembleOutputToNetworkArrayOutputMap = newIntArray(retVal->totalOutputSize, name);

  name = "NDterminationInputIndexor";
  retVal->NDterminationInputIndexor = newIntArray(retVal->numNDterminations, name);
  name = "NDterminationWeights";
  retVal->NDterminationWeights = newFloatArray(retVal->totalNonDecodedTransformSize, name);

  name = "NDterminationEnsembleOffset";
  retVal->NDterminationEnsembleOffset = newIntArray(retVal->numEnsembles, name);

}


// Called at the end of initializeNengoGPUData to determine whether any of the mallocs failed.
void checkNengoGPUData(NengoGPUData* currentData)
{
  jint status = 0;

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

    moveToDeviceIntArray(currentData->terminationOffsetInInput);
    moveToDeviceFloatArray(currentData->terminationTransforms);
    moveToDeviceFloatArray(currentData->terminationTau);
    moveToDeviceIntArray(currentData->inputDimension);
    moveToDeviceIntArray(currentData->transformRowToInputIndexor);
    moveToDeviceIntArray(currentData->terminationOutputIndexor);

    moveToDeviceFloatArray(currentData->encoders);
    moveToDeviceFloatArray(currentData->decoders);

    moveToDeviceFloatArray(currentData->neuronBias);
    moveToDeviceFloatArray(currentData->neuronScale);
    moveToDeviceFloatArray(currentData->ensembleTauRC);
    moveToDeviceFloatArray(currentData->ensembleTauRef);
    moveToDeviceIntArray(currentData->neuronToEnsembleIndexor);

    moveToDeviceIntArray(currentData->isSpikingEnsemble);
    
    moveToDeviceIntArray(currentData->ensembleDimension);
    moveToDeviceIntArray(currentData->ensembleNumNeurons);

    moveToDeviceIntArray(currentData->ensembleOffsetInDimensions);
    moveToDeviceIntArray(currentData->ensembleOffsetInNeurons);

    moveToDeviceIntArray(currentData->encoderRowToEnsembleIndexor);
    moveToDeviceIntArray(currentData->encoderRowToNeuronIndexor);

    moveToDeviceIntArray(currentData->decoderRowToEnsembleIndexor);
    moveToDeviceIntArray(currentData->decoderRowToOutputIndexor);

    moveToDeviceIntArray(currentData->encoderStride);
    moveToDeviceIntArray(currentData->decoderStride);

    moveToDeviceIntArray(currentData->spikeMap);

    moveToDeviceIntArray(currentData->GPUTerminationToOriginMap);

    moveToDeviceIntArray(currentData->ensembleOutputToNetworkArrayOutputMap);

    moveToDeviceIntArray(currentData->NDterminationInputIndexor);
    moveToDeviceFloatArray(currentData->NDterminationWeights);
    moveToDeviceIntArray(currentData->NDterminationEnsembleOffset);

    currentData->onDevice = 1;
  }
}

// Free the NengoGPUData. Makes certain assumptions about where each array is (device or host).
void freeNengoGPUData(NengoGPUData* currentData)
{
  freeIntArray(currentData->networkArrayIndexInJavaArray);

  freeFloatArray(currentData->input);
  freeIntArray(currentData->terminationOffsetInInput);

  freeFloatArray(currentData->terminationTransforms);
  freeIntArray(currentData->transformRowToInputIndexor);
  freeFloatArray(currentData->terminationTau);
  freeIntArray(currentData->inputDimension);
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
  freeIntArray(currentData->neuronToEnsembleIndexor);

  freeIntArray(currentData->isSpikingEnsemble);

  freeIntArray(currentData->ensembleNumTerminations);
  freeIntArray(currentData->ensembleDimension);
  freeIntArray(currentData->ensembleNumNeurons);
  freeIntArray(currentData->ensembleNumOrigins);

  freeIntArray(currentData->ensembleOffsetInDimensions);
  freeIntArray(currentData->ensembleOffsetInNeurons);
  freeIntArray(currentData->encoderRowToEnsembleIndexor);
  freeIntArray(currentData->encoderRowToNeuronIndexor);
  freeIntArray(currentData->decoderRowToEnsembleIndexor);
  freeIntArray(currentData->decoderRowToOutputIndexor);

  freeIntArray(currentData->encoderStride);
  freeIntArray(currentData->decoderStride);

  freeIntArray(currentData->ensembleOrderInEncoders);
  freeIntArray(currentData->ensembleOrderInDecoders);

  freeFloatArray(currentData->spikes);

  freeFloatArray(currentData->ensembleOutput);
  freeFloatArray(currentData->output);
  freeFloatArray(currentData->outputHost);

  freeIntArray(currentData->ensembleOriginDimension);
  freeIntArray(currentData->ensembleIndexInJavaArray);
  freeIntArray(currentData->ensembleOriginOffsetInOutput);
  freeIntArray(currentData->networkArrayOriginOffsetInOutput);
  freeIntArray(currentData->networkArrayOriginDimension);
  freeIntArray(currentData->networkArrayNumOrigins);
  freeIntArray(currentData->spikeMap);
  freeIntArray(currentData->GPUTerminationToOriginMap);
  freeIntArray(currentData->ensembleOutputToNetworkArrayOutputMap);
  freeIntArray(currentData->sharedData_outputIndex);
  freeIntArray(currentData->sharedData_sharedIndex);

  freeIntArray(currentData->NDterminationInputIndexor);
  freeFloatArray(currentData->NDterminationCurrents);
  freeFloatArray(currentData->NDterminationWeights);
  freeIntArray(currentData->NDterminationEnsembleOffset);
  freeFloatArray(currentData->NDterminationEnsembleSums);
  
  free(currentData);
};

// print the NengoGPUData. Should only be called once the data has been set.
void printNengoGPUData(NengoGPUData* currentData, jint printArrays)
{
  
  printf("printing NengoGPUData:\n");
  printf("onDevice; %d\n", currentData->onDevice);
  printf("initialized; %d\n", currentData->initialized);
  printf("device; %d\n", currentData->device);
  printf("maxTimeStep; %f\n", currentData->maxTimeStep);
   
  printf("numNeurons; %d\n", currentData->numNeurons);
  printf("numEnsembles; %d", currentData->numEnsembles);
  printf("numNetworkArrays; %d\n", currentData->numNetworkArrays);
  printf("numInputs; %d\n", currentData->numInputs);
  printf("numTerminations; %d\n", currentData->numTerminations);
  printf("numNDterminations; %d\n", currentData->numNDterminations);
  printf("numDecodedTerminations; %d\n", currentData->numDecodedTerminations);
  printf("numNetworkArrayOrigins; %d\n", currentData->numNetworkArrayOrigins);
  printf("numOrigins; %d\n", currentData->numOrigins);

  printf("totalInputSize; %d\n", currentData->totalInputSize);
  printf("GPUInputSize; %d\n", currentData->GPUInputSize);
  printf("CPUInputSize; %d\n", currentData->CPUInputSize);
  printf("JavaInputSize; %d\n", currentData->JavaInputSize);
  printf("offsetInSharedInput; %d\n", currentData->offsetInSharedInput);
  printf("numSpikesToSendBack; %d\n", currentData->numSpikesToSendBack);

  printf("totalTransformSize; %d\n", currentData->totalTransformSize);
  printf("totalNumTransformRows; %d\n", currentData->totalNumTransformRows);
  printf("totalNonDecodedTransformSize; %d\n", currentData->totalNonDecodedTransformSize);
  printf("totalEnsembleDimension; %d\n", currentData->totalEnsembleDimension);
  printf("totalEncoderSize; %d\n", currentData->totalEncoderSize);
  printf("totalDecoderSize; %d\n", currentData->totalDecoderSize);
  printf("totalOutputSize; %d\n", currentData->totalOutputSize);

  printf("maxDecodedTerminationDimension; %d\n", currentData->maxDecodedTerminationDimension);
  printf("maxNumDecodedTerminations; %d\n", currentData->maxNumDecodedTerminations);
  printf("maxDimension; %d\n", currentData->maxDimension);
  printf("maxNumNeurons; %d\n", currentData->maxNumNeurons);
  printf("maxOriginDimension; %d\n", currentData->maxOriginDimension);
  printf("maxEnsembleNDTransformSize; %d\n", currentData->maxEnsembleNDTransformSize);

  printf("GPUOutputSize; %d\n", currentData->GPUOutputSize);
  printf("JavaOutputSize; %d\n", currentData->JavaOutputSize);
  printf("interGPUOutputSize; %d\n", currentData->interGPUOutputSize);
  printf("CPUOutputSize; %d\n", currentData->CPUOutputSize);
 
  if(printArrays)
  {
    printIntArray(currentData->networkArrayIndexInJavaArray, currentData->numNetworkArrays, 1);
    printIntArray(currentData->ensembleIndexInJavaArray, currentData->numEnsembles, 1);

    printFloatArray(currentData->input, currentData->totalInputSize, 1);
    printIntArray(currentData->terminationOffsetInInput, currentData->numTerminations, 1);

    printFloatArray(currentData->terminationTransforms, currentData->totalNumTransformRows, currentData->maxDecodedTerminationDimension);
    printIntArray(currentData->transformRowToInputIndexor, currentData->totalNumTransformRows, 1);
    printFloatArray(currentData->terminationTau, currentData->numTerminations, 1);
    printIntArray(currentData->inputDimension, currentData->numInputs, 1);
    printFloatArray(currentData->terminationOutput, currentData->totalNumTransformRows, 1);
    printIntArray(currentData->terminationOutputIndexor, currentData->totalNumTransformRows, 1);
    printIntArray(currentData->ensembleNumTerminations, currentData->numEnsembles, 1);

    printFloatArray(currentData->encoders, currentData->totalEncoderSize, 1);
  //  printIntArray(currentData->ensembleOrderInEncoders);
    printFloatArray(currentData->encodeResult, currentData->numNeurons, 1);
    printFloatArray(currentData->ensembleSums, currentData->totalEnsembleDimension, 1);

    printFloatArray(currentData->decoders, currentData->totalDecoderSize, 1);
  //  printIntArray(currentData->ensembleOrderInDecoders, 1);

    // data for calculating spikes
    printFloatArray(currentData->neuronVoltage, currentData->numNeurons, 1);
    printFloatArray(currentData->neuronReftime, currentData->numNeurons, 1);
    printFloatArray(currentData->neuronBias, currentData->numNeurons, 1);
    printFloatArray(currentData->neuronScale, currentData->numNeurons, 1);
    printFloatArray(currentData->ensembleTauRC, currentData->numEnsembles, 1);
    printFloatArray(currentData->ensembleTauRef, currentData->numEnsembles, 1);
    printIntArray(currentData->neuronToEnsembleIndexor, currentData->numNeurons, 1);

    printIntArray(currentData->isSpikingEnsemble, currentData->numNeurons, 1);

    // supplementary arrays for doing encoding
    printIntArray(currentData->ensembleDimension, currentData->numEnsembles, 1);
    printIntArray(currentData->ensembleOffsetInDimensions, currentData->numEnsembles, 1);
    printIntArray(currentData->encoderRowToEnsembleIndexor, currentData->numNeurons, 1); 
    printIntArray(currentData->encoderStride, currentData->maxDimension, 1);
    printIntArray(currentData->encoderRowToNeuronIndexor, currentData->numNeurons, 1);

    // supplementary arrays for doing decoding
    printIntArray(currentData->ensembleNumNeurons, currentData->numEnsembles, 1);
    printIntArray(currentData->ensembleOffsetInNeurons, currentData->numEnsembles, 1); 
    printIntArray(currentData->decoderRowToEnsembleIndexor, currentData->totalOutputSize, 1); 
    printIntArray(currentData->decoderStride, currentData->maxDimension, 1);
    printIntArray(currentData->decoderRowToOutputIndexor, currentData->totalOutputSize, 1);

    printFloatArray(currentData->spikes, currentData->numNeurons, 1);

    printFloatArray(currentData->ensembleOutput, currentData->totalOutputSize, 1);
    printFloatArray(currentData->output, currentData->totalOutputSize + currentData->numSpikesToSendBack, 1);
    printFloatArray(currentData->outputHost, currentData->CPUOutputSize + currentData->numSpikesToSendBack, 1);

    printIntArray(currentData->GPUTerminationToOriginMap , currentData->GPUInputSize, 1);
    printIntArray(currentData->spikeMap, currentData->numSpikesToSendBack, 1);

    // non decoded termination data
    printIntArray(currentData->NDterminationInputIndexor, currentData->numNDterminations, 1);
    printFloatArray(currentData->NDterminationCurrents, currentData->numNDterminations, 1);
    printFloatArray(currentData->NDterminationWeights, currentData->totalNonDecodedTransformSize, 1);
    printIntArray(currentData->NDterminationEnsembleOffset, currentData->numEnsembles, 1);
    printFloatArray(currentData->NDterminationEnsembleSums, currentData->numEnsembles, 1);

    // for organizing the output in terms of ensembles
    printIntArray(currentData->ensembleOriginOffsetInOutput, currentData->numOrigins, 1);
    printIntArray(currentData->ensembleNumOrigins , currentData->numEnsembles, 1);
    printIntArray(currentData->ensembleOriginDimension, currentData->numOrigins, 1);

    printIntArray(currentData->ensembleOutputToNetworkArrayOutputMap, currentData->totalOutputSize, 1);

    printIntArray(currentData->networkArrayOriginOffsetInOutput, currentData->numNetworkArrayOrigins, 1); 
    printIntArray(currentData->networkArrayOriginDimension, currentData->numNetworkArrayOrigins, 1); 
    printIntArray(currentData->networkArrayNumOrigins, currentData->numNetworkArrays, 1);

    printIntArray(currentData->sharedData_outputIndex, currentData->interGPUOutputSize, 1);
    printIntArray(currentData->sharedData_sharedIndex, currentData->interGPUOutputSize, 1);
  }

//  jint bytesOnGPU = sizeof(float) * (8 * currentData->numEnsembles + currentData->totalInputSize + currentData->totalTransformSize + 2 * currentData->totalNumTransformRows + 2 * currentData->numTerminations + currentData->maxNumDecodedTerminations * currentData->totalEnsembleDimension + currentData->maxDimension * currentData->numNeurons + currentData->totalEnsembleDimension + currentData->numNeurons * 6 + currentData->maxNumNeurons * currentData->totalOutputSize + 2 * currentData->totalOutputSize);
 // printf("bytes on GPU: %d\n", bytesOnGPU);
 
}

void printDynamicNengoGPUData(NengoGPUData* currentData)
{
  
 /* 
  printf("input:\n");
  printFloatArrayFromDevice(NULL, currentData->input, currentData->totalInputSize, 1, 0);

//  printf("input to output map:\n");
 // printIntArrayFromDevice(NULL, currentData->GPUTerminationToOriginMap, currentData->GPUInputSize, 1, 0);

  printf("terminationOutput:\n");
  printFloatArrayFromDevice(NULL, currentData->terminationOutput, currentData->totalEnsembleDimension * currentData->maxNumDecodedTerminations, 1, 0);

  printf("ensembleSums:\n");
  printFloatArrayFromDevice(NULL, currentData->ensembleSums, currentData->totalEnsembleDimension, 1, 0);

  printf("encodeResult:\n");
  printFloatArrayFromDevice(NULL, currentData->encodeResult, currentData->numNeurons, 1, 0);
*/
  printf("neuronVoltage:\n");
  printFloatArrayFromDevice(NULL, currentData->neuronVoltage, currentData->numNeurons, 1, 1);
  /*
  //printFloatArrayFromDevice(NULL, currentData->neuronVoltage, currentData->maxNumNeurons, currentData->numEnsembles, 0);

  //printf("neuronReftime:\n");
  //printFloatArrayFromDevice(NULL, currentData->neuronReftime, currentData->numNeurons, 1, 0);


  //printf("spikes:\n");
  //printFloatArrayFromDevice(NULL, currentData->spikes, currentData->numNeurons, 1, 0);
  //printFloatArrayFromDevice(NULL, currentData->spikes, currentData->maxNumNeurons, currentData->numEnsembles, 0);


 

  printf("output:\n");
  printFloatArrayFromDevice(NULL, currentData->output, currentData->totalOutputSize, 1, 0);

  printf("outputHost:\n");
  printFloatArray(currentData->outputHost, currentData->totalOutputSize, 1);

  printf("NDterminationInputIndexor:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationInputIndexor, currentData->numNDterminations, 1, 0);

  printf("NDterminationWeights:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationWeights, currentData->numNDterminations, 1, 0);

  printf("NDterminationEnsembleOffset:\n");
  printIntArrayFromDevice(NULL, currentData->NDterminationEnsembleOffset, currentData->numEnsembles, 1, 0);

  printf("NDterminationCurrents:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationCurrents, currentData->numNDterminations, 1, 0);
  

  printf("NDterminationEnsembleSums:\n");
  printFloatArrayFromDevice(NULL, currentData->NDterminationEnsembleSums, currentData->numEnsembles, 1, 0);
*/  
}


void printIntArray(intArray* a, jint n, jint m)
{
  if(!a)
    return;

  if(!a->array)
    return;

  if(a->onDevice)
  {
    printIntArrayFromDevice(NULL, a, m, n, 0);
    return;
  }

  printf("%s:\n", a->name);

  jint i, j;
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

void printFloatArray(floatArray* a, jint n, jint m)
{
  if(!a)
    return;

  if(!a->array)
    return;

  if(a->onDevice)
  {
    printFloatArrayFromDevice(NULL, a, m, n, 0);
    return;
  }

  printf("%s:\n", a->name);

  jint i, j;

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

  jint* result;
  cudaError_t err;

  jint size = a->size;

  err = cudaMalloc((void**)&result, size * sizeof(jint));
  if(err)
  {
    printf("%s", cudaGetErrorString(err));
    exit(EXIT_FAILURE);
  }

  err = cudaMemcpy(result, a->array, size * sizeof(jint), cudaMemcpyHostToDevice);
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

  jint size = a->size;

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
  jint size = a->size;
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

  jint* result;
  jint size = a->size;
  result = (jint*)malloc(size * sizeof(jint));
  cudaMemcpy(result, a->array, size * sizeof(jint), cudaMemcpyDeviceToHost);
  cudaFree(a->array);
  a->array = result;
  a->onDevice = 0;
}

