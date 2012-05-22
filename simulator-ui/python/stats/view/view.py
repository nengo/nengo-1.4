import java
import javax
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import org.jfree.chart


import os
import stats
import time
import stats.runner
import numeric
import random

class ModelSelectionPanel(JPanel):
    def __init__(self,view):
        self.dir='.'
        self.view=view
        self.files=JComboBox([],editable=False,itemStateChanged=self.view.file_changed)
        self.find_files()

        self.layout=BorderLayout()
        self.add(self.files)
        
        self.add(JLabel('select model:'),BorderLayout.WEST)
        
        #TODO: make this button open a file dialog, allowing directory changes
        self.add(JButton(icon=ImageIcon('python/images/open.png')),BorderLayout.EAST)            
        
    def find_files(self):
        selected=self.files.selectedItem
        files=set()
        for f in os.listdir(self.dir):
            if f.endswith('.py'): files.add(f[:-3])
            elif os.path.exists(os.path.join(self.dir,f,'code.py')): files.add(f)
        self.files.model=DefaultComboBoxModel(sorted(files))
        self.files.selectedItem=selected
        
class ParameterPanel(JPanel):
    def __init__(self,view,name,values,default):
        self.view=view
        self.layout=BorderLayout()
        self.name=name
        self.values=JList(values,border=BorderFactory.createLineBorder(Color.black,1),
                                 valueChanged=self.adjust)
        self.add(JLabel(name),BorderLayout.NORTH)
        self.add(self.values)      
        self.values.setSelectedValue(default,True)
    def adjust(self,event):
        if not event.valueIsAdjusting:
            self.view.graph.update()    
        
class ParametersPanel(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=GridLayout()
        self.background=Color.yellow
        self.params={}
    def update(self,names,options,defaults):
        self.removeAll()
        for name in names:
            self.params[name]=ParameterPanel(self.view,name,getattr(options,name),defaults[name])
            self.add(self.params[name])
        self.revalidate()
    def get_selected(self):
        values={}
        for k,v in self.params.items():
            values[k]=[x for x in v.values.selectedValues]
        return values    
                
        
        
class OptionsPanel(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=BorderLayout()
        self.parameters=ParametersPanel(self.view)
        self.add(self.parameters,BorderLayout.NORTH)
        self.background=Color.white
        
    def update(self):
        self.parameters.update(self.view.stats.params,self.view.stats.options,self.view.stats.defaults)
        self.revalidate()
            
        
class RunPanel(JPanel, java.lang.Runnable):
    def __init__(self,view):
        self.view=view
        self.layout=BorderLayout()
        self.run_sim=JCheckBox('generate more data',False)
        self.add(self.run_sim)
        self.thread=java.lang.Thread(self)
        self.thread.priority=java.lang.Thread.MIN_PRIORITY
        self.thread.start()
    def run(self):
        while True:
            if self.run_sim.isSelected():
                name=self.view.stats.name
                params=self.view.selected_params()
                stats.runner.run(name,**params)
                self.view.graph.update()
            else:    
                time.sleep(100)

class Graph(JPanel,java.lang.Runnable):
    def __init__(self,view,**args):
        JPanel.__init__(self,**args)
        self.view=view
        self.layout=BorderLayout()
        self.chart=None
        self.view_dpi=80
        self.graph=JLabel()
        self.add(self.graph)
        self.should_update=True
        self.thread=java.lang.Thread(self)
        self.thread.priority=java.lang.Thread.MIN_PRIORITY
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
                self.thread.sleep(100)    
    def do_update(self):            
        if self.view.stats is None: return
        if len(self.view.stats.data)==0: return 
        #print 'update graph'
        
        for f in os.listdir('.'):
            if f.startswith('.view.') and f.endswith('.png'):
                os.remove(f)
        
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
        lines=[]
        lines.append('import sys;sys.path.append("python")')
        lines.append('import stats')
        lines.append('s=stats.Stats("%s")'%self.view.stats.name)
        params=self.view.selected_params()
        for k,v in params.items():
            if isinstance(v,list) and len(v)==1: params[k]=v[0]
        lines.append('s=s(%s)'%','.join(['%s=%s'%(k,`v`) for k,v in params.items()]))
        
        lines.append('import stats.plot')
        lines.append('p=stats.plot.Bar(width=%g,height=%g)'%(
                                    self.size.width/float(self.view_dpi),
                                    self.size.height/float(self.view_dpi)))
        
        for k in self.view.stats.data[0].computed.value_names():
            lines.append('p.plot("%s",s.mean.%s)'%(k,k))
        lines.append('p.save("%s",dpi=%d)'%(filename,self.view_dpi)) 
        
        return '\n'.join(lines)
                
class View:
    def __init__(self):
        self.stats=None
        self.frame=JFrame('Statistics',visible=True,layout=BorderLayout())
        
        self.graph=Graph(self,componentResized=lambda event: self.graph.update())
        self.frame.add(self.graph)
        
        self.options=OptionsPanel(self)
        self.frame.add(self.options,BorderLayout.WEST)
        
        
        self.model_selection=ModelSelectionPanel(self)
        self.frame.add(self.model_selection,BorderLayout.NORTH)
        
        self.frame.add(RunPanel(self),BorderLayout.SOUTH)
        
        self.frame.size=(400,600)
    def selected_params(self):
        return self.options.parameters.get_selected()

    def file_changed(self,event):
        if event.stateChange==ItemEvent.SELECTED:
            self.select_model(event.item)


    
    def select_model(self,name):
        self.model_selection.files.selectedItem=name        
        self.stats=stats.Stats(name)
        self.options.update()
        self.graph.update()
            
            


        
        
        
