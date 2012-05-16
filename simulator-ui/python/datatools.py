from __future__ import with_statement
import os, re, time

try:
    import numpy as np
except:
    import numeric as np

    
last_run_in_nengo=None

def run_in_nengo(code):
    global last_run_in_nengo
    m=re.search('nef\.Network[(](.+)[),]',code)
    if m is None:
        fn='experiment-'+time.strftime('%Y%m%d-%H%M%S')+'.py'
    else:
        fn='experiment-%s.py'%(m.group(1).replace(' ','_').replace('"','').replace("'",''))
    last_run_in_nengo=fn    
    f=open(fn,'w')    
    f.write(code)
    f.close()
    os.system('./nengo-cl '+fn)
    print 'finished running',fn
    
def rms(x):
    x=np.ravel(x)    
    return np.sqrt(sum(x*x)/len(x))
    
   
