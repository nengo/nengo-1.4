import timeview.components.core as core
import timeview.view

from java.awt import Color

class RuleView(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),label=None,names=[]):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)
        self.names=names
        self.margin=10

        self.setSize(150,300)

    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

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
        self.objs={}
    def check(self,obj):
        return obj in self.objs.keys()
    def measure(self,obj):
        return obj.getNode('rules').getOrigin('X').getValues().getValues()
    def views(self,obj):
        return [('rule activation',RuleView,dict(func=self.measure,label="Rules",names=self.objs[obj]))]
    def add(self,obj,names):
        self.objs[obj]=names

		
class UtilityView(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),label=None,names=[]):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)
        self.names=names
        self.margin=10

        self.setSize(150,300)

    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

        dt_tau=None
        if self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
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
            if c<0: c=0.0
            if c>1: c=1.0
            g.color=Color(c,c,c)
            g.fillRect(int(x0),int(y0+dy*i),int(self.size.width-self.margin-1),int(dy+1))

            if c<0.5: g.color=Color.white
            else: g.color=Color.black
			
            bounds=g.font.getStringBounds(n,g.fontRenderContext)
            g.drawString(n,self.size.width/4-bounds.width/2,int(y0+dy*i+dy/2+bounds.height/2))

            v='%4.2f'%data[i]
            bounds=g.font.getStringBounds(v,g.fontRenderContext)
            g.drawString(v,self.size.width*3/4-bounds.width/2,int(y0+dy*i+dy/2+bounds.height/2))
			
			
            
class UtilityWatch:
    def __init__(self):
        self.objs={}
    def check(self,obj):
        return obj in self.objs.keys()
    def measure(self,obj):
        return obj.getNode('StrD2').getOrigin('X').getValues().getValues()
    def views(self,obj):
        return [('rule utility',UtilityView,dict(func=self.measure,label="Utility",names=self.objs[obj]))]
    def add(self,obj,names):
        self.objs[obj]=names
		
rule_watch=RuleWatch()
timeview.view.watches.append(rule_watch)
utility_watch=UtilityWatch()
timeview.view.watches.append(utility_watch)

    

