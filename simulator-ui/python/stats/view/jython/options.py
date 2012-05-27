import java
from javax.swing import *
from java.awt import *
from java.awt.event import *

class ParameterPanel(JPanel):
    def __init__(self,view,name,values,default):
        self.view=view
        self.layout=BorderLayout()
        self.name=name
        self.values_data=values
        self.values=JList(values,border=BorderFactory.createLineBorder(Color.black,1),
                                 valueChanged=self.adjust)
        self.add(JLabel(name,horizontalAlignment=SwingConstants.LEFT),BorderLayout.NORTH)
        self.new_value=JTextField('',actionPerformed=self.create_new_value)
        self.add(self.new_value,BorderLayout.SOUTH)
        self.add(self.values)      
        self.values.setSelectedValue(default,True)
    def adjust(self,event):
        if not event.valueIsAdjusting:
            self.view.graph.update()    
    def create_new_value(self,event):
        value=self.new_value.text
        self.new_value.text=''
        try:
            value=eval(value)
        except:
            return
        if value not in self.values_data:
            selected=self.values.selectedValue
            self.values_data.append(value)
            self.values_data.sort()
            model=DefaultListModel()
            for v in self.values_data:
                model.addElement(v) 
            self.values.model=model    
            self.values.setSelectedValue(selected,True) 
        
class ParametersPanel(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=BoxLayout(self,BoxLayout.X_AXIS)
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

class BarTab(JPanel):
    def __init__(self,options):
        self.options=options
                        
        self.show_samples=JCheckBox('Show samples',False,actionPerformed=self.options.changed)
        self.add(self.show_samples)
        
class LineTab(JPanel,ActionListener):
    def __init__(self,options):
        self.options=options
        self.layout=BoxLayout(self,BoxLayout.Y_AXIS)

        self.area=JCheckBox('CI area',False,actionPerformed=self.options.changed)
        self.add(self.area)
        self.x_axis=JComboBox(['equal','linear','log'],editable=False)
        self.x_axis.selectedIndex=0
        self.x_axis.addActionListener(self)
        self.y_axis=JComboBox(['linear','log'],editable=False)
        self.y_axis.selectedIndex=0
        self.y_axis.addActionListener(self)
        axis=JPanel(border=BorderFactory.createTitledBorder('axis'))
        axis.layout=BoxLayout(axis,BoxLayout.X_AXIS)
        axis.add(JLabel('x:'))
        axis.add(self.x_axis)
        axis.add(JLabel('y:'))
        axis.add(self.y_axis)
        axis.maximumSize=axis.minimumSize
        self.add(axis)
    def actionPerformed(self,event):
        self.options.changed(event)    
        
        
def make_option(label,component):
    panel=JPanel(layout=BorderLayout())
    panel.add(JLabel(label+':'),BorderLayout.WEST)
    panel.add(component)
    return panel        
        
class GlobalPanel(JPanel, ActionListener):
    def __init__(self,options):
        self.options=options
        self.layout=BoxLayout(self,BoxLayout.Y_AXIS)

        self.opt_color=JCheckBox('Colour',True,actionPerformed=self.options.changed)
        self.add(self.opt_color)
        self.dpi=JTextField('80',actionPerformed=self.options.changed)
        self.add(make_option('dpi',self.dpi))
        
        self.metrics=JComboBox(['mean','median','sd','var'],editable=False)
        self.metrics.selectedIndex=0
        self.metrics.addActionListener(self)
        self.add(make_option('metric',self.metrics))
    def actionPerformed(self,event):
        self.options.changed(event)    
        
                
        
class OptionsPanel(JPanel):
    def __init__(self,view):
        self.view=view
        self.layout=BorderLayout()
        self.parameters=ParametersPanel(self.view)
        self.add(self.parameters,BorderLayout.NORTH)
        
        self.tabs=JTabbedPane(stateChanged=self.changed)
        self.bar_tab=BarTab(self)
        self.line_tab=LineTab(self)
        self.tabs.addTab('Bar',self.bar_tab)
        self.tabs.addTab('Line',self.line_tab)
        self.add(self.tabs)
        
        self.global=GlobalPanel(self)
        self.add(self.global,BorderLayout.SOUTH)
    def get_graph_type(self):
        if self.tabs.selectedIndex==0:
            return 'bar'
        elif self.tabs.selectedIndex==1:
            return 'line'
        else:
            return None    
        
    def update(self):
        self.parameters.update(self.view.stats.params,self.view.stats.options,self.view.stats.defaults)
    def changed(self,event):
        self.view.graph.update()    
            

