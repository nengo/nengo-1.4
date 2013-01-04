#ifndef CUSTOM_CUDA_UTILS_H
#define CUSTOM_CUDA_UTILS_H

#include<stdio.h>
#include<cuda_runtime.h>
#ifdef NENGO_USE_CULA
#include<cula_lapack_device.h>
#endif

int getGPUDeviceCount();
float findExtremeFloatArray(float* A, int size, int onDevice, char type, int* index);
void printFloatArray(float* A, int M, int N, int onDevice);
void switchFloatArrayStorage(float* A, float* B, int ldA, int ldB);
void printMatrixToFile(FILE *fp, float* A, int M, int N);
void printDeviceMatrixToFile(FILE *fp, float* devicePointer, int M, int N);

#ifdef NENGO_USE_CULA
__host__ void checkStatus(culaStatus);
#endif
__host__ void checkCudaError(cudaError_t);

#endif
