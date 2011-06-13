import java
import javax
from javax.swing import *
from javax.swing.table import DefaultTableModel
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from java.io import *
from javax.swing.filechooser import FileFilter

import view

class CSVFilter(FileFilter):
    def accept(self,f):
        return f.isDirectory() or f.name.endswith('.csv')
    def getDescription(self):
        return 'comma-separated values'

class DataPanel(JPanel,MouseListener):
    def __init__(self,view):
        JPanel.__init__(self)
        self.view=view
        self.background=Color.white        self.layout=BorderLayout()

        self.popup=JPopupMenu()
        self.popup_items={}

        self.add(self.make_controls(),BorderLayout.SOUTH)
        
        data,title=self.extract_data()
        self.table=JTable(DefaultTableModel(data,title))
        
        scroll=JScrollPane(self.table)
        self.add(scroll)        

        scroll.addMouseListener(self)
        self.table.tableHeader.addMouseListener(self)
        self.table.addMouseListener(self)

        self.fileChooser=JFileChooser()
        self.fileChooser.setFileFilter(CSVFilter())
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
        pause_state=self.view.paused
        self.view.paused=True
        
        while self.view.simulating:
            java.lang.Thread.sleep(10)
    
        tau=float(self.filter.value)
        dt=self.view.dt
        if tau<dt: dt_tau=None
        else: dt_tau=dt/tau
        
        decimals=int(self.decimals.value)
        format='%%1.%df'%decimals
    
        start_index=max(0,self.view.timelog.tick_count-self.view.timelog.tick_limit+1)
        count=min(self.view.timelog.tick_limit,self.view.timelog.tick_count)
        start_time=start_index*self.view.dt
        
        data=None
        title=['t']
        keys = self.view.watcher.active.keys()
        keys.sort()
        for key in keys:
            watch = self.view.watcher.active[key]
            name,func,args=key
            
            code='%s.%s%s'%(name,func.__name__,args)
            if code not in self.popup_items:
                state=True
                if 'spike' in func.__name__: state=False
                if 'voltage' in func.__name__: state=False
                self.popup_items[code]=JCheckBoxMenuItem(code,state,stateChanged=self.refresh)
                self.popup.add(self.popup_items[code])
            
            if self.popup_items[code].state is False: continue
                
            d=watch.get(dt_tau=dt_tau,start=start_index,count=count)
            n=len(watch.get_first())
            if data is None:
                data=[]
                while len(data)<len(d): 
                    data.append(['%0.4f'%(start_time+(len(data)+0)*self.view.dt)])
            
            
            for i in range(n):
                title.append('%s[%d]'%(code,i))
                for j in range(len(data)):
                    dd=d[j]
                    if dd is None: data[j].append('')
                    else: data[j].append(format%dd[i])

        self.view.paused=pause_state            
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
        
        
    def mouseClicked(self, event):     
        if event.button==MouseEvent.BUTTON3 or (event.button==MouseEvent.BUTTON1 and event.isControlDown()):
            if self.popup is not None:
                self.popup.show(event.source,event.x-5,event.y-5)   
            
    def mouseEntered(self, event):
        pass
    def mouseExited(self, event):        
        pass
    def mousePressed(self, event):  
        pass
    def mouseReleased(self, event):        
        pass
    
