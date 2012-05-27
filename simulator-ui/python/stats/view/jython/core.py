from javax.swing import *
from java.awt import *
from java.awt.event import *

import stats

from .graph import GraphPanel
from .run import RunPanel
from .options import OptionsPanel
from .model import ModelSelectionPanel
        

                
class View:
    def __init__(self):
        self.stats=None
        self.frame=JFrame('Statistics',visible=True,layout=BorderLayout())
        
        self.graph=GraphPanel(self,componentResized=lambda event: self.graph.update())
        self.frame.add(self.graph)
        
        self.options=OptionsPanel(self)
        self.frame.add(self.options,BorderLayout.WEST)
        
        
        self.model_selection=ModelSelectionPanel(self)
        self.frame.add(self.model_selection,BorderLayout.NORTH)
        
        self.frame.add(RunPanel(self),BorderLayout.SOUTH)
        
        self.frame.size=(800,600)
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
            
            


        
        
        
