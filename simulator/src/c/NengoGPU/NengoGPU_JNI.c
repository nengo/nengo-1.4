#ifdef __cplusplus
extern "C"{
#endif

#include <stdlib.h>
#include <jni.h>
#include <pthread.h>
#include <sys/timeb.h>

#include "GPUThread_JNI.h"
#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPUData.h"

// Get the decoders out of the java array and store in NengoGPUData structure in proper format
void getDecoders(JNIEnv* env, NengoGPUData* currentData, int* deviceForEnsemble, jobjectArray decoders_JAVA)
{
    int decoderElementIndex = 0, decoderIndex = 0, decoderRowIndex = 0, ensembleIndex = 0;
    int currentNumNeurons = 0, currentNumDecoders = 0, currentDecoderDimension = 0;
    int j = 0, k = 0, l = 0, n = 0;

    jobjectArray currentDecoders_JAVA, currentDecoder_JAVA;
    jfloatArray currentDecoderRow_JAVA;

    for(j = 0; j < currentData->maxNumNeurons; j++)
    {
      ensembleIndex = 0;
      for(k = 0; k < totalNumEnsembles; k++)
      {
        if(deviceForEnsemble[k] == currentData->device)
        {
          currentDecoders_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decoders_JAVA, k);
          currentNumDecoders = (int) (*env)->GetArrayLength(env, currentDecoders_JAVA);

          currentNumNeurons = currentData->ensembleNeuronsHost[ensembleIndex];

          if(j < currentNumNeurons)
          {
            for(l = 0; l < currentNumDecoders; l++)
            {
              currentDecoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, currentDecoders_JAVA, l);
              currentDecoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentDecoder_JAVA, j);
              
              currentDecoderDimension = (*env)->GetArrayLength(env, currentDecoderRow_JAVA);

              (*env)->GetFloatArrayRegion(env, currentDecoderRow_JAVA, 0, currentDecoderDimension, currentData->decoders + decoderElementIndex);

              decoderElementIndex += currentDecoderDimension;

              if(j == 0)
              {
                for(n = 0; n < currentDecoderDimension; n++)
                {
                  currentData->decoderRowToEnsembleIndexor[decoderRowIndex] = ensembleIndex;
                  decoderRowIndex++;
                }
                  
                currentData->decoderDimension[decoderIndex] = currentDecoderDimension;
              }

              decoderIndex++;
            }
          }

          ensembleIndex++;
        }
      }
    }

    currentData->totalNumDecoderRows = decoderRowIndex;
}


void createIndexArrayAndGatherData(int gatherData, float* data, float* tempData, int* indices, int* columnLengths, int* dataIndexor, int numRows, int numColumns )
{
    int j = 0, k = 0;
    int offset = 0, dataIndex = 0, columnLength = 0;
    int elementIndex = 0, previousRowElementIndex = 0;

    for(j = 0; j < numRows; j++)
    {
      offset = 0;

      for(k = 0; k < numColumns; k++)
      {
        if(dataIndexor)
        {
          dataIndex = dataIndexor[k];
        }
        else
        {
          dataIndex = k;
        }

        columnLength = columnLengths[dataIndex];

        if(columnLength > j)
        {
          if(gatherData)
          {
            data[elementIndex] = tempData[offset + j];
          }
            
          if(j > 0)
          {
            indices[previousRowElementIndex++] = elementIndex;
          }

          elementIndex++;
        }
        else if(columnLength == j)
        {
          indices[previousRowElementIndex++] = -1;
        } 

        if(gatherData)
        {
          offset += columnLength;
        }
      }
    }
}

void createHelper(int* helper, int* columnLengths, int numRows, int numColumns)
{
    int columnLength = 0, index = 0, offset = 0;
    int j = 0, k = 0;

    for(j = 0; j < numRows; j++)
    {
      offset = 0;

      for(k = 0; k < numColumns; k++)
      {
        columnLength = columnLengths[k];

        if(columnLength > j)
        {
          helper[offset + j] = index++;
        }
        offset += columnLength;
      }
    }
}

