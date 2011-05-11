#ifndef __EMP_CULADEVICE_H__
#define __EMP_CULADEVICE_H__

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

#include "culastatus.h"

#ifdef __cplusplus
extern "C" {
#endif


/** 
 * @brief Reports the number of GPU devices
 * Can be called before culaInitialize
 *
 * @param num Pointer to receive the number of devices
 *
 * @return culaNoError on sucess, culaArgumentError on invalid pointer
 */
culaStatus culaGetDeviceCount(int* dev);


/** 
 * @brief Selects a device with which CULA will operate
 * To bind without error, this function must be called before culaInitialize
 *
 * @param dev Specifies the device id of the GPU device
 *
 * @return culaNoError on sucess, culaArgumentError on an invalid device id,
 * culaRuntimeError if the running thread has already been bound to a GPU device
 */
culaStatus culaSelectDevice(int dev);


/** 
 * @brief Reports the id of the GPU device executing CULA
 *
 * @param dev Pointer to receive the GPU device number
 *
 * @return culaNoError on sucess, culaArgumentError on invalid pointer
 */
culaStatus culaGetExecutingDevice(int* dev);


/** 
 * @brief Prints information to a buffer about a specified device
 *
 * @param dev CUDA device id to print information about
 * @param buf Pointer to a buffer into which information will be printed
 * @param bufsize The size of buf, printed information will not exceed bufsize
 *
 * @return culaNoError on sucess, culaArgumentError on invalid buf pointer,
 * invalid device id, or invalid bufsize
 */
culaStatus culaGetDeviceInfo(int dev, char* buf, int bufsize);


#ifdef __cplusplus
} // extern "C"
#endif

#endif // __EMP_CULADEVICE_H__

