
#ifdef __cplusplus
extern "C"{
#endif

#include <stdlib.h>
#include <jni.h>
#include <pthread.h>
#include <string.h>
#include <limits.h>
#include <math.h>
#include <sys/timeb.h>
#include <assert.h>

#include "NEFGPUInterface_JNI.h"
#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"
#include "NengoGPUData.h"
#include "GraphTheoryRoutines.h"


// returns not the sort array but the indices of the values in the sorted order. So newOrder[0] is the index of 
// the largest element in values, newOrder[1] is the index of the second largest, etc.
int* sort(int* values, int length, int order)
{
  int* newOrder = (int*) malloc( length * sizeof(int));
  int* scratch = (int*) malloc( length * sizeof(int));
  memset(scratch, '\0', length * sizeof(int));

  int i, j;

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


int* partitionNetwork(int* adjacencyMatrix, int numNeurons, int* ensembleNumNeurons, int numEnsembles)
{
  if(numEnsembles == 0)
    return NULL;

  int* partitionArray = NULL;

  if(numEnsembles == 1)
    partitionArray = (int*)malloc(numEnsembles * sizeof(int));
    partitionArray[0] = 0;
    return partitionArray;

  if(numEnsembles == 2)
    partitionArray = (int*)malloc(numEnsembles * sizeof(int));
    partitionArray[0] = 0;
    partitionArray[1] = 1;
    return partitionArray;

  Graph* G = convertAdjacencyMatrixToGraph(adjacencyMatrix, numEnsembles);  

  int *cutValues, **cutPartition, i, j;

  stoerWagner_allPairsMinCut(G, &cutValues, &cutPartition);

  int* order = sort(cutValues, numEnsembles-1, 0);

  int num_neurons_set_one, num_neurons_set_two, index;
  
  i = 0;
  do
  {
    index = order[i];

    num_neurons_set_one = num_neurons_set_two = 0;
    for(j = 0; j < numEnsembles; j++)
    {
      if(cutPartition[index][j])
      {
        num_neurons_set_one += ensembleNumNeurons[j];
      }
      else
      {
        num_neurons_set_two += ensembleNumNeurons[j];
      }
    }
    
    //printf("num neurons set one : %d, num neurons set 2: %d, cut val: %d\n", num_neurons_set_one, num_neurons_set_two, cutValues[index]);

    i++;
  }while((num_neurons_set_one < numNeurons / 4 || num_neurons_set_two < numNeurons / 4) && i < numEnsembles-1);

  free(order);

  // if we failed to find a cut with a good balance, we just take the minimum cut, whatever its balance is
  int chosen_index = (i == numEnsembles-1) ? 0 : index;

/*
  for(i = 0; i < numEnsembles; i++)
  {
    printf("%d, ", cutPartition[chosen_index][j]);
  }

  //printf("\nset one neurons: %d, set two neurons: %d, cut value: %d\n", set_one_neurons, set_two_neurons, cutValues[i]);
  */

  partitionArray = (int*)malloc(numEnsembles * sizeof(int));

  memcpy(partitionArray, cutPartition[chosen_index], numEnsembles * sizeof(int));

  free_stoerWagnerResults(cutValues, cutPartition, numEnsembles);
  free_graph(G);

  return partitionArray;
}


int* createSubgraphAdjacencyMatrix(int* originalAdjacencyMatrix, int originalSize, int newSize, int* partition, int flag)
{
  int* newAdjacency = (int*)malloc(newSize * newSize * sizeof(int));
  memset(newAdjacency, '\0', newSize * newSize * sizeof(int));
  int p = 0, q = 0, i, j;

  for(i = 0; i < originalSize; i++)
  {
    if(partition[i] == flag)
    {
      q = 0;
      for(j = 0; j < i; j++)
      {
        if(partition[j] == flag)
        {
          newAdjacency[p * newSize + q] = originalAdjacencyMatrix[i * originalSize + j]; 
          q++;
        }
      }

      p++;
    }
  }

  return newAdjacency;
}

// Assign ensembles and projections to the devices. We employ a simple algorithm to limit the data being passed between devices.
// Basically, choose an arbitary first ensemble, assign it to the first device, then perform breadth first search, assigning to
// the first device until we run out of space on that device. 
void generateNengoGPUDeviceConfiguration(int totalNumNeurons, int* numNeurons, int numProjections, projection* projections, int* adjacencyMatrix, int* deviceForEnsemble)
{
  int i, j;
  memset(deviceForEnsemble, '\0', totalNumEnsembles * sizeof(int));

  if(numDevices > 1)
  {
    // partition the network. Uses a mincut-finding algorithm to ensure as little communication takes place between GPUs as possible
    int* partition = partitionNetwork(adjacencyMatrix, totalNumNeurons, numNeurons, totalNumEnsembles);

    // find number of ensembles on each side of partition
    int set_one_ensembles = 0, set_two_ensembles = 0;
    for(i = 0; i < totalNumEnsembles; i++)
    {
      partition[i] ? set_one_ensembles++ : set_two_ensembles++;
    }

    memcpy(deviceForEnsemble, partition, totalNumEnsembles * sizeof(int));

    int cutValue = 0;
    for(i = 0; i < totalNumEnsembles; i++)
    {
      for(j = 0; j < i; j++)
      {
        if(partition[i] != partition[j])
          cutValue += adjacencyMatrix[i * totalNumEnsembles + j];
      }
    }

    // partition one of the partitions
    if(numDevices > 2)
    {
      // create an adjacency matrix containing only vertices in one of the partitions we made earlier
      int* newAdjacency = createSubgraphAdjacencyMatrix(adjacencyMatrix, totalNumEnsembles, set_one_ensembles, partition, 1);

      int* numNeurons_set_one = (int*)malloc(set_one_ensembles * sizeof(int));
      int totalNumNeurons_set_one = 0;

      // create the numNeurons array for the current subgraph
      j = 0;
      for(i = 0; i < totalNumEnsembles; i++)
      {
        if(partition[i])
        {
          numNeurons_set_one[j++] = numNeurons[i];
          totalNumNeurons_set_one += numNeurons[i];
        }
      }

      // partition the subgraph via a minimum cut
      int* partition_two = partitionNetwork(newAdjacency, totalNumNeurons_set_one, numNeurons_set_one, set_one_ensembles);
      
      free(numNeurons_set_one);
      free(newAdjacency);

      // assign ensembles that fall on different sides of the subgraph partition to different devices
      j = 0;
      for(i = 0; i < totalNumEnsembles; i++)
      {
        if(partition[i])
        {
          if(partition_two[j])
          {
            deviceForEnsemble[i] = 1;
          }
          else
          {
            deviceForEnsemble[i] = 2;
          }

          j++;
        }
      }
      
      free(partition_two);
    }

    // we do the same as above except we work on the other subgraph created by the partition
    if(numDevices > 3)
    {
      int* newAdjacency = createSubgraphAdjacencyMatrix(adjacencyMatrix, totalNumEnsembles, set_two_ensembles, partition, 0);

      int* numNeurons_set_two = (int*)malloc(set_two_ensembles * sizeof(int));
      int totalNumNeurons_set_two = 0;

      j = 0;
      for(i = 0; i < totalNumEnsembles; i++)
      {
        if(!partition[i])
        {
          numNeurons_set_two[j++] = numNeurons[i];
          totalNumNeurons_set_two += numNeurons[i];
        }
      }

      int* partition_three = partitionNetwork(newAdjacency, totalNumNeurons_set_two, numNeurons_set_two, set_two_ensembles);

      free(numNeurons_set_two);
      free(newAdjacency);

      j = 0;
      for(i = 0; i < totalNumEnsembles; i++)
      {
        if(!partition[i])
        {
          if(partition_three[j])
          {
            deviceForEnsemble[i] = 0;
          }
          else
          {
            deviceForEnsemble[i] = 3;
          }

          j++;
        }
      }

      free(partition_three);
    }

    free(partition);
  }

  printf("device for ensemble:\n");
  for(i = 0; i < totalNumEnsembles; i++)
  {
    printf("%d ", deviceForEnsemble[i]);
  }
  printf("\n");

  int originNodeIndex, termNodeIndex;
  for(i = 0; i < numProjections; i++)
  {
    originNodeIndex = projections[i].sourceEnsemble;
    termNodeIndex = projections[i].destinationEnsemble;

    projections[i].sourceDevice = deviceForEnsemble[originNodeIndex];
    projections[i].destDevice = deviceForEnsemble[termNodeIndex];
  }
}

void adjustProjections(int numProjections, projection* projections, int* ensembleJavaIndexToDeviceIndex)
{
  int i;
  projection* p;
  for(i = 0; i < numProjections; i++)
  {
    p = (projections + i);

    p->sourceEnsemble = ensembleJavaIndexToDeviceIndex[p->sourceEnsemble];
    p->destinationEnsemble = ensembleJavaIndexToDeviceIndex[p->destinationEnsemble];
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
  
  int ensembleIndex = 0;
  int NDterminationIndex = 0;
  int terminationIndex = 0;
  int transformRowIndex = 0;
  int dimensionIndex = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleIndexInJavaArray, i);

    intArraySetElement(currentData->NDterminationEnsembleOffset, i, NDterminationIndex);

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

    // get the array that says whether a termination is decoded for the current ensemble
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

        intArraySetElement(currentData->terminationDimension, terminationIndex, dimensionOfCurrentTermination);
      }
      else
      {
        // for non decoded terminations
        currentTransform_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, transformsForCurrentEnsemble_JAVA, j);
        currentTransformRow_JAVA = (jfloatArray)(*env)->GetObjectArrayElement(env, currentTransform_JAVA, 0);

        (*env)->GetFloatArrayRegion(env, currentTransformRow_JAVA, 0, 1, currentTransformRow);

        floatArraySetElement(currentData->NDterminationWeights, NDterminationIndex, currentTransformRow[0]);
        intArraySetElement(currentData->NDterminationInputIndexor, NDterminationIndex, terminationIndex);
        intArraySetElement(currentData->terminationDimension, terminationIndex, 1);

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

  float* temp_encoders = (float*) malloc(currentData->totalEncoderSize * sizeof(float));
  int* temp_encoder_offset = (int*)malloc(currentData->numEnsembles * sizeof(int));

  int offset = 0;
  // totalNumEnsembles is a global variable denoting the number of ensembles in the entire run, not just those allocated to this device
  // here we get encoders for this device out of encoders java array, but they're not in the order we want
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
  int* temp = sort(currentData->ensembleDimension->array, currentData->numEnsembles, 1);
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

    neuronOffset += numNeuronsForCurrentEnsemble;
  }

  free(temp_encoder_offset);
  free(temp_encoders);



  // construct array encoderRowToEnsembleIndexor which maintains, for each row encoder row, which ensemble it belongs to
  int encoderRowIndex = 0, ensembleOffsetInNeurons;
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

void storeDecoders(JNIEnv* env, jobjectArray decoders_JAVA, NengoGPUData* currentData, int* deviceForEnsemble)
{
  int i, j, k;

  jobjectArray decodersForCurrentEnsemble_JAVA;
  jobjectArray currentDecoder_JAVA;
  jfloatArray currentDecoderRow_JAVA;
  int decoderIndex = 0, dimensionOfCurrentDecoder, numDecodersForCurrentEnsemble, ensembleIndexInJavaArray;

  int* ensembleOutputSize = (int*)malloc(currentData->numEnsembles * sizeof(int)); 
  int* ensembleOffsetInOutput = (int*)malloc(currentData->numEnsembles * sizeof(int)); 

  // populate currentData->originOffsetInOutput and ensembleOutputSize
  int outputSize, originOffsetInOutput = 0;
  decoderIndex = 0;
  for(j = 0; j < currentData->numEnsembles; j++)
  {
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray,j);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (int) (*env)->GetArrayLength(env, decodersForCurrentEnsemble_JAVA);

    outputSize = 0;

    ensembleOffsetInOutput[j] = originOffsetInOutput;

    for(k = 0; k < numDecodersForCurrentEnsemble; k++)
    {
      currentDecoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decodersForCurrentEnsemble_JAVA, k);
      currentDecoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentDecoder_JAVA, 0);

      dimensionOfCurrentDecoder = (int) (*env)->GetArrayLength(env, currentDecoderRow_JAVA);
      intArraySetElement(currentData->originDimension,decoderIndex, dimensionOfCurrentDecoder);

      outputSize += dimensionOfCurrentDecoder;

      intArraySetElement(currentData->originOffsetInOutput, decoderIndex, originOffsetInOutput);
      originOffsetInOutput += dimensionOfCurrentDecoder;
      decoderIndex++;
    }

    ensembleOutputSize[j] = outputSize;
  }

  // populate decoder stride
  int numOutputs;
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
  int* temp = sort(currentData->ensembleNumNeurons->array, currentData->numEnsembles, 1);
  intArraySetData(currentData->ensembleOrderInDecoders, temp, currentData->numEnsembles);
  free(temp);



  int ensembleIndex = 0;
  int numNeuronsForCurrentEnsemble;
  int decoderRowIndex = 0;
  int offset = 0;
  int rowOffset = 0;

  for(i = 0; i < currentData->numEnsembles; i++)
  {
    ensembleIndex = intArrayGetElement(currentData->ensembleOrderInDecoders,i);
    ensembleIndexInJavaArray = intArrayGetElement(currentData->ensembleIndexInJavaArray, ensembleIndex);

    numNeuronsForCurrentEnsemble = intArrayGetElement(currentData->ensembleNumNeurons,ensembleIndex);

    decodersForCurrentEnsemble_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decoders_JAVA, ensembleIndexInJavaArray);
    numDecodersForCurrentEnsemble = (int) (*env)->GetArrayLength(env, decodersForCurrentEnsemble_JAVA);

    intArraySetElement(currentData->ensembleNumOrigins, ensembleIndex, numDecodersForCurrentEnsemble);

    rowOffset = 0;

    for(j = 0; j < numNeuronsForCurrentEnsemble; j++)
    {
      decoderRowIndex = offset;
      for(k = 0; k < numDecodersForCurrentEnsemble; k++)
      {
        currentDecoder_JAVA = (jobjectArray) (*env)->GetObjectArrayElement(env, decodersForCurrentEnsemble_JAVA, k);
        currentDecoderRow_JAVA = (jfloatArray) (*env)->GetObjectArrayElement(env, currentDecoder_JAVA, j);
        
        dimensionOfCurrentDecoder = (*env)->GetArrayLength(env, currentDecoderRow_JAVA);

        if(rowOffset + decoderRowIndex + dimensionOfCurrentDecoder <= currentData->totalDecoderSize)
        {
          (*env)->GetFloatArrayRegion(env, currentDecoderRow_JAVA, 0, dimensionOfCurrentDecoder, currentData->decoders->array + rowOffset + decoderRowIndex);
        }
        else
        {
          printf("error: accessing array out of bounds: decoders\n");
          exit(EXIT_FAILURE);
        }

        decoderRowIndex += dimensionOfCurrentDecoder;
      }

      rowOffset += intArrayGetElement(currentData->decoderStride,j);
    }
    
    offset = decoderRowIndex;
  }


  int offsetInOutput;
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


