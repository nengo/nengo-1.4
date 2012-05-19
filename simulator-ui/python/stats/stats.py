import os
import runner
import reader

try:
    import numpy as np
except:
    import numeric as np


class Data:
    def __init__(self,dir=None,readers=None):
        if readers is None:
            self.readers=[reader.Reader(fn,dir) for fn in sorted(os.listdir(dir)) if fn.endswith('.csv')]
        else:
            self.readers=readers   
    def __len__(self):
        return len(self.readers)         
    def __getattr__(self,key):
        d=[getattr(r,key) for r in self.readers]
        d=np.concatenate(np.array(d).T)
        return d
    def __getitem__(self,key):
        return Data(readers=[self.readers[key]])
    def __getslice__(self,i,j):
        return Data(readers=self.readers[i:j])

            
            
class Computed:
    def __init__(self,dir=None,files=None):
        if files is None:
            self._files=[]
            for fn in os.listdir(dir):
                if fn.endswith('.data') and fn not in self._files:
                    self._files.append(os.path.join(dir,fn))
                if fn.endswith('.csv'):
                    fn2=fn[:-3]+'data'
                    if fn2 not in self._files:
                        self._files.append(os.path.join(dir,fn2))    
        else:
            self._files=files
        self._data=[self.parse_file(f) for f in self._files]

    def parse_file(self,fn):
        data={}
        if os.path.exists(fn):
            for line in open(fn):
                k,v=line.strip().split('=',1)
                data[k]=eval(v)
        return data    

    def __getattr__(self,key):
        d=[data.get(key) for data in self._data]
        #d=np.concatenate(np.array(d).T)
        return d
    def __getitem__(self,key):
        return Record(files=[self._files[key]])
    def __getslice__(self,i,j):
        return Record(files=self._files[i:j])
        
    def compute(self,recompute=False,**funcs):
        for key,func in funcs.items():
            for i,data in enumerate(self._data):
                if recompute or not data.has_key(key):
                    dir,fn=self._files[i].rsplit('/',1)
                    d=reader.Reader(fn[:-4]+'csv',dir)
                    value=func(d,data)
                    data[key]=value
                    f=open(self._files[i],'a')
                    f.write('%s=%s\n'%(key,`value`))
                    f.close()
        


class Stats:
    def __init__(self,name):
        if name.endswith('.py'): name=name[:-3]

        if os.path.exists(name+'.py'):
            lines=open(name+'.py').readlines()
        elif os.path.exists(os.path.join(name,'code.py')):
            lines=open(os.path.join(name,'code.py')).readlines()
        self.params,self.defaults,self.core_code=runner.parse_code(lines)
                
        self.name=name
    
    def parameter_values(self):
        values={}
        for k,v in self.defaults.items():
            values[k]=set()   
            values[k].add(v)
        if os.path.exists(self.name):
            for f in os.listdir(self.name):
                if '=' in f:
                    for part in f.split(' '):
                        if '=' in part:
                            k,v=part.split('=',1)
                            if k in values:
                                values[k].add(v)
        for k,v in values.items():
            values[k]=self.sort_settings(v)
        return values
    
    def sort_settings(self,vals):
        r=[]
        for v in vals:
            try:
                v=eval(v,{},{})
            except:
                pass
            r.append(v)
        return [str(v) for v in sorted(r)]
        
    def data(self,**params):
        for k,v in params.items():
            params[k]=`v`
        dir='%s/%s'%(self.name,runner.make_param_text(self.params,self.defaults,params))
        return Data(dir)
        
    def computed(self,**params):
        for k,v in params.items():
            params[k]=`v`
        dir='%s/%s'%(self.name,runner.make_param_text(self.params,self.defaults,params))
        return Computed(dir)
        
    def compute(self,recompute=False,**funcs):
        for d in ['%s/%s'%(self.name,dir) for dir in os.listdir(self.name)]:
            if os.access(d+'/',os.F_OK):
                Computed(d).compute(recompute=recompute,**funcs)
        
        
