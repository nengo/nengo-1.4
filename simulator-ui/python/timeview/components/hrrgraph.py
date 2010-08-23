import core
import graph
import hrr

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

class HRRGraph(graph.Graph):
    def __init__(self,view,name,func,value_func,args=(),value_args=(),filter=True,ylimits=(-1.0,1.0),label=None):
        graph.Graph.__init__(self,view,name,func,args=args,filter=filter,ylimits=ylimits,split=False,neuronmapped=False,label=label)
        
        self.value_data=self.view.watcher.watch(name,value_func,args=value_args)
        self.border_top=30
        
        self.vocab=hrr.Vocabulary.defaults[len(self.value_data.get_first())]
        
        self.popup_set=JMenuItem('set value',actionPerformed=self.set_value)
        self.popup.add(self.popup_set)
        self.popup_release=JMenuItem('release value',actionPerformed=self.release_value)
        self.popup_release.setEnabled(False)
        self.popup.add(self.popup_release)
        
        self.fixed_value=None

    def label_for_index(self,index):
        if index<len(self.vocab.keys): text=self.vocab.keys[index]
        else: text=self.vocab.key_pairs[index-len(self.vocab.keys)]            
        return text
        
    def set_value(self,event):
        try:
            text=JOptionPane.showInputDialog(self.view.frame,'Enter the symbolic value to represent.\nFor example: "a*b+0.3*(c*d+e)*~f"',"Set semantic pointer",JOptionPane.PLAIN_MESSAGE,None,None,None)
            v=self.vocab.parse(text)
        except:
            self.release_value(event)
            return
        if isinstance(v,(int,float)): v=[v]*self.vocab.dimensions
        else: v=v.v
        
        self.fixed_value=v
                
        self.popup_release.setEnabled(True)
        self.view.forced_origins[(self.name,'X',None)]=self.fixed_value
        
    def release_value(self,event):
        self.fixed_value=None
        self.popup_release.setEnabled(False)
        
        key=(self.name,'X',None)
        if key in self.view.forced_origins: del self.view.forced_origins[key]
    
    def paintComponent(self,g):
        graph.Graph.paintComponent(self,g)
        
        dt_tau=None
        if self.filter and self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
        try:    
            data=self.value_data.get(start=self.view.current_tick,count=1,dt_tau=dt_tau)[0]
        except:
            return
        
        if data is None: return
        text=self.vocab.text(data,threshold=0.3,join=',')
        
        g.color=Color.black
        bounds=g.font.getStringBounds(text,g.fontRenderContext)
        g.drawString(text,self.size.width/2-bounds.width/2,25+self.label_offset)
        

