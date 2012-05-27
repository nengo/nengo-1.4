import java
from javax.swing import *
from java.awt import *

import time


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
                print params
                self.view.stats(**params).run(call_after=self.view.graph.update)
            else:    
                time.sleep(0.1)

