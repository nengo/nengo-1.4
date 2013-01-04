#include <stdlib.h>
#include <jni.h>
#include <string.h>
#include <limits.h>
#include <math.h>
#include <sys/timeb.h>
#include <assert.h>
#ifndef _MSC_VER
#include <pthread.h>
#endif

#include "NEFGPUInterface_JNI.h"
#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPUData.h"

// returns not the sorted array but the indices of the values in the sorted order. So newOrder[0] is the index of 
// the largest element in values, newOrder[1] is the index of the second largest, etc.
// Order allows the user to choose between ascending and descending. 1 
jint* sort(jint* values, jint length, jint order)
{
  jint* newOrder = (jint*) malloc( length * sizeof(jint));
  jint* scratch = (jint*) malloc( length * sizeof(jint));
  memset(scratch, '\0', length * sizeof(jint));

  jint i, j;

  for(i = 0; i < length; i++)
  {
    j = i;
    while(j > 0 && ((order && values[i] > scratch[j - 1]) || (!order && values[i] < scratch[j-1])))
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

void adjustProjections(jint numProjections, projection* projections, jint* networkArrayJavaIndexToDeviceIndex)
{
  jint i;
  projection* p;
  for(i = 0; i < numProjections; i++)
  {
    p = (projections + i);

    p->sourceNode = networkArrayJavaIndexToDeviceIndex[p->sourceNode];
    p->destinationNode = networkArrayJavaIndexToDeviceIndex[p->destinationNode];
  }
}

// since terminations do not typically take up as much room as encoders and decoders, we are not at pains to minimize the
// space they take up
void storeTerminationData(JNIEnv* env, jobjectArray transforms_JAVA, jobjectArray tau_JAVA, jobjectArray isDecodedTermination_JAVA, NengoGPUData* currentData, jint* networkArrayData)
{
  jint h, i, j, k, l;

  jintArray tempIntArray_JAVA;
  jobjectArray transformsForCurrentEnsemble_JAVA;
  jobjectArray currentTransform_JAVA;
  jfloatArray currentTransformRow_JAVA;
  jfloatArray tauForCurrentEnsemble_JAVA;
  float* currentTransformRow = (float*)malloc(currentData->maxDecodedTerminationDimension * sizeof(float));

  jint dimensionOfCurrentEnsemble = 0, dimensionOfCurrentTermination = 0, numTerminationsForCurrentEnsemble = 0;

  jint* isDecodedTermination  = (jint*)malloc(currentData->numTerminations * sizeof(jint));
  
  jint ensembleIndexInJavaArray = 0, NDterminationIndex = 0, transformRowIndex = 0, dimensionIndex = 0;
  jint NDterminationIndexInEnsemble = 0;
  jint startEnsembleIndex, endEnsembleIndex, networkArrayIndexInJavaArray, networkArrayOffsetInTerminations = 0;
  jint networkArrayTerminationIndex = 0;
  jint terminationOffset = 0;

  for(h = 0; h < currentData->numNetworkArrays; h++)
  {
    networkArrayIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray, h); 

    startEnsembleIndex = networkArrayData[networkArrayIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_FIRST_INDEX];
    endEnsembleIndex = networkArrayData[networkArrayIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_END_INDEX];

    for(i = startEnsembleIndex; i < endEnsembleIndex; i++)
    {
      ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);

      intArraySetElement(currentData->NDterminationEnsembleOffset, i, NDterminationIndex);

      transformsForCurrentEnsemble_JAVA = (jobjectArray) env->GetObjectArrayElement(transforms_JAVA, ensembleIndexInJavaArray);
      numTerminationsForCurrentEnsemble = (jint) env->GetArrayLength(transformsForCurrentEnsemble_JAVA);

      tauForCurrentEnsemble_JAVA = (jfloatArray) env->GetObjectArrayElement(tau_JAVA, ensembleIndexInJavaArray);

      if(i == startEnsembleIndex)
      {
        if(numTerminationsForCurrentEnsemble + networkArrayTerminationIndex <= currentData->numInputs)
        {
          env->GetFloatArrayRegion(tauForCurrentEnsemble_JAVA, 0, numTerminationsForCurrentEnsemble, currentData->terminationTau->array + networkArrayTerminationIndex);
        }
        else
        {
          printf("error: accessing array out of bounds: terminationTau\n");
          exit(EXIT_FAILURE);
        }
      }

      // get the array that says whether a termination is decoded for the current ensemble
      tempIntArray_JAVA = (jintArray)env->GetObjectArrayElement(isDecodedTermination_JAVA, ensembleIndexInJavaArray);
      env->GetIntArrayRegion(tempIntArray_JAVA, 0, numTerminationsForCurrentEnsemble, isDecodedTermination);
      env->DeleteLocalRef(tempIntArray_JAVA);

      terminationOffset = 0;

      NDterminationIndexInEnsemble = i;

      // loop through the terminations for the current ensemble and store the relevant data
      dimensionOfCurrentEnsemble = 0;
      for(j = 0; j < numTerminationsForCurrentEnsemble; j++)
      {
        if(isDecodedTermination[j])
        {
          // for decoded terminations
          currentTransform_JAVA = (jobjectArray) env->GetObjectArrayElement(transformsForCurrentEnsemble_JAVA, j);
          dimensionOfCurrentEnsemble = (jint) env->GetArrayLength(currentTransform_JAVA);

          for(k = 0; k < dimensionOfCurrentEnsemble; k++)
          {
            currentTransformRow_JAVA = (jfloatArray)env->GetObjectArrayElement(currentTransform_JAVA, k);
            dimensionOfCurrentTermination = env->GetArrayLength(currentTransformRow_JAVA);

            env->GetFloatArrayRegion(currentTransformRow_JAVA, 0, dimensionOfCurrentTermination, currentTransformRow);

            for(l = 0; l < dimensionOfCurrentTermination; l++)
            {
              floatArraySetElement(currentData->terminationTransforms, l * currentData->totalNumTransformRows + transformRowIndex, currentTransformRow[l]); 
            }

            intArraySetElement(currentData->terminationOutputIndexor, transformRowIndex, terminationOffset + dimensionIndex + k);
            intArraySetElement(currentData->transformRowToInputIndexor, transformRowIndex, networkArrayOffsetInTerminations + j);

            transformRowIndex++;

            env->DeleteLocalRef(currentTransformRow_JAVA);
          }

          terminationOffset += currentData->totalEnsembleDimension;

          if(i == startEnsembleIndex)
          {
            intArraySetElement(currentData->inputDimension, networkArrayTerminationIndex, dimensionOfCurrentTermination);
            networkArrayTerminationIndex++;
          }

          env->DeleteLocalRef(currentTransform_JAVA);
        }
        else
        {
          // for non decoded terminations
          currentTransform_JAVA = (jobjectArray) env->GetObjectArrayElement(transformsForCurrentEnsemble_JAVA, j);
          currentTransformRow_JAVA = (jfloatArray)env->GetObjectArrayElement(currentTransform_JAVA, 0);

          dimensionOfCurrentTermination = env->GetArrayLength(currentTransformRow_JAVA);

          env->GetFloatArrayRegion(currentTransformRow_JAVA, 0, dimensionOfCurrentTermination, currentTransformRow);

          for(l = 0; l < dimensionOfCurrentTermination; l++)
          {
            floatArraySetElement(currentData->NDterminationWeights, NDterminationIndexInEnsemble, currentTransformRow[l]); 
            NDterminationIndexInEnsemble += currentData->numEnsembles;
          }

          if(i == startEnsembleIndex)
          {
            intArraySetElement(currentData->inputDimension, networkArrayTerminationIndex, dimensionOfCurrentTermination);
            networkArrayTerminationIndex++;
          }

          intArraySetElement(currentData->NDterminationInputIndexor, NDterminationIndex, networkArrayOffsetInTerminations + j);

          NDterminationIndex++;

          env->DeleteLocalRef(currentTransform_JAVA);
          env->DeleteLocalRef(currentTransformRow_JAVA);
        }
        
      }

      intArraySetElement(currentData->ensembleOffsetInDimensions, i, dimensionIndex);
      intArraySetElement(currentData->ensembleDimension, i, dimensionOfCurrentEnsemble);
      intArraySetElement(currentData->ensembleNumTerminations, i, numTerminationsForCurrentEnsemble);

      dimensionIndex += dimensionOfCurrentEnsemble;

      env->DeleteLocalRef(transformsForCurrentEnsemble_JAVA);
      env->DeleteLocalRef(tauForCurrentEnsemble_JAVA);
    }

    networkArrayOffsetInTerminations += numTerminationsForCurrentEnsemble;
  }

  free(isDecodedTermination);
  free(currentTransformRow);
}

