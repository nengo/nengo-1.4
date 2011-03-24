#ifdef __cplusplus
extern "C"{
#endif

#include <stdio.h>
#include <stdlib.h>
//#include <cutil.h>
#include <cuda_runtime.h>

#include "NengoGPU.h"
#include "NengoGPU_CUDA.h"

// print the contents of an array of integers located on the device
void printIntArrayFromDevice(FILE* fp, intArray* a, int n, int m)
{
  int* temp = (int*) malloc( m * n * sizeof(int));
  cudaMemcpy(temp, a->array, m * n * sizeof(int), cudaMemcpyDeviceToHost);

  int i, j;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "line %d: ", i) : printf("line %d:", i);
    for(j = 0; j < n; j++)
    {
      fp ? fprintf(fp, "%d ", temp[i * n + j]) : printf("%d ", temp[i * n + j]);
    }
    fp ? fprintf(fp, "\n") : printf("\n");
  }

  fp ? fprintf(fp, "\n") : printf("\n");

  free(temp);
}

// print the contents of an array of floats located on the device
void printFloatArrayFromDevice(FILE* fp, floatArray* a, int n, int m)
{
  cudaError_t err;
  float* temp = (float*) malloc( m * n * sizeof(float));
  err = cudaMemcpy(temp, a->array, m * n * sizeof(float), cudaMemcpyDeviceToHost);
  checkCudaError(err);

  int i, j;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "line %d: ", i) : printf("line %d:", i);
    for(j = 0; j < n; j++)
    {
      fp ? fprintf(fp, "%f ", temp[i * n + j]) : printf("%f ", temp[i * n + j]);
    }

    fp ? fprintf(fp, "\n") : printf("\n");
  }

  fp ? fprintf(fp, "\n") : printf("\n");

  free(temp);
}

void printIntColumn(FILE* fp, int* array, int m, int n, int col)
{
  int* temp = (int*) malloc( m * n * sizeof(int));
  cudaMemcpy(temp, array, m * n * sizeof(int), cudaMemcpyDeviceToHost);

  int i;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "%d ", temp[i * n + col]) : printf("%d ", temp[i * n + col]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}

