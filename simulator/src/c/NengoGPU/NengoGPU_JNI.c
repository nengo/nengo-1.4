#ifdef __cplusplus
extern "C"{
#endif

#include <stdlib.h>
#include <jni.h>
#include <pthread.h>
#include <string.h>
#include <sys/timeb.h>

#include "GPUThread_JNI.h"
#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPUData.h"


// returns not the sort array but the indices of the values in the sorted order. So return[0] is the index of 
// the largest element in values, return[1] is the index of the second largest, etc.
int* sort(int* values, int length)
{
  int* newOrder = (int*) malloc( length * sizeof(int));
  int* scratch = (int*) malloc( length * sizeof(int));
  memset(scratch, '\0', length * sizeof(int));

  int i, j;

  for(i = 0; i < length; i++)
  {
    j = i;
    while(j > 0 && values[i] > scratch[j - 1])
    {
      scratch[j] = scratch[j-1];
      newOrder[j] = newOrder[j-1];
      j--;
    }

    scratch[j] = values[i];
    newOrder[j] = i;
  }

  free(scratch);

  return newOrder;
}

// this has to assign each projection to a GPU. If we are using multiple GPUs, its likely that some of the projections
// will have to cross GPUs, though we will try to minimize this. Those projections will be handled on the Java side for now.
// For now, none of this is actually true, we'll just use one GPU so we can get the rest of the code running
void distributeEnsemblesToDevices(int numDevices, int numEnsembles, int numProjections, projection* projections, int* deviceForEnsemble)
{
  if(!(numDevices == 1))
  {
    //error
    printf("just use one device for now!\n");
  }

  int i;
  for(i = 0; i < numProjections; i++)
  {
    projections[i].device = 0;
  }

  for(i = 0; i < numEnsembles; i++)
  {
    deviceForEnsemble[i] = 0;
  }
}

// since terminations do not typically take up as much room as encoders and decoders, we are not at pains to minimize the
// space they take up
void storeTerminationData(JNIEnv* env, jobjectArray transforms_JAVA, jobjectArray tau_JAVA, jobjectArray isDecodedTermination_JAVA, NengoGPUData* currentData, int* deviceForEnsemble)
{
  int i, j, k, l;

  jintArray tempIntArray_JAVA;
  jobjectArray transformsForCurrentEnsemble_JAVA;
  jobjectArray currentTransform_JAVA;
  jfloatArray currentTransformRow_JAVA;
  jfloatArray tauForCurrentEnsemble_JAVA;
  float* currentTransformRow = (float*)malloc(currentData->maxDecodedTerminationDimension * sizeof(float));

  int dimensionOfCurrentEnsemble = 0, dimensionOfCurrentTermination = 0, numTerminationsForCurrentEnsemble;

  int* isDecodedTermination  = (int*)malloc(currentData->numTerminations * sizeof(int));
  
  int ensembleIndex;
  int NDterminationIndex = 0;
  int terminationIndex = 0;
  int transformRowIndex = 0;
  int dimensionIndex = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);

    intArraySetElement(currentData->NDterminationEnsembleOffset, ensembleIndex, NDterminationIndex);

    transformsForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, transforms_JAVA, ensembleIndex);
    numTerminationsForCurrentEnsemble = (int) (*env)->GetArrayLength(env, transformsForCurrentEnsemble_JAVA);

    tauForCurrentEnsemble_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, tau_JAVA, ensembleIndex);

    if(numTerminationsForCurrentEnsemble + terminationIndex <= currentData->numTerminations)
    {
      (*env)->GetFloatArrayRegion(env, tauForCurrentEnsemble_JAVA, 0, numTerminationsForCurrentEnsemble, currentData->terminationTau->array + terminationIndex);
    }
    else
    {
      printf("error: accessing array out of bounds: terminationTau\n");
      exit(EXIT_FAILURE);
    }

    // get the array that says whether an ensemble is decoded for the current ensemble
    tempIntArray_JAVA = (jintArray)(*env)->GetObjectArrayElement(env, isDecodedTermination_JAVA, ensembleIndex);
    (*env)->GetIntArrayRegion(env, tempIntArray_JAVA, 0, numTerminationsForCurrentEnsemble, isDecodedTermination);


    // loop through the terminations for the current ensemble and store the relevant data
    dimensionOfCurrentEnsemble = 0;
    for(j = 0; j < numTerminationsForCurrentEnsemble; j++)
    {
      if(isDecodedTermination[j])
      {
        // for decoded terminations
        currentTransform_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, transformsForCurrentEnsemble_JAVA, j);
        dimensionOfCurrentEnsemble = (int) (*env)->GetArrayLength(env, currentTransform_JAVA);

        for(k = 0; k < dimensionOfCurrentEnsemble; k++)
        {
          currentTransformRow_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, currentTransform_JAVA, k);
          dimensionOfCurrentTermination = (*env)->GetArrayLength(env, currentTransformRow_JAVA);

          (*env)->GetFloatArrayRegion(env, currentTransformRow_JAVA, 0, dimensionOfCurrentTermination, currentTransformRow);

          for(l = 0; l < dimensionOfCurrentTermination; l++)
          {
            floatArraySetElement(currentData->terminationTransforms, l * currentData->totalNumTransformRows + transformRowIndex, currentTransformRow[l]); 
          }

          intArraySetElement(currentData->terminationOutputIndexor, transformRowIndex, j * currentData->totalEnsembleDimension + dimensionIndex + k);
          intArraySetElement(currentData->transformRowToInputIndexor, transformRowIndex, terminationIndex);
          
          transformRowIndex++;
        }

        intArraySetElement(currentData->inputDimensions, terminationIndex, dimensionOfCurrentTermination);
      }
      else
      {
        // for non decoded terminations
        currentTransform_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, transformsForCurrentEnsemble_JAVA, j);
        currentTransformRow_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, currentTransform_JAVA, 0);

        (*env)->GetFloatArrayRegion(env, currentTransformRow_JAVA, 0, 1, currentTransformRow);

        floatArraySetElement(currentData->NDterminationWeights, NDterminationIndex, currentTransformRow[0]);
        intArraySetElement(currentData->NDterminationInputIndexor, NDterminationIndex, terminationIndex);
        intArraySetElement(currentData->inputDimensions, terminationIndex, 1);

        NDterminationIndex++;
      }
      
      terminationIndex++;
    }


    intArraySetElement(currentData->ensembleOffsetInDimensions, i, dimensionIndex);
    intArraySetElement(currentData->ensembleDimension, i, dimensionOfCurrentEnsemble);
    intArraySetElement(currentData->ensembleNumTerminations, i, numTerminationsForCurrentEnsemble);

    dimensionIndex += dimensionOfCurrentEnsemble;
  }

  free(isDecodedTermination);
  free(currentTransformRow);
}