void storeNeuronData(JNIEnv *env, jobjectArray neuronData_JAVA, jint* isSpikingEnsemble, NengoGPUData* currentData)
{
  jint i, j, currentNumNeurons, neuronDataLength;

  jfloatArray neuronDataForCurrentEnsemble_JAVA;
  float* neuronDataForCurrentEnsemble;
  
  jint ensembleJavaIndex = 0, neuronIndex = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleJavaIndex = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);
    neuronDataForCurrentEnsemble_JAVA = (jfloatArray) env->GetObjectArrayElement(neuronData_JAVA, ensembleJavaIndex);
    neuronDataLength = env->GetArrayLength(neuronDataForCurrentEnsemble_JAVA);
    neuronDataForCurrentEnsemble = (float*) malloc( neuronDataLength * sizeof(float));
    env->GetFloatArrayRegion(neuronDataForCurrentEnsemble_JAVA, 0, neuronDataLength, neuronDataForCurrentEnsemble);

    env->DeleteLocalRef(neuronDataForCurrentEnsemble_JAVA);

    currentNumNeurons = (jint)neuronDataForCurrentEnsemble[0];
    intArraySetElement(currentData->ensembleNumNeurons, i, currentNumNeurons);
    floatArraySetElement(currentData->ensembleTauRC, i, neuronDataForCurrentEnsemble[1]);
    floatArraySetElement(currentData->ensembleTauRef, i, neuronDataForCurrentEnsemble[2]);

    intArraySetElement(currentData->ensembleOffsetInNeurons, i, neuronIndex);

    for(j = 0; j < currentNumNeurons; j++)
    {
      floatArraySetElement(currentData->neuronBias, neuronIndex, neuronDataForCurrentEnsemble[5+j]);
      floatArraySetElement(currentData->neuronScale, neuronIndex, neuronDataForCurrentEnsemble[5 + currentNumNeurons + j]);
      intArraySetElement(currentData->neuronToEnsembleIndexor, neuronIndex, i);

      neuronIndex++;
    }

    intArraySetElement(currentData->isSpikingEnsemble, i, isSpikingEnsemble[ensembleJavaIndex]);

    free(neuronDataForCurrentEnsemble);
  }

}

void storeEncoders(JNIEnv *env, jobjectArray encoders_JAVA, NengoGPUData* currentData)
{
  jint i, j, k;
  jint ensembleIndexInJavaArray = 0;
  jint numNeuronsForCurrentEnsemble;
  jint dimensionOfCurrentEnsemble;

  jobjectArray encoderForCurrentEnsemble_JAVA;
  jfloatArray currentEncoderRow_JAVA;

  float* temp_encoders = (float*) malloc(currentData->totalEncoderSize * sizeof(float));
  jint* temp_encoder_offset = (jint*)malloc(currentData->numEnsembles * sizeof(jint));

  jint offset = 0;
  // totalNumEnsembles is a global variable denoting the number of ensembles in the entire run, not just those allocated to this device
  // here we get encoders for this device out of encoders java array, but they're not in the order we want
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    temp_encoder_offset[i] = offset; 

    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);
    encoderForCurrentEnsemble_JAVA = (jobjectArray) env->GetObjectArrayElement(encoders_JAVA, ensembleIndexInJavaArray);
    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons, i);
    dimensionOfCurrentEnsemble = intArrayGetElement(currentData->ensembleDimension, i);

    for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
    {
      currentEncoderRow_JAVA = (jfloatArray) env->GetObjectArrayElement(encoderForCurrentEnsemble_JAVA, j);
      env->GetFloatArrayRegion(currentEncoderRow_JAVA, 0, dimensionOfCurrentEnsemble, temp_encoders + offset); 
      offset += dimensionOfCurrentEnsemble;

      env->DeleteLocalRef(currentEncoderRow_JAVA);
    }
    
    env->DeleteLocalRef(encoderForCurrentEnsemble_JAVA);
  }

  // So now we have all the encoders in a C array, temp_encoders, but not in the order we want them in


  // order ensembles by decreasing dimension
  char* name = "ensembleOrderInEncoders";
  currentData->ensembleOrderInEncoders = newIntArray(currentData->numEnsembles, name);
  jint* temp = sort(currentData->ensembleDimension->array, currentData->numEnsembles, 1);
  intArraySetData(currentData->ensembleOrderInEncoders, temp, currentData->numEnsembles);
  free(temp);


  // we have to make an array of offsets into the final encoder array so that we know how long each row is
  // what this is essentially doing is recording, for each x between 1 and the max number of dimensions,
  // how many ensembles have at least x dimensions
  jint numNeurons;
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
  jint rowOffset;
  jint neuronOffset = 0;
  jint ensembleIndex = 0;

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

    neuronOffset += numNeuronsForCurrentEnsemble;
  }

  free(temp_encoder_offset);
  free(temp_encoders);



  // construct array encoderRowToEnsembleIndexor which maintains, for each row encoder row, which ensemble it belongs to
  jint encoderRowIndex = 0, ensembleOffsetInNeurons;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInEncoders, i);

    numNeurons = intArrayGetElement(currentData->ensembleNumNeurons, ensembleIndex);
    ensembleOffsetInNeurons = intArrayGetElement(currentData->ensembleOffsetInNeurons, ensembleIndex);

    for(j = 0; j < numNeurons; j++)
    {
      intArraySetElement(currentData->encoderRowToEnsembleIndexor, encoderRowIndex, ensembleIndex); 
      intArraySetElement(currentData->encoderRowToNeuronIndexor, encoderRowIndex, ensembleOffsetInNeurons + j); 
      encoderRowIndex++;
    }
  }

}

