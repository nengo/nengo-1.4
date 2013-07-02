import math
import threading

class TimeLogItem:
    def __init__(self,parent,func,args=(),kwargs={},type=None,offset=0):
        self.semaphore=threading.Semaphore()
        self.parent=parent
        self.func=func
        self.args=args
        self.kwargs=kwargs
        self.data=[]
        self.filtered={}
        self.offset=offset
        self.type=type
        self.length=None
        self.tick()
    def tick(self,limit=None):
        self.semaphore.acquire()
        try:
            v=self.func(*self.args,**self.kwargs)
        except Exception,e:
            import java
            java.lang.System.out.println("Tick error: %s %s %s\n%s"%(self.func,self.args,self.kwargs,e))
            v=None
        if self.length is None: self.length=len(v)
        else:
            if len(v)<self.length: v.extend([0]*(self.length-len(v)))
            elif len(v)>self.length: v=v[:self.length]    
        self.data.append(v)
        if limit is not None and len(self.data)>limit:
            delta=len(self.data)-limit
            self.offset+=delta
            self.data=self.data[delta:]
            for k,v in self.filtered.items():
                if len(v)<=delta: del self.filtered[k]
                else:
                    self.filtered[k]=v[delta:]
        self.semaphore.release()
    def reset(self):
        self.semaphore.acquire()
        del self.data[:]
        self.filtered={}
        self.offset=0
        self.semaphore.release()
        self.tick()


    def update_filter(self,dt_tau):
        self.semaphore.acquire()
        self.parent.processing=True
        if dt_tau not in self.filtered:
            f=[[x*dt_tau for x in self.data[0]]]
            #f=[self.data[0]*dt_tau]
            self.filtered[dt_tau]=f
        else:
            f=self.filtered[dt_tau]
        if len(f)<len(self.data):
            v=f[-1][:]
            d=self.data
            decay=math.exp(-dt_tau)
            n=len(self.data[0])
            for i in range(len(f),len(d)):
                for j in range(n):
                    v[j]=v[j]*decay+d[i][j]*(1-decay)#dt_tau
                #v=v*decay+d[i]*scale
                f.append(v[:])
        self.parent.processing=False
        self.semaphore.release()
        return f        

    def get(self,start=None,count=None,dt_tau=None):
        self.semaphore.acquire()
        if dt_tau is None:
            d=self.data
        else:
            self.semaphore.release()
            d=self.update_filter(dt_tau)
            self.semaphore.acquire()
        off=self.offset
        if start is None: start=0
        if count is None: count=len(d)+off
        if off>start+count:
            r=[]
        elif off>start:
            r=[None]*(off-start)
            r+=d[:count-off+start]
        else:
            r=d[start-off:start-off+count]
        if len(r)<count:
            r+=[None]*(count-len(r))
        self.semaphore.release()
        return r

    def get_first(self):
        return self.data[0]

class TimeLog:
    def __init__(self):
        self.items=[]
        self.tick_count=0
        self.tick_limit=4001
        self.processing=False
    
    def add(self,func,args=(),kwargs={},type=None):
        item=TimeLogItem(self,func,args=args,kwargs={},type=type,offset=self.tick_count)
        self.items.append(item)
        return item
    
    def remove(self,item):
        self.items.remove(item)    
        
    def tick(self):
        for item in self.items: item.tick(limit=self.tick_limit)
        self.tick_count+=1
        
    def reset(self):
        for item in self.items: item.reset()
        self.tick_count=0
            
    
