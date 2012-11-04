
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

    def watch(self,name,func,args=()):
        for key in self.active.keys():
            if (name,func,args)==key:
                w=self.active[key]
                break
        else:
            w=self.timelog.add(func,args=tuple([self.objects[name]]+list(args)))
            w.watch_count=0
            self.active[(name,func,args)]=w
        w.watch_count+=1
        return w

    def list(self,name):
        r=[]
        obj=self.objects[name]
        for w in self.watches:
            if w.check(obj):
                r.extend(w.views(obj))
        return r

    def contains(self,name):
        obj = self.objects[name]
        for w in self.watches:
            if w.check(obj):
                return True

        return False

    def reset(self):
        self.timelog.reset()
