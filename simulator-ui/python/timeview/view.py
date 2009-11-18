import watcher
reload(watcher)
import components
reload(components)
import timelog
reload(timelog)


import java
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from ca.nengo.model.nef import *
from ca.nengo.model.impl import *
from ca.nengo.math.impl import *
from ca.nengo.model import Node


class EnsembleWatch:
    def check(self,obj):
        return isinstance(obj,NEFEnsemble)
    def voltage(self,obj):
        return [n.generator.voltage for n in obj.nodes]
    def spikes(self,obj):
        return obj.getOrigin('AXON').values.values
    def encoder(self,obj):
        return [x[0] for x in obj.encoders]
    def views(self,obj):
        return [
            ('voltage',lambda view,name: components.Grid(view,name,self.voltage,sfunc=self.spikes)),
            ('firing rate',lambda view,name: components.Grid(view,name,self.spikes,min=0,max=0.2,filter=True)),       
            ('encoders',lambda view,name: components.Grid(view,name,self.encoder,min=-1,max=1)),
            ]

class NodeWatch:
    def check(self,obj):
        return isinstance(obj,Node)
    def value(self,obj,origin):
        return obj.getOrigin(origin).values.values
    def views(self,obj):
        origins=[o.name for o in obj.origins]
        
        default=None
        if isinstance(obj,NEFEnsemble): default='X'
        elif isinstance(obj,FunctionInput): default='origin'
        
        if default in origins:
            origins.remove(default)
            origins=[default]+origins
        
        r=[]
        for name in origins:
            if name in ('AXON','current'): continue
            if name==default: text='value'
            else: text='value: '+name
            r.append((text,lambda view,name,origin=name: components.Graph(view,name,lambda obj,self=self,origin=origin: self.value(obj,origin))))
        return r    



class InputWatch:
    def check(self,obj):
        if isinstance(obj,FunctionInput):
            for x in obj.functions:
                if not isinstance(x,ConstantFunction): return False
            return True
        return False
    def constantFuncs(self,obj):
        return [x.value for x in obj.functions]
    def views(self,obj):
        return [
            ('control',lambda view,name: components.Input(view,name,self.constantFuncs)),
            ]

  
class View(MouseListener, ActionListener, java.lang.Runnable):
    def __init__(self,network,size=(800,600)):
        self.dt=0.001
        self.tau_filter=0.03
        self.current_tick=0
        self.time_shown=0.5
        
        self.timelog=timelog.TimeLog()
        self.network=network
        self.watcher=watcher.Watcher(self.timelog)
        self.watcher.add_watch(NodeWatch())
        self.watcher.add_watch(EnsembleWatch())
        self.watcher.add_watch(InputWatch())
        
        self.frame=JFrame(network.name)
        self.frame.visible=True
        self.frame.layout=BorderLayout()
        
        self.area=JPanel()
        self.area.background=Color.white
        self.area.layout=None
        self.area.addMouseListener(self)
        self.frame.add(self.area)

        self.time_control=TimeControl(self)
        self.frame.add(self.time_control,BorderLayout.SOUTH)
        
        self.frame.size=size        
       
        self.popup=JPopupMenu()
        self.area.add(self.popup)
       
        names=[(n.name,n) for n in network.nodes]
        names.sort()
        
        for i,(name,n) in enumerate(names):
            self.watcher.add_object(name,n)
            self.add_item(name,location=(100+i*110,100))
            self.popup.add(JMenuItem(name,actionPerformed=lambda event,self=self,name=name: self.add_item(name,self.mouse_click_location)))

        self.restart=False
        self.paused=True
        java.lang.Thread(self).start()

    def add_item(self,name,location):
        g=components.Item(self,name)
        g.setLocation(*location)
        self.area.add(g)
            
    def mouseClicked(self, event):     
        self.mouse_click_location=event.x,event.y
        if event.button==MouseEvent.BUTTON3:
            self.popup.show(self.area,event.x-5,event.y-5)   
            
    def mouseEntered(self, event):
        pass
    def mouseExited(self, event):        
        pass
    def mousePressed(self, event):  
        pass
    def mouseReleased(self, event):        
        pass

    def run(self):
        while True:
            self.network.simulator.resetNetwork(True)
            self.watcher.reset()
            now=0
            self.time_control.set_min_time(max(0,self.timelog.tick_count-self.timelog.tick_limit+1))
            self.time_control.set_max_time(self.timelog.tick_count)                
            self.area.repaint()
            while True:
                while self.paused and not self.restart:
                    java.lang.Thread.sleep(10)
                if self.restart:
                    self.restart=False
                    break
                self.network.simulator.run(now,now+self.dt,self.dt)
                now+=self.dt
                self.timelog.tick()                
                self.time_control.set_min_time(max(0,self.timelog.tick_count-self.timelog.tick_limit+1))
                self.time_control.set_max_time(self.timelog.tick_count)                
                if self.current_tick==self.timelog.tick_count-1:
                    self.time_control.slider.value=self.timelog.tick_count
                self.area.repaint()
                java.lang.Thread.sleep(10)
        
    
