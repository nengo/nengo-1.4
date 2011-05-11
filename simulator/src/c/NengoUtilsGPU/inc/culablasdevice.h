#ifndef __EMP_CULABLAS_DEVICE_H__
#define __EMP_CULABLAS_DEVICE_H__

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

culaStatus culaDeviceCgemm(char transa, char transb, int m, int n, int k, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaFloatComplex beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCgemv(char trans, int m, int n, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* x, int incx, culaFloatComplex beta, culaDeviceFloatComplex* y, int incy);
culaStatus culaDeviceChemm(char side, char uplo, int m, int n, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaFloatComplex beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCher2k(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, float beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCherk(char uplo, char trans, int n, int k, float alpha, culaDeviceFloatComplex* a, int lda, float beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCsymm(char side, char uplo, int m, int n, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaFloatComplex beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCsyr2k(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaFloatComplex beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCsyrk(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaFloatComplex beta, culaDeviceFloatComplex* c, int ldc);
culaStatus culaDeviceCtrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb);
culaStatus culaDeviceCtrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex alpha, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb);
culaStatus culaDeviceDgemm(char transa, char transb, int m, int n, int k, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* b, int ldb, culaDouble beta, culaDeviceDouble* c, int ldc);
culaStatus culaDeviceDgemv(char trans, int m, int n, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* x, int incx, culaDouble beta, culaDeviceDouble* y, int incy);
culaStatus culaDeviceDsymm(char side, char uplo, int m, int n, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* b, int ldb, culaDouble beta, culaDeviceDouble* c, int ldc);
culaStatus culaDeviceDsyr2k(char uplo, char trans, int n, int k, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* b, int ldb, culaDouble beta, culaDeviceDouble* c, int ldc);
culaStatus culaDeviceDsyrk(char uplo, char trans, int n, int k, culaDouble alpha, culaDeviceDouble* a, int lda, culaDouble beta, culaDeviceDouble* c, int ldc);
culaStatus culaDeviceDtrmm(char side, char uplo, char transa, char diag, int m, int n, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* b, int ldb);
culaStatus culaDeviceDtrsm(char side, char uplo, char transa, char diag, int m, int n, culaDouble alpha, culaDeviceDouble* a, int lda, culaDeviceDouble* b, int ldb);
culaStatus culaDeviceSgemm(char transa, char transb, int m, int n, int k, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb, culaFloat beta, culaDeviceFloat* c, int ldc);
culaStatus culaDeviceSgemv(char trans, int m, int n, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* x, int incx, culaFloat beta, culaDeviceFloat* y, int incy);
culaStatus culaDeviceSsymm(char side, char uplo, int m, int n, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb, culaFloat beta, culaDeviceFloat* c, int ldc);
culaStatus culaDeviceSsyr2k(char uplo, char trans, int n, int k, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb, culaFloat beta, culaDeviceFloat* c, int ldc);
culaStatus culaDeviceSsyrk(char uplo, char trans, int n, int k, culaFloat alpha, culaDeviceFloat* a, int lda, culaFloat beta, culaDeviceFloat* c, int ldc);
culaStatus culaDeviceStrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb);
culaStatus culaDeviceStrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloat alpha, culaDeviceFloat* a, int lda, culaDeviceFloat* b, int ldb);
culaStatus culaDeviceZgemm(char transa, char transb, int m, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZgemv(char trans, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* x, int incx, culaDoubleComplex beta, culaDeviceDoubleComplex* y, int incy);
culaStatus culaDeviceZhemm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZher2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, double beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZherk(char uplo, char trans, int n, int k, double alpha, culaDeviceDoubleComplex* a, int lda, double beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZsymm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZsyr2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZsyrk(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc);
culaStatus culaDeviceZtrmm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb);
culaStatus culaDeviceZtrsm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb);

#ifdef __cplusplus
} // extern "C"
#endif

#endif  // __EMP_CULABLAS_DEVICE_H__

