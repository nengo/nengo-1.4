#ifndef NENGO_GPU_H
#define NENGO_GPU_H

#ifdef __cplusplus
extern "C"{
#endif

#include <stdio.h>
#include "NengoGPUData.h"

extern int totalNumEnsembles;
extern int* deviceForEnsemble;
extern NengoGPUData** nengoData;
extern int numDevices;
extern float startTime;
extern float endTime;
extern volatile int myCVsignal;
extern pthread_mutex_t* mutex;
extern pthread_cond_t* cv_GPUThreads;
extern pthread_cond_t* cv_JNI;
extern FILE* fp;

int manipulateNumNodesProcessed(int action, int value);
int manipulateKill(int action);

void* run_nodes(void* arg);
void run_start();
void run_kill();

#ifdef __cplusplus
}
#endif

#endif