void storeNeuronData(JNIEnv *env, jobjectArray neuronData_JAVA, NengoGPUData* currentData, int* deviceForEnsemble)
{
  int i, j, currentNumNeurons, neuronDataLength;

  jfloatArray neuronDataForCurrentEnsemble_JAVA;
  float* neuronDataForCurrentEnsemble;
  
  int ensembleIndex = 0, neuronIndex = 0;

  for(i = 0; i < totalNumEnsembles; i++)
  {
    if(deviceForEnsemble[i] == currentData->device)
    {
      neuronDataForCurrentEnsemble_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, neuronData_JAVA, i);
      neuronDataLength = (*env)->GetArrayLength(env, neuronDataForCurrentEnsemble_JAVA);
      neuronDataForCurrentEnsemble = (float*) malloc( neuronDataLength * sizeof(float));
      (*env)->GetFloatArrayRegion(env, neuronDataForCurrentEnsemble_JAVA, 0, neuronDataLength, neuronDataForCurrentEnsemble);

      currentNumNeurons = neuronDataForCurrentEnsemble[0];
      intArraySetElement(currentData->ensembleNumNeurons, ensembleIndex, currentNumNeurons);
      floatArraySetElement(currentData->ensembleTauRC, ensembleIndex, neuronDataForCurrentEnsemble[1]);
      floatArraySetElement(currentData->ensembleTauRef, ensembleIndex, neuronDataForCurrentEnsemble[2]);

      /*
      if(currentData->numNonDecodedTerminations > 0)
      {
        currentData->tauPSC[ensembleIndex] = neuronData[3];
      }
      */

      intArraySetElement(currentData->ensembleOffsetInNeurons, ensembleIndex, neuronIndex);

      for(j = 0; j < currentNumNeurons; j++)
      {
        floatArraySetElement(currentData->neuronBias, neuronIndex, neuronDataForCurrentEnsemble[5+j]);
        floatArraySetElement(currentData->neuronScale, neuronIndex, neuronDataForCurrentEnsemble[5 + currentNumNeurons + j]);
        intArraySetElement(currentData->neuronToEnsembleIndexor, neuronIndex, ensembleIndex);

        neuronIndex++;
      }


      ensembleIndex++;

      free(neuronDataForCurrentEnsemble);
    }
  }
}

