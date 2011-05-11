#ifndef __EMP_CULABLAS_HPP__
#define __EMP_CULABLAS_HPP__

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
#include "culablas.h"

#ifndef __cplusplus
#error "This header is intended to be used by a C++ compiler"
#endif

inline culaStatus culaGemm(char transa, char transb, int m, int n, int k, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         b, int ldb, culaFloat         beta, culaFloat*         c, int ldc) { return culaSgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaGemm(char transa, char transb, int m, int n, int k, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        b, int ldb, culaDouble        beta, culaDouble*        c, int ldc) { return culaDgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaGemm(char transa, char transb, int m, int n, int k, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb, culaFloatComplex  beta, culaFloatComplex*  c, int ldc) { return culaCgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaGemm(char transa, char transb, int m, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc) { return culaZgemm(transa,transb,m,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaGemv(char trans, int m, int n, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         x, int incx, culaFloat         beta, culaFloat*         y, int incy) { return culaSgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaGemv(char trans, int m, int n, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        x, int incx, culaDouble        beta, culaDouble*        y, int incy) { return culaDgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaGemv(char trans, int m, int n, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  x, int incx, culaFloatComplex  beta, culaFloatComplex*  y, int incy) { return culaCgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 
inline culaStatus culaGemv(char trans, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* x, int incx, culaDoubleComplex beta, culaDoubleComplex* y, int incy) { return culaZgemv(trans,m,n,alpha,a,lda,x,incx,beta,y,incy); } 

inline culaStatus culaSymm(char side, char uplo, int m, int n, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         b, int ldb, culaFloat         beta, culaFloat*         c, int ldc) { return culaSsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSymm(char side, char uplo, int m, int n, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        b, int ldb, culaDouble        beta, culaDouble*        c, int ldc) { return culaDsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSymm(char side, char uplo, int m, int n, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb, culaFloatComplex  beta, culaFloatComplex*  c, int ldc) { return culaCsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSymm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc) { return culaZsymm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaSyr2k(char uplo, char trans, int n, int k, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         b, int ldb, culaFloat         beta, culaFloat*         c, int ldc) { return culaSsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSyr2k(char uplo, char trans, int n, int k, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        b, int ldb, culaDouble        beta, culaDouble*        c, int ldc) { return culaDsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSyr2k(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb, culaFloatComplex  beta, culaFloatComplex*  c, int ldc) { return culaCsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaSyr2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc) { return culaZsyr2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaSyrk(char uplo, char trans, int n, int k, culaFloat         alpha, culaFloat*         a, int lda, culaFloat         beta, culaFloat*         c, int ldc) { return culaSsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaSyrk(char uplo, char trans, int n, int k, culaDouble        alpha, culaDouble*        a, int lda, culaDouble        beta, culaDouble*        c, int ldc) { return culaDsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaSyrk(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex  beta, culaFloatComplex*  c, int ldc) { return culaCsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaSyrk(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex beta, culaDoubleComplex* c, int ldc) { return culaZsyrk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 

inline culaStatus culaTrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         b, int ldb) { return culaStrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrmm(char side, char uplo, char transa, char diag, int m, int n, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        b, int ldb) { return culaDtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrmm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb) { return culaCtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrmm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb) { return culaZtrmm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 

inline culaStatus culaTrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloat         alpha, culaFloat*         a, int lda, culaFloat*         b, int ldb) { return culaStrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrsm(char side, char uplo, char transa, char diag, int m, int n, culaDouble        alpha, culaDouble*        a, int lda, culaDouble*        b, int ldb) { return culaDtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrsm(char side, char uplo, char transa, char diag, int m, int n, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb) { return culaCtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 
inline culaStatus culaTrsm(char side, char uplo, char transa, char diag, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb) { return culaZtrsm(side,uplo,transa,diag,m,n,alpha,a,lda,b,ldb); } 

inline culaStatus culaHemm(char side, char uplo, int m, int n, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb, culaFloatComplex  beta, culaFloatComplex*  c, int ldc) { return culaChemm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaHemm(char side, char uplo, int m, int n, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDoubleComplex beta, culaDoubleComplex* c, int ldc) { return culaZhemm(side,uplo,m,n,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaHer2k(char uplo, char trans, int n, int k, culaFloatComplex  alpha, culaFloatComplex*  a, int lda, culaFloatComplex*  b, int ldb, culaFloat  beta, culaFloatComplex*  c, int ldc) { return culaCher2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 
inline culaStatus culaHer2k(char uplo, char trans, int n, int k, culaDoubleComplex alpha, culaDoubleComplex* a, int lda, culaDoubleComplex* b, int ldb, culaDouble beta, culaDoubleComplex* c, int ldc) { return culaZher2k(uplo,trans,n,k,alpha,a,lda,b,ldb,beta,c,ldc); } 

inline culaStatus culaHerk(char uplo, char trans, int n, int k, culaFloat  alpha, culaFloatComplex*  a, int lda, culaFloat  beta, culaFloatComplex*  c, int ldc) { return culaCherk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 
inline culaStatus culaHerk(char uplo, char trans, int n, int k, culaDouble alpha, culaDoubleComplex* a, int lda, culaDouble beta, culaDoubleComplex* c, int ldc) { return culaZherk(uplo,trans,n,k,alpha,a,lda,beta,c,ldc); } 

#endif  // __EMP_CULABLAS_HPP__

