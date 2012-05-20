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
    def __init__(self,name,values,default):
        self.layout=BorderLayout()
        self.name=name
        self.values=JList(values,border=BorderFactory.createLineBorder(Color.black,1))
        self.add(JLabel(name),BorderLayout.NORTH)
        self.add(self.values)      
        self.values.setSelectedValue(default,True)
        
class ParametersPanel(JPanel):
    def __init__(self):
        self.layout=GridLayout()
        self.background=Color.yellow
        self.params={}
    def update(self,names,values,defaults):
        self.removeAll()
        for name in names:
            self.params[name]=ParameterPanel(name,values[name],defaults[name])
            self.add(self.params[name])
        self.revalidate()
    def get_selected(self):
        values={}
        for k,v in self.params.items():
            values[k]=[eval(x) for x in v.values.selectedValues]
        return values    
                
        
        
class OptionsPanel(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=BorderLayout()
        self.parameters=ParametersPanel()
        self.add(self.parameters,BorderLayout.NORTH)
        self.background=Color.white
        
    def update(self):
        self.parameters.update(self.view.stats.params,self.view.stats.parameter_values(),self.view.stats.defaults)
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
                time.sleep(1)

def make_settings_combinations(settings,keys=None):
    if keys is None: keys=settings.keys()
    if len(keys)==0: 
        yield {}
        return
    
    k=keys.pop()
    v=settings[k]
    for setting in make_settings_combinations(settings,keys):
        if type(v) is list:
            for vv in v:
                setting[k]=vv
                yield setting
        else:
            setting[k]=v
            yield setting


class Graph(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=BorderLayout()
        self.chart=None
    def update(self):
        if self.view.stats is None: return
        for params in make_settings_combinations(self.view.selected_params()):
            print 'update',params
            data=self.view.stats.data(**params)
            print [r.computed.rmse for r in data.readers]
            
            self.removeAll()
            self.chart=org.jfree.chart.ChartFactory.createBoxAndWhiskerChart(str(params),'','value',None,True)
            self.add(org.jfree.chart.ChartPanel(self.chart))
            
            #print numeric.mean([r.computed.rmse for r in data.readers])
        
class View:
    def __init__(self):
        self.stats=None
        self.frame=JFrame('Statistics',visible=True,layout=BorderLayout())
        
        self.options=OptionsPanel(self)
        self.frame.add(self.options,BorderLayout.WEST)
        
        self.graph=Graph(self)
        self.frame.add(self.graph)
        
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
            
            


        
        
        