// This function takes as arguments all the information required by the ensembles to run that won't change from step to step: decoders, encoders, transformations.
// This is called only once, at the beginning of a run (specifically, when the GPUNodeThreadPool is created). 
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_GPUThread_nativeSetupRun
  (JNIEnv *env, jclass class, jobjectArray terminationTransforms_JAVA, jobjectArray terminationTau_JAVA, jobjectArray encoders_JAVA, jobjectArray decoders_JAVA, jobjectArray neuronData_JAVA, jbooleanArray collectSpikes_JAVA, jobjectArray GPUData_JAVA, jfloat maxTimeStep)
{
  printf("NengoGPU: SETUP\n");

  // This assigns each ensemble to a device, attempting to get an even distribution of neurons between the devices
  // It also retreives the GPU data, which it will need to allocate the arrays later on
  numDevices = getGPUDeviceCount();
  nengoData = (NengoGPUData**) malloc(sizeof(NengoGPUData*) * numDevices);
  if(!nengoData)
  {
    printf("bad malloc\n");
    exit(EXIT_FAILURE);
  }

  totalNumEnsembles = (int) (*env)->GetArrayLength(env, neuronData_JAVA);

  int i = 0, j = 0, deviceWithFewestNeurons = 0, minNeurons;
  deviceForEnsemble = (int*) malloc(totalNumEnsembles * sizeof(int));
  int* GPUData = (int*)malloc(NENGO_GPU_DATA_NUM * sizeof(int));
  unsigned char* collectSpikes = (unsigned char*)malloc(totalNumEnsembles * sizeof(unsigned char));

  if(!deviceForEnsemble || !GPUData || !collectSpikes)
  {
    printf("bad malloc\n");
    exit(EXIT_FAILURE);
  }

  (*env)->GetBooleanArrayRegion(env, collectSpikes_JAVA, 0, totalNumEnsembles, collectSpikes);

  jfloatArray tempFloatArray_JAVA;
  jobjectArray tempObjectArray_JAVA;
  jintArray tempIntArray_JAVA;

  NengoGPUData* currentData;

  for(i = 0; i < numDevices; i++)
  {
    nengoData[i] = getNewNengoGPUData();
    nengoData[i]->maxTimeStep = (float)maxTimeStep;
  }

  for(i = 0; i < totalNumEnsembles; i++)
  {
    // find the device that has the fewest number of neurons assigned to it. assign the current ensemble to that device.
    minNeurons = nengoData[0]->numNeurons;
    deviceWithFewestNeurons = 0;
    for(j = 1; j < numDevices; j++)
    {
      if(nengoData[j]->numNeurons < minNeurons)
      {
        minNeurons = nengoData[j]->numNeurons;
        deviceWithFewestNeurons = j;
      }
    }

    tempIntArray_JAVA = (jintArray) (*env)->GetObjectArrayElement(env, GPUData_JAVA, i);
    (*env)->GetIntArrayRegion(env, tempIntArray_JAVA, 0, NENGO_GPU_DATA_NUM, GPUData);

    deviceForEnsemble[i] = deviceWithFewestNeurons;

    currentData = nengoData[deviceWithFewestNeurons];
    currentData->numEnsembles++;
    currentData->numNeurons += GPUData[NENGO_GPU_DATA_NUM_NEURONS];

    // Used in transformAndIntegrate 
    currentData->numTerminations += GPUData[NENGO_GPU_DATA_NUM_TERMINATIONS];
    currentData->totalInputSize += GPUData[NENGO_GPU_DATA_TOTAL_INPUT_SIZE];
    currentData->totalTransformSize +=  GPUData[NENGO_GPU_DATA_TOTAL_INPUT_SIZE] * GPUData[NENGO_GPU_DATA_DIMENSION];
    currentData->totalNumTransformRows += GPUData[NENGO_GPU_DATA_NUM_TERMINATIONS] * GPUData[NENGO_GPU_DATA_DIMENSION];

    if(GPUData[NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION] > currentData->maxTransformDimension)
    {
      currentData->maxTransformDimension = GPUData[NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION];
    }

    currentData->totalEncoderSize += GPUData[NENGO_GPU_DATA_NUM_NEURONS] * GPUData[NENGO_GPU_DATA_DIMENSION];

    currentData->numOrigins += GPUData[NENGO_GPU_DATA_NUM_ORIGINS];
    currentData->totalOutputSize += GPUData[NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE];

    currentData->totalDecoderSize += GPUData[NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE] * GPUData[NENGO_GPU_DATA_NUM_NEURONS];

    
    if(GPUData[NENGO_GPU_DATA_MAX_DECODER_DIMENSION] > currentData->maxDecoderDimension)
    {
      currentData->maxDecoderDimension = GPUData[NENGO_GPU_DATA_MAX_DECODER_DIMENSION];
    }

    currentData->totalDimension += GPUData[NENGO_GPU_DATA_DIMENSION];

    if(GPUData[NENGO_GPU_DATA_DIMENSION] > currentData->maxDimension)
    {
      currentData->maxDimension = GPUData[NENGO_GPU_DATA_DIMENSION];
    }

    if(GPUData[NENGO_GPU_DATA_NUM_NEURONS] > currentData->maxNumNeurons)
    {
      currentData->maxNumNeurons = GPUData[NENGO_GPU_DATA_NUM_NEURONS];
    }

    currentData->totalNumDecoderRows += GPUData[NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE]; 
  }

  free(GPUData);

  jobjectArray currentTransforms_JAVA, currentEncoder_JAVA;
  int currentNumTerminations, currentEnsembleDimension = 0, currentTerminationDimension = 0, transformRowIndex, terminationIndex, transformElementIndex, currentNumNeurons;
  int ensembleIndex, dimensionIndex, neuronIndex, encoderIndex;
  int k = 0, l = 0, length;
  float* temp_transforms, *temp_encoders, *neuronData;

  // Now we start to load the data into the NengoGPUData objects for each device
  // Because of the CUDA archetecture, we have to do some really weird things to get a good speedup. These arrays that store the transforms, decoders, are setup
  // in a non-intuitive way so that memory accesses can be parallelized in CUDA kernels
  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoData[i];

    initializeNengoGPUData(currentData);
    
    terminationIndex = 0;
    transformRowIndex = 0;
    transformElementIndex = 0;
    ensembleIndex = 0;
    dimensionIndex = 0;
    neuronIndex = 0;
    encoderIndex = 0;
  
    temp_transforms = (float*) malloc(currentData->totalTransformSize * sizeof(float));
    temp_encoders = (float*) malloc(currentData->totalEncoderSize * sizeof(float));

    if(!temp_transforms || !temp_encoders)
    {
      printf("bad malloc\n");
      exit(EXIT_FAILURE);
    }

    for(j = 0; j < totalNumEnsembles; j++)
    {
      if( deviceForEnsemble[j] == i)
      {
        currentData->collectSpikes[ensembleIndex] = collectSpikes[j];
        currentData->ensemblePositionsInTerminationValues[ensembleIndex] = transformRowIndex;

        currentTransforms_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, terminationTransforms_JAVA, j);
        currentNumTerminations = (int) (*env)->GetArrayLength(env, currentTransforms_JAVA);

        tempFloatArray_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, terminationTau_JAVA, j);
        (*env)->GetFloatArrayRegion(env, tempFloatArray_JAVA, 0, currentNumTerminations, currentData->terminationTau + terminationIndex);

        for(k = 0; k < currentNumTerminations; k++)
        {
          tempObjectArray_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, currentTransforms_JAVA, k);
          currentEnsembleDimension = (int) (*env)->GetArrayLength(env, tempObjectArray_JAVA);

          for(l = 0; l < currentEnsembleDimension; l++)
          {
            tempFloatArray_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, tempObjectArray_JAVA, l);
            currentTerminationDimension = (int)(*env)->GetArrayLength(env, tempFloatArray_JAVA);
            
            (*env)->GetFloatArrayRegion(env, tempFloatArray_JAVA, 0, currentTerminationDimension, temp_transforms + transformElementIndex); 

            transformElementIndex += currentTerminationDimension;

            currentData->terminationValues[transformRowIndex] = 0;
            currentData->terminationRowToTerminationIndexor[transformRowIndex] = terminationIndex;
            transformRowIndex++;
          }
        
          currentData->terminationDimensionsHost[terminationIndex] = currentTerminationDimension;
          terminationIndex++;
        }

        currentData->ensembleTerminations[ensembleIndex] = currentNumTerminations;
        currentData->ensembleDimensions[ensembleIndex] = currentEnsembleDimension;

        for(k = 0; k < currentEnsembleDimension; k++)
        {
          currentData->dimensionIndexInEnsemble[dimensionIndex] = k;
          currentData->dimensionToEnsembleIndexor[dimensionIndex] = ensembleIndex;
          dimensionIndex++;
        }


        tempFloatArray_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, neuronData_JAVA, j);
        length = (*env)->GetArrayLength(env, tempFloatArray_JAVA);
        neuronData = (float*)malloc(length * sizeof(float));
        if(!neuronData)
        {
          printf("bad malloc\n");
          exit(EXIT_FAILURE);
        }

        (*env)->GetFloatArrayRegion(env, tempFloatArray_JAVA, 0, length, neuronData);

        currentNumNeurons = neuronData[0];
        currentData->ensembleNeuronsHost[ensembleIndex] = currentNumNeurons;
        currentData->ensembleTauRC[ensembleIndex] = neuronData[1];
        currentData->ensembleTauRef[ensembleIndex] = neuronData[2];

        for(k = 0; k < currentNumNeurons; k++)
        {
          currentData->neuronVoltage[neuronIndex] = 0;
          currentData->neuronReftime[neuronIndex] = 0;
          currentData->neuronBias[neuronIndex] = neuronData[4 + k];
          currentData->neuronScale[neuronIndex] = neuronData[4 + currentNumNeurons + k];
          currentData->neuronToEnsembleIndexor[neuronIndex] = ensembleIndex;

          neuronIndex++;
        }

        free(neuronData);

        currentEncoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, encoders_JAVA, j);
        
        for(k = 0; k < currentNumNeurons; k++)
        {
          tempFloatArray_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentEncoder_JAVA, k);
          (*env)->GetFloatArrayRegion(env, tempFloatArray_JAVA, 0, currentEnsembleDimension, temp_encoders + encoderIndex);
          encoderIndex += currentEnsembleDimension;
        }

        ensembleIndex++;
      }
    }

    getDecoders(env, currentData, deviceForEnsemble, decoders_JAVA);

    // transforms
    createIndexArrayAndGatherData(1, currentData->terminationTransforms, temp_transforms, currentData->terminationTransformIndices, currentData->terminationDimensionsHost, currentData->terminationRowToTerminationIndexor, currentData->maxTransformDimension, currentData->totalNumTransformRows);
    free(temp_transforms);

    //input
    createIndexArrayAndGatherData(0, NULL, NULL, currentData->inputIndices, currentData->terminationDimensionsHost, NULL, currentData->maxTransformDimension, currentData->numTerminations);

    //sums
    createIndexArrayAndGatherData(0, NULL, NULL, currentData->sumIndices, currentData->ensembleDimensions, NULL, currentData->maxDimension, currentData->numEnsembles);
    
    //encoders
    createIndexArrayAndGatherData(1, currentData->encoders, temp_encoders, currentData->encoderIndices, currentData->ensembleDimensions, currentData->neuronToEnsembleIndexor, currentData->maxDimension, currentData->numNeurons);
    free(temp_encoders);

    //spikes
    createIndexArrayAndGatherData(0, NULL, NULL, currentData->spikeIndicesHost, currentData->ensembleNeuronsHost, NULL, currentData->maxNumNeurons, currentData->numEnsembles);

    //decoders
    createIndexArrayAndGatherData(0, NULL, NULL, currentData->decoderIndices, currentData->ensembleNeuronsHost, currentData->decoderRowToEnsembleIndexor, currentData->maxNumNeurons, currentData->totalNumDecoderRows);

    //sumHelper
    createHelper(currentData->sumHelper, currentData->ensembleDimensions, currentData->maxDimension, currentData->numEnsembles); 

    //spikeHelper
    createHelper(currentData->spikeHelper, currentData->ensembleNeuronsHost, currentData->maxNumNeurons, currentData->numEnsembles); 

    //printNengoGPUData(currentData);
  } 

  free(collectSpikes);

  run_start();
}



