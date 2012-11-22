from com.github.jnumeric.JNumeric import *

# gets fft() and inverse_fft()
import com.github.jnumeric.FFT as FFT


def diag(values,typecode='f'):
    a=zeros((len(values),len(values)),typecode=typecode)
    for i,v in enumerate(values):
        a[i][i]=v
    return a

eye=identity    

def norm(data,axis=0):
    return sqrt(sum(data*data,axis=axis))
    
_fft_cache={}
def fft(x):
    N=len(x)
    if N in [1<<i for i in range(16)]: return FFT.fft(x)
    if N not in _fft_cache:
        _fft_cache[N]=fftm(N)
    return dot(_fft_cache[N],x)
    
_ifft_cache={}    
def ifft(x):
    N=len(x)
    if N in [1<<i for i in range(16)]: return FFT.inverse_fft(x)
    if N not in _ifft_cache:
        _ifft_cache[N]=ifftm(N)
    return dot(_ifft_cache[N],x)
    
    
_circconv_cache={}    
def circconv(a,b):
    if len(a)!=len(b): raise Exception('Vectors must be of same length to circconv: (%d!=%d)'%(len(a),len(b)))
    return ifft(fft(a)*fft(b)).real
        
    
    
    
import math    
# DxD discrete fourier transform matrix            
def fftm(D):
    m=[]
    for i in range(D):
        row=[]
        for j in range(D):            
            row.append(complex_exp((-2*math.pi*1.0j/D)*(i*j)))
        m.append(row)
    return m

# DxD discrete inverse fourier transform matrix            
def ifftm(D):
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
    
    
def mean(x,axis=0):
    try:
        length=x.shape[axis]
    except:
        x=array(x)    
        length=x.shape[axis]        
    return sum(x,axis=axis)/length    