void storeDecoders(JNIEnv* env, jobjectArray decoders_JAVA, NengoGPUData* currentData, jint* networkArrayData)
{
  jint i, j, k;

  jobjectArray decodersForCurrentEnsemble_JAVA;
  jobjectArray currentDecoder_JAVA;
  jfloatArray currentDecoderRow_JAVA;
  jint decoderIndex = 0, dimensionOfCurrentDecoder, numDecodersForCurrentEnsemble, ensembleIndexInJavaArray;

  jint* ensembleOutputSize = (jint*)malloc(currentData->numEnsembles * sizeof(jint)); 
  jint* ensembleOffsetInOutput = (jint*)malloc(currentData->numEnsembles * sizeof(jint)); 

  // populate currentData->originOffsetInOutput and ensembleOutputSize
  jint outputSize, originOffsetInOutput = 0;

  decoderIndex = 0;
  for(j = 0; j < currentData->numEnsembles; j++)
  {
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray,j);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) env->GetObjectArrayElement(decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (jint) env->GetArrayLength(decodersForCurrentEnsemble_JAVA);

    outputSize = 0;

    ensembleOffsetInOutput[j] = originOffsetInOutput;

    for(k = 0; k < numDecodersForCurrentEnsemble; k++)
    {
      currentDecoder_JAVA = (jobjectArray) env->GetObjectArrayElement(decodersForCurrentEnsemble_JAVA, k);
      currentDecoderRow_JAVA = (jfloatArray) env->GetObjectArrayElement(currentDecoder_JAVA, 0);

      dimensionOfCurrentDecoder = (jint) env->GetArrayLength(currentDecoderRow_JAVA);
      intArraySetElement(currentData->ensembleOriginDimension, decoderIndex, dimensionOfCurrentDecoder);

      outputSize += dimensionOfCurrentDecoder;

      intArraySetElement(currentData->ensembleOriginOffsetInOutput, decoderIndex, originOffsetInOutput);

      originOffsetInOutput += dimensionOfCurrentDecoder;
      decoderIndex++;

      env->DeleteLocalRef(currentDecoder_JAVA);
      env->DeleteLocalRef(currentDecoderRow_JAVA);
    }

    ensembleOutputSize[j] = outputSize;

    env->DeleteLocalRef(decodersForCurrentEnsemble_JAVA);
  }
  
  
 // populate decoder stride
  jint numOutputs;
  for(i = 1; i <= currentData->maxNumNeurons; i++)
  {
    numOutputs = 0;

    for(j = 0; j < currentData->numEnsembles; j++)
    {
      if(intArrayGetElement(currentData->ensembleNumNeurons,j) >= i)
      {
        numOutputs += ensembleOutputSize[j]; 
      }
    }

    intArraySetElement(currentData->decoderStride, i - 1, numOutputs);
  }
  
  // sort the ensembles in order of decreasing number of neurons
  char* name = "ensembleOrderInDecoders";
  currentData->ensembleOrderInDecoders = newIntArray(currentData->numEnsembles, name); 
  jint* temp = sort(currentData->ensembleNumNeurons->array, currentData->numEnsembles, 1);
  intArraySetData(currentData->ensembleOrderInDecoders, temp, currentData->numEnsembles);
  free(temp);



  jint ensembleIndex = 0;
  jint numNeuronsForCurrentEnsemble;
  jint decoderRowIndex = 0;
  jint offset = 0;
  jint rowOffset = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInDecoders,i);
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, ensembleIndex);

    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons,ensembleIndex);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) env->GetObjectArrayElement(decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (jint) env->GetArrayLength(decodersForCurrentEnsemble_JAVA);

    intArraySetElement(currentData->ensembleNumOrigins, ensembleIndex, numDecodersForCurrentEnsemble);

    rowOffset = 0;

    for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
    {
      decoderRowIndex = offset;
      for(k = 0; k < numDecodersForCurrentEnsemble; k++)
      {
        currentDecoder_JAVA = (jobjectArray) env->GetObjectArrayElement(decodersForCurrentEnsemble_JAVA, k);
        currentDecoderRow_JAVA = (jfloatArray) env->GetObjectArrayElement(currentDecoder_JAVA, j);
        
        dimensionOfCurrentDecoder = env->GetArrayLength(currentDecoderRow_JAVA);

        if(rowOffset + decoderRowIndex + dimensionOfCurrentDecoder <= currentData->totalDecoderSize)
        {
          env->GetFloatArrayRegion(currentDecoderRow_JAVA, 0, dimensionOfCurrentDecoder, currentData->decoders->array + rowOffset + decoderRowIndex);
        }
        else
        {
          printf("error: accessing array out of bounds: decoders\n");
          exit(EXIT_FAILURE);
        }

        decoderRowIndex += dimensionOfCurrentDecoder;
         
        env->DeleteLocalRef(currentDecoder_JAVA);
        env->DeleteLocalRef(currentDecoderRow_JAVA);
      }

      rowOffset += intArrayGetElement(currentData->decoderStride,j);
    }
    
    offset = decoderRowIndex;

    env->DeleteLocalRef(decodersForCurrentEnsemble_JAVA);
  }


  jint offsetInOutput;
  decoderRowIndex = 0;

  // set decoderRowIndexors. Tells each decoder which ensemble it belongs to and where to put its output
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInDecoders, i);

    outputSize = ensembleOutputSize[ensembleIndex]; 
    offsetInOutput = ensembleOffsetInOutput[ensembleIndex];

    for(j = 0; j < outputSize; j++)
    {
      intArraySetElement(currentData->decoderRowToEnsembleIndexor, decoderRowIndex, ensembleIndex); 
      intArraySetElement(currentData->decoderRowToOutputIndexor, decoderRowIndex, offsetInOutput + j); 
      decoderRowIndex++;
    }
  }
  
  free(ensembleOutputSize);
  free(ensembleOffsetInOutput);
}


