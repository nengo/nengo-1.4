
from numeric import array,reshape,zeros,dot,ravel
import numeric as num
import math

import nef.simplenode
class DirectConvolution2D(nef.simplenode.SimpleNode):
    def __init__(self,name,Ashape,Bshape,rotateA=False,rotateB=False,pstc_gate=0.01,pstc_input=0):
        self.A = zeros( Ashape )
        self.B = zeros( Bshape )
        self.rotateA = rotateA
        self.rotateB = rotateB
        self.gate = 0
        # Determine DFT matrices
        ra,ca = Ashape
        rb,cb = Bshape
        M,N = (ra+rb, ca+cb)             # determine padded FFT size
        self.DM = DFT( M )               # get DFT matrix for rows
        self.DN = DFT( N )               # get DFT matrix for cols
        self.ZM = DFTinverse( M )        # get inverse DFT matrix
        self.ZN = DFTinverse( N )        # get inverse DFT matrix

        nef.simplenode.SimpleNode.__init__(self,name)
        self.getTermination('A').setDimensions( prod(Ashape) )
        self.getTermination('B').setDimensions( prod(Bshape) )
        self.getTermination('gate').setTau(pstc_gate)
        if( pstc_input > 0 ):
            self.getTermination('A').setTau(pstc_input)
            self.getTermination('B').setTau(pstc_input)
    def termination_gate(self,value):
        self.gate = value[0]
    def termination_A(self,value):
        if( self.rotateA ):
            self.A = reshape( value, self.A.shape )[::-1,::-1]
        else:
            self.A = reshape( value, self.A.shape )
    def termination_B(self,value):
        if( self.rotateB ):
            self.B = reshape( value, self.B.shape )[::-1,::-1]
        else:
            self.B = reshape( value, self.B.shape )
    def origin_C(self):
        if( self.gate > 0.1 ):
            return zeros( self.A.shape )
        else:
            return ravel( conv2dftmatrix( self.A, self.B, self.DM, self.DN, self.ZM, self.ZN ) )
    def reset(self,randomize=False):
        self.A = zeros( self.A.shape )
        self.B = zeros( self.B.shape )
        self.gate = 0

def prod( x ):
    p = x[0]
    for e in x[1:]:
        p *= e
    return p

def complex_exp(z):
    return num.exp( z.real ) * ( num.cos( z.imag ) + 1.0j*num.sin( z.imag ) )

def reprow( r, N ):
    A = zeros( (len(r),N) )
    for i in range(N):
        A[i,:] = r
    return A

def DFT(N):
    m = reprow( array(range(N)), N )
    m = m * m.T
    m = complex_exp( -2*math.pi*1.0j/N * m ) / math.sqrt(N)
    return m

def DFTinverse(N):
    m = reprow( array(range(N)), N )
    m = m * m.T
    m = complex_exp( 2*math.pi*1.0j/N * m )
    return m