JNIEXPORT void JNICALL Java_ca_nengo_util_impl_GPUThread_nativeStep
  (JNIEnv *env, jclass class, jobjectArray input, jobjectArray output, jobjectArray spikes, jfloat startTime_JAVA, jfloat endTime_JAVA)
{
  startTime = (float) startTime_JAVA;
  endTime = (float) endTime_JAVA;

  jobjectArray currentInputs_JAVA;
  jfloatArray currentInput_JAVA;
  float* temp_input;
  
  NengoGPUData* currentData;

  int i, j, k;
  int ensembleIndex, inputIndex, numInputs, inputDimension, offset;

  for( i = 0; i < numDevices; i++)
  {
    currentData = nengoData[i];
    temp_input = (float*)malloc(currentData->totalInputSize * sizeof(float));
    if(!temp_input)
    {
      printf("bad malloc\n");
      exit(EXIT_FAILURE);
    }

    ensembleIndex = 0;
    inputIndex = 0;
    // get the input data from the java array
    for( j = 0; j < totalNumEnsembles; j++)
    {
      if(deviceForEnsemble[j] == i)
      {
        currentInputs_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, input, j);
        numInputs = (*env)->GetArrayLength(env, currentInputs_JAVA);

        for(k = 0; k < numInputs; k++)
        {
          currentInput_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, currentInputs_JAVA, k);
          inputDimension = (*env)->GetArrayLength(env, currentInput_JAVA);

          (*env)->GetFloatArrayRegion(env, currentInput_JAVA, 0, inputDimension, temp_input + inputIndex);
          inputIndex += inputDimension;
        }

        ensembleIndex++;
      }
    }

    // put the input data in the proper form
    inputIndex = 0;
    for( j = 0; j < currentData->maxTransformDimension; j++)
    {
      offset = 0;

      for( k = 0; k < currentData->numTerminations; k++)
      {
        inputDimension = currentData->terminationDimensionsHost[k];

        if(inputDimension > j)
        {
          currentData->input[inputIndex] = temp_input[offset + j];
          inputIndex++;
        }

        offset += inputDimension;
      }
    }

    free(temp_input);
  }
     
  // tell the runner threads to run and then wait for them to finish. One of them will wake this thread up once they are all finished
  pthread_mutex_lock(mutex);
  myCVsignal = 1;
  pthread_cond_broadcast(cv_GPUThreads);
  pthread_cond_wait(cv_JNI, mutex);
  pthread_mutex_unlock(mutex);

  jobjectArray currentOutputs_JAVA;
  jfloatArray currentOutput_JAVA;
  jfloatArray currentSpikes_JAVA;
  float* temp_spikes;

  int outputIndex, numOutputs, outputDimension;
  int spikeIndex, numNeurons, spikeEnsembleIndex = 0;

  for(i = 0; i < numDevices; i++)
  {
    currentData = nengoData[i];

    ensembleIndex = 0;
    outputIndex = 0;

    for( j = 0; j < totalNumEnsembles; j++)
    {
      if(deviceForEnsemble[j] == i)
      {
        currentOutputs_JAVA = (jobjectArray)(*env)->GetObjectArrayElement(env, output, j);
        numOutputs = (*env)->GetArrayLength(env, currentOutputs_JAVA); 

        for(k = 0; k < numOutputs; k++)
        {
          currentOutput_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentOutputs_JAVA, k);
          outputDimension = (*env)->GetArrayLength(env, currentOutput_JAVA);

          (*env)->SetFloatArrayRegion(env, currentOutput_JAVA, 0, outputDimension, currentData->output + outputIndex);
          outputIndex += outputDimension;
        }

        if(currentData->collectSpikes[j])
        {
          numNeurons = currentData->ensembleNeuronsHost[ensembleIndex];
          spikeIndex = ensembleIndex;

          temp_spikes = (float*)malloc(numNeurons * sizeof(float));
          if(!temp_spikes)
          {
            printf("bad malloc\n");
            exit(EXIT_FAILURE);
          }

          for(k = 0 ; k < numNeurons; k++)
          {
            temp_spikes[k] = currentData->spikesHost[spikeIndex];
            spikeIndex = currentData->spikeIndicesHost[spikeIndex];
          }

          currentSpikes_JAVA = (*env)->GetObjectArrayElement(env, spikes, spikeEnsembleIndex);
          (*env)->SetFloatArrayRegion(env, currentSpikes_JAVA, 0, numNeurons, temp_spikes);

          free(temp_spikes);
          spikeEnsembleIndex++;
        }

        ensembleIndex++;
      }
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
