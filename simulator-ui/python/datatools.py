from __future__ import with_statement
import os

try:
    import numpy
    has_numpy=True
except:
    has_numpy=False

try:
    import numeric
    has_numeric=True
except:
    has_numeric=False

class LogReader:
    def __init__(self,filename='',dir=None):
        if dir is None: dir='.'
        self.dir=dir
        self.filename=self.find_file(filename)
        if self.filename is not None:
            self.read_header()
    def find_file(self,filename):
        files=os.listdir(self.dir)
        files=[x for x in files if x.endswith('.csv') and x.startswith(filename)]
        times=[os.path.getmtime(os.path.join(self.dir,x)) for x in files]
        if len(times)==0:
            print 'No log files found in "%s/%s*"'%(self.dir,self.filename)
            return None
        return files[times.index(max(times))]
    def read_header(self):
        with open(os.path.join(self.dir,self.filename)) as f:
            for row in f:
                row=row.strip()
                if len(row)==0 or row.startswith('#'): 
                    continue
                self.header=row.split(',')
                break
    def parse(self,text):
        data=text.split(';')
        return [float(x) for x in data]
    def __getattr__(self,key):
        return self.get(key)    
    def __getitem__(self,key):
        return self.get(key)    
    def get_index_for_time(self,time):
        t=self.get('time')
        index=0
        while len(t)>index and t[index][0]<time:
            index+=1        
        return index    
        
    def get(self,name,time=None):
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
        if time is not None:
            if isinstance(time,(float,int)):
                data=data[self.get_index_for_time(time)]
            else:
                data=data[self.get_index_for_time(time[0]):self.get_index_for_time(time[1])]        
        if has_numpy: 
            data=numpy.array(data)        
        elif has_numeric:
            data=numeric.array(data)    
        return data