def conv2dftmatrix( A, B, DM, DN, ZM, ZN ):
    ra,ca = A.shape
    rb,cb = B.shape
    rao,cao = ((DM.shape[0] - ra) // 2, (DN.shape[0] - ca) // 2)
    X = dot( dot( DM[:,:ra], A ), DN[:ca,:] )
    Y = dot( dot( DM[:,:rb], B ), DN[:cb,:] )
    return dot( dot( ZM[rao:rao+ra,:], X * Y ), ZN[:,cao:cao+ca] ).real

def conv2dft( A, B ):
    ra,ca = A.shape
    rb,cb = B.shape
    M,N = (ra+rb, ca+cb)        # determine padded FFT size

    DM = DFT( M )               # get DFT matrix for rows
    DN = DFT( N )               # get DFT matrix for cols
    ZM = DFTinverse( M )        # get inverse DFT matrix
    ZN = DFTinverse( N )        # get inverse DFT matrix

    X = dot( dot( DM[:,:ra], A ), DN[:ca,:] )   # determine FFT of A (with padding)
    Y = dot( dot( DM[:,:rb], B ), DN[:cb,:] )   # determine FFT of B (with padding)

    rao,cao = ((M - ra) // 2, (N - ca) // 2)
    return (M*N) * dot( dot( ZM[rao:rao+ra,:], X * Y ), ZN[:,cao:cao+ca] ).real

# def tile( A, reps ):
#     n = A.shape[0]
#     B = zeros( (n*reps,) )
#     for i in range(reps):
#         B[i*n:(i+1)*n] = A
#     return B

def unpaddedTransform( DM, DN ):
    M,m = DM.shape
    n,N = DN.shape
    Tshape = (M*N,m*n)
    T = zeros( Tshape ) + 1.0j * zeros( Tshape )
    for i in range(M):
        for j in range(N):
            row = i*N + j

            for k in range(m):
                for l in range(n):
                    T[row,k*n + l] = DM[i,k] * DN[l,j]
                    
            # p = ravel( repeat( DM[i,:], [n]*DM.shape[1] ) ) * ravel( tile( DN[:,j], m ) )
            # Tr[row,:] = p.real
            # Ti[row,:] = p.imag
            # for i in range(M):
            # T[row,:] = ravel( repeat( DM[i,:], [n]*DM.shape[1] ) ) * ravel( tile( DN[:,j], m ) )
            # T[row,:] = DM[i,:].repeat(n) * tile( DN[:,j].flatten(), (1,m) )

    return T

def input_transform( Ashape, Bshape, FFTshape, first ):
    am,an = Ashape
    bm,bn = Bshape
    M,N = FFTshape

    DM = DFT( M )               # get DFT matrix for rows
    DN = DFT( N )               # get DFT matrix for cols

    if( first ):
        c = am*an
        W = unpaddedTransform( DM[:,:am], DN[:an,:] )
    else:
        c = bm*bn
        W = unpaddedTransform( DM[:,:bm], DN[:bn,:] )

    T = []
    for i in range(4*M*N):
        if( first ):
            if( i % 2 == 0 ):
                T.extend( array( [W[i/4,:].real, zeros(c)] ) )
            else:
                T.extend( array( [W[i/4,:].imag, zeros(c)] ) )
        else:
            if( i % 4 == 0 or i % 4 == 3 ):
                T.extend( array( [zeros(c), W[i/4,:].real] ) )
            else:
                T.extend( array( [zeros(c), W[i/4,:].imag] ) )

    return array(T)

def output_transform( Ashape, Bshape, FFTshape ):
    am,an = Ashape
    M,N = FFTshape

    ZM = DFTinverse( M )        # get inverse DFT matrix
    ZN = DFTinverse( N )        # get inverse DFT matrix

    amo,ano = ((M - am) // 2, (N - an) // 2)
    W = unpaddedTransform( ZM[amo:amo+am,:], ZN[:,ano:ano+an] )

    T = []
    for j in range(M*N):
        T.append( W[:,j].real )
        T.append( -W[:,j].real )
        T.append( -W[:,j].imag )
        T.append( -W[:,j].imag )

    return array(T).T

def conv2neuron( A, B ):
    ra,ca = A.shape
    rb,cb = B.shape
    FFTshape = (ra+rb, ca+cb)        # determine padded FFT size

    AT = input_transform( A.shape, B.shape, FFTshape, True )
    BT = input_transform( A.shape, B.shape, FFTshape, False )

    C = dot( AT, ravel( A ) ) + dot( BT, ravel( B ) )
    print "Max D before multiplication %0.3f" % max( ravel( C ) )

    C = C[0::2] * C[1::2]
    print "Max D after multiplication %0.3f" % max( ravel( C ) )

    T = output_transform( A.shape, B.shape, FFTshape )
    return dot( T, C )

def make_convolution2D(self,name,A,B,C,N_per_D,
                       quick=False,encoders=[[1,1],[1,-1],[-1,1],[-1,-1]],radius=3,
                       pstc_out=0.01,pstc_in=0.01,pstc_gate=0.01,
                       rotateA=False,rotateB=False,mode='default',output_scale=1):
    if isinstance(A,str):
        A = self.network.getNode(A)
    if isinstance(B,str):
        B = self.network.getNode(B)
    if isinstance(C,str):
        C = self.network.getNode(C)

    # assume all images square
    Ashape = tuple( [int(math.sqrt(A.dimension))]*2 )
    Bshape = tuple( [int(math.sqrt(B.dimension))]*2 )
    Cshape = tuple( [int(math.sqrt(C.dimension))]*2 )

    if( Ashape != Cshape ):
        raise Exception('A and C must have the same dimensions')
    
    if mode=='direct':
        D = DirectConvolution2D(name,Ashape,Bshape,rotateA,rotateB)
        self.add(D)
        D.getTermination('A').setTau(pstc_in)
        D.getTermination('B').setTau(pstc_in)
        D.getTermination('gate').setTau(pstc_gate)
        if A is not None:
            self.connect(A,D.getTermination('A'))
        if B is not None:
            self.connect(B,D.getTermination('B'))
        self.connect(D.getOrigin('C'),C,pstc=pstc_out,weight=output_scale)
    else:
        ra,ca = Ashape
        rb,cb = Bshape
        FFTshape = (ra+rb, ca+cb)   # determine padded FFT size
        
        Ddimensions = prod(FFTshape)
        D = self.make_array(name,N_per_D,4*Ddimensions,dimensions=2,
                            quick=quick,encoders=encoders,radius=radius,mode=mode)

        AT = input_transform( Ashape, Bshape, FFTshape, True )
        BT = input_transform( Ashape, Bshape, FFTshape, False )
        
        D.addDecodedTermination('A',AT,pstc_in,False)
        D.addDecodedTermination('B',BT,pstc_in,False)
        
        if A is not None:
            self.connect(A,D.getTermination('A'))
        if B is not None:
            self.connect(B,D.getTermination('B'))
            
        T = output_transform( Ashape, Bshape, FFTshape )
        self.connect(D,C,func=(lambda x: x[0]*x[1]),transform=T*output_scale,pstc=pstc_out)
        
    return D
