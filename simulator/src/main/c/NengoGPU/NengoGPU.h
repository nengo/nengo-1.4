#ifndef NENGO_GPU_H
#define NENGO_GPU_H

#include <stdio.h>
#include "NengoGPUData.h"

#ifdef _MSC_VER
typedef void pthread_mutex_t;
typedef void pthread_cond_t;
#endif

extern jint totalNumEnsembles;
extern jint* deviceForEnsemble;
extern jint totalNumNetworkArrays;
extern jint* deviceForNetworkArray;
extern NengoGPUData** nengoDataArray;
extern jint numDevices;
extern float startTime;
extern float endTime;
extern volatile jint myCVsignal;
extern pthread_mutex_t* mutex;
extern pthread_cond_t* cv_GPUThreads;
extern pthread_cond_t* cv_JNI;
extern FILE* fp;

extern float* sharedInput;
extern jint sharedInputSize;

jint manipulateNumNodesProcessed(jint action, jint value);
jint manipulateKill(jint action);

void* start_GPU_thread(void* arg);
void run_start();
void run_kill();


#endif
