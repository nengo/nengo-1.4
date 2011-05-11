#ifndef __EMP_CULABLASDEVICE_HPP__
#define __EMP_CULABLASDEVICE_HPP__

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
#include "culablasdevice.h"

#ifndef __cplusplus
#error "This header is intended to be used by a C++ compiler"
#endif

inline culaStatus culaDeviceGemm(char transa, char transb, int m, int n, int k, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         b, int ldb, culaFloat         beta, culaDeviceFloat*         c, int ldc) { return culaDeviceSgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceGemm(char transa, char transb, int m, int n, int k, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        b, int ldb, culaDouble        beta, culaDeviceDouble*        c, int ldc) { return culaDeviceDgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceGemm(char transa, char transb, int m, int n, int k, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb, culaFloatComplex  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceGemm(char transa, char transb, int m, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaDeviceGemv(char trans, int m, int n, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         x, int incx, culaFloat         beta, culaDeviceFloat*         y, int incy) { return culaDeviceSgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaDeviceGemv(char trans, int m, int n, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        x, int incx, culaDouble        beta, culaDeviceDouble*        y, int incy) { return culaDeviceDgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaDeviceGemv(char trans, int m, int n, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  x, int incx, culaFloatComplex  beta, culaDeviceFloatComplex*  y, int incy) { return culaDeviceCgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaDeviceGemv(char trans, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* x, int incx, culaDoubleComplex beta, culaDeviceDoubleComplex* y, int incy) { return culaDeviceZgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 

inline culaStatus culaDeviceSymm(char side, char uplo, int m, int n, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         b, int ldb, culaFloat         beta, culaDeviceFloat*         c, int ldc) { return culaDeviceSsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSymm(char side, char uplo, int m, int n, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        b, int ldb, culaDouble        beta, culaDeviceDouble*        c, int ldc) { return culaDeviceDsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSymm(char side, char uplo, int m, int n, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb, culaFloatComplex  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSymm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaDeviceSyr2k(char uplo, char trans, int n, int k, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         b, int ldb, culaFloat         beta, culaDeviceFloat*         c, int ldc) { return culaDeviceSsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSyr2k(char uplo, char trans, int n, int k, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        b, int ldb, culaDouble        beta, culaDeviceDouble*        c, int ldc) { return culaDeviceDsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSyr2k(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb, culaFloatComplex  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceSyr2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaDeviceSyrk(char uplo, char trans, int n, int k, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaFloat         beta, culaDeviceFloat*         c, int ldc) { return culaDeviceSsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaDeviceSyrk(char uplo, char trans, int n, int k, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDouble        beta, culaDeviceDouble*        c, int ldc) { return culaDeviceDsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaDeviceSyrk(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaFloatComplex  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaDeviceSyrk(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 

inline culaStatus culaDeviceTrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         b, int ldb) { return culaDeviceStrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrmm(char side, char uplo, char transa, char diag, int m, int n, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        b, int ldb) { return culaDeviceDtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb) { return culaDeviceCtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrmm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb) { return culaDeviceZtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 

inline culaStatus culaDeviceTrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloat         alpha, culaDeviceFloat*         a, int lda, culaDeviceFloat*         b, int ldb) { return culaDeviceStrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrsm(char side, char uplo, char transa, char diag, int m, int n, culaDouble        alpha, culaDeviceDouble*        a, int lda, culaDeviceDouble*        b, int ldb) { return culaDeviceDtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb) { return culaDeviceCtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaDeviceTrsm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb) { return culaDeviceZtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 

inline culaStatus culaDeviceHemm(char side, char uplo, int m, int n, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb, culaFloatComplex  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceChemm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceHemm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZhemm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaDeviceHer2k(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaDeviceFloatComplex*  a, int lda, culaDeviceFloatComplex*  b, int ldb, culaFloat  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCher2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaDeviceHer2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDeviceDoubleComplex* a, int lda, culaDeviceDoubleComplex* b, int ldb, culaDouble beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZher2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaDeviceHerk(char uplo, char trans, int n, int k, culaFloat  alpha, culaDeviceFloatComplex*  a, int lda, culaFloat  beta, culaDeviceFloatComplex*  c, int ldc) { return culaDeviceCherk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaDeviceHerk(char uplo, char trans, int n, int k, culaDouble alpha, culaDeviceDoubleComplex* a, int lda, culaDouble beta, culaDeviceDoubleComplex* c, int ldc) { return culaDeviceZherk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 

#endif  // __EMP_CULABLASDEVICE_HPP__

