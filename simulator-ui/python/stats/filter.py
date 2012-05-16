import math

try:
    import numpy as np
except:
    import numeric as np


def filter(data,dt,tau):
    if tau is None or tau<dt:
        return data
    else:
        d=[]
        decay=math.exp(-dt/tau)
        v=np.zeros(len(data[0]))
        for i,dd in enumerate(data):
            v=v*decay+dd*(1-decay)
            d.append(v)
        return np.array(d)    
