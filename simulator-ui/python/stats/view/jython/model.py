from javax.swing import *
from java.awt import *

import os


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
        

