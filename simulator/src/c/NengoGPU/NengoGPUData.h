#ifndef NENGO_GPU_DATA_H
#define NENGO_GPU_DATA_H

#ifdef __cplusplus
extern "C"{
#endif

#define NEURONS_PER_GPU 100000

enum GPUDataIndex_enum
{
  NENGO_GPU_DATA_DIMENSION = 0,
  NENGO_GPU_DATA_NUM_NEURONS,
  NENGO_GPU_DATA_NUM_ORIGINS,

  NENGO_GPU_DATA_TOTAL_INPUT_SIZE,
  NENGO_GPU_DATA_TOTAL_OUTPUT_SIZE,
  
  NENGO_GPU_DATA_MAX_TRANSFORM_DIMENSION,
  NENGO_GPU_DATA_MAX_DECODER_DIMENSION,

  NENGO_GPU_DATA_NUM_DECODED_TERMINATIONS,
  NENGO_GPU_DATA_NUM_NON_DECODED_TERMINATIONS,
  
  NENGO_GPU_DATA_NUM
};

typedef enum GPUDataIndex_enum GPUDataIndex;


struct intArray_t{
  int* array;
  int size;
  char* name;
  int onDevice;
};
typedef struct intArray_t intArray;


struct floatArray_t{
  float* array;
  int size;
  char* name;
  int onDevice;
};
typedef struct floatArray_t floatArray;

intArray* newIntArray(int size, const char* name);
void freeIntArray(intArray* a);
intArray* newIntArrayOnDevice(int size, const char* name);

floatArray* newFloatArray(int size, const char* name);
void freeFloatArray(floatArray* a);
floatArray* newFloatArrayOnDevice(int size, const char* name);

void intArraySetElement(intArray* a, int index, int value);
void floatArraySetElement(floatArray* a, int index, float value);
int intArrayGetElement(intArray* a, int index);
float floatArrayGetElement(floatArray* a, int index);

void intArraySetData(intArray* a, int* data, int dataSize);
void floatArraySetData(floatArray* a, float* data, int dataSize);


#define PROJECTION_DATA_SIZE 6
struct projection_t{
  int sourceEnsemble;
  int sourceOrigin;
  int destinationEnsemble;
  int destinationTermination;
  int size;
  int device;
};

typedef struct projection_t projection;

void storeProjection(projection* proj, int* data);
void printProjection(projection* proj);

struct NengoGPUData_t{

  FILE *fp;
  int onDevice;
  int initialized;
  int device;
  float maxTimeStep;
   
  int numNeurons;
  int numEnsembles;
  int numTerminations;
  int numNDterminations;
  int numDecodedTerminations;
  int numOrigins;

  int totalInputSize;
  int GPUInputSize;
  int CPUInputSize;
  int totalTransformSize;
  int totalNumTransformRows;
  int totalEnsembleDimension;
  int totalEncoderSize;
  int totalDecoderSize;
  int totalOutputSize;

  int maxDecodedTerminationDimension;
  int maxNumDecodedTerminations;
  int maxDimension;
  int maxNumNeurons;
  int maxOriginDimension;

  floatArray* input;
  floatArray* inputHost; // this is allocated in NengoGPU_JNI.c/setupInput
  intArray* inputOffset;

  floatArray* terminationTransforms;
  intArray* transformRowToInputIndexor;
  floatArray* terminationTau;
  intArray* inputDimensions;
  floatArray* terminationOutput;
  intArray* terminationOutputIndexor;

  floatArray* ensembleSums;
  floatArray* encoders;
  floatArray* decoders;
  floatArray* encodeResult;

  floatArray* neuronVoltage;
  floatArray* neuronReftime;
  floatArray* neuronBias;
  floatArray* neuronScale;
  floatArray* ensembleTauRC;
  floatArray* ensembleTauRef;

  intArray* ensembleNumTerminations;
  intArray* ensembleDimension;
  intArray* ensembleNumNeurons;
  intArray* ensembleOutputSize;
  intArray* ensembleNumOrigins;

//  intArray* ensembleOffsetInTerminations;
  intArray* ensembleOffsetInDimensions;
  intArray* ensembleOffsetInNeurons;
  intArray* ensembleOffsetInEncoders;
  intArray* ensembleOffsetInDecoders;
  intArray* ensembleOffsetInOutput;
  intArray* neuronToEnsembleIndexor;

  int blockSizeForEncode;
  int numBlocksForEncode;

  int blockSizeForDecode;
  int numBlocksForDecode;

  intArray* blockToEnsembleMapForEncode;
  intArray* blockToEnsembleMapForDecode;

  intArray* ensembleIndexOfFirstBlockForEncode;
  intArray* ensembleIndexOfFirstBlockForDecode;

  intArray* encoderStride;
  intArray* decoderStride;

  intArray* ensembleOrderInEncoders;
  intArray* ensembleOrderInDecoders;

  floatArray* spikes;
  floatArray* spikesHost;

  floatArray* output;
  floatArray* outputHost;

  intArray* originDimension;
  intArray* outputOffset;
  intArray* GPUTerminationToOriginMap;
  intArray* ensembleIndexInJavaArray;


  intArray* NDterminationInputIndexor;
  floatArray* NDterminationCurrents;
  floatArray* NDterminationWeights;
  intArray* NDterminationEnsembleOffset;
  floatArray* NDterminationEnsembleSums;
};

typedef struct NengoGPUData_t NengoGPUData;

NengoGPUData* getNewNengoGPUData();
void initializeNengoGPUData(NengoGPUData*);
void checkNengoGPUData(NengoGPUData*);
void moveToDeviceNengoGPUData(NengoGPUData*);
void freeNengoGPUData(NengoGPUData*);
void printNengoGPUData(NengoGPUData* currentData);
void printDynamicNengoGPUData(NengoGPUData* currentData);

void printIntArray(intArray* a, int n, int m);
void printFloatArray(floatArray* a, int n, int m);
void moveToDeviceIntArray(intArray* a);
void moveToDeviceFloatArray(floatArray* a);
void moveToHostFloatArray(floatArray* a);
void moveToHostIntArray(intArray* a);

#ifdef __cplusplus
}
#endif

#endif
