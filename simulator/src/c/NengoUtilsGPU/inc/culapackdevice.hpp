#ifndef __EMP_CULAPACKDEVICE_HPP__
#define __EMP_CULAPACKDEVICE_HPP__

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
#include "culapackdevice.h"

#ifndef __cplusplus
#error "This header is intended to be used by a C++ compiler"
#endif

inline culaStatus culaDeviceGels(char trans, int m, int n, int nrhs, culaDeviceFloat*        a, int lda, culaDeviceFloat*        b, int ldb) { return culaDeviceSgels(trans,m,n,nrhs,a,lda,b,ldb); } 
inline culaStatus culaDeviceGels(char trans, int m, int n, int nrhs, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb) { return culaDeviceCgels(trans,m,n,nrhs,a,lda,b,ldb); } 

inline culaStatus culaDeviceGeqrf(int m, int n, culaDeviceFloat*        a, int lda, culaDeviceFloat*        tau) { return culaDeviceSgeqrf(m,n,a,lda,tau); } 
inline culaStatus culaDeviceGeqrf(int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* tau) { return culaDeviceCgeqrf(m,n,a,lda,tau); } 

inline culaStatus culaDeviceGesv(int n, int nrhs, culaDeviceFloat*        a, int lda, culaDeviceInt* ipiv, culaDeviceFloat*        b, int ldb) { return culaDeviceSgesv(n,nrhs,a,lda,ipiv,b,ldb); } 
inline culaStatus culaDeviceGesv(int n, int nrhs, culaDeviceFloatComplex* a, int lda, culaDeviceInt* ipiv, culaDeviceFloatComplex* b, int ldb) { return culaDeviceCgesv(n,nrhs,a,lda,ipiv,b,ldb); } 

inline culaStatus culaDeviceGesvd(char jobu, char jobvt, int m, int n, culaDeviceFloat*        a, int lda, culaDeviceFloat* s, culaDeviceFloat*        u, int ldu, culaDeviceFloat*        vt, int ldvt) { return culaDeviceSgesvd(jobu,jobvt,m,n,a,lda,s,u,ldu,vt,ldvt); } 
inline culaStatus culaDeviceGesvd(char jobu, char jobvt, int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceFloat* s, culaDeviceFloatComplex* u, int ldu, culaDeviceFloatComplex* vt, int ldvt) { return culaDeviceCgesvd(jobu,jobvt,m,n,a,lda,s,u,ldu,vt,ldvt); } 

inline culaStatus culaDeviceGetrf(int m, int n, culaDeviceFloat*        a, int lda, culaDeviceInt* ipiv) { return culaDeviceSgetrf(m,n,a,lda,ipiv); } 
inline culaStatus culaDeviceGetrf(int m, int n, culaDeviceFloatComplex* a, int lda, culaDeviceInt* ipiv) { return culaDeviceCgetrf(m,n,a,lda,ipiv); } 

inline culaStatus culaDeviceGglse(int m, int n, int p, culaDeviceFloat*        a, int lda, culaDeviceFloat*        b, int ldb, culaDeviceFloat*        c, culaDeviceFloat*        d, culaDeviceFloat*        x) { return culaDeviceSgglse(m,n,p,a,lda,b,ldb,c,d,x); } 
inline culaStatus culaDeviceGglse(int m, int n, int p, culaDeviceFloatComplex* a, int lda, culaDeviceFloatComplex* b, int ldb, culaDeviceFloatComplex* c, culaDeviceFloatComplex* d, culaDeviceFloatComplex* x) { return culaDeviceCgglse(m,n,p,a,lda,b,ldb,c,d,x); } 

#endif  // __EMP_CULAPACKDEVICE_HPP__

