#ifndef __EMP_CULAPACK_DEVICE_H__
#define __EMP_CULAPACK_DEVICE_H__

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
#include "culatypes.h"
#include "culadevice.h"

#ifdef __cplusplus
extern "C" {
#endif

culaStatus culaDeviceCgels(char trans, int m, int n, int nrhs, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb);
culaStatus culaDeviceCgeqrf(int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* tau);
culaStatus culaDeviceCgesv(int n, int nrhs, culaDeviceFloatComplex* a, int lda, culaDeviceInt* ipiv, culaDeviceFloatComplex* b, int ldb);
culaStatus culaDeviceCgesvd(char jobu, char jobvt, int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceFloat* s, culaDeviceFloatComplex* u, int ldu, culaDeviceFloatComplex* vt, int ldvt);
culaStatus culaDeviceCgetrf(int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceInt* ipiv);
culaStatus culaDeviceCgglse(int m, int n, int p, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaDeviceFloatComplex* c, culaDeviceFloatComplex* d, culaDeviceFloatComplex* x);
culaStatus culaDeviceSgels(char trans, int m, int n, int nrhs, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb);
culaStatus culaDeviceSgeqrf(int m, int n, culaDeviceFloat* a, int lda, culaDeviceFloat* tau);
culaStatus culaDeviceSgesv(int n, int nrhs, culaDeviceFloat* a, int lda, culaDeviceInt* ipiv, culaDeviceFloat* b, int ldb);
culaStatus culaDeviceSgesvd(char jobu, char jobvt, int m, int n, culaDeviceFloat* a, int lda, culaDeviceFloat* s, culaDeviceFloat* u, int ldu, culaDeviceFloat* vt, int ldvt);
culaStatus culaDeviceSgetrf(int m, int n, culaDeviceFloat* a, int lda, culaDeviceInt* ipiv);
culaStatus culaDeviceSgglse(int m, int n, int p, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb, culaDeviceFloat* c, culaDeviceFloat* d, culaDeviceFloat* x);

#ifdef __cplusplus
} // extern "C"
#endif

#endif  // __EMP_CULAPACK_DEVICE_H__