void assignNetworkArrayToDevice(jint networkArrayIndex, jint* networkArrayData, jint* ensembleData, jint* collectSpikes, NengoGPUData* currentData)
{
  currentData->numNetworkArrays++;
  currentData->numInputs += networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_NUM_TERMINATIONS];
  currentData->totalInputSize += networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_TOTAL_INPUT_SIZE];
  currentData->numNetworkArrayOrigins += networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_NUM_ORIGINS];

  jint i, startEnsembleIndex, endEnsembleIndex;
  startEnsembleIndex = networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_FIRST_INDEX];
  endEnsembleIndex = networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_END_INDEX];

  networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_END_INDEX] -= 
    networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_FIRST_INDEX] - currentData->numEnsembles;

  networkArrayData[NENGO_NA_DATA_NUM * networkArrayIndex + NENGO_NA_DATA_FIRST_INDEX] = currentData->numEnsembles;

  for( i = startEnsembleIndex; i < endEnsembleIndex; i++)
  {
    currentData->numEnsembles++;
    currentData->numNeurons += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS];

    currentData->numDecodedTerminations += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS];
    currentData->totalNumTransformRows += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS] * ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_DIMENSION];
    
    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS] > currentData->maxNumDecodedTerminations)
    {
      currentData->maxNumDecodedTerminations = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS];
    }

    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_TRANSFORM_DIMENSION] > currentData->maxDecodedTerminationDimension)
    {
      currentData->maxDecodedTerminationDimension = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_TRANSFORM_DIMENSION];
    }

    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_ND_TRANSFORM_SIZE] > currentData->maxEnsembleNDTransformSize)
    {
      currentData->maxEnsembleNDTransformSize = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_ND_TRANSFORM_SIZE];
    }
    
    currentData->totalEncoderSize += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS] * ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_DIMENSION];

    currentData->numOrigins += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_ORIGINS];
    currentData->totalOutputSize += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_TOTAL_OUTPUT_SIZE];
    currentData->totalDecoderSize += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_TOTAL_OUTPUT_SIZE] * ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS];
    
    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_DECODER_DIMENSION] > currentData->maxOriginDimension)
    {
      currentData->maxOriginDimension = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_MAX_DECODER_DIMENSION];
    }

    currentData->totalEnsembleDimension += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_DIMENSION];

    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_DIMENSION] > currentData->maxDimension)
    {
      currentData->maxDimension = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_DIMENSION];
    }

    if(ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS] > currentData->maxNumNeurons)
    {
      currentData->maxNumNeurons = ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS];
    }
    
    currentData->numNDterminations += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NON_DECODED_TERMINATIONS];

    if(collectSpikes[i])
    {
      currentData->numSpikesToSendBack += ensembleData[NENGO_ENSEMBLE_DATA_NUM * i + NENGO_ENSEMBLE_DATA_NUM_NEURONS];
    }
  }
}

