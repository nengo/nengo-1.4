import os

class Computed:
    def __init__(self,name):
        self._filename=name+'.data'
        self._data=None
    def value_names(self):
        if self._data is None: self._readfile()
        return self._data.keys()    
    def _readfile(self):
        self._data={}
        if os.path.exists(self._filename):
            for line in open(self._filename).readlines():
                if '=' in line:
                    k,v=line.split('=',1)
                    self._data[k]=eval(v)
    def __hasattr__(self,key):
        if key.startswith('_'): return self.__dict__.has_key(key)
        if self._data is None: self._readfile()
        return self._data.has_key(key)
                        
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__.get(key)
        if self._data is None: self._readfile()
        return self._data[key]
    def __setattr__(self,key,value):
        if key.startswith('_'): 
            self.__dict__[key]=value
        else:
            f=open(self._filename,'a')
            f.write('%s=%s\n'%(key,`value`))
            f.close()   
            if self._data is None: self._readfile()            
            self._data[key]=value
    def __str__(self):
        if self._data is None: self._readfile()
        return str(self._data)         
        
