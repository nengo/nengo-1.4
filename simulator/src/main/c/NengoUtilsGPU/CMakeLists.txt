set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)
set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)

add_library(NengoUtilsGPU SHARED customCudaUtils.c weightedCostApproximatorGPU.cu NengoUtilsGPU_JNI.c)