void setupIO(jint numProjections, projection* projections, NengoGPUData* currentData, jint* networkArrayData, jint** originRequiredByJava)
{
  jint i, j, k, l;
  jint currentDimension, networkArrayIndex = -1, terminationIndexInNetworkArray = -1;
  jint projectionMatches, currentNumTerminations = 0, networkArrayJavaIndex; 

  jint* flattenedProjections = (jint*) malloc(2 * numProjections * sizeof(jint));

  jint JavaInputIndex = 0, GPUInputIndex = 0, CPUInputIndex = 0;
  
  // 0 = GPU, 1 = Java, 2 = CPU
  jint* terminationLocation = (jint*)malloc(currentData->numInputs * sizeof(jint));

  // flatten the termination side of the projection array
  for(i = 0; i < currentData->numInputs; i++)
  {
    terminationIndexInNetworkArray++;
    while(terminationIndexInNetworkArray == currentNumTerminations)
    {
      networkArrayIndex++;
      networkArrayJavaIndex = intArrayGetElement(currentData->networkArrayIndexInJavaArray, networkArrayIndex);
      currentNumTerminations = networkArrayData[NENGO_NA_DATA_NUM * networkArrayJavaIndex + NENGO_NA_DATA_NUM_TERMINATIONS];
      terminationIndexInNetworkArray = 0;
    }

    currentDimension = intArrayGetElement(currentData->inputDimension, i);

    // find a GPU projection that terminates at the current termination. We can stop as soon as we find
    // one because terminations can only be involved in one projection

    j = 0;
    projectionMatches = 0;
    while(!projectionMatches && j < numProjections)
    {
      projectionMatches = projections[j].destDevice == currentData->device
                    && projections[j].destinationNode == networkArrayIndex
                    && projections[j].destinationTermination == terminationIndexInNetworkArray;

      if(projectionMatches)
        break;

      j++;
    }

    // if we found one then we have to see whether that projection is inter or intra device
    if(!projectionMatches)
    {
      intArraySetElement(currentData->terminationOffsetInInput, i, JavaInputIndex);
      JavaInputIndex += currentDimension;
      terminationLocation[i] = 1;
    }
    else
    {
      flattenedProjections[2 * j] = i;

      if(projections[j].sourceDevice == currentData->device)
      {
        intArraySetElement(currentData->terminationOffsetInInput, i, GPUInputIndex);
        GPUInputIndex += currentDimension;
        terminationLocation[i] = 0;
      }
      else
      {
        intArraySetElement(currentData->terminationOffsetInInput, i, CPUInputIndex);
        CPUInputIndex += currentDimension;
        terminationLocation[i] = 2;
      }
    }
  }

  currentData->GPUInputSize = GPUInputIndex;
  currentData->CPUInputSize = CPUInputIndex;
  currentData->JavaInputSize = JavaInputIndex;

  assert(GPUInputIndex + CPUInputIndex + JavaInputIndex == currentData->totalInputSize);

  // adjust the terminationOffsets to reflect the location of each termination (GPU, Java or CPU)
  // can only be done once we know the size of each location
  jint oldVal, location;
  for(i = 0; i < currentData->numInputs; i++)
  {
    oldVal = intArrayGetElement(currentData->terminationOffsetInInput, i);
    
    location = terminationLocation[i];

    switch(location)
    {
      case 0:
        break;
      case 1:
        intArraySetElement(currentData->terminationOffsetInInput, i, oldVal + currentData->GPUInputSize);
        break;
      case 2:
        intArraySetElement(currentData->terminationOffsetInInput, i, oldVal + currentData->GPUInputSize + currentData->JavaInputSize);
        break;
    }
  }

  free(terminationLocation);


  jint ensembleOriginDimension, ensembleOriginIndex = 0, naOriginIndex = 0, numOrigins, originDimension;
  jint naOffsetInEnsembleOriginIndices = 0, naIndexInJavaArray, startEnsembleIndex, endEnsembleIndex;

  // here goal is to set networkArrayNumOrigins and networkArrayOriginDimension;
  //
  for(i = 0; i < currentData->numNetworkArrays; i++)
  {  
    naIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray, i);
    
    numOrigins = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_NUM_ORIGINS];

    startEnsembleIndex = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_FIRST_INDEX];
    endEnsembleIndex = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_END_INDEX];

    intArraySetElement(currentData->networkArrayNumOrigins, i, numOrigins);

    for(j = 0; j < numOrigins; j++)
    {
      originDimension = 0;
      ensembleOriginIndex = naOffsetInEnsembleOriginIndices + j;

      for(k = startEnsembleIndex; k < endEnsembleIndex; k++)
      {
        ensembleOriginDimension = intArrayGetElement(currentData->ensembleOriginDimension, ensembleOriginIndex);
        originDimension += ensembleOriginDimension;
        ensembleOriginIndex += numOrigins;
      }

      intArraySetElement(currentData->networkArrayOriginDimension, naOriginIndex, originDimension);


      naOriginIndex++;
    }

    naOffsetInEnsembleOriginIndices += numOrigins * (endEnsembleIndex - startEnsembleIndex);
  }

  jint interGPUFlag, interGPUOutputSize = 0, CPUOutputIndex = 0, GPUOutputIndex = 0, JavaOutputIndex = 0;
  jint* originLocation = (jint*)malloc(currentData->numNetworkArrayOrigins * sizeof(jint));

  naOriginIndex = 0;

  // here my goal is JUST to set populate the origin side of the flattened projections array, populate
  // origonLocations, temporaririly populate networkArrayOriginOffsetInOutput (which will be corrected later),
  // and determine the sizes of each of the output sections
  //
  for(i = 0; i < currentData->numNetworkArrays; i++)
  {  
    naIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray, i);
    
    numOrigins = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_NUM_ORIGINS];

    for(j = 0; j < numOrigins; j++)
    {
      interGPUFlag = 0;

      for(k = 0; k < numProjections; k++)
      {
        projectionMatches = projections[k].sourceDevice == currentData->device
                        && projections[k].sourceNode == i
                        && projections[k].sourceOrigin == j;

        if(projectionMatches)
        {
          flattenedProjections[2 * k + 1] = naOriginIndex;

          if(projections[k].destDevice != currentData->device)
          {
            interGPUFlag = 1;
          }
        }
      }

      currentDimension = intArrayGetElement(currentData->networkArrayOriginDimension, naOriginIndex);

      interGPUOutputSize += interGPUFlag ? currentDimension : 0;

      // set which section of the output array the current origin belongs in: 0 if it stays on the GPU, 1 if it 
      // has to go all the way to Java, 2 if it doesn't have to go all the way back to java but does hava to go to another GPU
      if(originRequiredByJava[naIndexInJavaArray][j])
      {
        intArraySetElement(currentData->networkArrayOriginOffsetInOutput, naOriginIndex, JavaOutputIndex);
        originLocation[naOriginIndex] = 1;
        JavaOutputIndex += currentDimension;
      }
      else if(interGPUFlag)
      {
        intArraySetElement(currentData->networkArrayOriginOffsetInOutput, naOriginIndex, CPUOutputIndex);
        originLocation[naOriginIndex] = 2;
        CPUOutputIndex += currentDimension;
      }
      else
      {
        intArraySetElement(currentData->networkArrayOriginOffsetInOutput, naOriginIndex, GPUOutputIndex);
        originLocation[naOriginIndex] = 0;
        GPUOutputIndex += currentDimension;
      }

      naOriginIndex++;
    }
  }


  currentData->GPUOutputSize = GPUOutputIndex;
  currentData->JavaOutputSize = JavaOutputIndex;
  currentData->CPUOutputSize = currentData->totalOutputSize - GPUOutputIndex;

  currentData->interGPUOutputSize = interGPUOutputSize;

  // adjust the networkArrayOriginOffsetInOutput to reflect the location of each network array Origin (GPU, Java or CPU)
  // can only be done once we know the size of each section
  //
  for(i = 0; i < currentData->numNetworkArrayOrigins; i++)
  {
    oldVal = intArrayGetElement(currentData->networkArrayOriginOffsetInOutput, i);
    
    location = originLocation[i];

    switch(location)
    {
      case 0:
        break;
      case 1:
        intArraySetElement(currentData->networkArrayOriginOffsetInOutput, i, oldVal + currentData->GPUOutputSize);
        break;
      case 2:
        intArraySetElement(currentData->networkArrayOriginOffsetInOutput, i, oldVal + currentData->GPUOutputSize + currentData->JavaOutputSize);
        break;
    }
  }

  free(originLocation);


  /*
  These are arrays whose size relies on GPUInputSize, CPUInputSize or JavaInput size, and thus cannot be made until we have those
  values. Because of this, we cannot create these arrays in the function initializeNengoGPUData like we do with all the other arrays
  */
  char* name = "GPUTerminationToOriginMap";
  currentData->GPUTerminationToOriginMap = newIntArray(currentData->GPUInputSize, name);

  name = "outputHost";
  currentData->outputHost = newFloatArray(currentData->CPUOutputSize + currentData->numSpikesToSendBack, name);

  name = "sharedData_outputIndex";
  currentData->sharedData_outputIndex = newIntArray(currentData->interGPUOutputSize, name);

  name = "sharedData_sharedIndex";
  currentData->sharedData_sharedIndex = newIntArray(currentData->interGPUOutputSize, name);


  // here my goal is JUST to populate the ensembleOutputToNetworkArrayOutputMap 
  naOffsetInEnsembleOriginIndices = 0;
  naOriginIndex = 0;
  
  jint indexInNetworkArrayOutput, indexInEnsembleOutput;

  for(i = 0; i < currentData->numNetworkArrays; i++)
  {  
    naIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray, i);
    
    numOrigins = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_NUM_ORIGINS];

    startEnsembleIndex = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_FIRST_INDEX];
    endEnsembleIndex = networkArrayData[naIndexInJavaArray * NENGO_NA_DATA_NUM + NENGO_NA_DATA_END_INDEX];

    for(j = 0; j < numOrigins; j++)
    {
      indexInNetworkArrayOutput = intArrayGetElement(currentData->networkArrayOriginOffsetInOutput, naOriginIndex);
      
      ensembleOriginIndex = naOffsetInEnsembleOriginIndices + j;

      for(k = startEnsembleIndex; k < endEnsembleIndex; k++)
      {
        indexInEnsembleOutput = intArrayGetElement(currentData->ensembleOriginOffsetInOutput, ensembleOriginIndex);
        ensembleOriginDimension = intArrayGetElement(currentData->ensembleOriginDimension, ensembleOriginIndex);

        for(l = 0; l < ensembleOriginDimension; l++)
        {
          intArraySetElement(currentData->ensembleOutputToNetworkArrayOutputMap, indexInNetworkArrayOutput + l, indexInEnsembleOutput + l);
        }

        ensembleOriginIndex += numOrigins;
        indexInNetworkArrayOutput += ensembleOriginDimension;
      }

      naOriginIndex++;
    }

    naOffsetInEnsembleOriginIndices += numOrigins * (endEnsembleIndex - startEnsembleIndex);
  }


  // Use the flattened projections to create a map from the input to the output following the projections
  // This way we can launch a kernel for each projection on the GPU, have it look up where it gets its output from
  // fetch it from the output array and put it in the input array
  jint terminationIndexOnDevice, originIndexOnDevice, terminationOffsetInInput, originOffsetInOutput;
  for(i = 0; i < numProjections; i++)
  {
    if(projections[i].sourceDevice == currentData->device && projections[i].destDevice == currentData->device)
    {
      terminationIndexOnDevice = flattenedProjections[i * 2];
      originIndexOnDevice = flattenedProjections[i * 2 + 1];

      terminationOffsetInInput = intArrayGetElement(currentData->terminationOffsetInInput, terminationIndexOnDevice);
      originOffsetInOutput = intArrayGetElement(currentData->networkArrayOriginOffsetInOutput, originIndexOnDevice);
      currentDimension = intArrayGetElement(currentData->inputDimension, terminationIndexOnDevice);

      for(j = 0; j < currentDimension; j++)
      {
        intArraySetElement(currentData->GPUTerminationToOriginMap, terminationOffsetInInput + j, originOffsetInOutput + j);
      }
    }
    else if(projections[i].sourceDevice == currentData->device && projections[i].destDevice != currentData->device)
    {
      originIndexOnDevice = flattenedProjections[i * 2 + 1];
      projections[i].offsetInSource = intArrayGetElement(currentData->networkArrayOriginOffsetInOutput, originIndexOnDevice) - currentData->GPUOutputSize;
    }
    else if(projections[i].destDevice == currentData->device && projections[i].sourceDevice != currentData->device)
    {

      terminationIndexOnDevice = flattenedProjections[i * 2];
      projections[i].offsetInDestination = intArrayGetElement(currentData->terminationOffsetInInput, terminationIndexOnDevice) - currentData->GPUInputSize + currentData->offsetInSharedInput;
    }
    
  }

  free(flattenedProjections);
}





