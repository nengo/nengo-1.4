
class Watcher:
    def __init__(self,timelog):
        self.timelog=timelog
        self.objects={}
        self.watches=[]
        self.active={}

    def add_object(self,name,obj):
        assert name not in self.objects
        self.objects[name]=obj
        
    def add_watch(self,watch):
        self.watches.append(watch)

    def watch(self,name,func):
        if (name,func) not in self.active:
            w=self.timelog.add(lambda self=self,name=name,func=func: func(self.objects[name]))
            w.watch_count=0
            self.active[(name,func)]=w
        w=self.active[(name,func)]
        w.watch_count+=1
        return w

    def list(self,name):
        r=[]
        obj=self.objects[name]
        for w in self.watches:
            if w.check(obj):
                r.extend(w.views(obj))
        return r

    def reset(self):
        self.timelog.reset()
