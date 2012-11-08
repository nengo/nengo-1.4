from __future__ import with_statement
import os, re, time

try:
    import numpy as np
except:
    import numeric as np

import filter
import computed
    
class Reader:
    def __init__(self,filename='',dir=None,search=True):
        if dir is None: dir='.'
        self.dir=dir
        if search:
            self.filename=self.find_file(filename)
        else:
            if filename.endswith('csv'):
                self.filename=filename
            else:
                self.filename=filename+'.csv'    
        if self.filename is not None:
            self.read_header()
        self.cache={}
        self.computed=computed.Computed(os.path.join(self.dir,self.filename)[:-4])   
    def find_file(self,filename):
        files=os.listdir(self.dir)
        files=[x for x in files if x.endswith('.csv') and x.startswith(filename)]
        times=[os.path.getmtime(os.path.join(self.dir,x)) for x in files]
        if len(times)==0:
            print 'No log files found in "%s/%s*"'%(self.dir,filename)
            return None
        return files[times.index(max(times))]
    def read_header(self):
        if not os.path.exists(os.path.join(self.dir,self.filename)):
            self.header=[]
            return    
        with open(os.path.join(self.dir,self.filename)) as f:
            for row in f:
                row=row.strip()
                if len(row)==0 or row.startswith('#'): 
                    continue
                self.header=row.split(',')
                break
    def parse(self,text):
        data=text.split(';')
        if '.' not in data[0]:
            return [int(x) for x in data]
        try:    
            return [float(x) for x in data]
        except:
            d={}
            for x in data:
                i=x.index('.')
                d[x[i+3:]]=float(x[:i+3])
            return d        
    def __str__(self):
        return '<Reader %s>'%os.path.join(self.dir,self.filename)    
    
    def __hasattr__(self,key):
        if key.startswith('_'): return self.__dict__.has_key(key)
        if key in self.header: return True
        return hasattr(self.computed,key)
            
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__[key]    
        try:
            return self.get(key)
        except ValueError:
            try:
                return getattr(self.computed,key)        
            except KeyError:
                raise Exception('Could not find data "%s"'%key)    
    def __getitem__(self,key):
        return self.get(key)    
    def get_index_for_time(self,time):
        t=self.get('time')
        index=0
        while len(t)>index and t[index][0]<time:
            index+=1        
        return index    

    def keys(self):
        return self.header

    def items(self):
        return [(k, self.get(k)) for k in self.keys()]

    def values(self):
        return [self.get(k) for k in self.keys()]
    
    def get(self,name,time=None,filter=None,normalize=False,keys=None):
        """
        Return a column of data from the csv

        Parameters:
        WRITEME
        """
        if name not in self.cache:
            data=[]
            done_header=False
            index=self.header.index(name)
            with open(os.path.join(self.dir,self.filename)) as f:
                for row in f:
                    row=row.strip()
                    if len(row)==0 or row.startswith('#'): 
                        continue
                    if not done_header:
                        done_header=True
                        continue
                    data.append(self.parse(row.split(',')[index]))
            data=np.array(data)        
            self.cache[name]=data
        else:
            data=self.cache[name]    
        # one of the types of data in the csv file is a *string* of the form
        # "8a;9b;<...>"
        # This string represent a vector (semantic pointer) in terms of
        # a projections onto named [non-orthogonalized] basis elements.
        # The numeric prefixes are the inner products, and the character suffixes
        # name the basis elements.
        #
        # if `keys` is specified, then it means to only pay attention to the
        # explicitly named suffix *keys*. Otherwise all of them are returned.
        # 
        if keys is not None:
            data2=np.zeros((len(data),len(keys)),dtype=float)            
            for i,d in enumerate(data):
                scale=1.0
                if normalize:
                    length=np.sqrt(sum([v*v for v in d.values()]))
                    if length>0.01: scale=1.0/length
                for j,key in enumerate(keys):
                    data2[i][j]=d.get(key,0)*scale
            data=data2
            # -- normalize has already been done in the previous loop
            normalize=False
                    
        if filter is not None:
            data=filter.filter(data,self.time[1]-self.time[0],tau=filter)            
        if time is not None:
            if isinstance(time,(float,int)):
                data=data[self.get_index_for_time(time)]
            else:
                data=data[self.get_index_for_time(time[0]):self.get_index_for_time(time[1])]        
        if normalize:
            for i,v in enumerate(data):
                length=np.linalg.norm(v)
                if length>0.1:
                    data[i]/=length
                
        return data
