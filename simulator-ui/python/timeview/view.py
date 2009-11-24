import watcher
reload(watcher)
import components
reload(components)
import timelog
reload(timelog)


import java
import javax
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from ca.nengo.model.nef import *
from ca.nengo.model.impl import *
from ca.nengo.math.impl import *
from ca.nengo.model import Node

from java.lang.System.err import println

class Icon:
    pass
class ShadedIcon:
    pass    
    
for name in 'pause play configure end start backward forward restart'.split():
    setattr(Icon,name,ImageIcon('python/images/%s.png'%name))
    setattr(ShadedIcon,name,ImageIcon('python/images/%s-pressed.png'%name))


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
            ('firing rate',lambda view,name: components.Grid(view,name,self.spikes,min=0,max=lambda view=view: 200*view.dt,filter=True)),       
            #('encoders',lambda view,name: components.Grid(view,name,self.encoder,min=-1,max=1)),
            ]

class NodeWatch:
    def check(self,obj):
        return isinstance(obj,Node)
    def value(self,obj,origin):
        return obj.getOrigin(origin).values.values
    def views(self,obj):
        origins=[o.name for o in obj.origins]
        
        default=None
        if isinstance(obj,NEFEnsemble): 
            default='X'
            max_radii = max(obj.radii)
        elif isinstance(obj,FunctionInput): 
            default='origin'
            max_radii = 1
        
        if default in origins:
            origins.remove(default)
            origins=[default]+origins
        
        r=[]
        for name in origins:
            if name in ['AXON','current']: continue
            if name == default: 
                text='value'
                text_grid = 'value (grid)'
            else: 
                text='value: '+name
                text_grid='value (grid): ' + name
            
            r.append((text,lambda view,name,origin=name: components.Graph(view,name,lambda obj,self=self,origin=origin: self.value(obj,origin))))
            r.append((text_grid,lambda view,name,origin=name: components.VectorGrid(view,name,lambda obj,self=self,origin=origin: self.value(obj,origin), -max_radii, max_radii)))
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
    def __init__(self,network,size=None,ui=None):
        self.dt=0.001
        self.tau_filter=0.03
        self.delay=10
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
        
        if size is None:
            if ui is None: size=(800,600)
            else: size=(int(ui.width),int(ui.height))
        
        self.frame.size=(size[0],size[1]+100)        
       
        self.popup=JPopupMenu()
        self.area.add(self.popup)
       
        names=[(n.name,n) for n in network.nodes]
        names.sort()
        
        for i,(name,n) in enumerate(names):
            self.watcher.add_object(name,n)
            self.popup.add(JMenuItem(name,actionPerformed=lambda event,self=self,name=name: self.add_item(name,self.mouse_click_location)))
        
        if ui is not None:
            p0=ui.localToView(java.awt.geom.Point2D.Double(0,0))
            p1=ui.localToView(java.awt.geom.Point2D.Double(ui.width,ui.height))
            
            for n in ui.UINodes:
                x=(n.offset.x-p0.x)/(p1.x-p0.x)*size[0]
                y=(n.offset.y-p0.y)/(p1.y-p0.y)*size[1]
                self.add_item(n.name,location=(int(x),int(y)))

        self.restart=False
        self.paused=True
        th=java.lang.Thread(self)
        th.priority=java.lang.Thread.MIN_PRIORITY
        th.start()

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
    def set_target_rate(self,value):
        if value=='fastest': self.delay=0
        elif value.endswith('x'):
            r=float(value[:-1])
            self.delay=self.dt*1000/r
        

    def run(self):
        while self.frame.visible:
            self.network.simulator.resetNetwork(True)
            self.watcher.reset()
            now=0
            self.time_control.set_min_time(max(0,self.timelog.tick_count-self.timelog.tick_limit+1))
            self.time_control.set_max_time(self.timelog.tick_count)                
            self.area.repaint()
            last_frame_time=None
            counter=0
            while self.frame.visible:
                while (self.paused or self.timelog.processing) and not self.restart and self.frame.visible:
                    java.lang.Thread.sleep(10)
                if self.restart or not self.frame.visible:
                    self.restart=False
                    break
                    
                if self.current_tick>=self.timelog.tick_count-1:    
                    self.network.simulator.run(now,now+self.dt,self.dt)
                    now+=self.dt
                    self.timelog.tick()                
                    self.time_control.set_min_time(max(0,self.timelog.tick_count-self.timelog.tick_limit+1))
                    self.time_control.set_max_time(self.timelog.tick_count)                
                    self.time_control.slider.value=self.timelog.tick_count
                else:
                    self.time_control.slider.value=self.current_tick+1
                self.area.repaint()
                this_frame_time=java.lang.System.currentTimeMillis()
                if last_frame_time is not None:
                    delta=this_frame_time-last_frame_time
                    sleep=self.delay-delta
                    if sleep<0: sleep=0
                    #if sleep<1:
                    #    sleep=1
                    java.lang.Thread.sleep(int(sleep))
                last_frame_time=this_frame_time
    
    
