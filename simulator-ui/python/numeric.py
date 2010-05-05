from ca.nengo.ui.JNumeric.JNumeric import *


# gets fft() and inverse_fft()
from ca.nengo.ui.JNumeric.JNumeric.FFT import fft,inverse_fft

def diag(values):
    a=zeros((len(values),len(values)))
    for i,v in enumerate(values):
        a[i][i]=v
    return a

eye=identity    
