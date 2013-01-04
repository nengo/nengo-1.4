#ifdef __cplusplus
extern "C"{
#endif

#include <stdio.h>
#include <stdlib.h>
#include <cuda_runtime.h>

#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"

# define MAX_SHARED_MEM_SIZE 16000

// print the contents of an array of integers located on the device
void printIntArrayFromDevice(FILE* fp, intArray* a, jint n, jint m, jint labels)
{
  jint* temp = (jint*) malloc( m * n * sizeof(jint));
  cudaMemcpy(temp, a->array, m * n * sizeof(jint), cudaMemcpyDeviceToHost);

  printf("%s:\n", a->name);

  jint i, j;
  for(i = 0; i < n; i++)
  {
    fp ? fprintf(fp, "line %d: ", i) : printf("line %d:", i);
    for(j = 0; j < m; j++)
    {
      if(labels)
        fp ? fprintf(fp, "(%d, %d) ", j, temp[i * n + j]) : printf("(%d, %d) ", j, temp[i * n + j]);
      else
        fp ? fprintf(fp, "%d ", temp[i * n + j]) : printf("%d ", temp[i * n + j]);
    }
    fp ? fprintf(fp, "\n") : printf("\n");
  }

  fp ? fprintf(fp, "\n") : printf("\n");

  free(temp);
}

// print the contents of an array of floats located on the device
void printFloatArrayFromDevice(FILE* fp, floatArray* a, jint n, jint m, jint labels)
{
  cudaError_t err;
  float* temp = (float*) malloc( m * n * sizeof(float));
  err = cudaMemcpy(temp, a->array, m * n * sizeof(float), cudaMemcpyDeviceToHost);
  checkCudaError(err, "in printFloatArrayFromDevice, copying from device to host");

  printf("%s:\n", a->name);

  jint i, j;
  for(i = 0; i < n; i++)
  {
    fp ? fprintf(fp, "line %d: ", i) : printf("line %d:", i);
    for(j = 0; j < m; j++)
    {
      if(labels)
        fp ? fprintf(fp, "(%d, %f) ", j, temp[i * n + j]) : printf("(%d, %f) ", j, temp[i * n + j]);
      else
        fp ? fprintf(fp, "%f ", temp[i * n + j]) : printf("%f ", temp[i * n + j]);
    }

    fp ? fprintf(fp, "\n") : printf("\n");
  }

  fp ? fprintf(fp, "\n") : printf("\n");

  free(temp);
}

void printIntColumn(FILE* fp, jint* array, jint m, jint n, jint col)
{
  jint* temp = (jint*) malloc( m * n * sizeof(jint));
  cudaMemcpy(temp, array, m * n * sizeof(jint), cudaMemcpyDeviceToHost);

  jint i;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "%d ", temp[i * n + col]) : printf("%d ", temp[i * n + col]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}

void printFloatColumn(FILE* fp, float* array, jint m, jint n, jint col)
{
  float* temp = (float*) malloc( m * n * sizeof(float));
  cudaMemcpy(temp, array, m * n * sizeof(float), cudaMemcpyDeviceToHost);

  jint i;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "%f ", temp[i * n + col]) : printf("%f ", temp[i * n + col]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}
 
void printFloatRange(FILE* fp, float* array, jint start, jint end)
{
  float* temp = (float*) malloc((end - start + 1)  * sizeof(float));
  cudaMemcpy(temp, array + start, (end - start + 1) * sizeof(float), cudaMemcpyDeviceToHost);

  jint i;
  for(i = 0; i < end - start + 1; i++)
  {
    fp ? fprintf(fp, "%f ", temp[i]) : printf("%f ", temp[i]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}

void printIntRange(FILE* fp, jint* array, jint start, jint end)
{
  jint* temp = (jint*) malloc((end - start + 1)  * sizeof(jint));
  cudaMemcpy(temp, array + start, (end - start + 1) * sizeof(jint), cudaMemcpyDeviceToHost);

  jint i;
  for(i = 0; i < end - start + 1; i++)
  {
    fp ? fprintf(fp, "%d ", temp[i]) : printf("%d ", temp[i]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}

// get number of devices available
jint getGPUDeviceCount(){
  cudaError_t err;
  jint numDevices;
  
  err = cudaGetDeviceCount(&numDevices);
  checkCudaError(err, "get GPU device count");
  
  return numDevices;
}

// Reserves device with number deviceNum for the thread that calls this function. No interaction with the device should take place until this has been called.
// Once the device is reserved for the thread, no other thread should try to interact with that device or reserve it. A thread can reserve only one device at a time
void initGPUDevice(jint deviceNum)
{
  cudaSetDevice(deviceNum);
}

void shutdownGPUDevice()
{
}

void checkCudaErrorWithDevice(cudaError_t err, jint device, char* message)
{
  if(!err)
      return;

  printf("device: %d", device);
  checkCudaError(err, message);
}

void checkCudaError(cudaError_t err, char* message)
{
    if(!err)
        return;

    printf(" CUDA ERROR: message: %s, description: %s\n", message, cudaGetErrorString(err));

    exit(EXIT_FAILURE);
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalNumTerminationRows kernels are launched.
// Dot product the ith termination row with the corresponding input vector. Integrate the result. Results are stored in terminationValues. 
__global__ void transform(float dt, jint numTransformRows, float* input, jint* terminationOffsetInInput, jint* transformRowToInputIndexor, float* transforms, float* tau, float* terminationOutput, jint* terminationOutputIndexor, jint* inputDimensions)
{
  
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < numTransformRows)
  {
    
    jint j;
    jint inputIndex = transformRowToInputIndexor[i];
    jint offset = terminationOffsetInInput[inputIndex];
    
    jint inputDimension = inputDimensions[inputIndex];
    jint transformRowIndex = i;
    
    float my_tau = tau[inputIndex];
    
    float dot_product = 0;
    
    for(j=0; j < inputDimension; j++)
    {
      dot_product += input[offset + j] * transforms[transformRowIndex];

      transformRowIndex += numTransformRows;
    }
   
    float dt_over_tau = dt / my_tau;
    

    jint outputIndex = terminationOutputIndexor[i];
    terminationOutput[outputIndex] = (1 - dt_over_tau) * terminationOutput[outputIndex] + dt_over_tau * dot_product;
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalDimension kernels are launched.
// Sum the termination values for one dimension of one ensemble. Results are stored in ensembleSums.
__global__ void sumTerminations(jint totalDimensions, jint maxNumDecodedTerminations, float* terminationOutput, float* ensembleSums)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalDimensions)
  {
    jint terminationOutputIndex = i;
    jint j;
    float sum = 0;

    for(j=0; j < maxNumDecodedTerminations; j++)
    {
      sum += terminationOutput[terminationOutputIndex];
      terminationOutputIndex += totalDimensions;
    }

    ensembleSums[i] = sum;
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least numNeurons kernels are launched.
// Multiply one encoder row by the sum vector for the corresponding ensemble. Then integrate to determine whether the neuron corresponding to that encoder row should spike. Results stored in spikes.
__global__ void encode(jint totalNumNeurons, float* encoders, float* sums, float* encodeResult, jint* encoderRowToEnsembleIndexor, jint* ensembleOffsetInDimension, jint* ensembleDimension, jint* encoderStride, jint* neuronIndexor)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if(i < totalNumNeurons)
  {
    jint ensembleIndex = encoderRowToEnsembleIndexor[i];
    jint currentEnsembleDimension = ensembleDimension[ensembleIndex];
    jint dimensionOffset = ensembleOffsetInDimension[ensembleIndex];

    jint j, encoderOffset = i;
    float dot_product = 0;


    for(j = 0; j < currentEnsembleDimension; j++)
    {
      dot_product += encoders[encoderOffset] * sums[dimensionOffset + j];
      encoderOffset += encoderStride[j];
    }
    
    jint neuronIndex = neuronIndexor[i];
    encodeResult[neuronIndex] = dot_product;
  }
}

__global__ void integrateAfterEncode(jint numNeurons, float dt, float adjusted_dt, jint steps, jint* neuronToEnsembleIndexor, float* encodingResult, float* neuronVoltage, float* neuronReftime, float* tau_RC, float* tauRef, float* bias, float* scale, float* spikes, float* NDterminationSums, jint* isSpikingEnsemble)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;
  
  if( i < numNeurons)
  {
    jint ensembleIndex = neuronToEnsembleIndexor[i];
    float voltage = neuronVoltage[i];
    float refTime = neuronReftime[i];
    float tau_rc = tau_RC[ensembleIndex];
    float tau_ref = tauRef[ensembleIndex];
    float current = bias[i] + scale[i] * (encodingResult[i] + NDterminationSums[ensembleIndex]);

    if(isSpikingEnsemble[ensembleIndex])
    {
      float dV, post_ref, v_threshold = 1.0f;
      float spike_float;
      jint j, spike = 0;

      for(j = 0; j < steps; j++)
      {
        dV = adjusted_dt / tau_rc * (current - voltage);
        voltage = max(voltage + dV, 0.0f);

        post_ref = 1.0f - (refTime - adjusted_dt) / adjusted_dt;

        voltage = post_ref >= 1.0f ? voltage : voltage * post_ref;

        voltage = post_ref <= 0.0f ? 0.0f : voltage;

        spike = spike ? spike : voltage > v_threshold;
        spike_float = spike ? 1.0f/dt : 0.0f;
        refTime = spike ? ((adjusted_dt / dV) * (dV - voltage + v_threshold)) + tau_ref : refTime - adjusted_dt;
        voltage = spike ? 0.0 : voltage;
      }

      neuronReftime[i] = refTime;
      neuronVoltage[i] = voltage;
      spikes[i] = spike_float;
    }
    else
    {
      spikes[i] = (current > 1.0) ? 1.0 / (tau_ref - tau_rc * logf(1.0 - 1.0 / current)) : 0.0;
    }
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalOutputSize kernels are launched.
// Multiply one decoder row by the spike vector for the corresponding ensemble. The result is one dimension of the output vector for the ensemble. Results stored in output.
__global__ void decode(jint totalOutputSize, float* decoders, float* spikes, float* output, jint* decoderRowToEnsembleIndexor, jint* ensembleNumNeurons, jint* ensembleOffsetInNeurons, jint* decoderStride, jint* outputIndexor)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;
  
  if( i < totalOutputSize)
  {
    
    jint ensembleIndex = decoderRowToEnsembleIndexor[i];
    jint numNeurons = ensembleNumNeurons[ensembleIndex];
    jint spikesOffset = ensembleOffsetInNeurons[ensembleIndex];
    
    jint j, decoderOffset = i;
    float dot_product = 0;

    for(j=0; j < numNeurons; j++)
    {
        dot_product += decoders[decoderOffset] * spikes[spikesOffset + j];

        decoderOffset += decoderStride[j];
    }
    

    jint currentOutputIndex = outputIndexor[i];
    output[currentOutputIndex] = dot_product;
  }
}



// launch as many as there are ensembles
__global__ void processNDterminations(jint numEnsembles, jint numNDterminations, jint steps, float adjusted_dt, jint* NDterminationEnsembleOffset, jint* terminationOffsetInInputs, jint* terminationDimensions, jint* inputIndex, float* input, float* weights, float* current, float* sum, float* tau)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if(i < numEnsembles)
  {
    jint offset = NDterminationEnsembleOffset[i];
    jint count = (i == numEnsembles - 1) ? numNDterminations - offset : NDterminationEnsembleOffset[i+1] - offset;
    jint j, k, terminationOffsetInInput, terminationDimension, index;
    float val, temp_sum = 0, temp_current, temp_tau;

    jint weightIndexInEnsemble = i;

    if(count > 0)
    {
      for(j = 0; j < count; j++)
      {
        index = inputIndex[offset + j];
        terminationOffsetInInput = terminationOffsetInInputs[index]; 
        terminationDimension = terminationDimensions[index];

        val = 0;
        for(k = 0; k < terminationDimension; k++)
        {
          // have to figure out how to index this properly
          val += input[terminationOffsetInInput + k] * weights[weightIndexInEnsemble];
          weightIndexInEnsemble += numEnsembles;
        }

        temp_current = current[offset + j];
        temp_tau = tau[index];

        for(k = 0; k < steps; k++)
        {
          // testing this order, though this is the one used in the java code so it should work
          temp_current *= 1 - adjusted_dt / temp_tau;
          temp_current += val * adjusted_dt / temp_tau;
        }

        current[offset + j] = temp_current;
        
        temp_sum += temp_current;
      }

      sum[i] = temp_sum;
    }
  }
}


__global__ void moveGPUData(jint size, jint* map, float* to, float* from)
{
  jint i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if(i < size)
  {
    to[i] = from[ map[i] ];
  }
}
      
// run a NengoGPUData struct for one step
void run_NEFEnsembles(NengoGPUData* nengoData, float startTime, float endTime)
{
  float dt = endTime - startTime;

  //printf("start time: %f, end time %f, dt: %f, device: %d\n", startTime, endTime, dt, nengoData->device);

  cudaError_t err;

  dim3 dimBlock(1, 1);
  dim3 dimGrid(1, 1);

//   jint NDsteps = 
  //float NDadjusted_dt = dt / NDsteps; /// steps;
  jint ND_steps = 1; //(jint)(ceil(dt / nengoData->maxTimeStep));
  float ND_adjusted_dt = dt;// / ND_steps;

  jint steps = 1;
  float adjusted_dt = dt;

//  if(((jint) (startTime * 1000)) < 4)
  //printDynamicNengoGPUData(nengoData);


///////////////////////////////////////////////////////
// Copy input from host to GPU
///////////////////////////////////////////////////////

  cudaMemcpy(nengoData->input->array + nengoData->GPUInputSize, sharedInput + nengoData->offsetInSharedInput, (nengoData->JavaInputSize + nengoData->CPUInputSize) * sizeof(float), cudaMemcpyHostToDevice);
  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: copying cpu input to device");

///////////////////////////////////////////////////////
// Multiply input vectors by corresponding termination transform
///////////////////////////////////////////////////////
  dimBlock.x = 256;
  dimGrid.x = nengoData->totalNumTransformRows / dimBlock.x + 1;

  transform<<<dimGrid, dimBlock>>> (dt, nengoData->totalNumTransformRows, nengoData->input->array, nengoData->terminationOffsetInInput->array, nengoData->transformRowToInputIndexor->array, nengoData->terminationTransforms->array, nengoData->terminationTau->array, nengoData->terminationOutput->array, nengoData->terminationOutputIndexor->array, nengoData->inputDimension->array);
  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: transform");

///// sum the activation in each dimension of each ensemble

  dimBlock.x = 256;
  dimGrid.x = nengoData->totalEnsembleDimension / dimBlock.x + 1;

  sumTerminations <<<dimGrid, dimBlock>>> (nengoData->totalEnsembleDimension, nengoData->maxNumDecodedTerminations, nengoData->terminationOutput->array, nengoData->ensembleSums->array);
  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: sum");


///// process ND (nonDecoded) terminations
  dimBlock.x = 256;
  dimGrid.x = nengoData->numEnsembles / dimBlock.x + 1;

  processNDterminations<<<dimGrid, dimBlock>>>(nengoData->numEnsembles, nengoData->numNDterminations, ND_steps, ND_adjusted_dt, nengoData->NDterminationEnsembleOffset->array, nengoData->terminationOffsetInInput->array, nengoData->inputDimension->array, nengoData->NDterminationInputIndexor->array, nengoData->input->array, nengoData->NDterminationWeights->array, nengoData->NDterminationCurrents->array, nengoData->NDterminationEnsembleSums->array, nengoData->terminationTau->array);

  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: process non decoded");

///// encode
  dimBlock.x = 256;
  dimGrid.x = nengoData->numNeurons / dimBlock.x + 1;

  encode<<<dimGrid, dimBlock>>> (nengoData->numNeurons, nengoData->encoders->array, nengoData->ensembleSums->array, nengoData->encodeResult->array, nengoData->encoderRowToEnsembleIndexor->array, nengoData->ensembleOffsetInDimensions->array, nengoData->ensembleDimension->array, nengoData->encoderStride->array, nengoData->encoderRowToNeuronIndexor->array);


  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: encode");



///// integrate after encoding
  dimBlock.x = 256;
  dimGrid.x = nengoData->numNeurons / dimBlock.x + 1;

    integrateAfterEncode <<<dimGrid, dimBlock>>> (nengoData->numNeurons, dt, adjusted_dt, steps, nengoData->neuronToEnsembleIndexor->array, nengoData->encodeResult->array, nengoData->neuronVoltage->array, nengoData->neuronReftime->array, nengoData->ensembleTauRC->array, nengoData->ensembleTauRef->array, nengoData->neuronBias->array, nengoData->neuronScale->array, nengoData->spikes->array, nengoData->NDterminationEnsembleSums->array, nengoData->isSpikingEnsemble->array);

  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: integrate after encode");

///// decode

  dimBlock.x = 256;
  dimGrid.x = nengoData->totalOutputSize / dimBlock.x + 1;

  decode<<<dimGrid, dimBlock>>>(nengoData->totalOutputSize, nengoData->decoders->array, nengoData->spikes->array, nengoData->ensembleOutput->array, nengoData->decoderRowToEnsembleIndexor->array, nengoData->ensembleNumNeurons->array, nengoData->ensembleOffsetInNeurons->array, nengoData->decoderStride->array, nengoData->decoderRowToOutputIndexor->array);

  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: decode");


//// move output to device

  // reorganize the output, which comes out of decode in terms of ensembles, so that it is in terms of network arrays.
  dimGrid.x = nengoData->totalOutputSize / (dimBlock.x * dimBlock.y) + 1;
  moveGPUData<<<dimGrid, dimBlock>>>(nengoData->totalOutputSize, nengoData->ensembleOutputToNetworkArrayOutputMap->array, nengoData->output->array, nengoData->ensembleOutput->array);
  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: moveensembleoutput to network array output");

  if(nengoData->numSpikesToSendBack > 0)
  { 
    dimGrid.x = nengoData->numSpikesToSendBack / (dimBlock.x * dimBlock.y) + 1;
    moveGPUData<<<dimGrid, dimBlock>>>(nengoData->numSpikesToSendBack, nengoData->spikeMap->array, nengoData->output->array + nengoData->totalOutputSize, nengoData->spikes->array);
    err = cudaGetLastError();
    checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: extract spikes to send back");
  }

  if(nengoData->CPUOutputSize + nengoData->numSpikesToSendBack > 0)
  {
    cudaMemcpy(nengoData->outputHost->array, nengoData->output->array + nengoData->GPUOutputSize, (nengoData->CPUOutputSize + nengoData->numSpikesToSendBack) * sizeof(float), cudaMemcpyDeviceToHost);
    err = cudaGetLastError();
    checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: move output from GPU to CPU");
  }
  
//// move data along GPU projections
  dimGrid.x = nengoData->GPUInputSize / (dimBlock.x * dimBlock.y) + 1;
  moveGPUData<<<dimGrid, dimBlock>>>(nengoData->GPUInputSize, nengoData->GPUTerminationToOriginMap->array, nengoData->input->array, nengoData->output->array);
  err = cudaGetLastError();
  checkCudaErrorWithDevice(err, nengoData->device, "run_NEFEnsembles: move output along GPU projections");
}

float* allocateCudaFloatArray(jint size)
{
  float* temp;
  cudaError_t err;
  err = cudaMalloc((void**)&temp, size * sizeof(float));
  checkCudaError(err, "allocate cuda float array");
  return temp;
}
  
jint* allocateCudaIntArray(jint size)
{
  jint* temp;
  cudaError_t err;
  err = cudaMalloc((void**)&temp, size * sizeof(jint));
  checkCudaError(err, "allocate cuda jint array");
  return temp;
}

long getDeviceCapacity(jint device)
{
  cudaDeviceProp deviceProperties;
  cudaGetDeviceProperties(&deviceProperties, device);  
  return deviceProperties.totalGlobalMem;
}
  
void initializeDeviceInputAndOutput(NengoGPUData* nengoData)
{
  char* name;
  cudaError_t err;

  name = "input";
  nengoData->input = newFloatArrayOnDevice(nengoData->totalInputSize, name); 
  
  name = "ensembleOutput";
  nengoData->ensembleOutput = newFloatArrayOnDevice(nengoData->totalOutputSize, name); 

  name = "output";
  nengoData->output = newFloatArrayOnDevice(nengoData->totalOutputSize + nengoData->numSpikesToSendBack, name); 
  
  name = "spikes";
  nengoData->spikes = newFloatArrayOnDevice(nengoData->numNeurons, name); 
  
  name = "terminationOutput";
  nengoData->terminationOutput = newFloatArrayOnDevice(nengoData->totalEnsembleDimension * nengoData->maxNumDecodedTerminations, name); 
  
  name = "ensembleSums";
  nengoData->ensembleSums = newFloatArrayOnDevice(nengoData->totalEnsembleDimension, name); 
  
  name = "encodeResult";
  nengoData->encodeResult = newFloatArrayOnDevice(nengoData->numNeurons, name); 
  
  name = "neuronVoltage";
  nengoData->neuronVoltage = newFloatArrayOnDevice(nengoData->numNeurons, name); 
  
  name = "neuronReftime";
  nengoData->neuronReftime = newFloatArrayOnDevice(nengoData->numNeurons, name); 


  err = cudaMemset(nengoData->input->array, 0, nengoData->GPUInputSize * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->ensembleOutput->array, 0, nengoData->totalOutputSize * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->output->array, 0, (nengoData->totalOutputSize + nengoData->numSpikesToSendBack) * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->spikes->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->terminationOutput->array, 0, nengoData->totalEnsembleDimension * nengoData->maxNumDecodedTerminations * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->neuronVoltage->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->neuronReftime->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  
  name = "NDterminationCurrents";
  nengoData->NDterminationCurrents = newFloatArrayOnDevice(nengoData->numNDterminations, name); 
  name = "NDterminationEnsembleSum";
  nengoData->NDterminationEnsembleSums = newFloatArrayOnDevice(nengoData->numEnsembles, name); 

  err = cudaMemset(nengoData->NDterminationCurrents->array, 0, nengoData->numNDterminations * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
  err = cudaMemset(nengoData->NDterminationEnsembleSums->array, 0, nengoData->numEnsembles * sizeof(float));
  checkCudaErrorWithDevice(err, nengoData->device, "cuda setup structures");
}

#ifdef __cplusplus
}
#endif

