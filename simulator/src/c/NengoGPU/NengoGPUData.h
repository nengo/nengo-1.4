#ifndef NENGO_GPU_DATA_H
#define NENGO_GPU_DATA_H

#ifdef __cplusplus
extern "C"{
#endif

#define NEURONS_PER_GPU 100000

enum GPUDataIndex_enum
{
  NENGO_GPU_DATA_DIMENSION = 0,
  NENGO_GPU_DATA_NUM_TERMINATIONS,
  NENGO_GPU_DATA_NUM_NEURONS,
  NENGO_GPU_DATA_NUM_ORIGINS,

  NENGO_GPU_DATA_TOTAL_INPUT_SIZE,
  NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE,
  
  NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION,
  NENGO_GPU_DATA_MAX_DECODER_DIMENSION,
  
  NENGO_GPU_DATA_NUM
};

typedef enum GPUDataIndex_enum GPUDataIndex;

struct NengoGPUData_t{

  int onDevice;
  int device;
  float maxTimeStep;
   
  int numNeurons;
  int numEnsembles;
  int numTerminations;
  int numOrigins;

  int totalInputSize;
  int totalTransformSize;
  int totalNumTransformRows;
  int totalDimension;
  int totalEncoderSize;
  int totalDecoderSize;
  int totalNumDecoderRows;
  int totalOutputSize;

  int maxTransformDimension;
  int maxDimension;
  int maxNumNeurons;
  int maxDecoderDimension;


  float* input;
  int* inputIndexor;

  float* terminationTransforms;
  int* terminationTransformIndices;
  float* terminationTau;
  int* terminationDimensions;
  int* terminationDimensionsHost;
  float* terminationValues;
  int* terminationRowToTerminationIndexor;

  int* ensembleNeurons;
  int* ensembleNeuronsHost;
  int* ensembleDimensions;
  int* ensembleTerminations;
  int* dimensionToEnsembleIndexor;
  int* ensemblePositionsInTerminationValues;

  float* ensembleSums;
  int* dimensionIndexInEnsemble;
  int* sumIndices;
  int* sumHelper;

  float* encoders;
  int* encoderIndices;

  float* neuronVoltage;
  float* neuronReftime;
  float* neuronBias;
  float* neuronScale;
  int* neuronToEnsembleIndexor;
  float* ensembleTauRC;
  float* ensembleTauRef;

  float* spikes;
  float* spikesHost;
  int* spikeHelper;
  int* spikeIndices;
  int* spikeIndicesHost;
  unsigned char* collectSpikes;

  float* decoders;
  int* decoderIndices;
  int* decoderDimension;
  int* decoderRowToEnsembleIndexor;
  
  float* output;
};

typedef struct NengoGPUData_t NengoGPUData;

NengoGPUData* getNewNengoGPUData();
void initializeNengoGPUData(NengoGPUData*);
void checkNengoGPUData(NengoGPUData*);
void moveToDeviceNengoGPUData(NengoGPUData*);
void freeNengoGPUData(NengoGPUData*);
void printNengoGPUData(NengoGPUData* currentData);

void printIntArray(int* array, int m, int onDevice);
void printFloatArray(float* array, int m, int onDevice);
int* moveToDeviceIntArray(int* array, int size);
float* moveToDeviceFloatArray(float* array, int size);
float* moveToHostFloatArray(float* array, int size);
int* moveToHostIntArray(int* array, int size);

#ifdef __cplusplus
}
#endif

#endif
