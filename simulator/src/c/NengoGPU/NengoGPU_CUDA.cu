#ifdef __cplusplus
extern "C"{
#endif

#include <stdio.h>
#include <stdlib.h>
#include <cutil.h>
#include <cuda_runtime.h>

#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"

// print the contents of an array of integers located on the device
void printIntArrayFromDevice(FILE* fp, int* array, int size)
{
  int* temp = (int*) malloc( size * sizeof(int));
  cudaMemcpy(temp, array, size * sizeof(int), cudaMemcpyDeviceToHost);

  int i = 0;
  for(; i < size; i++)
  {
    if(fp != NULL)
    {
      fprintf(fp, "%d ", temp[i]);
    }
    else
    {
      printf("%d ", temp[i]);
    }
  }

  if(fp != NULL)
  {
    fprintf(fp, "\n");
  }
  else
  {
    printf("\n");
  }

  free(temp);
}

// print the contents of an array of floats located on the device
void printFloatArrayFromDevice(FILE* fp, float* array, int size)
{
  float* temp = (float*) malloc( size * sizeof(float));
  cudaMemcpy(temp, array, size * sizeof(float), cudaMemcpyDeviceToHost);

  int i = 0;
  for(; i < size; i++)
  {
    if(fp != NULL)
    {
      fprintf(fp, "%f ", temp[i]);
    }
    else
    {
      printf("%f ", temp[i]);
    }
  }

  if(fp != NULL)
  {
    fprintf(fp, "\n");
  }
  else
  {
    printf("\n");
  }

  free(temp);
}


// get number of devices available
int getGPUDeviceCount(){
  cudaError_t err;
  int numDevices;
  
  err = cudaGetDeviceCount(&numDevices);
  checkCudaError(err);
  
  return numDevices;
}

// Reserves device with number deviceNum for the thread that calls this function. No interaction with the device should take place until this has been called.
// Once the device is reserved for the thread, no other thread should try to interact with that device or reserve it. A thread can reserve only one device at a time
void initGPUDevice(int deviceNum)
{
  cudaSetDevice(deviceNum);
}

void shutdownGPUDevice()
{
}

void checkCudaError(cudaError_t err)
{
    if(!err)
        return;

    printf("%s\n", cudaGetErrorString(err));

    exit(EXIT_FAILURE);
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalNumTerminationRows kernels are launched.
// Dot product the ith termination row with the corresponding input vector. Integrate the result. Results are stored in terminationValues. 
__global__ void transformAndIntegrate(float dt, int totalNumTerminationRows, float* input, float* transforms, float* terminationTauValues, float* terminationValues, int* inputIndices, int* transformIndices, int* terminationRowToTerminationIndexor, int* terminationDimensions)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalNumTerminationRows)
  {
    int terminationIndex = terminationRowToTerminationIndexor[i];
    int terminationDimension = terminationDimensions[terminationIndex];

    int j = 0;
    int inputIndex = terminationIndex;
    int transformIndex = i;
  
    float dot_product = 0;
    for(; j < terminationDimension; j++)
    {
      dot_product += input[inputIndex] * transforms[transformIndex];

      transformIndex = transformIndices[transformIndex];

      inputIndex = inputIndices[inputIndex];
    }

    float dt_over_tau = dt / terminationTauValues[terminationIndex];

    terminationValues[i] = (1 - dt_over_tau) * terminationValues[i] + dt_over_tau * dot_product;
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalDimension kernels are launched.
// Sum the termination values for one dimension of one ensemble. Results are stored in ensembleSums.
__global__ void sumTerminations(int totalDimensions, float* ensembleSums, float* terminationValues, int* dimensionToEnsembleIndexor, int* dimensionIndexInEnsemble, int* ensembleDimensions, int* ensembleTerminations, int* ensemblePositionsInTerminationValues, int* sumHelper)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalDimensions)
  {
  int ensembleIndex = dimensionToEnsembleIndexor[i];
  int ensembleDimension = ensembleDimensions[ensembleIndex];
  int ensembleNumTerminations = ensembleTerminations[ensembleIndex];
  int indexInEnsemble = dimensionIndexInEnsemble[i];
  int ensembleStart = ensemblePositionsInTerminationValues[ensembleIndex];

  int j = 0;
  float sum = 0;

  for(; j < ensembleNumTerminations; j++)
  {
    sum += terminationValues[ensembleStart + ensembleDimension * j + indexInEnsemble];
  }

  ensembleSums[sumHelper[i]] = sum;
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least numNeurons kernels are launched.
// Multiply one encoder row by the sum vector for the corresponding ensemble. Then integrate to determine whether the neuron corresponding to that encoder row should spike. Results stored in spikes.
__global__ void encodeAndIntegrate(float dt, float adjusted_dt, int steps, int totalNumNeurons, float* encoders, float* ensembleSums, int* encoderIndices, int* sumIndices, int* ensembleDimensions, float* neuronVoltage, float* neuronReftime, float* spikes, int* spikesHelper, int* neuronToEnsembleIndexor, float* ensembleTauRC, float* ensembleTauRef, float* bias, float* scale)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalNumNeurons)
  {

  int ensembleIndex = neuronToEnsembleIndexor[i];
  int sumIndex = ensembleIndex;
  int encoderIndex = i;
  int ensembleDimension = ensembleDimensions[ensembleIndex];
  
  int j = 0;
  float dot_product = 0;
  for(; j < ensembleDimension; j++)
  {
    dot_product += encoders[encoderIndex] * ensembleSums[sumIndex];

    encoderIndex = encoderIndices[encoderIndex];
    sumIndex = sumIndices[sumIndex];
  }

  // integrate
  float voltage = neuronVoltage[i];
  float refTime = neuronReftime[i];
  float tau_rc = ensembleTauRC[ensembleIndex];
  float tau_ref = ensembleTauRef[ensembleIndex];
  float current = bias[i] + scale[i] * dot_product;
  float dV, post_ref, v_threshold = 1.0f;
  float spike_float;
  int spikeIndex, spike = 0;
  

  for(j = 0; j < steps; j++)
  {
    dV = adjusted_dt / tau_rc * (current - voltage);
    voltage = max(voltage + dV, 0.0f);

    post_ref = 1.0f - (refTime - adjusted_dt) / adjusted_dt;

    voltage = post_ref >= 1.0f ? voltage : voltage * post_ref;

    voltage = post_ref <= 0.0f ? 0.0f : voltage;

    v_threshold = 1.0f;

    spike = spike ? spike : voltage > v_threshold;
    spike_float = spike ? 1.0f/dt : 0.0f;
    refTime = spike ? ((adjusted_dt / dV) * (dV - voltage + v_threshold)) + tau_ref : refTime - adjusted_dt;
    voltage = spike ? 0.0 : voltage;
  }

  neuronReftime[i] = refTime;
  neuronVoltage[i] = voltage;
  spikeIndex = spikesHelper[i];
  spikes[spikeIndex] = spike_float;
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalOutputSize kernels are launched.
// Multiply one decoder row by the spike vector for the corresponding ensemble. The result is one dimension of the output vector for the ensemble. Results stored in output.
__global__ void decode(int totalOutputSize, float* decoder, float* spikes, int* ensembleNumNeurons, float* output, int* decoderIndices, int* spikeIndices, int* decoderRowToEnsembleIndexor)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalOutputSize)
  {
  int ensembleIndex = decoderRowToEnsembleIndexor[i];
  int decoderIndex = i;
  int spikeIndex = ensembleIndex;
  int numNeurons = ensembleNumNeurons[ensembleIndex];

  int j = 0;
  float dot_product = 0;

  for(; j < numNeurons; j++)
  {
    dot_product += decoder[decoderIndex] * spikes[spikeIndex];

    decoderIndex = decoderIndices[decoderIndex];

    spikeIndex = spikeIndices[spikeIndex];
  }

  output[i] = dot_product;
  }
}