void storeEncoders(JNIEnv *env, jobjectArray encoders_JAVA, NengoGPUData* currentData, int* deviceForEnsemble)
{
  int i, j, k;
  int ensembleIndex = 0;
  int numNeuronsForCurrentEnsemble;
  int dimensionOfCurrentEnsemble;

  jobjectArray encoderForCurrentEnsemble_JAVA;
  jfloatArray currentEncoderRow_JAVA;

  // get encoders for this device out of encoders java array
  float* temp_encoders = (float*) malloc(currentData->totalEncoderSize * sizeof(float));
  int* temp_encoder_offset = (int*)malloc(currentData->numEnsembles * sizeof(int));

  int offset = 0;
  // totalNumEnsembles is a global variable denoting the number of ensembles in the entire run, not just those allocated to this device
  for(i = 0; i < totalNumEnsembles; i++)
  {
    if(deviceForEnsemble[i] == currentData->device)
    {
      temp_encoder_offset[ensembleIndex] = offset; 

      encoderForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, encoders_JAVA, i);
      numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons, ensembleIndex);
      dimensionOfCurrentEnsemble = intArrayGetElement(currentData->ensembleDimension, ensembleIndex);

      for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
      {
        currentEncoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, encoderForCurrentEnsemble_JAVA, j);
        (*env)->GetFloatArrayRegion(env, currentEncoderRow_JAVA, 0, dimensionOfCurrentEnsemble, temp_encoders + offset); 
        offset += dimensionOfCurrentEnsemble;
      }

      ensembleIndex++;
    }
  }

  
  // So now we have all the encoders in a C array, temp_encoders, but not in the order we want them in


  // order ensembles by decreasing dimension
  
  char* name = "ensembleOrderInEncoders";
  currentData->ensembleOrderInEncoders = newIntArray(currentData->numEnsembles, name);
  int* temp = sort(currentData->ensembleDimension->array, currentData->numEnsembles);
  intArraySetData(currentData->ensembleOrderInEncoders, temp, currentData->numEnsembles);
  free(temp);


  // we have to make an array of offsets into the final encoder array so that we know how long each row is
  // what this is essentially doing is recording, for each x between 1 and the max number of dimensions,
  // how many ensembles have at least x dimensions
  int numNeurons;
  for(i = 1; i <= currentData->maxDimension; i++)
  {
    numNeurons = 0;

    for(j = 0; j < currentData->numEnsembles; j++)
    {
      if(intArrayGetElement(currentData->ensembleDimension,j) >= i)
      {
        numNeurons += intArrayGetElement(currentData->ensembleNumNeurons, j);
      }
    }

    intArraySetElement(currentData->encoderStride, i - 1,  numNeurons);
  }


  // now we have the order we want the encoders to appear in the array, and the row lengths in the new array, 
  // so we just have to transform temp_encoders using this ordering
  int rowOffset;
  int neuronOffset = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInEncoders,i);

    offset = temp_encoder_offset[ensembleIndex];

    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons, ensembleIndex);
    dimensionOfCurrentEnsemble = intArrayGetElement(currentData->ensembleDimension, ensembleIndex);

    rowOffset = 0;
    for(k = 0; k < dimensionOfCurrentEnsemble; k++)
    {
      for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
      {
        floatArraySetElement(currentData->encoders, rowOffset + neuronOffset + j,  temp_encoders[offset + j * dimensionOfCurrentEnsemble + k]);
      }

      rowOffset += intArrayGetElement(currentData->encoderStride,k);
    }

    intArraySetElement(currentData->ensembleOffsetInEncoders, ensembleIndex, neuronOffset);
    neuronOffset += numNeuronsForCurrentEnsemble;
  }

  free(temp_encoder_offset);
  free(temp_encoders);
 

  // figure out how many CUDA blocks to use for encoding
  j = 0;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons, i);

    while(numNeuronsForCurrentEnsemble > 0)
    {
      numNeuronsForCurrentEnsemble -= currentData->blockSizeForEncode;
      j++;
    }
  }

  currentData->numBlocksForEncode = j;

  name = "blockToEnsembleMapForEncode";
  currentData->blockToEnsembleMapForEncode = newIntArray(j,name);

  // then populate currentData->blockToEnsembleMapForEncode
  j = 0;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons, i);
    intArraySetElement(currentData->ensembleIndexOfFirstBlockForEncode, i, j);

    while(numNeuronsForCurrentEnsemble > 0)
    {
      intArraySetElement(currentData->blockToEnsembleMapForEncode, j, i);
      numNeuronsForCurrentEnsemble -= currentData->blockSizeForEncode;
      j++;
    }
  }
}

