
from numeric import array,zeros
import math

# DxD discrete fourier transform matrix            
def discrete_fourier_transform(D):
    m=[]
    for i in range(D):
        row=[]
        for j in range(D):            
            row.append(complex_exp((-2*math.pi*1.0j/D)*(i*j)))
        m.append(row)
    return m

# DxD discrete inverse fourier transform matrix            
def discrete_fourier_transform_inverse(D):
    m=[]
    for i in range(D):
        row=[]
        for j in range(D):            
            row.append(complex_exp((2*math.pi*1.0j/D)*(i*j))/D)
        m.append(row)
    return m

# formula for e^z for complex z
def complex_exp(z):
    a=z.real
    b=z.imag
    return math.exp(a)*(math.cos(b)+1.0j*math.sin(b))

def product(x):
    return x[0]*x[1]

def make_convolution(self,name,A,B,C,N_per_D,quick=False,encoders=[[1,1],[1,-1],[-1,1],[-1,-1]],pstc_out=0.01,pstc_in=0.01,invert_first=False,invert_second=False):
    if isinstance(A,str):
        A=self.network.getNode(A)
    if isinstance(B,str):
        B=self.network.getNode(B)
    if isinstance(C,str):
        C=self.network.getNode(C)

    dimensions=A.dimension
    if B.dimension!=dimensions or C.dimension!=dimensions:
        raise Exception('Dimensions not the same for convolution (%d,%d->%d)'%(A.dimension,B.dimension,C.dimension))
    D=self.make_array(name,N_per_D,(dimensions/2+1)*4,dimensions=2,quick=quick,encoders=encoders)
    
    fft=array(discrete_fourier_transform(dimensions))/math.sqrt(dimensions)
    ifft=array(discrete_fourier_transform_inverse(dimensions))*dimensions

    def makeA2(i):
        if invert_first:
            row=fft[-(i/4)]
        else:
            row=fft[i/4]
        if i%2==0:
            return array([row.real,zeros(dimensions)])
        else:    
            return array([row.imag,zeros(dimensions)])
    def makeB2(i):
        if invert_second:
            row=fft[-(i/4)]
        else:
            row=fft[i/4]
        if i%4==0 or i%4==3:
            return array([zeros(dimensions),row.real])
        else:    
            return array([zeros(dimensions),row.imag])

    self.connect(A,D,transform=[makeA2(i) for i in range((dimensions/2+1)*4)],pstc=pstc_in)
    self.connect(B,D,transform=[makeB2(i) for i in range((dimensions/2+1)*4)],pstc=pstc_in)

    
    def makeifftrow(D,i):
        if i==0 or i*2==D: return ifft[i]
        if i<=D/2: return ifft[i]+ifft[-i].real-ifft[-i].imag*1j
        return zeros(dimensions)
    ifftm=array([makeifftrow(dimensions,i) for i in range(dimensions/2+1)])
    
    
    ifftm2=[]
    for i in range(dimensions/2+1):
        ifftm2.append(ifftm[i].real)
        ifftm2.append(-ifftm[i].real)
        ifftm2.append(-ifftm[i].imag)
        ifftm2.append(-ifftm[i].imag)
    ifftm2=array(ifftm2)    
    
        
    self.connect(D,C,func=product,transform=ifftm2.T,pstc=pstc_out)
    return D

    