void printFloatColumn(FILE* fp, float* array, int m, int n, int col)
{
  float* temp = (float*) malloc( m * n * sizeof(float));
  cudaMemcpy(temp, array, m * n * sizeof(float), cudaMemcpyDeviceToHost);

  int i;
  for(i = 0; i < m; i++)
  {
    fp ? fprintf(fp, "%f ", temp[i * n + col]) : printf("%f ", temp[i * n + col]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}
 
void printFloatRange(FILE* fp, float* array, int start, int end)
{
  float* temp = (float*) malloc((end - start + 1)  * sizeof(float));
  cudaMemcpy(temp, array + start, (end - start + 1) * sizeof(float), cudaMemcpyDeviceToHost);

  int i;
  for(i = 0; i < end - start + 1; i++)
  {
    fp ? fprintf(fp, "%f ", temp[i]) : printf("%f ", temp[i]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
}

void printIntRange(FILE* fp, int* array, int start, int end)
{
  int* temp = (int*) malloc((end - start + 1)  * sizeof(int));
  cudaMemcpy(temp, array + start, (end - start + 1) * sizeof(int), cudaMemcpyDeviceToHost);

  int i;
  for(i = 0; i < end - start + 1; i++)
  {
    fp ? fprintf(fp, "%d ", temp[i]) : printf("%d ", temp[i]);
  }
  fp ? fprintf(fp, "\n") : printf("\n");
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
__global__ void transform(float dt, int numTransformRows, float* input, int* inputOffset, int* transformRowToInputIndexor, float* transforms, float* tau, float* terminationOutput, int* terminationOutputIndexor, int* inputDimensions)
{
  
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < numTransformRows)
  {
    
    int j;
    int inputIndex = transformRowToInputIndexor[i];
    int offset = inputOffset[inputIndex];
    
    int inputDimension = inputDimensions[inputIndex];
    int transformRowIndex = i;
    
    float my_tau = tau[inputIndex];
    
    float dot_product = 0;
    
   for(j=0; j < inputDimension; j++)
    {
      dot_product += input[offset + j] * transforms[transformRowIndex];

      transformRowIndex += numTransformRows;
    }
   
    float dt_over_tau = dt / my_tau;
    

    int outputIndex = terminationOutputIndexor[i];
    terminationOutput[outputIndex] = (1 - dt_over_tau) * terminationOutput[outputIndex] + dt_over_tau * dot_product;
    
  }
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalDimension kernels are launched.
// Sum the termination values for one dimension of one ensemble. Results are stored in ensembleSums.
__global__ void sumTerminations(int totalDimensions, int maxNumDecodedTerminations, float* terminationOutput, float* ensembleSums)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if( i < totalDimensions)
  {
    int terminationOutputIndex = i;
    int j;
    float sum = 0;

    for(j=0; j < maxNumDecodedTerminations; j++)
    {
      sum += terminationOutput[terminationOutputIndex];
      terminationOutputIndex += totalDimensions;
    }

    ensembleSums[i] = sum;
  }
}

extern __shared__ char shared[];
// Kernel, run on GPU. block size and grid size should be set so that at least numNeurons kernels are launched.
// Multiply one encoder row by the sum vector for the corresponding ensemble. Then integrate to determine whether the neuron corresponding to that encoder row should spike. Results stored in spikes.
__global__ void encode(int maxDimension, float* encoders, float* sums, float* encodingResult, int* blockToEnsembleMap, int* ensembleIndexOfFirstBlock, int* ensembleOffsetInDimension, int* ensembleOffsetInNeurons, int* ensembleOffsetInEncoders, int* ensembleNumNeurons, int* ensembleDimension, int* encoderStride)
{
  int thread_id = threadIdx.x;// + blockDim.x * threadIdx.y;
  int block_size = blockDim.x * blockDim.y;
  int block_id = blockIdx.x;// + gridDim.x * blockIdx.y;
  
  int ensembleIndex = blockToEnsembleMap[block_id];
  int numNeurons = ensembleNumNeurons[ensembleIndex];
  int ensembleFirstBlock = ensembleIndexOfFirstBlock[ensembleIndex];
  int dimension = ensembleDimension[ensembleIndex];
  int neuronIndexInEnsemble = thread_id + (block_id - ensembleFirstBlock) * block_size;
  int dimensionOffset = ensembleOffsetInDimension[ensembleIndex];


  int j, index;
  // now load the the sum vector and encoder stride vector into shared memory
  int* encoder_stride_shared = (int*)shared;
  float* sum_shared = (float*)(encoder_stride_shared + maxDimension);

  for(j = 0; j < dimension; j+=block_size)
  {
    index = j + thread_id;

    if(index < maxDimension)
      encoder_stride_shared[index] = encoderStride[index];
  }

  syncthreads();


  for(j = 0; j < dimension; j+=block_size)
  {
    index = j + thread_id;

    if(index < dimension)
      sum_shared[index] = sums[dimensionOffset + index];
  }

  syncthreads();


  if( neuronIndexInEnsemble < numNeurons)
  {
    int neuronIndexOnDevice = neuronIndexInEnsemble + ensembleOffsetInNeurons[ensembleIndex];
    int encoderOffset = neuronIndexInEnsemble + ensembleOffsetInEncoders[ensembleIndex];
    float dot_product = 0;
    for(j=0; j < dimension; j++)
    {
      dot_product += encoders[encoderOffset] * sum_shared[j];
      encoderOffset += encoder_stride_shared[j];
    }

    encodingResult[neuronIndexOnDevice] = dot_product;
  }
  
}

__global__ void integrateAfterEncode(int numNeurons, float dt, float adjusted_dt, int steps, int* neuronToEnsembleIndexor, float* encodingResult, float* neuronVoltage, float* neuronReftime, float* tau_RC, float* tauRef, float* bias, float* scale, float* spikes, float* NDterminationSums)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;
  
  if( i < numNeurons)
  {
    int ensembleIndex = neuronToEnsembleIndexor[i];
    float voltage = neuronVoltage[i];
    float refTime = neuronReftime[i];
    float tau_rc = tau_RC[ensembleIndex];
    float tau_ref = tauRef[ensembleIndex];
    float current = bias[i] + scale[i] * (encodingResult[i] + NDterminationSums[ensembleIndex]);
    float dV, post_ref, v_threshold = 1.0f;
    float spike_float;
    int j, spike = 0;

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
}

// Kernel, run on GPU. block size and grid size should be set so that at least totalOutputSize kernels are launched.
// Multiply one decoder row by the spike vector for the corresponding ensemble. The result is one dimension of the output vector for the ensemble. Results stored in output.
__global__ void decode(int maxNumNeurons, int* blockToEnsembleMap, int* ensembleNumNeurons, int* ensembleIndexOfFirstBlock, int* ensembleOffsetInNeurons, int* ensembleOutputSize, float* spikes, float* decoders, float* output, int* ensembleOffsetInDecoders, int* ensembleOffsetInOutput, int* decoderStride)
{
  
  int thread_id = threadIdx.x;// + blockDim.x * threadIdx.y;
  int block_size = blockDim.x * blockDim.y;
  int block_id = blockIdx.x ;//+ gridDim.x * blockIdx.y;

  int ensembleIndex = blockToEnsembleMap[block_id];
  int numNeurons = ensembleNumNeurons[ensembleIndex];
  int ensembleFirstBlock = ensembleIndexOfFirstBlock[ensembleIndex];
  int outputIndexInEnsemble = thread_id + (block_id - ensembleFirstBlock) * block_size;
  int spikesOffset = ensembleOffsetInNeurons[ensembleIndex];
  int outputSize = ensembleOutputSize[ensembleIndex];
  
  
  int j, index;
  int* decoder_stride_shared = (int*)shared;
  float* spikes_shared = (float*)(decoder_stride_shared + maxNumNeurons);

  for(j = 0; j < numNeurons; j+=block_size)
  {
    index = j + thread_id;

    if(index < numNeurons)
      decoder_stride_shared[index] = decoderStride[index];
  }

  syncthreads();
  
  for(j = 0; j < numNeurons; j+=block_size)
  {
    index = j + thread_id;

    if(index < numNeurons)
      spikes_shared[index] = spikes[spikesOffset + index];
  }
  
  syncthreads();


  if(outputIndexInEnsemble < outputSize)
  {
    
    int decoderOffset = outputIndexInEnsemble + ensembleOffsetInDecoders[ensembleIndex]; 
    float dot_product = 0;
    for(j=0; j < numNeurons; j++)
    {
      dot_product += decoders[decoderOffset] * spikes_shared[j];

      decoderOffset += decoder_stride_shared[j];
    }

    int outputIndexOnDevice = outputIndexInEnsemble + ensembleOffsetInOutput[ensembleIndex];
    output[outputIndexOnDevice] = dot_product;
  }
}



// launch as many as there are ensembles
__global__ void processNDterminations(int numEnsembles, int numNDterminations, int steps, float adjusted_dt, int* NDterminationEnsembleOffset, int* inputOffsets, int* inputIndex, float* input, float* weights, float* current, float* sum, float* tau)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if(i < numEnsembles)
  {
    int offset = NDterminationEnsembleOffset[i];
    int count = (i == numEnsembles - 1) ? numNDterminations - offset : NDterminationEnsembleOffset[i+1] - offset;
    int j, inputOffset, index;
    float val, temp_sum = 0, temp_current, temp_tau;

    for(j = 0; j < count; j++)
    {
      index = inputIndex[offset + j];
      inputOffset = inputOffsets[index]; 

      val = input[inputOffset] * weights[offset + j];
      temp_current = current[offset + j];
      temp_tau = tau[index];

      for(j = 0; j < steps; j++)
      {
        //temp_current = (temp_current + val * adjusted_dt / temp_tau) * (1 - adjusted_dt / temp_tau);
        temp_current += val * adjusted_dt / temp_tau;
        temp_current *= (1 - adjusted_dt / temp_tau);
      }

      current[offset + j] = temp_current;
      //current[offset + j] = temp_tau;
      
      temp_sum += temp_current;
    }

    sum[i] = temp_sum;
  }
}


__global__ void moveGPUOutputIntoInput(int GPUInputSize, int* map, float* input, float* output)
{
  int i = threadIdx.x + (blockDim.x * threadIdx.y) + (blockIdx.x + (gridDim.x * blockIdx.y)) * blockDim.x * blockDim.y;

  if(i < GPUInputSize)
  {
    input[ i ] = output[ map[i] ];
  }
}
      
// run a NengoGPUData struct for one step
void run_NEFEnsembles(NengoGPUData* nengoData, float startTime, float endTime)
{
  float dt = endTime - startTime;

  //printf("start time: %f, end time %f, dt: %f\n", startTime, endTime, dt);

  cudaError_t err;

  dim3 dimBlock(1, 1);
  dim3 dimGrid(1, 1);

  int steps = (int)(ceil(dt / nengoData->maxTimeStep));
  float adjusted_dt = (int) (dt / steps); /// steps;



///////////////////////////////////////////////////////
// Copy input from host to GPU
///////////////////////////////////////////////////////

 // printf("Copy input from host\n");
  err = cudaMemcpy(nengoData->input->array + nengoData->GPUInputSize, nengoData->inputHost->array, nengoData->CPUInputSize * sizeof(float), cudaMemcpyHostToDevice);
  err = cudaGetLastError();
  checkCudaError(err);


///////////////////////////////////////////////////////
// Multiply input vectors by corresponding termination transform
///////////////////////////////////////////////////////
  dimBlock.x = 256;
  dimGrid.x = nengoData->totalNumTransformRows / dimBlock.x + 1;

//  printf("transform\n");
  transform<<<dimGrid, dimBlock>>> (dt, nengoData->totalNumTransformRows, nengoData->input->array, nengoData->inputOffset->array, nengoData->transformRowToInputIndexor->array, nengoData->terminationTransforms->array, nengoData->terminationTau->array, nengoData->terminationOutput->array, nengoData->terminationOutputIndexor->array, nengoData->inputDimensions->array);
  err = cudaGetLastError();
  checkCudaError(err);

///// sum

  dimBlock.x = 256;
  dimGrid.x = nengoData->totalEnsembleDimension / dimBlock.x + 1;

  //printf("sum\n");
  sumTerminations <<<dimGrid, dimBlock>>> (nengoData->totalEnsembleDimension, nengoData->maxNumDecodedTerminations, nengoData->terminationOutput->array, nengoData->ensembleSums->array);
  err = cudaGetLastError();
  checkCudaError(err);


  //printf("ensembleSums:\n");

///// process ND (nonDecoded) terminations
  dimBlock.x = 256;
  dimGrid.x = nengoData->numEnsembles / dimBlock.x + 1;

  //printf("process ND\n");
  processNDterminations<<<dimGrid, dimBlock>>>(nengoData->numEnsembles, nengoData->numNDterminations, 1, dt, nengoData->NDterminationEnsembleOffset->array, nengoData->inputOffset->array, nengoData->NDterminationInputIndexor->array, nengoData->input->array, nengoData->NDterminationWeights->array, nengoData->NDterminationCurrents->array, nengoData->NDterminationEnsembleSums->array, nengoData->terminationTau->array);

  err = cudaGetLastError();
  checkCudaError(err);



///// encode
  int cuda_sharedMemSize = 2 * nengoData->maxDimension * sizeof(int);

  dimBlock.x = nengoData->blockSizeForEncode;
  dimGrid.x = nengoData->numBlocksForEncode;

//  printf("encode\n");
  encode<<<dimGrid, dimBlock, cuda_sharedMemSize>>> (nengoData->maxDimension, nengoData->encoders->array, nengoData->ensembleSums->array, nengoData->encodeResult->array, nengoData->blockToEnsembleMapForEncode->array, nengoData->ensembleIndexOfFirstBlockForEncode->array, nengoData->ensembleOffsetInDimensions->array, nengoData->ensembleOffsetInNeurons->array, nengoData->ensembleOffsetInEncoders->array, nengoData->ensembleNumNeurons->array, nengoData->ensembleDimension->array, nengoData->encoderStride->array);

  err = cudaGetLastError();
  checkCudaError(err);



///// integrate after encoding
  dimBlock.x = 256;
  dimGrid.x = nengoData->numNeurons / dimBlock.x + 1;

//  printf("integrate after encode\n");
  integrateAfterEncode <<<dimGrid, dimBlock>>> (nengoData->numNeurons, dt, dt, 1, nengoData->neuronToEnsembleIndexor->array, nengoData->encodeResult->array, nengoData->neuronVoltage->array, nengoData->neuronReftime->array, nengoData->ensembleTauRC->array, nengoData->ensembleTauRef->array, nengoData->neuronBias->array, nengoData->neuronScale->array, nengoData->spikes->array, nengoData->NDterminationEnsembleSums->array);

  err = cudaGetLastError();
  checkCudaError(err);


///// decode
  dimBlock.x = nengoData->blockSizeForDecode;
  dimGrid.x = nengoData->numBlocksForDecode;

  cuda_sharedMemSize = 2 * nengoData->maxNumNeurons * sizeof(int);


 // printf("decode\n");
  decode <<<dimGrid, dimBlock, cuda_sharedMemSize>>> (nengoData->maxNumNeurons, nengoData->blockToEnsembleMapForDecode->array, nengoData->ensembleNumNeurons->array, nengoData->ensembleIndexOfFirstBlockForDecode->array, nengoData->ensembleOffsetInNeurons->array, nengoData->ensembleOutputSize->array, nengoData->spikes->array, nengoData->decoders->array, nengoData->output->array, nengoData->ensembleOffsetInDecoders->array, nengoData->ensembleOffsetInOutput->array, nengoData->decoderStride->array);

  err = cudaGetLastError();
  checkCudaError(err);


//// move output to device

  //printf("copy output from device\n");
  cudaMemcpy(nengoData->outputHost->array, nengoData->output->array, nengoData->totalOutputSize * sizeof(float), cudaMemcpyDeviceToHost);
  err = cudaGetLastError();
  checkCudaError(err);
  

  //printf("copy spikes from device\n");
  cudaMemcpy(nengoData->spikesHost->array, nengoData->spikes->array, nengoData->numNeurons * sizeof(float), cudaMemcpyDeviceToHost);
  err = cudaGetLastError();
  checkCudaError(err);

//// move data along GPU projections
  dimGrid.y = nengoData->totalOutputSize / (dimBlock.x * dimBlock.y) + 1;
  //printf("move output along projections\n");
  moveGPUOutputIntoInput<<<dimGrid, dimBlock>>>(nengoData->GPUInputSize, nengoData->GPUTerminationToOriginMap->array, nengoData->input->array, nengoData->output->array);
  err = cudaGetLastError();
  checkCudaError(err);
}

float* allocateCudaFloatArray(int size)
{
  float* temp;
  cudaError_t err;
  err = cudaMalloc((void**)&temp, size * sizeof(float));
  checkCudaError(err);
  return temp;
}
  
int* allocateCudaIntArray(int size)
{
  int* temp;
  cudaError_t err;
  err = cudaMalloc((void**)&temp, size * sizeof(int));
  checkCudaError(err);
  return temp;
}
  
void initializeDeviceInputAndOutput(NengoGPUData* nengoData)
{
  char* name;
  cudaError_t err;

  name = "input";
  nengoData->input = newFloatArrayOnDevice(nengoData->totalInputSize, name); 
  
  name = "output";
  nengoData->output = newFloatArrayOnDevice(nengoData->totalOutputSize, name); 
  
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
  checkCudaError(err);
  err = cudaMemset(nengoData->output->array, 0, nengoData->totalOutputSize * sizeof(float));
  checkCudaError(err);
  err = cudaMemset(nengoData->spikes->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaError(err);
  err = cudaMemset(nengoData->terminationOutput->array, 0, nengoData->totalEnsembleDimension * nengoData->maxNumDecodedTerminations * sizeof(float));
  checkCudaError(err);
  err = cudaMemset(nengoData->neuronVoltage->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaError(err);
  err = cudaMemset(nengoData->neuronReftime->array, 0, nengoData->numNeurons * sizeof(float));
  checkCudaError(err);
  
  name = "NDterminationCurrents";
  nengoData->NDterminationCurrents = newFloatArrayOnDevice(nengoData->numNDterminations, name); 
  name = "NDterminationEnsembleSum";
  nengoData->NDterminationEnsembleSums = newFloatArrayOnDevice(nengoData->numEnsembles, name); 

  err = cudaMemset(nengoData->NDterminationCurrents->array, 0, nengoData->numNDterminations * sizeof(float));
  checkCudaError(err);
  err = cudaMemset(nengoData->NDterminationEnsembleSums->array, 0, nengoData->numEnsembles * sizeof(float));
  checkCudaError(err);
}

#ifdef __cplusplus
}
#endif