class TimeControl(JPanel,ChangeListener):
    def __init__(self,view):
        JPanel.__init__(self)
        self.view=view
        self.layout=BorderLayout()
        self.background=Color.blue  
        self.slider=JSlider(0,1,0)          
        self.slider.snapToTicks=True
        self.add(self.slider)
        self.slider.addChangeListener(self)


        self.min_time=JLabel(' 0.0000 ',opaque=True)
        self.max_time=JLabel(' 0.0000 ',opaque=True)
        
        self.left_panel=JPanel()
        self.left_panel.add(self.min_time)
        self.left_panel.add(JButton('<',actionPerformed=lambda x: self.slider.setValue(self.slider.value-1)))
                            
        self.right_panel=JPanel()
        self.right_panel.add(JButton('>',actionPerformed=lambda x: self.slider.setValue(self.slider.value+1)))
        self.right_panel.add(JButton('>|',actionPerformed=lambda x: self.slider.setValue(self.slider.maximum)))
        self.right_panel.add(self.max_time)
                             
        self.add(self.left_panel,BorderLayout.WEST)
        self.add(self.right_panel,BorderLayout.EAST)


        self.buttons=JPanel()

        spin=JPanel(layout=BorderLayout())
        spin.add(JSpinner(SpinnerNumberModel((self.view.timelog.tick_limit-1)*self.view.dt,1,100,1),stateChanged=self.tick_limit))
        spin.add(JLabel('recording time'),BorderLayout.SOUTH)
        self.buttons.add(spin)

        self.buttons.add(JButton('restart',actionPerformed=self.start))

        self.buttons.add(JButton('continue',actionPerformed=self.pause))
        
        spin=JPanel(layout=BorderLayout())
        spin.add(JSpinner(SpinnerNumberModel(self.view.tau_filter,0,0.5,0.01),stateChanged=self.tau_filter))
        spin.add(JLabel('filter'),BorderLayout.SOUTH)
        self.buttons.add(spin)

        spin=JPanel(layout=BorderLayout())
        spin.add(JSpinner(SpinnerNumberModel(self.view.time_shown,0.1,50,0.1),stateChanged=self.time_shown))
        spin.add(JLabel('time shown'),BorderLayout.SOUTH)
        self.buttons.add(spin)


        self.add(self.buttons,BorderLayout.SOUTH)
    def set_max_time(self,maximum):
        self.slider.maximum=maximum
        self.max_time.text=' %1.4f '%(self.view.dt*maximum)
    def set_min_time(self,minimum):
        self.slider.minimum=minimum    
        self.min_time.text=' %1.4f '%(self.view.dt*minimum)
    def stateChanged(self,event):
        self.view.current_tick=self.slider.value
        self.view.area.repaint()
    def start(self,event):
        self.view.restart=True
    def pause(self,event):
        self.view.paused=not self.view.paused
        if self.view.paused:
            event.source.text='continue'
        else:
            event.source.text=' pause '    
    def tau_filter(self,event):
        self.view.tau_filter=float(event.source.value)
        self.view.area.repaint()
    def time_shown(self,event):
        self.view.time_shown=float(event.source.value)
        self.view.area.repaint()
    def tick_limit(self,event):
        self.view.timelog.tick_limit=int(event.source.value/self.view.dt)+1
