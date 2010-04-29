#ifndef NENGO_GPU_JNI_H
#define NENGO_GPU_JNI_H

#ifdef __cplusplus
extern "C"{
#endif

#include <stdio.h>
#include <jni.h>

void setGPUData(int* GPUData, int i, GPUDataIndex element, int value);
int getGPUData(int* GPUData, int i, GPUDataIndex element);
void getDecoders(JNIEnv* env, NengoGPUData* currentData, int* deviceForEnsemble, jobjectArray decoders_JAVA);
void createIndexArrayAndGatherData(int gatherData, float* data, float* tempData, int* indices, int* columnLengths, int* dataIndexor, int numRows, int numColumns );
void createHelper(int* helper, int* columnLengths, int numRows, int numColumns);

#ifdef __cplusplus
}
#endif

#endif
