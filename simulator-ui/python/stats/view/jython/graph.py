import java
from javax.swing import *
from java.awt import *

import os
import time
import random

class GraphPanel(JPanel,java.lang.Runnable):
    def __init__(self,view,**args):
        JPanel.__init__(self,**args)
        self.view=view
        self.layout=BorderLayout()
        self.chart=None
        self.graph=JLabel()
        self.add(self.graph)
        self.should_update=True
        self.thread=java.lang.Thread(self)
        #self.thread.priority=java.lang.Thread.MIN_PRIORITY
        self.thread.start()
    def update(self):
        self.should_update=True
        self.graph.enabled=False
        
    
    def run(self):
        while True:
            if self.should_update:
                self.should_update=False
                self.do_update()
            if self.should_update:
                self.graph.enabled=False
            else:    
                time.sleep(0.1)    
    def do_update(self):            
        if self.view.stats is None: return
        if len(self.view.stats.data)==0: return
        #print 'update graph'
        
        for f in os.listdir('python'):
            if f.startswith('.view.') and f.endswith('.png'):
                os.remove('python/'+f)
        
        fn='python/.view.%08x'%random.randrange(0x7fffffff)
        
        code=self.make_pylab_code(fn+'.png')
        #print code
        f=open(fn+'.py','w')
        f.write(code)
        f.close()
        os.system('python %s.py'%fn)
        
        icon=ImageIcon(fn+'.png')
        self.graph.icon=icon
        self.graph.enabled=True
        os.remove(fn+'.py')
        self.revalidate()
    
    def make_pylab_code(self,filename):
        opt=self.view.options

        dpi=opt.global.dpi.text
        try: dpi=int(dpi)
        except: dpi=80

        lines=[]
        lines.append('import sys;sys.path.append("python")')
        lines.append('import stats')
        lines.append('s=stats.Stats("%s")'%self.view.stats.name)
        params=self.view.selected_params()
        for k,v in params.items():
            if isinstance(v,list) and len(v)==1: params[k]=v[0]
        lines.append('s=s(%s)'%','.join(['%s=%s'%(k,`v`) for k,v in params.items()]))
        
        lines.append('import stats.plot')
        color=opt.global.opt_color.isSelected()
        values=self.view.stats.data[0].computed.value_names()
        metric=opt.global.metrics.selectedItem
        
        width=self.size.width/float(dpi)
        height=self.size.height/float(dpi)
        if opt.get_graph_type()=='bar':
            lines.append('p=stats.plot.Bar(width=%g,height=%g,color=%s)'%(
                                    width,height,color))        
            scatter=opt.bar_tab.show_samples.isSelected()
            lines.append('p.plot(s,values=%s,scatter=%s,metric=%s)'%(`values`,`scatter`,`metric`))
        elif opt.get_graph_type()=='line':
            
            spaced=opt.line_tab.x_axis.selectedItem=='equal'        
            logx=opt.line_tab.x_axis.selectedItem=='log'        
            logy=opt.line_tab.y_axis.selectedItem=='log'        
            lines.append('p=stats.plot.Line(width=%g,height=%g,color=%s,log_x=%s,log_y=%s,spaced=%s)'%(
                                    width,height,color,logx,logy,spaced)) 
            area=opt.line_tab.area.isSelected()
            lines.append('p.plot(s,values=%s,area=%s,metric=%s)'%(`values`,`area`,`metric`))
        
        lines.append('p.save("%s",dpi=%d)'%(filename,dpi)) 
        
        return '\n'.join(lines)