void storeDecoders(JNIEnv* env, jobjectArray decoders_JAVA, NengoGPUData* currentData, int* deviceForEnsemble)
{
  int i, j, k;

  jobjectArray decodersForCurrentEnsemble_JAVA;
  jobjectArray currentDecoder_JAVA;
  jfloatArray currentDecoderRow_JAVA;
  int decoderIndex = 0, dimensionOfCurrentDecoder, numDecodersForCurrentEnsemble, ensembleIndexInJavaArray;

  // setup ensembleOutputSize and ensembleOffsetInOutput
  int outputSize, outputOffset = 0;
  decoderIndex = 0;
  for(j = 0; j < currentData->numEnsembles; j++)
  {
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray,j);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (int) (*env)->GetArrayLength(env, decodersForCurrentEnsemble_JAVA);

    outputSize = 0;

    intArraySetElement(currentData->ensembleOffsetInOutput, j, outputOffset);

    for(k = 0; k < numDecodersForCurrentEnsemble; k++)
    {
      currentDecoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decodersForCurrentEnsemble_JAVA, k);
      currentDecoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentDecoder_JAVA, 0);

      dimensionOfCurrentDecoder = (int) (*env)->GetArrayLength(env, currentDecoderRow_JAVA);
      intArraySetElement(currentData->originDimension,decoderIndex, dimensionOfCurrentDecoder);

      outputSize += dimensionOfCurrentDecoder;

      intArraySetElement(currentData->outputOffset,decoderIndex, outputOffset);
      outputOffset += dimensionOfCurrentDecoder;
      decoderIndex++;
    }

    intArraySetElement(currentData->ensembleOutputSize,j, outputSize);
  }

  // setup decoderStride
  int numOutputs;
  for(i = 1; i <= currentData->maxNumNeurons; i++)
  {
    numOutputs = 0;

    for(j = 0; j < currentData->numEnsembles; j++)
    {
      if(intArrayGetElement(currentData->ensembleNumNeurons,j) >= i)
      {
        numOutputs += intArrayGetElement(currentData->ensembleOutputSize, j);
      }
    }

    intArraySetElement(currentData->decoderStride, i - 1, numOutputs);
  }
  
  // sort the ensembles in order of decreasing number of neurons
  char* name = "ensembleOrderInDecoders";
  currentData->ensembleOrderInDecoders = newIntArray(currentData->numEnsembles, name); 
  int* temp = sort(currentData->ensembleNumNeurons->array, currentData->numEnsembles);
  intArraySetData(currentData->ensembleOrderInDecoders, temp, currentData->numEnsembles);
  free(temp);



  int ensembleIndex = 0;
  int numNeuronsForCurrentEnsemble;
  int offsetInEnsemble = 0;
  int offset = 0;
  int rowOffset = 0;


  // setup decoders using the ordering found above (decreasing number of neurons)
  // also setup ensembleNumOrigins
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInDecoders,i);
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, ensembleIndex);

    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons,ensembleIndex);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (int) (*env)->GetArrayLength(env, decodersForCurrentEnsemble_JAVA);

    intArraySetElement(currentData->ensembleNumOrigins,ensembleIndex, numDecodersForCurrentEnsemble);

    rowOffset = 0;

    for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
    {
      offsetInEnsemble = offset;
      for(k = 0; k < numDecodersForCurrentEnsemble; k++)
      {
        currentDecoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decodersForCurrentEnsemble_JAVA, k);
        currentDecoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentDecoder_JAVA, j);
        
        dimensionOfCurrentDecoder = (*env)->GetArrayLength(env, currentDecoderRow_JAVA);

        if(rowOffset + offsetInEnsemble + dimensionOfCurrentDecoder <= currentData->totalDecoderSize)
        {
          (*env)->GetFloatArrayRegion(env, currentDecoderRow_JAVA, 0, dimensionOfCurrentDecoder, currentData->decoders->array + rowOffset + offsetInEnsemble);
        }
        else
        {
          printf("error: accessing array out of bounds: decoders\n");
          exit(EXIT_FAILURE);
        }

        offsetInEnsemble += dimensionOfCurrentDecoder;
      }

      rowOffset += intArrayGetElement(currentData->decoderStride,j);
    }
    
    intArraySetElement(currentData->ensembleOffsetInDecoders, ensembleIndex, offset);
    offset = offsetInEnsemble;
  }
  

  // setup block related data
  // figure out how many blocks to use for decoding
  j = 0;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    outputSize = intArrayGetElement(currentData->ensembleOutputSize, i);

    while(outputSize > 0)
    {
      outputSize -= currentData->blockSizeForDecode;
      j++;
    }
  }

  currentData->numBlocksForDecode = j;

  name = "blockToEnsembleMapForDecode";
  currentData->blockToEnsembleMapForDecode = newIntArray(j,name);

  // then populate currentData->blockToEnsembleMapForEncode
  j = 0;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    outputSize = intArrayGetElement(currentData->ensembleOutputSize,i);

    intArraySetElement(currentData->ensembleIndexOfFirstBlockForDecode, i, j);

    while(outputSize > 0)
    {
      intArraySetElement(currentData->blockToEnsembleMapForDecode, j, i);
      outputSize -= currentData->blockSizeForDecode;
      j++;
    }
  }
}


