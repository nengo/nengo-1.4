cmake_minimum_required(VERSION 2.6)

set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)
set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)

find_package(CUDA REQUIRED)
find_package(JNI REQUIRED)

set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)
set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/bin)

include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${CUDA_TOOLKIT_INCLUDE} ${CUDA_CUT_INCLUDE_DIR})

add_subdirectory("NengoGPU")
add_subdirectory("NengoUtilsGPU")

