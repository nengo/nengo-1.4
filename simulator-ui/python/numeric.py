from ca.nengo.ui.JNumeric.JNumeric import *


# gets fft() and inverse_fft()
from ca.nengo.ui.JNumeric.JNumeric.FFT import fft,ifft

def diag(values,typecode='f'):
    a=zeros((len(values),len(values)),typecode=typecode)
    for i,v in enumerate(values):
        a[i][i]=v
    return a

eye=identity    