// this function should be called before setupIO, but after setupNeuronData.
void setupSpikes(jint* collectSpikes, NengoGPUData* currentData)
{
  //create an array which can be used by the GPU to extract the spikes we want to send back form the main spike array 
  currentData->spikeMap = newIntArray(currentData->numSpikesToSendBack, "spikeMap");

  //populate these arrays
  jint i, j, indexInJavaArray, spikeIndex = 0, currentNumNeurons, currentOffsetInNeurons;
  for(i = 0; i < currentData->numEnsembles; i++)
  {
    indexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);

    if(collectSpikes[indexInJavaArray])
    {
      currentOffsetInNeurons = intArrayGetElement(currentData->ensembleOffsetInNeurons, i);
      currentNumNeurons = intArrayGetElement(currentData->ensembleNumNeurons, i);

      for(j = 0; j < currentNumNeurons; j++)
      {
        intArraySetElement(currentData->spikeMap, spikeIndex, currentOffsetInNeurons + j); 
        spikeIndex++;
      }
    }
  }
}

// this sets the shared memory maps (maps for getting data out of the output arrays and into
// the shared input arrays which is the means of inter-gpu communication). relies on the offsetInSource
// and offsetInDestination fields of the projection structure being set properly (happens in setupIO).
void createSharedMemoryMaps(jint numProjections, projection* projections)
{
  jint i, j, sharedIndex = 0, indexInProjection;
  projection* p;
  NengoGPUData* currentData;

  for(j = 0; j < numDevices; j++)
  {
    currentData = nengoDataArray[j];
    sharedIndex = 0;
    for(i = 0; i < numProjections; i++)
    {
      p = projections + i;

      if(p->sourceDevice == currentData->device && p->destDevice != currentData->device)
      {
        for(indexInProjection = 0; indexInProjection < p->size; indexInProjection++)
        {
          intArraySetElement(currentData->sharedData_outputIndex, sharedIndex, p->offsetInSource + indexInProjection);
          intArraySetElement(currentData->sharedData_sharedIndex, sharedIndex, p->offsetInDestination + indexInProjection);
          sharedIndex++;
        }
      }
    }
  }
}

JNIEXPORT jint JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeGetNumDevices(JNIEnv *env, jclass clazz)
{
  jint numGPU = (jint) getGPUDeviceCount();

  return numGPU;
}

