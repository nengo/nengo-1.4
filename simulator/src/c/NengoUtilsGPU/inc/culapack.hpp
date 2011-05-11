#ifndef __EMP_CULAPACK_HPP__
#define __EMP_CULAPACK_HPP__

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
#include "culapack.h"

#ifndef __cplusplus
#error "This header is intended to be used by a C++ compiler"
#endif

inline culaStatus culaGels(char trans, int m, int n, int nrhs, culaFloat*        a, int lda, culaFloat*        b, int ldb) { return culaSgels(trans,m,n,nrhs,a,lda,b,ldb); } 
inline culaStatus culaGels(char trans, int m, int n, int nrhs, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb) { return culaCgels(trans,m,n,nrhs,a,lda,b,ldb); } 

inline culaStatus culaGeqrf(int m, int n, culaFloat*        a, int lda, culaFloat*        tau) { return culaSgeqrf(m,n,a,lda,tau); } 
inline culaStatus culaGeqrf(int m, int n, culaFloatComplex* a, int lda, culaFloatComplex* tau) { return culaCgeqrf(m,n,a,lda,tau); } 

inline culaStatus culaGesv(int n, int nrhs, culaFloat*        a, int lda, culaInt* ipiv, culaFloat*        b, int ldb) { return culaSgesv(n,nrhs,a,lda,ipiv,b,ldb); } 
inline culaStatus culaGesv(int n, int nrhs, culaFloatComplex* a, int lda, culaInt* ipiv, culaFloatComplex* b, int ldb) { return culaCgesv(n,nrhs,a,lda,ipiv,b,ldb); } 

inline culaStatus culaGesvd(char jobu, char jobvt, int m, int n, culaFloat*        a, int lda, culaFloat* s, culaFloat*        u, int ldu, culaFloat*        vt, int ldvt) { return culaSgesvd(jobu,jobvt,m,n,a,lda,s,u,ldu,vt,ldvt); } 
inline culaStatus culaGesvd(char jobu, char jobvt, int m, int n, culaFloatComplex* a, int lda, culaFloat* s, culaFloatComplex* u, int ldu, culaFloatComplex* vt, int ldvt) { return culaCgesvd(jobu,jobvt,m,n,a,lda,s,u,ldu,vt,ldvt); } 

inline culaStatus culaGetrf(int m, int n, culaFloat*        a, int lda, culaInt* ipiv) { return culaSgetrf(m,n,a,lda,ipiv); } 
inline culaStatus culaGetrf(int m, int n, culaFloatComplex* a, int lda, culaInt* ipiv) { return culaCgetrf(m,n,a,lda,ipiv); } 

inline culaStatus culaGglse(int m, int n, int p, culaFloat*        a, int lda, culaFloat*        b, int ldb, culaFloat*        c, culaFloat*        d, culaFloat*        x) { return culaSgglse(m,n,p,a,lda,b,ldb,c,d,x); } 
inline culaStatus culaGglse(int m, int n, int p, culaFloatComplex* a, int lda, culaFloatComplex* b, int ldb, culaFloatComplex* c, culaFloatComplex* d, culaFloatComplex* x) { return culaCgglse(m,n,p,a,lda,b,ldb,c,d,x); } 

#endif  // __EMP_CULAPACK_HPP__