void assignEnsembleToDevice(int* ensembleData, NengoGPUData* currentData)
{
  currentData->numEnsembles++;
  currentData->numNeurons += ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS];

  currentData->numDecodedTerminations += ensembleData[NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS];
  currentData->totalInputSize += ensembleData[NENGO_ENSEMBLE_DATA_TOTAL_INPUT_SIZE];
  currentData->totalNumTransformRows += ensembleData[NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS] * ensembleData[NENGO_ENSEMBLE_DATA_DIMENSION];
  
  if(ensembleData[NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS] > currentData->maxNumDecodedTerminations)
  {
    currentData->maxNumDecodedTerminations = ensembleData[NENGO_ENSEMBLE_DATA_NUM_DECODED_TERMINATIONS];
  }

  if(ensembleData[NENGO_ENSEMBLE_DATA_MAX_TRANSFORM_DIMENSION] > currentData->maxDecodedTerminationDimension)
  {
    currentData->maxDecodedTerminationDimension = ensembleData[NENGO_ENSEMBLE_DATA_MAX_TRANSFORM_DIMENSION];
  }
  
  currentData->totalEncoderSize += ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS] * ensembleData[NENGO_ENSEMBLE_DATA_DIMENSION];

  currentData->numOrigins += ensembleData[NENGO_ENSEMBLE_DATA_NUM_ORIGINS];
  currentData->totalOutputSize += ensembleData[NENGO_ENSEMBLE_DATA_TOTAL_OUTPUT_SIZE];
  currentData->totalDecoderSize += ensembleData[NENGO_ENSEMBLE_DATA_TOTAL_OUTPUT_SIZE] * ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS];
  
  if(ensembleData[NENGO_ENSEMBLE_DATA_MAX_DECODER_DIMENSION] > currentData->maxOriginDimension)
  {
    currentData->maxOriginDimension = ensembleData[NENGO_ENSEMBLE_DATA_MAX_DECODER_DIMENSION];
  }

  currentData->totalEnsembleDimension += ensembleData[NENGO_ENSEMBLE_DATA_DIMENSION];

  if(ensembleData[NENGO_ENSEMBLE_DATA_DIMENSION] > currentData->maxDimension)
  {
    currentData->maxDimension = ensembleData[NENGO_ENSEMBLE_DATA_DIMENSION];
  }

  if(ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS] > currentData->maxNumNeurons)
  {
    currentData->maxNumNeurons = ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS];
  }
  
  currentData->numNDterminations += ensembleData[NENGO_ENSEMBLE_DATA_NUM_NON_DECODED_TERMINATIONS];
}

