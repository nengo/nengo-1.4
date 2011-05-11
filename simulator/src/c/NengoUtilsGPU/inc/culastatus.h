#ifndef __EMP_CULASTATUS_H__
#define __EMP_CULASTATUS_H__

/*
 * Copyright (C) 2009-2010 EM Photonics, Inc.  All rights reserved.
 *
 * NOTICE TO USER:   
 *
 * This source code is subject to EM Photonics ownership rights under U.S. and
 * international Copyright laws.  Users and possessors of this source code may
 * not redistribute this code without the express written consent of EM
 * Photonics, Inc.
 *
 * EM PHOTONICS MAKES NO REPRESENTATION ABOUT THE SUITABILITY OF THIS SOURCE
 * CODE FOR ANY PURPOSE.  IT IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED
 * WARRANTY OF ANY KIND.  EM PHOTONICS DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOURCE CODE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NONINFRINGEMENT, AND FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL EM
 * PHOTONICS BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION,  ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
 * SOURCE CODE.  
 *
 * U.S. Government End Users.   This source code is a "commercial item" as that
 * term is defined at  48 C.F.R. 2.101 (OCT 1995), consisting  of "commercial
 * computer  software"  and "commercial computer software documentation" as
 * such terms are  used in 48 C.F.R. 12.212 (SEPT 1995) and is provided to the
 * U.S. Government only as a commercial end item.  Consistent with 48
 * C.F.R.12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4 (JUNE 1995), all
 * U.S. Government End Users acquire the source code with only those rights set
 * forth herein. 
 *
 * Any use of this source code in individual and commercial software must
 * include, in the user documentation and internal comments to the code, the
 * above Disclaimer and U.S. Government End Users Notice.
 *
 */

#include "culatypes.h"

#ifdef __cplusplus
extern "C" {
#endif


/** 
 * @brief Enumerates error conditions
 */
typedef enum
{
    culaNoError,                       // No error
    culaNotInitialized,                // CULA has not been initialized
    culaNoHardware,                    // No hardware is available to run
    culaInsufficientRuntime,           // CUDA runtime or driver is not supported
    culaInsufficientComputeCapability, // Available GPUs do not support the requested operation
    culaInsufficientMemory,            // There is insufficient memory to continue
    culaFeatureNotImplemented,         // The requested feature has not been implemented
    culaArgumentError,                 // An invalid argument was passed to a function
    culaDataError,                     // An operation could not complete because of singular data
    culaBlasError,                     // A blas error was encountered
    culaRuntimeError                   // A runtime error has occurred
}culaStatus;


/** 
 * @brief Initializes CULA
 * Must be called before using any other function.  Some functions have an
 * exception to this rule:  culaGetDeviceCount, culaSelectDevice
 * 
 * @return culaNoError on a successful initialization or the culaStatus enum
 * that specifies an error
 */
culaStatus culaInitialize();


/** 
 * @brief Shuts down CULA
 * Must be called to deallocate CULA internal data
 */
void culaShutdown();


/** 
 * @brief Returns the last status code returned from a CULA function
 * 
 * @return The last CULA status code
 */
culaStatus culaGetLastStatus();


/** 
 * @brief Associates a culaStatus enum with a readable error string
 * 
 * @param e A culaStatus error code
 * 
 * @return A string that corresponds with the specified culaStatus enum
 */
const char* culaGetStatusString(culaStatus e);


/** 
 * @brief This function is used to provide extended functionality that LAPACK's
 * info parameter typically provides
 *
 * @return Extended information about the last error or zero if it is
 * unavailable
 */
info_t culaGetErrorInfo();


/** 
 * @brief Associates a culaStatus and info_t with a readable error string
 *
 * @param e A culaStatus error code
 * @param i An info_t error code
 * @param buf Pointer to a buffer into which information will be printed
 * @param bufsize The size of buf, printed information will not exceed bufsize
 *
 * @return culaNoError on a successful error report or culaArgumentError on an
 * invalid argument to this function
 */
culaStatus culaGetErrorInfoString(culaStatus e, info_t i, char* buf, int bufsize);


/** 
 * @brief Releases any memory buffers stored internally by CULA
 */
void culaFreeBuffers();


#ifdef __cplusplus
} // extern "C"
#endif

#endif  // __EMP_CULASTATUS_H__

