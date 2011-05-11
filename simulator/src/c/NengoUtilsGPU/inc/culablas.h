#ifndef __EMP_CULABLAS_H__
#define __EMP_CULABLAS_H__

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

#ifdef __cplusplus
extern "C" {
#endif

culaStatus culaCgemm(char transa, char transb, int m, int n, int k, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, culaFloatComplex beta, culaFloatComplex* c, int ldc);
culaStatus culaCgemv(char trans, int m, int n, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* x, int incx, culaFloatComplex beta, culaFloatComplex* y, int incy);
culaStatus culaChemm(char side, char uplo, int m, int n, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, culaFloatComplex beta, culaFloatComplex* c, int ldc);
culaStatus culaCher2k(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, float beta, culaFloatComplex* c, int ldc);
culaStatus culaCherk(char uplo, char trans, int n, int k, float alpha, culaFloatComplex* a, int lda, float beta, culaFloatComplex* c, int ldc);
culaStatus culaCsymm(char side, char uplo, int m, int n, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, culaFloatComplex beta, culaFloatComplex* c, int ldc);
culaStatus culaCsyr2k(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, culaFloatComplex beta, culaFloatComplex* c, int ldc);
culaStatus culaCsyrk(char uplo, char trans, int n, int k, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex beta, culaFloatComplex* c, int ldc);
culaStatus culaCtrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb);
culaStatus culaCtrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex alpha, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb);
culaStatus culaDgemm(char transa, char transb, int m, int n, int k, culaDouble alpha, culaDouble* a, int lda, culaDouble* b, int ldb, culaDouble beta, culaDouble* c, int ldc);
culaStatus culaDgemv(char trans, int m, int n, culaDouble alpha, culaDouble* a, int lda, culaDouble* x, int incx, culaDouble beta, culaDouble* y, int incy);
culaStatus culaDsymm(char side, char uplo, int m, int n, culaDouble alpha, culaDouble* a, int lda, culaDouble* b, int ldb, culaDouble beta, culaDouble* c, int ldc);
culaStatus culaDsyr2k(char uplo, char trans, int n, int k, culaDouble alpha, culaDouble* a, int lda, culaDouble* b, int ldb, culaDouble beta, culaDouble* c, int ldc);
culaStatus culaDsyrk(char uplo, char trans, int n, int k, culaDouble alpha, culaDouble* a, int lda, culaDouble beta, culaDouble* c, int ldc);
culaStatus culaDtrmm(char side, char uplo, char transa, char diag, int m, int n, culaDouble alpha, culaDouble* a, int lda, culaDouble* b, int ldb);
culaStatus culaDtrsm(char side, char uplo, char transa, char diag, int m, int n, culaDouble alpha, culaDouble* a, int lda, culaDouble* b, int ldb);
culaStatus culaSgemm(char transa, char transb, int m, int n, int k, culaFloat alpha, culaFloat* a, int lda, culaFloat* b, int ldb, culaFloat beta, culaFloat* c, int ldc);
culaStatus culaSgemv(char trans, int m, int n, culaFloat alpha, culaFloat* a, int lda, culaFloat* x, int incx, culaFloat beta, culaFloat* y, int incy);
culaStatus culaSsymm(char side, char uplo, int m, int n, culaFloat alpha, culaFloat* a, int lda, culaFloat* b, int ldb, culaFloat beta, culaFloat* c, int ldc);
culaStatus culaSsyr2k(char uplo, char trans, int n, int k, culaFloat alpha, culaFloat* a, int lda, culaFloat* b, int ldb, culaFloat beta, culaFloat* c, int ldc);
culaStatus culaSsyrk(char uplo, char trans, int n, int k, culaFloat alpha, culaFloat* a, int lda, culaFloat beta, culaFloat* c, int ldc);
culaStatus culaStrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloat alpha, culaFloat* a, int lda, culaFloat* b, int ldb);
culaStatus culaStrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloat alpha, culaFloat* a, int lda, culaFloat* b, int ldb);
culaStatus culaZgemm(char transa, char transb, int m, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc);
culaStatus culaZgemv(char trans, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* x, int incx, culaDoubleComplex beta, culaDoubleComplex* y, int incy);
culaStatus culaZhemm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc);
culaStatus culaZher2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, double beta, culaDoubleComplex* c, int ldc);
culaStatus culaZherk(char uplo, char trans, int n, int k, double alpha, culaDoubleComplex* a, int lda, double beta, culaDoubleComplex* c, int ldc);
culaStatus culaZsymm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc);
culaStatus culaZsyr2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc);
culaStatus culaZsyrk(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex beta, culaDoubleComplex* c, int ldc);
culaStatus culaZtrmm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb);
culaStatus culaZtrsm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb);

#ifdef __cplusplus
} // extern "C"
#endif

#endif  // __EMP_CULABLAS_H__

