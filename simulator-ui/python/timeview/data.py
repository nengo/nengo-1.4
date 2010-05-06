import java
import javax
from javax.swing import *
from javax.swing.table import DefaultTableModel
from javax.swing.filechooser import FileNameExtensionFilter
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from java.io import *

import view

class DataPanel(JPanel):
    def __init__(self,view):
        JPanel.__init__(self)
        self.view=view
        self.background=Color.white        self.layout=BorderLayout()

        self.add(self.make_controls(),BorderLayout.SOUTH)
        
        data,title=self.extract_data()
        self.table=JTable(DefaultTableModel(data,title))
        
        self.add(JScrollPane(self.table))        

        self.fileChooser=JFileChooser()
        self.fileChooser.setFileFilter(FileNameExtensionFilter('Comma-separated values',['csv']))
        self.fileChooser.setSelectedFile(File('%s.csv'%self.view.network.name))
        
        
        
    def make_controls(self):
        panel=JPanel(background=self.background)
        panel.add(JButton(view.Icon.refresh,actionPerformed=self.refresh,rolloverIcon=view.ShadedIcon.refresh,toolTipText='refresh',borderPainted=False,focusPainted=False,contentAreaFilled=False))

        filter=JPanel(layout=BorderLayout(),opaque=False)
        self.filter = JSpinner(SpinnerNumberModel(self.view.tau_filter,0,0.5,0.01),stateChanged=self.refresh)
        filter.add(self.filter)
        filter.add(JLabel('filter'),BorderLayout.NORTH)
        filter.maximumSize=filter.preferredSize
        panel.add(filter)

        decimals=JPanel(layout=BorderLayout(),opaque=False)
        self.decimals = JSpinner(SpinnerNumberModel(3,0,10,1),stateChanged=self.refresh)
        decimals.add(self.decimals)
        decimals.add(JLabel('decimal places'),BorderLayout.NORTH)
        decimals.maximumSize=decimals.preferredSize
        panel.add(decimals)

        panel.add(JButton(view.Icon.save,actionPerformed=self.save,rolloverIcon=view.ShadedIcon.save,toolTipText='save',borderPainted=False,focusPainted=False,contentAreaFilled=False))

        return panel
        
    def extract_data(self):
    
        tau=float(self.filter.value)
        dt=self.view.dt
        if tau<dt: dt_tau=None
        else: dt_tau=dt/tau
        
        decimals=int(self.decimals.value)
        format='%%1.%df'%decimals
    
        start_time=max(0,self.view.timelog.tick_count-self.view.timelog.tick_limit+1)*self.view.dt
        
        data=[]
        title=['t']
        for key,watch in self.view.watcher.active.items():
            name,func,args=key
            d=watch.get(dt_tau=dt_tau)
            n=len(watch.get_first())
            while len(data)<len(d): 
                data.append(['%0.4f'%(start_time+(len(data)+0)*self.view.dt)])
            
            for i in range(n):
                title.append('%s.%s%s[%d]'%(name,func.__name__,args,i))
                for j in range(len(d)):
                    dd=d[j]
                    if dd is None: data[j].append(None)
                    else: data[j].append(format%dd[i])
        return data,title
        
        
    def save(self,event=None):
        if self.fileChooser.showSaveDialog(self)==JFileChooser.APPROVE_OPTION:
            f=self.fileChooser.getSelectedFile()
            writer=BufferedWriter(FileWriter(f))
            
            data,title=self.extract_data()
            title=[t.replace(',',' ') for t in title]
            writer.write(','.join(title)+'\n')
            for row in data:
                writer.write(','.join(row)+'\n')
            writer.close()    
        
    def refresh(self,event=None):
        data,title=self.extract_data()
        self.table.model.setDataVector(data,title)
        
        
    