void setupInput(int numProjections, projection* projections, NengoGPUData* currentData)
{
  int i, j;
  int currentDimension, ensembleIndex = -1, terminationIndexInEnsemble = -1;
  int projectionMatches, currentNumTerminations = 0; 

  int* flattenedProjections = (int*) malloc(2 * numProjections * sizeof(int));

  int JavaInputIndex = 0, GPUInputIndex = 0, CPUInputIndex = 0;
  
  // 0 = GPU, 1 = Java, 2 = CPU
  int* terminationLocation = (int*)malloc(currentData->numTerminations * sizeof(int));
  // flatten the termination side of the projection array
  for(i = 0; i < currentData->numTerminations; i++)
  {
    terminationIndexInEnsemble++;
    while(terminationIndexInEnsemble == currentNumTerminations)
    {
      ensembleIndex++;
      terminationIndexInEnsemble = 0;
      currentNumTerminations = intArrayGetElement(currentData->ensembleNumTerminations, ensembleIndex);
    }

    currentDimension = intArrayGetElement(currentData->terminationDimension, i);

    // find a GPU projection that terminates at the current termination. We can stop as soon as we find
    // one because terminations can only be involved in one projection
    j = 0;
    projectionMatches = 0;
    while(!projectionMatches && j < numProjections)
    {
      projectionMatches = projections[j].destDevice == currentData->device
                    && projections[j].destinationEnsemble == ensembleIndex
                    && projections[j].destinationTermination == terminationIndexInEnsemble;

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

  // find the size of input that stays on the gpu
  currentData->GPUInputSize = GPUInputIndex;
  currentData->CPUInputSize = CPUInputIndex;
  currentData->JavaInputSize = JavaInputIndex;

  assert(GPUInputIndex + CPUInputIndex + JavaInputIndex == currentData->totalInputSize);

  // adjust the terminationOffsets to reflect the location of each termination (GPU, Java or CPU)
  // can only be done once we know the size of each location
  int oldVal, location;
  for(i = 0; i < currentData->numTerminations; i++)
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


  /*
  These are arrays whose size relies on GPUInputSize, CPUInputSize or JavaInput size, and thus cannot be made until we have those
  values. Because of this, we cannot create these arrays in the function initializeNengoGPUData like we do with all the other arrays
  */
  char* name = "GPUTerminationToOriginMap";
  currentData->GPUTerminationToOriginMap = newIntArray(currentData->GPUInputSize, name);

  name = "inputHost";
  currentData->inputHost = newFloatArray(currentData->JavaInputSize, name);

  
  // flatten the origin side of the projection array. This is slightly different than flattening the termination side
  // because any one termination can only be involved in one projection whereas any origin can be involved in any number of projections.
  // Effectively this means we have to scan the entire projection array for each origin, we can't stop as soon as we find one.
  ensembleIndex = -1;
  int originIndexInEnsemble = -1, currentNumOrigins = 0, CPUOutputSize = 0;

  for(i = 0; i < currentData->numOrigins; i++)
  {
    originIndexInEnsemble++;

    if(originIndexInEnsemble == currentNumOrigins)
    {
      ensembleIndex++;
      originIndexInEnsemble = 0;
      currentNumOrigins = intArrayGetElement(currentData->ensembleNumOrigins, ensembleIndex);
    }

    for(j = 0; j < numProjections; j++)
    {
      projectionMatches = projections[j].sourceDevice == currentData->device
                      && projections[j].sourceEnsemble == ensembleIndex
                      && projections[j].sourceOrigin == originIndexInEnsemble;

      if(projectionMatches)
      {
        flattenedProjections[2 * j + 1] = i;

        if(projections[j].destDevice != currentData->device)
        {
          CPUOutputSize += projections[j].size;
        }
      }
    }
  }

  currentData->CPUOutputSize = CPUOutputSize;

  name = "sharedData_outputIndex";
  currentData->sharedData_outputIndex = newIntArray(currentData->CPUOutputSize, name);

  name = "sharedData_sharedIndex";
  currentData->sharedData_sharedIndex = newIntArray(currentData->CPUOutputSize, name);

  // Use the flattened projections to create a map from the input to the output following the projections
  // This way we can launch a kernel for each projection on the GPU, have it look up where it gets its output from
  // fetch it from the output array and put it in the input array
  int terminationIndexOnDevice, originIndexOnDevice, terminationOffsetInInput, originOffsetInOutput;
  for(i = 0; i < numProjections; i++)
  {
    if(projections[i].sourceDevice == currentData->device && projections[i].destDevice == currentData->device)
    {
      terminationIndexOnDevice = flattenedProjections[i * 2];
      originIndexOnDevice = flattenedProjections[i * 2 + 1];

      terminationOffsetInInput = intArrayGetElement(currentData->terminationOffsetInInput, terminationIndexOnDevice);
      originOffsetInOutput = intArrayGetElement(currentData->originOffsetInOutput, originIndexOnDevice);
      currentDimension = intArrayGetElement(currentData->terminationDimension, terminationIndexOnDevice);

      for(j = 0; j < currentDimension; j++)
      {
        intArraySetElement(currentData->GPUTerminationToOriginMap, terminationOffsetInInput + j, originOffsetInOutput + j);
      }
    }
    else if(projections[i].sourceDevice == currentData->device && projections[i].destDevice != currentData->device)
    {
      originIndexOnDevice = flattenedProjections[i * 2 + 1];
      projections[i].offsetInSource = intArrayGetElement(currentData->originOffsetInOutput, originIndexOnDevice);
    }
    else if(projections[i].destDevice == currentData->device && projections[i].sourceDevice != currentData->device)
    {
      terminationIndexOnDevice = flattenedProjections[i * 2];
      projections[i].offsetInDestination = intArrayGetElement(currentData->terminationOffsetInInput, terminationIndexOnDevice) - currentData->GPUInputSize - currentData->JavaInputSize + currentData->offsetInSharedInput;
    }
  }

  free(flattenedProjections);
}

void createSharedMemoryMaps(int numProjections, projection* projections)
{
  int i, j, sharedIndex = 0, indexInProjection;
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

JNIEXPORT jboolean JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_hasGPU
  (JNIEnv *env, jclass class)
{
  jboolean hasGPU = (jboolean) (getGPUDeviceCount() > 0);

  return hasGPU;
}

// This function takes as arguments all the information required by the ensembles to run that won't change from step to step: decoders, encoders, transformations.
// This is called only once, at the beginning of a run (specifically, when the GPUNodeThreadPool is created). 
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeSetupRun
  (JNIEnv *env, jclass class, jobjectArray terminationTransforms_JAVA, jobjectArray isDecodedTermination_JAVA, 
  jobjectArray terminationTau_JAVA, jobjectArray encoders_JAVA, jobjectArray decoders_JAVA, 
  jobjectArray neuronData_JAVA, jobjectArray projections_JAVA, jobjectArray ensembleData_JAVA, 
  jobjectArray adjacencyMatrix_JAVA, jfloat maxTimeStep, jint numDevicesRequested)
{
  printf("NengoGPU: SETUP\n");
  int i, j, k;

  
  nengoDataArray = (NengoGPUData**) malloc(sizeof(NengoGPUData*) * numDevices);

  totalNumEnsembles = (int) (*env)->GetArrayLength(env, neuronData_JAVA);

  int numAvailableDevices = getGPUDeviceCount();
  
  // make sure the num devices we use isn't bigger than the number of devices available or the number of ensembles we are processing
  numDevices = numAvailableDevices < numDevicesRequested ? numAvailableDevices : numDevicesRequested;
  numDevices = totalNumEnsembles < numDevices ? totalNumEnsembles : numDevicesRequested;
  printf("Using %d devices. %d available\n", numDevices, numAvailableDevices);

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

  // store the adjacency matrix in a c array
  int* adjacencyMatrix = (int*) malloc(totalNumEnsembles * totalNumEnsembles * sizeof(int)); 
  for(i = 0; i < totalNumEnsembles; i++)
  {
    tempIntArray_JAVA = (jintArray)(*env)->GetObjectArrayElement(env, adjacencyMatrix_JAVA, i);
    (*env)->GetIntArrayRegion(env, tempIntArray_JAVA, 0, totalNumEnsembles, adjacencyMatrix + i * totalNumEnsembles);
  }

  // create an array of the number of neurons in each ensemble, used only in generating the device config
  int* ensembleNumNeurons = (int*)malloc(totalNumEnsembles * sizeof(int));
  int* ensembleData = (int*)malloc(NENGO_ENSEMBLE_DATA_NUM * sizeof(int));
  int totalNumNeurons = 0, numNeurons;
  jintArray ensembleDataRow_JAVA;

  for(i = 0; i < totalNumEnsembles; i++)
  {
    ensembleDataRow_JAVA = (jintArray) (*env)->GetObjectArrayElement(env, ensembleData_JAVA, i);
    (*env)->GetIntArrayRegion(env, ensembleDataRow_JAVA, 0, NENGO_ENSEMBLE_DATA_NUM, ensembleData);
    
    numNeurons = ensembleData[NENGO_ENSEMBLE_DATA_NUM_NEURONS];
    ensembleNumNeurons[i] = numNeurons;
    totalNumNeurons += numNeurons;
  }
  
  // The distributes the ensembles to the devices. Right now it tries to maximize the number of GPU projections
  // so the amount of data being passed to the GPU every step is minimized.
  generateNengoGPUDeviceConfiguration(totalNumNeurons, ensembleNumNeurons, numProjections, projections, adjacencyMatrix, deviceForEnsemble);

  free(adjacencyMatrix);
  free(ensembleNumNeurons);

  // We have to set the number fields in the NengoGPUData structs so that it knows how big to make its internal arrays
  int* ensembleJavaIndexToDeviceIndex = (int*)malloc(totalNumEnsembles * sizeof(int));
  for(i = 0; i < totalNumEnsembles; i++)
  {
    ensembleDataRow_JAVA = (jintArray) (*env)->GetObjectArrayElement(env, ensembleData_JAVA, i);
    (*env)->GetIntArrayRegion(env, ensembleDataRow_JAVA, 0, NENGO_ENSEMBLE_DATA_NUM, ensembleData);

    currentData = nengoDataArray[deviceForEnsemble[i]];

    ensembleJavaIndexToDeviceIndex[i] = currentData->numEnsembles;

    assignEnsembleToDevice(ensembleData, currentData); 

  }
  free(ensembleData);


  // Adjust projections to reflect the distribution of ensembles to devices.
  adjustProjections(numProjections, projections, ensembleJavaIndexToDeviceIndex);
  free(ensembleJavaIndexToDeviceIndex);

  sharedInputSize = 0;
  
  // Now we start to load the data into the NengoGPUData struct for each device.
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

    sharedInputSize += currentData->CPUInputSize;
  }

  // set the shared memory maps for the device. We can't do this until
  // all the intra-device projections have had their offsets set, which means each device has to have
  // had setupInput called on it.
  createSharedMemoryMaps(numProjections, projections);

  // Allocate and initialize the shared array
  sharedInput = (float*)malloc(sharedInputSize * sizeof(float));
  for(i = 0; i < sharedInputSize; i++)
  {
    sharedInput[i] = 0.0;
  }

  int sum = 0;
  for(i = 0; i < numDevices; i++)
  {
    sum += nengoDataArray[i]->JavaInputSize;
  }

  free(projections);

  run_start();
}


// Called once per step from the Java code. Puts the representedInputValues in the proper form for processing, then tells each GPU thread
// to take a step. Once they've finished the step, this function puts the representedOutputValues and spikes in the appropriate Java
// arrays so that they can be read on the Java side when this call returns.
JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeStep
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

            if(inputIndex  + inputDimension <= currentData->JavaInputSize)
            {
              (*env)->GetFloatArrayRegion(env, currentInput_JAVA, 0, inputDimension, currentData->inputHost->array + inputIndex);
            }
            else
            {
              printf("error: accessing inputHost out of bounds. size: %d, index: %d\n", currentData->JavaInputSize, inputIndex + inputDimension);
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

  int numOutputs, outputDimension, numNeurons, outputIndex, spikeIndex, sharedIndex;

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

    // write from the output array of the current device to the shared data array
    for( k = 0; k < currentData->CPUOutputSize; k++)
    {
      outputIndex = intArrayGetElement(currentData->sharedData_outputIndex, k);
      sharedIndex = intArrayGetElement(currentData->sharedData_sharedIndex, k);

      sharedInput[sharedIndex] = floatArrayGetElement(currentData->output, outputIndex);
    }
  }
}

JNIEXPORT void JNICALL Java_ca_nengo_util_impl_NEFGPUInterface_nativeKill
(JNIEnv *env, jclass class)
{
  printf("NengoGPU: KILL\n");
  run_kill();
}

#ifdef __cplusplus
}
#endif
