#include <stdlib.h>
#include <cuda_runtime.h>
#include <cublas.h>

#include "customCudaUtils.h"
    
#ifdef NENGO_USE_CULA
#include <cula_lapack_device.h>
#endif

// get number of devices available
jint getGPUDeviceCount(){
  cudaError_t err;
  jint numDevices;
  
  err = cudaGetDeviceCount(&numDevices);
  checkCudaError(err);
  
  return numDevices;
}

// get the max or min in a float array. type is 'M' for max, 'm' for min
float findExtremeFloatArray(float* A, jint size, jint onDevice, char type, jint* index)
{
  cudaError_t err;

  if(onDevice)
  {
    float* temp = A;
    A = (float*)malloc(size * sizeof(float));
    
    err = cudaMemcpy(A, temp, size * sizeof(float), cudaMemcpyDeviceToHost);
    checkCudaError(err);
  }

  jint i;
  float max = 0;
  for(i = 0; i < size; i++)
  {
    if(type == 'M')
    {
      if(A[i] > max)
      {
        max = A[i];
        *index = i;
      }
    }else if(type == 'm')
    {
      if(A[i] < max)
      {
        max = A[i];
        *index = i;
      }
    }
  }

  if(onDevice)
    free(A);

  return max;
}

void printFloatArray(float* A, jint M, jint N, jint onDevice)
{
  cudaError_t err;

  if(onDevice)
  {
    float* temp = A;
    A = (float*)malloc(M * N * sizeof(float));
    
    err = cudaMemcpy(A, temp, M * N * sizeof(float), cudaMemcpyDeviceToHost);
    checkCudaError(err);
  }

  jint i, j;
  for(i = 0; i < M; i++)
  {
    for(j = 0; j < N; j++)
    {
      printf("%f ", A[j + i * N]);
    }
    printf("\n");
  }

  printf("\n");

  if(onDevice)
  {
    free(A);
  }
}


// Takes in two arrays, each of size ldA * ldB. It takes the contents of A and stores it in B
// in the opposite storage orientation (eg if A is row major, then B comes out column major)
// lda is the leading dimension of A, ldb that of B
void switchFloatArrayStorage(float* A, float* B, jint ldA, jint ldB)
{
    jint i, j;

    for(i=0;i<ldA; i++)
    {
       for(j=0;j<ldB; j++)
       {
          B[i*ldB + j] = A[j*ldA + i];
       }
    }
}

void printMatrixToFile(FILE *fp, float* A, jint M, jint N)
{
    jint i = 0;

    for(;i<N*M;i++)
    {
        fprintf(fp, "%f ", A[i]);
      if(i % M == M - 1)
      {
         fprintf(fp, "\n");
      }
    }
    fprintf(fp, "\n");
}

void printDeviceMatrixToFile(FILE *fp, float* devicePointer, jint M, jint N)
{
	float* temp = (float*)malloc(N*M*sizeof(float));

	cudaMemcpy(temp, devicePointer, N*M*sizeof(float), cudaMemcpyDeviceToHost);

	printMatrixToFile(fp, temp, M, N);

	free(temp);
}

#ifdef NENGO_USE_CULA
__host__ void checkStatus(culaStatus status)
{
    if(!status)
        return;

    if(status == culaArgumentError)
        printf("Invalid value for parameter %d\n", culaGetErrorInfo());
    else if(status == culaDataError)
        printf("Data error (%d)\n", culaGetErrorInfo());
    else if(status == culaBlasError)
        printf("Blas error (%d)\n", culaGetErrorInfo());
    else if(status == culaRuntimeError)
        printf("Runtime error (%d)\n", culaGetErrorInfo());
    else
        printf("%s\n", culaGetStatusString(status));

    checkCudaError(cudaGetLastError());
    culaShutdown();
    exit(EXIT_FAILURE);
}
#endif

__host__ void checkCudaError(cudaError_t err)
{
    if(!err)
        return;

    printf("%s\n", cudaGetErrorString(err));
#ifdef NENGO_USE_CULA
    culaShutdown();
#endif
}

