import timeview.components.core as core
import timeview.view

from java.awt import Color

class RuleView(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),label=None):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)
        self.names=None
        self.margin=10

        self.setSize(150,300)

    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

        if self.names is None:
            self.names=list(self.view.watcher.objects[self.name].bg.rules._names)

        dt_tau=None
        # no filter
        #if self.view.tau_filter>0:
        #    dt_tau=self.view.dt/self.view.tau_filter
        try:    
            data=self.data.get(start=self.view.current_tick,count=1,dt_tau=dt_tau)[0]
        except:
            return

        x0=self.margin/2.0
        y0=self.margin/2.0+self.label_offset
        g.color=Color.black
        g.drawRect(int(x0)-1,int(y0)-1,int(self.size.width-self.margin),int(self.size.height-self.label_offset-self.margin)+1)
        
        dy=float(self.size.height-self.label_offset-self.margin)/len(self.names)

        for i,n in enumerate(self.names):
            c=1-data[i]
            if c<0: c=0
            if c>1: c=1
            g.color=Color(c,c,c)
            g.fillRect(int(x0),int(y0+dy*i),int(self.size.width-self.margin-1),int(dy+1))

            if c<0.5: g.color=Color.white
            else: g.color=Color.black
            bounds=g.font.getStringBounds(n,g.fontRenderContext)
            g.drawString(n,self.size.width/2-bounds.width/2,int(y0+dy*i+dy/2+bounds.height/2))
            
        

        


class RuleWatch:
    def __init__(self):
        self.objs=[]
    def check(self,obj):
        return obj in self.objs
    def measure(self,obj):
        return obj.rules.getOrigin('X').getValues().getValues()
    def views(self,obj):
        return [('rule activation',RuleView,dict(func=self.measure,label="Rules"))]
    def add(self,obj):
        self.objs.append(obj)

rule_watch=RuleWatch()
timeview.view.watches.append(rule_watch)

    