void assignEnsembleToDevice(int* GPUData, NengoGPUData* currentData)
{
  currentData->numEnsembles++;
  currentData->numNeurons += GPUData[NENGO_GPU_DATA_NUM_NEURONS];

  currentData->numDecodedTerminations += GPUData[NENGO_GPU_DATA_NUM_DECODED_TERMINATIONS];
  currentData->totalInputSize += GPUData[NENGO_GPU_DATA_TOTAL_INPUT_SIZE];
  currentData->totalNumTransformRows += GPUData[NENGO_GPU_DATA_NUM_DECODED_TERMINATIONS] * GPUData[NENGO_GPU_DATA_DIMENSION];
  
  if(GPUData[NENGO_GPU_DATA_NUM_DECODED_TERMINATIONS] > currentData->maxNumDecodedTerminations)
  {
    currentData->maxNumDecodedTerminations = GPUData[NENGO_GPU_DATA_NUM_DECODED_TERMINATIONS];
  }

  if(GPUData[NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION] > currentData->maxDecodedTerminationDimension)
  {
    currentData->maxDecodedTerminationDimension = GPUData[NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION];
  }
  
  currentData->totalEncoderSize += GPUData[NENGO_GPU_DATA_NUM_NEURONS] * GPUData[NENGO_GPU_DATA_DIMENSION];

  currentData->numOrigins += GPUData[NENGO_GPU_DATA_NUM_ORIGINS];
  currentData->totalOutputSize += GPUData[NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE];
  currentData->totalDecoderSize += GPUData[NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE] * GPUData[NENGO_GPU_DATA_NUM_NEURONS];
  
  if(GPUData[NENGO_GPU_DATA_MAX_DECODER_DIMENSION] > currentData->maxOriginDimension)
  {
    currentData->maxOriginDimension = GPUData[NENGO_GPU_DATA_MAX_DECODER_DIMENSION];
  }

  currentData->totalEnsembleDimension += GPUData[NENGO_GPU_DATA_DIMENSION];

  if(GPUData[NENGO_GPU_DATA_DIMENSION] > currentData->maxDimension)
  {
    currentData->maxDimension = GPUData[NENGO_GPU_DATA_DIMENSION];
  }

  if(GPUData[NENGO_GPU_DATA_NUM_NEURONS] > currentData->maxNumNeurons)
  {
    currentData->maxNumNeurons = GPUData[NENGO_GPU_DATA_NUM_NEURONS];
  }
  
  currentData->numNDterminations += GPUData[NENGO_GPU_DATA_NUM_NON_DECODED_TERMINATIONS];
}

