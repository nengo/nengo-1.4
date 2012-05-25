import random
try:
    import numpy as np
except:
    import numeric as np

def sample(data):
  for x in data:
    yield random.choice(data)

"""
def safe(f,d):
  try:
    return f(d)
  except:
    return None  



def bootstrapci(data,funcList,n=3000,p=0.95):  
  if not isinstance(funcList,(tuple,list)):
    funcList=[funcList]
  if type(data[0]) is list:
    return [(None,(None,None))]*len(funcList)
  func=lambda d: [safe(ff,d) for ff in funcList]

  index=int(n*(1-p)/2)

  s=func(data)
  r=[func(list(sample(data))) for i in range(n)]
  
  res=[]
  for i,raw in enumerate(s):
    d=sorted(x[i] for x in r)
    res.append((raw,(d[index],d[-index])))
  return res  
"""

def bootstrapci(data,func,n=300,p=0.95):
    try:
        N=len(data[0])
        return bootstrap_list(data,func,n=n,p=p)
    except:
        index=int(n*(1-p)/2)
        r=[func(list(sample(data))) for i in range(n)]
        r.sort()
        return r[index],r[-index]

def bootstrap_list(data,func,n=300,p=0.95):
    index=int(n*(1-p)/2)

    data=data.T
    r=[func(np.array(list(sample(data))).T) for i in range(n)]

    N=len(r[0])
    r=np.array(r)
    result=[]
    for i in range(N):
            d=r[:,i]
            d=list(d)
            d.sort()
            result.append((d[index],d[-index]))
    return np.array(result).T

