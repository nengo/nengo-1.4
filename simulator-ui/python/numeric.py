from ca.nengo.ui.JNumeric.JNumeric import *

def diag(values):
    a=zeros((len(values),len(values)))
    for i,v in enumerate(values):
        a[i][i]=v
    return a

def eye(i):
    return diag([1.0]*i)
    
    