void setupInput(int numProjections, projection* projections, NengoGPUData* currentData)
{
  int i, j;
  int currentDimension, GPUInputSize = 0, ensembleIndex = 0, indexInEnsemble = 0;
  int currentNumObjects;

  int* flattenedProjections = (int*) malloc(2 * numProjections * sizeof(int));

  // flatten the termination side of the projection array
  for(i = 0; i < currentData->numTerminations; i++)
  {
    if(indexInEnsemble == 0)
    {
      currentNumObjects = intArrayGetElement(currentData->ensembleNumTerminations, ensembleIndex);
    }

    currentDimension = intArrayGetElement(currentData->inputDimensions, i);
    j = 0;
    while(j < numProjections && (projections[j].destinationEnsemble != ensembleIndex || projections[j].destinationTermination != indexInEnsemble))
    {
      j++;
    }

    if(j < numProjections)
    {
      GPUInputSize += currentDimension;
      flattenedProjections[2 * j] = i;
    }
    
    indexInEnsemble++;

    if(indexInEnsemble == currentNumObjects)
    {
      ensembleIndex++;
      indexInEnsemble = 0;
    }
  }

  // find the size of input that stays on the gpu
  currentData->GPUInputSize = GPUInputSize;
  currentData->CPUInputSize = currentData->totalInputSize - GPUInputSize;

  char* name = "GPUTerminationToOriginMap";
  currentData->GPUTerminationToOriginMap = newIntArray(currentData->GPUInputSize, name);

  // create the inputHost array. only as large as the number of CPU inputs
  // since GPU inputs are supposed to stay on the device
  name = "inputHost";
  currentData->inputHost = newFloatArray(currentData->CPUInputSize, name);

  
  // flatten the origin side of the projection array. This is slightly different than flattening the termination side
  // because any one termination can only be involved in one projection whereas any origin can be involved in any number of projections.
  // Effectively this means we have to scan the entire projection array for each origin, we can't stop as soon as we find one.
  ensembleIndex = 0;
  indexInEnsemble = 0;

  for(i = 0; i < currentData->numOrigins; i++)
  {
    if(indexInEnsemble == 0)
    {
      currentNumObjects = intArrayGetElement(currentData->ensembleNumOrigins, ensembleIndex);
    }

    for(j = 0; j < numProjections; j++)
    {
      if(projections[j].sourceEnsemble == ensembleIndex && projections[j].sourceOrigin == indexInEnsemble)
      {
        flattenedProjections[2 * j + 1] = i;
      }
    }

    indexInEnsemble++;

    if(indexInEnsemble == currentNumObjects)
    {
      ensembleIndex++;
      indexInEnsemble = 0;
    }
  }

  int CPUInputIndex = 0, GPUInputIndex = 0;
  // setup the array of offsets into input. Note that all GPU inputs come before
  // any CPU inputs in the input array.
  for( i = 0; i < currentData->numTerminations; i++)
  {
    currentDimension = intArrayGetElement(currentData->inputDimensions,i);

    j = 0;
    while(j < numProjections && flattenedProjections[2 * j] != i)
    {
      j++;
    }

    if(j == numProjections)
    {
      intArraySetElement(currentData->inputOffset, i, GPUInputSize + CPUInputIndex);
      CPUInputIndex += currentDimension;
    }
    else
    {
      intArraySetElement(currentData->inputOffset, i, GPUInputIndex);
      GPUInputIndex += currentDimension;
    }
  }

  // Use the flattened projections to create a map from the input to the output following the projections
  // This way we can launch a kernel for each projection on the GPU, have it look up where it gets its output from
  // fetch it from the output array and put it in the input array
  int terminationIndexOnDevice, originIndexOnDevice, inputOffset, outputOffset;
  for(i = 0; i < numProjections; i++)
  {
    if(projections[i].device == currentData->device)
    {
      terminationIndexOnDevice = flattenedProjections[i * 2];
      originIndexOnDevice = flattenedProjections[i * 2 + 1];

      inputOffset = intArrayGetElement(currentData->inputOffset,terminationIndexOnDevice);
      outputOffset = intArrayGetElement(currentData->outputOffset,originIndexOnDevice);
      currentDimension = intArrayGetElement(currentData->inputDimensions,terminationIndexOnDevice);

      for(j = 0; j < currentDimension; j++)
      {
        intArraySetElement(currentData->GPUTerminationToOriginMap,inputOffset + j, outputOffset + j);
      }
    }
  }
  
  free(flattenedProjections);
}

