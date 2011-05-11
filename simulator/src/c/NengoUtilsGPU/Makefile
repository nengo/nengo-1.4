CUDA_BIN_PATH=/usr/local/cuda/bin
CUDA_INC_PATH=/usr/local/cuda/include
CUDA_LIB_PATH=/usr/local/cuda/lib64

CULA_INC_PATH=/home/e2crawfo/Nengo/simulator/src/c/NengoUtilsGPU/inc
CULA_LIB_PATH=/home/e2crawfo/Nengo/simulator/src/c/NengoUtilsGPU/lib

JAVA_INC=/usr/lib/jvm/java-6-sun-1.6.0.22/include
JAVA_INC_LINUX=/usr/lib/jvm/java-6-sun-1.6.0.22/include/linux

PATH:=${PATH}:${CUDA_BIN_PATH}:${CULA_BIN_PATH}

CC=gcc
CUDA_CC=nvcc
CUDA_INCLUDES=-I${CULA_INC_PATH} -I${CUDA_INC_PATH}
JAVA_INCLUDES=-I${JAVA_INC} -I${JAVA_INC_LINUX}

LIBS=-lcula -lcublas -lcudart -lm

libNengoUtilsGPU.so: customCudaUtils.o weightedCostApproximatorGPU.o NengoUtilsGPU_JNI.o
	${CC} -shared -Wl,-soname,libNengoUtilsGPU.so -o libNengoUtilsGPU.so.1.0 customCudaUtils.o weightedCostApproximatorGPU.o NengoUtilsGPU_JNI.o -lc \
	-I. ${CUDA_INCLUDES} ${JAVA_INCLUDES} -L${CULA_LIB_PATH} -L${CUDA_LIB_PATH} ${LIBS}

customCudaUtils.o : customCudaUtils.c customCudaUtils.h
	${CC} -fPIC -c -m64 -Wall -o customCudaUtils.o customCudaUtils.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} -L${CUDA_LIB_PATH} -L${CULA_LIB_PATH}

weightedCostApproximatorGPU.o : weightedCostApproximatorGPU.cu NengoUtilsGPU.h
	${CUDA_CC} -c -o weightedCostApproximatorGPU.o --compiler-options '-fPIC' weightedCostApproximatorGPU.cu ${CUDA_INCLUDES} -L${CUDA_LIB_PATH} -L${CULA_LIB_PATH}

NengoUtilsGPU_JNI.o : NengoUtilsGPU_JNI.c NengoUtilsGPU.h weightedCostApproximator_JNI.h
	${CC} -fPIC -c -m64 -Wall -o NengoUtilsGPU_JNI.o NengoUtilsGPU_JNI.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} -L${CUDA_LIB_PATH} -L${CULA_LIB_PATH}

clean:
	rm -rf libNengoUtilsGPU.so.1.0 *.o
