#ifndef NENGO_GPU_CUDA_H
#define NENGO_GPU_CUDA_H

#ifdef __cplusplus
extern "C"{
#endif

#include <cuda_runtime.h>

#include "NengoGPUData.h"

void printIntArrayFromDevice(FILE* fp, int* array, int size);
void printFloatArrayFromDevice(FILE* fp, float* array, int size);

int getGPUDeviceCount();

void initGPUDevice(int device);

void shutdownGPUDevice();

void checkCudaError(cudaError_t err);

__global__ void transformAndIntegrate(float dt, int totalNumTerminationRows, float* input, float* transforms, float* terminationTauValues, float* terminationValues, int* inputIndices, int* transformIndices, int* terminationRowToTerminationIndexor, int* terminationDimensions);

__global__ void sumTerminations(int totalDimensions, float* ensembleSums, float* terminationValues, int* dimensionToEnsembleIndexor, int* dimensionIndexInEnsemble, int* ensembleDimensions, int* ensembleTerminations, int* ensemblePositionsInTerminationValues, int* sumHelper);

__global__ void encodeAndIntegrate(float dt, float adjusted_dt, int steps, int totalNumNeurons, float* encoders, float* ensembleSums, int* encoderIndices, int* sumIndices, int* ensembleDimensions, float* neuronVoltage, float* neuronReftime, float* spikes, int* spikesHelper, int* neuronToEnsembleIndexor, float* ensembleTauRC, float* ensembleTauRef, float* bias, float* scale);

__global__ void decode(int totalOutputSize, float* decoder, float* spikes, int* ensembleNumNeurons, float* output, int* decoderIndices, int* spikeIndices, int* decoderRowToEnsembleIndexor);

void run_NEFEnsembles(NengoGPUData*, float, float);

#ifdef __cplusplus
}
#endif

#endif 