class RoundedBorder(javax.swing.border.AbstractBorder):
    def __init__(self):
        self.color=Color(0.7,0.7,0.7)
    def getBorderInsets(self,component):
        return java.awt.Insets(5,5,5,5)
    def paintBorder(self,c,g,x,y,width,height):
        g.color=self.color
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.drawRoundRect(x,y,width-1,height-1,10,10)
        

        
    
class TimeControl(JPanel,ChangeListener,ActionListener):
    def __init__(self,view):
        JPanel.__init__(self)
        self.view=view
        self.background=Color.white
        
        
        
        mainPanel=JPanel(background=self.background,layout=BorderLayout())
        mainPanel.border=RoundedBorder()
        configPanel=JPanel(background=self.background)


        self.layout=BorderLayout()
        self.add(mainPanel)        
        self.add(configPanel,BorderLayout.SOUTH)
        
        self.configPanel=configPanel

        #self.splitPane=JSplitPane(JSplitPane.VERTICAL_SPLIT,mainPanel,configPanel)
        #self.add(self.splitPane)
        
        
        self.slider=JSlider(0,1,0,background=self.background)          
        self.slider.snapToTicks=True
        mainPanel.add(self.slider)
        self.slider.addChangeListener(self)


        self.min_time=JLabel(' 0.0000 ',opaque=True,background=self.background)
        self.max_time=JLabel(' 0.0000 ',opaque=True,background=self.background)
        
        self.left_panel=JPanel(background=self.background)
        self.config_button=JButton(Icon.configure,rolloverIcon=ShadedIcon.configure,toolTipText='configure',actionPerformed=self.configure,borderPainted=False,focusPainted=False,contentAreaFilled=False)
        self.left_panel.add(self.config_button)
        self.left_panel.add(JButton(Icon.restart,rolloverIcon=ShadedIcon.restart,toolTipText='restart',actionPerformed=self.start,borderPainted=False,focusPainted=False,contentAreaFilled=False))
        self.left_panel.add(self.min_time)
        #self.left_panel.add(JButton(icon=Icon.backward,rolloverIcon=ShadedIcon.backward,toolTipText='backward one frame',actionPerformed=self.backward_one_frame))
        self.left_panel.add(JButton(icon=Icon.start,rolloverIcon=ShadedIcon.start,toolTipText='jump to beginning',actionPerformed=lambda x: self.slider.setValue(self.slider.minimum),borderPainted=False,focusPainted=False,contentAreaFilled=False))
                            
        self.right_panel=JPanel(background=self.background)
        #self.right_panel.add(JButton(icon=Icon.forward,rolloverIcon=ShadedIcon.forward,toolTipText='forward one frame',actionPerformed=self.forward_one_frame))
        self.right_panel.add(JButton(icon=Icon.end,rolloverIcon=ShadedIcon.end,toolTipText='jump to end',actionPerformed=lambda x: self.slider.setValue(self.slider.maximum),borderPainted=False,focusPainted=False,contentAreaFilled=False))
        self.right_panel.add(self.max_time)
        self.right_panel.add(JButton(Icon.play,actionPerformed=self.pause,rolloverIcon=ShadedIcon.play,toolTipText='continue',borderPainted=False,focusPainted=False,contentAreaFilled=False))



                             
        mainPanel.add(self.left_panel,BorderLayout.WEST)
        mainPanel.add(self.right_panel,BorderLayout.EAST)





        dt=JPanel(layout=BorderLayout())
        cb=JComboBox(['0.001','0.0005','0.0002','0.0001'])
        cb.setSelectedItem(0)
        self.view.dt=float(cb.getSelectedItem())
        cb.addActionListener(self)
        self.dt_combobox=cb        
        dt.add(cb)
        dt.add(JLabel('time step'),BorderLayout.SOUTH)
        configPanel.add(dt)

        rate=JPanel(layout=BorderLayout())
        self.rate_combobox=JComboBox(['fastest','1x','0.5x','0.2x','0.1x','0.05x','0.02x','0.01x','0.005x','0.002x','0.001x'])
        self.rate_combobox.setSelectedItem(0)
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())
        self.rate_combobox.addActionListener(self)
        rate.add(self.rate_combobox)
        rate.add(JLabel('speed'),BorderLayout.SOUTH)
        configPanel.add(rate)


        spin=JPanel(layout=BorderLayout())
        self.record_time_spinner=JSpinner(SpinnerNumberModel((self.view.timelog.tick_limit-1)*self.view.dt,0.1,100,1),stateChanged=self.tick_limit)
        spin.add(self.record_time_spinner)
        spin.add(JLabel('recording time'),BorderLayout.SOUTH)
        configPanel.add(spin)

        
        spin=JPanel(layout=BorderLayout())
        spin.add(JSpinner(SpinnerNumberModel(self.view.tau_filter,0,0.5,0.01),stateChanged=self.tau_filter))
        spin.add(JLabel('filter'),BorderLayout.SOUTH)
        configPanel.add(spin)

        spin=JPanel(layout=BorderLayout())
        spin.add(JSpinner(SpinnerNumberModel(self.view.time_shown,0.01,50,0.1),stateChanged=self.time_shown))
        spin.add(JLabel('time shown'),BorderLayout.SOUTH)
        configPanel.add(spin)

        configPanel.minimumSize=(0,0)
        



    def forward_one_frame(self,event):
        self.slider.setValue(self.slider.value+1)            
    def backward_one_frame(self,event):
        self.slider.setValue(self.slider.value-1)            
        
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
    def configure(self,event):
        if self.configPanel.visible:
            self.view.frame.setSize(self.view.frame.width,self.view.frame.height-self.configPanel.height)
            self.configPanel.visible=False
        else:    
            self.view.frame.setSize(self.view.frame.width,self.view.frame.height+self.configPanel.height)
            self.configPanel.visible=True
    def pause(self,event):
        self.view.paused=not self.view.paused
        if self.view.paused:
            event.source.icon=Icon.play
            event.source.rolloverIcon=ShadedIcon.play
            event.source.toolTipText='continue'
        else:
            event.source.icon=Icon.pause
            event.source.rolloverIcon=ShadedIcon.pause
            event.source.toolTipText='pause'
    def tau_filter(self,event):
        self.view.tau_filter=float(event.source.value)
        self.view.area.repaint()
    def time_shown(self,event):
        self.view.time_shown=float(event.source.value)
        self.view.area.repaint()
    def actionPerformed(self,event):
        dt=float(self.dt_combobox.getSelectedItem())
        if dt!=self.view.dt:
            self.view.dt=dt
            self.record_time_spinner.value=(self.view.timelog.tick_limit-1)*self.view.dt
            self.dt_combobox.repaint()
            self.view.restart=True
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())
    def tick_limit(self,event):
        self.view.timelog.tick_limit=int(event.source.value/self.view.dt)+1
