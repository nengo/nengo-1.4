
CC=gcc
CUDA_CC=nvcc
CFLAGS=-fPIC -c -m64 -O3 -Wall  
CUDAFLAGS=  
CUDA_INCLUDES=-I${CULA_INC_PATH} -I${CUDA_INC_PATH} -I/home/e2crawfo/NVIDIA_GPU_Computing_SDK/C/common/inc
JAVA_INCLUDES=-I${JAVA_INC} -I${JAVA_INC_LINUX}
LIBPATH32=-L${CULA_LIB_PATH_32}
LIBPATH64=-L${CULA_LIB_PATH_64}

LIBS=-lcula -lcublas -lcudart -lm 

libNengoGPU.so: NengoGPU_JNI.o NengoGPU.o NengoGPU_CUDA.o NengGPUData.o
	${CC} -shared -Wl,-soname,libNengoGPU.so -o libNengoGPU.so.1.0 NengoGPU_JNI.o NengoGPU.o NengoGPU_CUDA.o NengoGPUData.o -lc \
	-I. ${CUDA_INCLUDES} ${JAVA_INCLUDES} ${LIBPATH64} ${LIBS}

NengGPUData.o: NengoGPUData.c NengoGPUData.h
	${CC} ${CFLAGS} -o NengoGPUData.o NengoGPUData.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH64} ${LIBS}

NengoGPU_CUDA.o: NengoGPU_CUDA.cu NengoGPU_CUDA.h NengoGPU.h
	${CUDA_CC} ${CUDAFLAGS} -lib -o NengoGPU_CUDA.o --compiler-options '-fPIC' NengoGPU_CUDA.cu ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH64}

NengoGPU.o : NengoGPU.c NengoGPU.h NengoGPU_CUDA.h
	${CC} ${CFLAGS} -o NengoGPU.o NengoGPU.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH64} ${LIBS}

NengoGPU_JNI.o : NengoGPU_JNI.c NengoGPU.h GPUThread_JNI.h
	${CC} ${CFLAGS} -o NengoGPU_JNI.o NengoGPU_JNI.c ${JAVA_INCLUDES} ${CUDA_INCLUDES} ${LIBPATH64} ${LIBS}

clean:
	rm -rf libNengoGPU.so.1.0 *.o