// This function takes as arguments all the information required by the ensembles to run that won't change from step to step: decoders, encoders, transformations.
// This is called only once, at the beginning of a run (specifically, when the GPUNodeThreadPool is created). 
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeSetupRun(
	JNIEnv *env,
	jclass clazz,
	jobjectArray terminationTransforms_JAVA,
	jobjectArray isDecodedTermination_JAVA,
	jobjectArray terminationTau_JAVA,
	jobjectArray encoders_JAVA,
	jobjectArray decoders_JAVA,
	jobjectArray neuronData_JAVA,
	jobjectArray projections_JAVA,
	jobjectArray networkArrayData_JAVA,
	jobjectArray ensembleData_JAVA,
	jintArray isSpikingEnsemble_JAVA, jintArray collectSpikes_JAVA,
	jobjectArray originRequiredByJava_JAVA,
	jfloat maxTimeStep,
	jintArray deviceForNetworkArrays_JAVA,
	jint numDevicesRequested
)
{
  printf("NengoGPU: SETUP\n"); 

  jint i, j, k;
  
  jint numAvailableDevices = getGPUDeviceCount();
  numDevices = numDevicesRequested;

  printf("Using %d devices. %d available\n", numDevices, numAvailableDevices);

  nengoDataArray = (NengoGPUData**) malloc(sizeof(NengoGPUData*) * numDevices);

  totalNumEnsembles = (jint) env->GetArrayLength(neuronData_JAVA);
  totalNumNetworkArrays = (jint)env->GetArrayLength(networkArrayData_JAVA);

  jintArray tempIntArray_JAVA;

  NengoGPUData* currentData;

  // Create the NengoGPUData structs, one per device.
  for(i = 0; i < numDevices; i++)
  {
    nengoDataArray[i] = getNewNengoGPUData();
    nengoDataArray[i]->maxTimeStep = (float)maxTimeStep;
  }

  // sort out the projections
  jint numProjections = (jint) env->GetArrayLength(projections_JAVA);
  jint* currentProjection = (jint*)malloc(PROJECTION_DATA_SIZE * sizeof(jint));
  projection* projections = (projection*) malloc( numProjections * sizeof(projection));
  for(i=0; i < numProjections; i++)
  {
    tempIntArray_JAVA = (jintArray)env->GetObjectArrayElement(projections_JAVA, i);
    env->GetIntArrayRegion(tempIntArray_JAVA, 0, PROJECTION_DATA_SIZE, currentProjection);
    storeProjection(projections + i, currentProjection);

    env->DeleteLocalRef(tempIntArray_JAVA);
  }

  free(currentProjection);
  env->DeleteLocalRef(projections_JAVA);

  jint* networkArrayData = (jint*)malloc(totalNumNetworkArrays * NENGO_NA_DATA_NUM * sizeof(jint));
  jint* ensembleData = (jint*)malloc(totalNumEnsembles * NENGO_ENSEMBLE_DATA_NUM * sizeof(jint));

  jint numNeurons;
  jintArray dataRow_JAVA;

  // store Network Array Data, and in a separate array store the number of neurons for each
  // networkArray to be used in creating the device configuration
  for(i = 0; i < totalNumNetworkArrays; i++)
  {
    dataRow_JAVA = (jintArray) env->GetObjectArrayElement(networkArrayData_JAVA, i);
    env->GetIntArrayRegion(dataRow_JAVA, 0, NENGO_NA_DATA_NUM, networkArrayData + i * NENGO_NA_DATA_NUM);
    
    numNeurons = networkArrayData[i * NENGO_NA_DATA_NUM + NENGO_NA_DATA_NUM_NEURONS];

    env->DeleteLocalRef(dataRow_JAVA);
  }

  env->DeleteLocalRef(networkArrayData_JAVA);

  // store ensemble data
  for(i = 0; i < totalNumEnsembles; i++)
  {
    dataRow_JAVA = (jintArray) env->GetObjectArrayElement(ensembleData_JAVA, i);
    env->GetIntArrayRegion(dataRow_JAVA, 0, NENGO_ENSEMBLE_DATA_NUM, ensembleData + i * NENGO_ENSEMBLE_DATA_NUM);
    env->DeleteLocalRef(dataRow_JAVA);
  }

  env->DeleteLocalRef(ensembleData_JAVA);
 
  // Distribute the ensembles to the devices. Tries to minimize communication required between GPUs.
  //generateNengoGPUDeviceConfiguration(totalNumNeurons, networkArrayNumNeurons, numProjections, projections, adjacencyMatrix, deviceForNetworkArray);
  
  deviceForNetworkArray = (jint*) malloc(totalNumNetworkArrays * sizeof(jint));
  deviceForEnsemble = (jint*) malloc(totalNumEnsembles * sizeof(jint));

  env->GetIntArrayRegion(deviceForNetworkArrays_JAVA, 0, totalNumNetworkArrays, deviceForNetworkArray); 
  env->DeleteLocalRef(deviceForNetworkArrays_JAVA);

  // Store which device each ensemble belongs to based on which device each network array belongs to
  jint endIndex;
  for(i = 0; i < totalNumNetworkArrays; i++)
  {
    j = networkArrayData[i * NENGO_NA_DATA_NUM + NENGO_NA_DATA_FIRST_INDEX];
    endIndex = networkArrayData[i * NENGO_NA_DATA_NUM + NENGO_NA_DATA_END_INDEX];

    for(; j < endIndex; j++)
    {
      deviceForEnsemble[j] = deviceForNetworkArray[i];
    }
  }

  jint originNodeIndex, termNodeIndex;
  for(i = 0; i < numProjections; i++)
  {
    originNodeIndex = projections[i].sourceNode;
    termNodeIndex = projections[i].destinationNode;

    projections[i].sourceDevice = deviceForNetworkArray[originNodeIndex];
    projections[i].destDevice = deviceForNetworkArray[termNodeIndex];
  }

  jint* isSpikingEnsemble = (jint*) malloc(totalNumEnsembles * sizeof(jint));
  env->GetIntArrayRegion(isSpikingEnsemble_JAVA, 0, totalNumEnsembles, isSpikingEnsemble); 
  env->DeleteLocalRef(isSpikingEnsemble_JAVA);

  jint* collectSpikes = (jint*) malloc(totalNumEnsembles * sizeof(jint));
  env->GetIntArrayRegion(collectSpikes_JAVA, 0, totalNumEnsembles, collectSpikes); 
  env->DeleteLocalRef(collectSpikes_JAVA);

  jint** originRequiredByJava = (jint**) malloc(totalNumNetworkArrays * sizeof(jint*));
  for(i = 0; i < totalNumNetworkArrays; i++)
  {
    tempIntArray_JAVA = (jintArray) env->GetObjectArrayElement(originRequiredByJava_JAVA, i); 
    j = env->GetArrayLength(tempIntArray_JAVA);

    originRequiredByJava[i] = (jint*) malloc(j * sizeof(jint));
    env->GetIntArrayRegion(tempIntArray_JAVA, 0, j, originRequiredByJava[i]); 
    
    env->DeleteLocalRef(tempIntArray_JAVA);
  }

  env->DeleteLocalRef(originRequiredByJava_JAVA);

  // set the number fields in the NengoGPUData structs so that it knows how big to make its internal arrays
  jint* networkArrayJavaIndexToDeviceIndex = (jint*)malloc(totalNumEnsembles * sizeof(jint));
  for(i = 0; i < totalNumNetworkArrays; i++)
  {
    currentData = nengoDataArray[deviceForNetworkArray[i]];

    networkArrayJavaIndexToDeviceIndex[i] = currentData->numNetworkArrays;

    assignNetworkArrayToDevice(i, networkArrayData, ensembleData, collectSpikes, currentData); 
  }

  // Adjust projections to reflect the distribution of ensembles to devices.
  adjustProjections(numProjections, projections, networkArrayJavaIndexToDeviceIndex);
  free( networkArrayJavaIndexToDeviceIndex );

  sharedInputSize = 0;

  // Now we start to load the data into the NengoGPUData struct for each device. 
  // (though the data doesn't get put on the actual device just yet).
  // Because of the CUDA architecture, we have to do some weird things to get a good speedup. 
  // These arrays that store the transforms, decoders, are setup in a non-intuitive way so 
  // that memory accesses can be parallelized in CUDA kernels. For more information, see the NengoGPU user manual.
  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];
    
    currentData->device = i;

    currentData->offsetInSharedInput = sharedInputSize;

    currentData->numTerminations = currentData->numDecodedTerminations + currentData->numNDterminations;
    currentData->totalTransformSize = currentData->maxDecodedTerminationDimension * currentData->totalNumTransformRows;
    currentData->totalNonDecodedTransformSize = currentData->maxEnsembleNDTransformSize * currentData->numEnsembles;

    initializeNengoGPUData(currentData);
    

    // set networkArrayIndexInJavaArray
    j = 0;
    for(k = 0; k < currentData->numNetworkArrays; k++)
    {
      while(deviceForNetworkArray[j] != currentData->device)
      {
        j++;
      }

      intArraySetElement(currentData->networkArrayIndexInJavaArray, k, j);
      j++;
    }

    // set ensembleIndexInJavaArray
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
 
    storeTerminationData(env, terminationTransforms_JAVA, terminationTau_JAVA, isDecodedTermination_JAVA, currentData, networkArrayData);

    storeNeuronData(env, neuronData_JAVA, isSpikingEnsemble, currentData);

    storeEncoders(env, encoders_JAVA, currentData);

    storeDecoders(env, decoders_JAVA, currentData, networkArrayData);

    setupSpikes(collectSpikes, currentData);

    setupIO(numProjections, projections, currentData, networkArrayData, originRequiredByJava);

    sharedInputSize += currentData->JavaInputSize + currentData->CPUInputSize;
  }

  env->DeleteLocalRef(terminationTransforms_JAVA);
  env->DeleteLocalRef(terminationTau_JAVA);
  env->DeleteLocalRef(isDecodedTermination_JAVA);
  env->DeleteLocalRef(neuronData_JAVA);
  env->DeleteLocalRef(encoders_JAVA);
  env->DeleteLocalRef(decoders_JAVA);

  
  for(i = 0; i < totalNumNetworkArrays; i++)
  {
    free(originRequiredByJava[i]);
  }

  free(originRequiredByJava);

  free(isSpikingEnsemble);
  free(collectSpikes);
  free(ensembleData);
  free(networkArrayData);

  // set the shared memory maps for the device. We can't do this until
  // all the intra-device projections have had their offsets set, which means each device has to have
  // had setupIO called on it.
  createSharedMemoryMaps(numProjections, projections);

  // Allocate and initialize the shared array
  sharedInput = (float*)malloc(sharedInputSize * sizeof(float));
  for(i = 0; i < sharedInputSize; i++)
  {
    sharedInput[i] = 0.0;
  }

  free(projections);


  // we have all the data we need, now start the worker threads which control the GPU's directly.
  run_start();
}


