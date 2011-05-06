
CUDA_BIN_PATH=/usr/local/cuda/bin
CUDA_INC_PATH=/usr/local/cuda/include
CUDA_LIB_PATH=/usr/local/cuda/lib64
JAVA_INC=/usr/lib/jvm/java-6-sun-1.6.0.22/include
JAVA_INC_LINUX=/usr/lib/jvm/java-6-sun-1.6.0.22/include/linux
PATH:=${PATH}:${CUDA_BIN_PATH}

CC=gcc
CUDA_CC=nvcc
CFLAGS=-fPIC -c -m64 -O3 -Wall
CUDAFLAGS= --ptxas-options=--verbose
CUDA_INCLUDES= -I${CUDA_INC_PATH} -I${HOME}/NVIDIA_GPU_Computing_SDK/C/common/inc
JAVA_INCLUDES=-I${JAVA_INC} -I${JAVA_INC_LINUX}
LIBPATH=-L${CUDA_LIB_PATH}

LIBS=-lcudart -lm

libNengoGPU.so: NengoGPU_CUDA.o NengoGPU.o NengoGPUData.o NengoGPU_JNI.o GraphTheoryRoutines.o fheap.o
	${CC} -shared -Wl,-soname,libNengoGPU.so -o libNengoGPU.so.1.0 NengoGPU_JNI.o NengoGPU.o NengoGPU_CUDA.o NengoGPUData.o GraphTheoryRoutines.o fheap.o -lc \
	-I. ${CUDA_INCLUDES} ${JAVA_INCLUDES} ${LIBPATH} ${LIBS}

fheap.o: fheap.c fheap.h heap_info.h
	${CC} ${CFLAGS} -o fheap.o fheap.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

GraphTheoryRoutines.o: GraphTheoryRoutines.c GraphTheoryRoutines.h fheap.h
	${CC} ${CFLAGS} -o GraphTheoryRoutines.o GraphTheoryRoutines.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

NengoGPUData.o: NengoGPUData.c NengoGPUData.h
	${CC} ${CFLAGS} -o NengoGPUData.o NengoGPUData.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

NengoGPU_CUDA.o: NengoGPU_CUDA.cu NengoGPU_CUDA.h NengoGPU.h
	${CUDA_CC} ${CUDAFLAGS} -lib -o NengoGPU_CUDA.o --compiler-options '-fPIC' NengoGPU_CUDA.cu ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

NengoGPU.o : NengoGPU.c NengoGPU.h NengoGPU_CUDA.h
	${CC} ${CFLAGS} -o NengoGPU.o NengoGPU.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

NengoGPU_JNI.o : NengoGPU_JNI.c NengoGPU.h NEFGPUInterface_JNI.h NengoGPU_CUDA.h
	${CC} ${CFLAGS} -o NengoGPU_JNI.o NengoGPU_JNI.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH} ${LIBS}

clean:
	rm -rf libNengoGPU.so.1.0 *.o