// run a NengoGPUData object for one step
void run_NEFEnsembles(NengoGPUData* nengoData, float startTime, float endTime)
{
  float dt = endTime - startTime;
  cudaError_t err;

  dim3 dimBlock(16, 16);
  dim3 dimGrid(1, nengoData->totalNumTransformRows / (dimBlock.x * dimBlock.y) + 1);

  nengoData->input = moveToDeviceFloatArray(nengoData->input, nengoData->totalInputSize);

  transformAndIntegrate <<<dimGrid, dimBlock>>> (dt, nengoData->totalNumTransformRows, nengoData->input, nengoData->terminationTransforms, nengoData->terminationTau, nengoData->terminationValues, nengoData->inputIndices, nengoData-> terminationTransformIndices, nengoData->terminationRowToTerminationIndexor, nengoData->terminationDimensions);
  err = cudaGetLastError();
  checkCudaError(err);
  
  err = cudaFree(nengoData->input);
  checkCudaError(err);

  nengoData->input = (float*)malloc(nengoData->totalInputSize * sizeof(float));
  if(!nengoData->input)
  {
    printf("bad malloc\n");
    exit(EXIT_FAILURE);
  }


  dimGrid.y = nengoData->totalDimension / (dimBlock.x * dimBlock.y) + 1;
  sumTerminations <<<dimGrid, dimBlock>>> (nengoData->totalDimension, nengoData->ensembleSums, nengoData->terminationValues, nengoData->dimensionToEnsembleIndexor, nengoData->dimensionIndexInEnsemble, nengoData->ensembleDimensions, nengoData->ensembleTerminations, nengoData->ensemblePositionsInTerminationValues, nengoData->sumHelper);
  err = cudaGetLastError();
  checkCudaError(err);

  dimGrid.y = nengoData->numNeurons / (dimBlock.x * dimBlock.y) + 1;
  

  int encoder_steps = (int)ceil(dt / nengoData->maxTimeStep);
  //float encoder_dt = dt / encoder_steps;

  encodeAndIntegrate <<<dimGrid, dimBlock>>> (dt, dt, 1, nengoData->numNeurons, nengoData->encoders, nengoData->ensembleSums, nengoData->encoderIndices, nengoData->sumIndices, nengoData->ensembleDimensions, nengoData->neuronVoltage, nengoData->neuronReftime, nengoData->spikes, nengoData->spikeHelper, nengoData->neuronToEnsembleIndexor, nengoData->ensembleTauRC, nengoData->ensembleTauRef, nengoData->neuronBias, nengoData->neuronScale);
  err = cudaGetLastError();
  checkCudaError(err);
  

  if(nengoData->output)
  {
    free(nengoData->output);
  }
    
  err = cudaMalloc((void**)&nengoData->output, nengoData->totalOutputSize * sizeof(float)); 
  checkCudaError(err);

  dimGrid.y = nengoData->totalOutputSize / (dimBlock.x * dimBlock.y) + 1;
  decode <<<dimGrid, dimBlock>>> (nengoData->totalOutputSize, nengoData->decoders, nengoData->spikes, nengoData->ensembleNeurons, nengoData->output, nengoData->decoderIndices, nengoData->spikeIndices, nengoData->decoderRowToEnsembleIndexor);
  err = cudaGetLastError();
  checkCudaError(err);

  nengoData->output = moveToHostFloatArray(nengoData->output, nengoData->totalOutputSize);

  cudaMemcpy(nengoData->spikesHost, nengoData->spikes, nengoData->numNeurons * sizeof(float), cudaMemcpyDeviceToHost);
}

#ifdef __cplusplus
}
#endif