// Called once per step from the Java code. Puts the representedInputValues in the proper form for processing, then tells each GPU thread
// to take a step. Once they've finished the step, this function puts the representedOutputValues and spikes in the appropriate Java
// arrays so that they can be read on the Java side when this call returns.
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeStep(
	JNIEnv *env,
	jclass clazz,
	jobjectArray input,
	jobjectArray output,
	jobjectArray spikes,
	jfloat startTime_JAVA,
	jfloat endTime_JAVA
) {
  startTime = (float) startTime_JAVA;
  endTime = (float) endTime_JAVA;

  jobjectArray currentInputs_JAVA;
  jfloatArray currentInput_JAVA;
  
  NengoGPUData* currentData;

  jint i, j, k, l;
  jint naIndexInJavaArray, inputIndex, numInputs, inputDimension;

  for( i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];

    inputIndex = 0;

    for( j = 0; j < currentData->numNetworkArrays; j++)
    {
      naIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray,j);
        
      currentInputs_JAVA = (jobjectArray) env->GetObjectArrayElement(input, naIndexInJavaArray);
      
      if(currentInputs_JAVA != NULL)
      {
        numInputs = env->GetArrayLength(currentInputs_JAVA);

        for(k = 0; k < numInputs; k++)
        {
          currentInput_JAVA = (jfloatArray)env->GetObjectArrayElement(currentInputs_JAVA, k);
          if(currentInput_JAVA != NULL)
          {
            inputDimension = env->GetArrayLength(currentInput_JAVA);

            if(inputIndex  + inputDimension <= currentData->JavaInputSize)
            {
              env->GetFloatArrayRegion(currentInput_JAVA, 0, inputDimension, sharedInput + currentData->offsetInSharedInput + inputIndex);
            }
            else
            {
              printf("error: accessing sharedInput out of bounds. size: %d, index: %d\n", sharedInputSize, currentData->offsetInSharedInput + inputIndex + inputDimension);
              exit(EXIT_FAILURE);
            }

            inputIndex += inputDimension;
          }
            
          env->DeleteLocalRef(currentInput_JAVA);
        }
      }

      env->DeleteLocalRef(currentInputs_JAVA);
    }
  }

  env->DeleteLocalRef(input);

  // tell the runner threads to run and then wait for them to finish. The last of them to finish running will wake this thread up. 
  pthread_mutex_lock(mutex);
  myCVsignal = 1;
  pthread_cond_broadcast(cv_GPUThreads);
  pthread_cond_wait(cv_JNI, mutex);
  pthread_mutex_unlock(mutex);
  jobjectArray currentOutputs_JAVA;
  jfloatArray currentOutput_JAVA;
  jfloatArray currentSpikes_JAVA;

  //store represented output in java array
  jint numOutputs, outputDimension, outputIndex, spikeIndex, sharedIndex, ensembleNumNeurons;
  jint ensembleIndexInJavaArray;

  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoDataArray[i];

    outputIndex = 0;

    for(k = 0; k < currentData->numNetworkArrays; k++)
    {
      naIndexInJavaArray = intArrayGetElement(currentData->networkArrayIndexInJavaArray,k);

      currentOutputs_JAVA = (jobjectArray)env->GetObjectArrayElement(output, naIndexInJavaArray);
      numOutputs = env->GetArrayLength(currentOutputs_JAVA); 

      for(l = 0; l < numOutputs; l++)
      {
        currentOutput_JAVA = (jfloatArray) env->GetObjectArrayElement(currentOutputs_JAVA, l);

        if(currentOutput_JAVA != NULL)
        {
          outputDimension = env->GetArrayLength(currentOutput_JAVA);

          if(outputIndex + outputDimension <= currentData->JavaOutputSize)
          {
             env->SetFloatArrayRegion(currentOutput_JAVA, 0, outputDimension, currentData->outputHost->array + outputIndex);
          }
          else
          {
            printf("error: accessing outputHost for java out of bounds. size: %d, index: %d, dim: %d, device: %d, na: %d out of %d\n", currentData->JavaOutputSize, outputIndex, outputDimension, currentData->device, k, currentData->numNetworkArrays);
            exit(EXIT_FAILURE);
          }

          outputIndex += outputDimension;
          env->DeleteLocalRef(currentOutput_JAVA);
        }
      }
      
      env->DeleteLocalRef(currentOutputs_JAVA);
    }

    spikeIndex = 0;

    for(k = 0; k < currentData->numEnsembles; k++)
    {
      ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray,k);

      currentSpikes_JAVA = (jfloatArray)env->GetObjectArrayElement(spikes, ensembleIndexInJavaArray);

      if(currentSpikes_JAVA != NULL)
      {
        ensembleNumNeurons = (jint) env->GetArrayLength(currentSpikes_JAVA);

        env->SetFloatArrayRegion(currentSpikes_JAVA, 0, ensembleNumNeurons, currentData->outputHost->array + currentData->CPUOutputSize + spikeIndex);

        spikeIndex += ensembleNumNeurons;

        env->DeleteLocalRef(currentSpikes_JAVA);
      }
    }

    // write from the output array of the current device to the shared data array
    for( k = 0; k < currentData->interGPUOutputSize; k++)
    {
      outputIndex = intArrayGetElement(currentData->sharedData_outputIndex, k);
      sharedIndex = intArrayGetElement(currentData->sharedData_sharedIndex, k);

      sharedInput[sharedIndex] = floatArrayGetElement(currentData->outputHost, outputIndex);
    }
  }


  env->DeleteLocalRef(output);
  env->DeleteLocalRef(spikes);
}

JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeKill(JNIEnv *env, jclass clazz)
{
  printf("NengoGPU: KILL\n");
  run_kill();
}