// This function takes as arguments all the information required by the ensembles to run that won't change from step to step: decoders, encoders, transformations.
// This is called only once, at the beginning of a run (specifically, when the GPUNodeThreadPool is created). 
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_GPUThread_nativeSetupRun
  (JNIEnv *env, jclass class, jobjectArray terminationTransforms_JAVA, jobjectArray isDecodedTermination_JAVA, jobjectArray terminationTau_JAVA, jobjectArray encoders_JAVA, jobjectArray decoders_JAVA, jobjectArray neuronData_JAVA, jobjectArray projections_JAVA, jobjectArray GPUData_JAVA, jintArray projectionOnGPU_JAVA, jfloat maxTimeStep)
{
  printf("NengoGPU: SETUP\n");
  int i, j, k;

  //numDevices = getGPUDeviceCount();
  numDevices = 1;
  
  nengoDataArray = (NengoGPUData**) malloc(sizeof(NengoGPUData*) * numDevices);

  totalNumEnsembles = (int) (*env)->GetArrayLength(env, neuronData_JAVA);

  deviceForEnsemble = (int*) malloc(totalNumEnsembles * sizeof(int));


  jintArray tempIntArray_JAVA;

  NengoGPUData* currentData;

  // Create the NengoGPUData structs, one per device.
  for(i = 0; i < numDevices; i++)
  {
    nengoDataArray[i] = getNewNengoGPUData();
    nengoDataArray[i]->maxTimeStep = (float)maxTimeStep;
  }

  // sort out the projections
  int numProjections = (int) (*env)->GetArrayLength(env, projections_JAVA);
  int* currentProjection = (int*)malloc(PROJECTION_DATA_SIZE * sizeof(int));
  projection* projections = (projection*) malloc( numProjections * sizeof(projection));
  for(i=0; i < numProjections; i++)
  {
    tempIntArray_JAVA = (jintArray)(*env)->GetObjectArrayElement(env, projections_JAVA, i);
    (*env)->GetIntArrayRegion(env, tempIntArray_JAVA, 0, PROJECTION_DATA_SIZE, currentProjection);
    storeProjection(projections + i, currentProjection);
  }
  free(currentProjection);

  // The distributes the ensembles to the devices. Right now it tries to maximize the number of GPU projections
  // so the amount of data being passed to the GPU every step is minimized.
  distributeEnsemblesToDevices(numDevices, totalNumEnsembles, numProjections, projections, deviceForEnsemble);

  int* projectionOnGPU = (int*)malloc(numProjections * sizeof(int));

  for(i = 0; i < numProjections; i++)
  {
    projectionOnGPU[i] = (projections[i].device < 0) ? 0 : 1;
  }
  
  // We have to inform the JAVA code which projections will run on the GPUs.
  // This JAVA array, projectionsOnGPU_JAVA, will get return to the JAVA side when the native call returns
  (*env)->SetIntArrayRegion(env, projectionOnGPU_JAVA, 0, numProjections, projectionOnGPU); 
  free(projectionOnGPU);


  // We have to set the number fields in the NengoGPUData structs so that it knows how big to make its internal arrays
  jintArray GPUDataRow_JAVA;
  int* GPUData = (int*)malloc(NENGO_GPU_DATA_NUM * sizeof(int));
  for(i = 0; i < totalNumEnsembles; i++)
  {
    GPUDataRow_JAVA = (jintArray) (*env)->GetObjectArrayElement(env, GPUData_JAVA, i);
    (*env)->GetIntArrayRegion(env, GPUDataRow_JAVA, 0, NENGO_GPU_DATA_NUM, GPUData);

    currentData = nengoDataArray[deviceForEnsemble[i]];

    assignEnsembleToDevice(GPUData, currentData); 

  }
  free(GPUData);



  // Now we start to load the data into the NengoGPUData struct for each device.
  // Because of the CUDA architecture, we have to do some weird things to get a good speedup. 
  // These arrays that store the transforms, decoders, are setup in a non-intuitive way so 
  // that memory accesses can be parallelized in CUDA kernels. For more information, see the NengoGPU user manual.
  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];
    
    currentData->device = i;

    currentData->numTerminations = currentData->numDecodedTerminations + currentData->numNDterminations;
    currentData->totalTransformSize = currentData->maxDecodedTerminationDimension * currentData->totalNumTransformRows;

    initializeNengoGPUData(currentData);
    
    j = 0;
    for(k = 0; k < currentData->numEnsembles; k++)
    {
      while(deviceForEnsemble[j] != currentData->device)
      {
        j++;
      }

      intArraySetElement(currentData->ensembleIndexInJavaArray, k, j);
      j++;
    }
   
    storeTerminationData(env, terminationTransforms_JAVA, terminationTau_JAVA, isDecodedTermination_JAVA, currentData, deviceForEnsemble);

    storeNeuronData(env, neuronData_JAVA, currentData, deviceForEnsemble);

    storeEncoders(env, encoders_JAVA, currentData, deviceForEnsemble);

    storeDecoders(env, decoders_JAVA, currentData, deviceForEnsemble);

    setupInput(numProjections, projections, currentData);
  }

  free(projections);

  run_start();
}


