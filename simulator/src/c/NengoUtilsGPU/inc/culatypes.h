#ifndef __EMP_CULATYPES_H__
#define __EMP_CULATYPES_H__

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

#ifdef CULA_USE_CUDA_COMPLEX
#include "cuComplex.h"
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define CULA_BASIC

#define CULA_VERSION_NUMBER 10
#define CULA_VERSION_STRING "R10 CUDA 3.2"

// Deprecated versioning symbols. Favor those above.
#define CULA_VERSION_MAJOR 10
#define CULA_VERSION_MINOR 0

/** 
 * @brief Provides extended error information for CULA functions.
 * When negative, this value specifies an argument position with an invalid
 * value.
 */
typedef int info_t;

// host types
typedef float culaFloat;
typedef double culaDouble;
typedef int culaInt;

// device types
typedef culaFloat culaDeviceFloat;
typedef culaDouble culaDeviceDouble;
typedef culaInt culaDeviceInt;

#ifndef CULA_USE_CUDA_COMPLEX
#   if defined(__GNUC__)
#       define CULA_ALIGN(n) __attribute__((__aligned__(n)))
#   elif defined(_WIN32)
#       define CULA_ALIGN(n) __declspec(align(n))
#   else
#       define CULA_ALIGN(n)
#   endif
struct CULA_ALIGN(8)  culaFloatComplex  { culaFloat  x, y; };
struct CULA_ALIGN(16) culaDoubleComplex { culaDouble x, y; };
#endif

// host complex types
#ifdef CULA_USE_CUDA_COMPLEX
typedef cuFloatComplex culaFloatComplex;
typedef cuDoubleComplex culaDoubleComplex;
#else
typedef struct culaFloatComplex culaFloatComplex;
typedef struct culaDoubleComplex culaDoubleComplex;
#endif

// device complex types
typedef culaFloatComplex culaDeviceFloatComplex;
typedef culaDoubleComplex culaDeviceDoubleComplex;

#ifdef __cplusplus
} // extern "C"
#endif

#endif // __EMP_CULATYPES_H__

