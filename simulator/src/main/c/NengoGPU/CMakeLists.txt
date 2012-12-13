set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)
set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)

find_package(CUDA REQUIRED)

add_library(NengoGPU SHARED NengoGPUData.c NengoGPU_JNI.c NengoGPU.c NengoGPU_CUDA.cu)