// Called once per step from the Java code. Puts the representedInputValues in the proper form for processing, then tells each GPU thread
// to take a step. Once they've finished the step, this function puts the representedOutputValues and spikes in the appropriate Java
// arrays so that they can be read on the Java side when this call returns.
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_GPUThread_nativeStep
  (JNIEnv *env, jclass class, jobjectArray input, jobjectArray output, jobjectArray spikes, jfloat startTime_JAVA, jfloat endTime_JAVA)
{
  startTime = (float) startTime_JAVA;
  endTime = (float) endTime_JAVA;

  jobjectArray currentInputs_JAVA;
  jfloatArray currentInput_JAVA;
  
  NengoGPUData* currentData;

  int i, j, k, l;
  int ensembleIndex, inputIndex, numInputs, inputDimension;
 
  for( i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];

    inputIndex = 0;

    for( j = 0; j < currentData->numEnsembles; j++)
    {
      ensembleIndex = intArrayGetElement(currentData->ensembleIndexInJavaArray,j);
        
      currentInputs_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, input, ensembleIndex);
      
      if(currentInputs_JAVA != NULL)
      {
        numInputs = (*env)->GetArrayLength(env, currentInputs_JAVA);

        for(k = 0; k < numInputs; k++)
        {
          currentInput_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, currentInputs_JAVA, k);
          if(currentInput_JAVA != NULL)
          {
            inputDimension = (*env)->GetArrayLength(env, currentInput_JAVA);

            if(inputIndex  + inputDimension <= currentData->CPUInputSize)
            {
              (*env)->GetFloatArrayRegion(env, currentInput_JAVA, 0, inputDimension, currentData->inputHost->array + inputIndex);
            }
            else
            {
              printf("error: accessing inputHost out of bounds. size: %d, index: %d\n", currentData->CPUInputSize, inputIndex + inputDimension);
              exit(EXIT_FAILURE);
            }
            

            inputIndex += inputDimension;
          }
        }
      }
    }
  }

  // tell the runner threads to run and then wait for them to finish. The last of them to finish running will wake this thread up. 
  pthread_mutex_lock(mutex);
  myCVsignal = 1;
  pthread_cond_broadcast(cv_GPUThreads);
  pthread_cond_wait(cv_JNI, mutex);
  pthread_mutex_unlock(mutex);

  jobjectArray currentOutputs_JAVA;
  jfloatArray currentOutput_JAVA;
  jfloatArray currentSpikes_JAVA;

  int numOutputs, outputDimension, numNeurons, outputIndex, spikeIndex;

  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];

    outputIndex = 0;
    spikeIndex = 0;

    for(k = 0; k < currentData->numEnsembles; k++)
    {
      ensembleIndex = intArrayGetElement(currentData->ensembleIndexInJavaArray,k);

      currentOutputs_JAVA = (jobjectArray)(*env)->GetObjectArrayElement(env, output, k);
      numOutputs = (*env)->GetArrayLength(env, currentOutputs_JAVA); 

      for(l = 0; l < numOutputs; l++)
      {
        currentOutput_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentOutputs_JAVA, l);
        outputDimension = (*env)->GetArrayLength(env, currentOutput_JAVA);

        (*env)->SetFloatArrayRegion(env, currentOutput_JAVA, 0, outputDimension, currentData->outputHost->array + outputIndex);
        outputIndex += outputDimension;
      }
      
      currentSpikes_JAVA = (*env)->GetObjectArrayElement(env, spikes, k);
      numNeurons = (*env)->GetArrayLength(env, currentSpikes_JAVA);
      
      (*env)->SetFloatArrayRegion(env, currentSpikes_JAVA, 0, numNeurons, currentData->spikesHost->array + spikeIndex);
      spikeIndex += numNeurons;
    }
  }
}

JNIEXPORT void JNICALL Java_ca_nengo_util_impl_GPUThread_nativeKill
(JNIEnv *env, jclass class)
{
  printf("NengoGPU: KILL\n");
  run_kill();
}

#ifdef __cplusplus
}
#endif
