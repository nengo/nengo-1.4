import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *


class Item(core.DataViewComponent):
    def __init__(self,view,name):
        core.DataViewComponent.__init__(self)
        self.view=view
        self.name=name
        self.resize_border=0
        self.min_width=5
        self.min_height=5
        self.border_size=5
        self.arc_size=20
        self.label_font=Font.decode('Arial-20') 
        
        self.descent=None
        
        self.popup.add(JPopupMenu.Separator())
        for (name,func) in self.view.watcher.list(name):
            self.popup.add(JMenuItem(name,actionPerformed=lambda event,self=self,func=func: self.add_component(func)))
            
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)
        
        g.color=Color.black
        g.font=self.label_font
        
        margin=5
        
        if self.descent is None:
            bounds=g.font.getStringBounds(self.name,g.fontRenderContext)
            lm=g.font.getLineMetrics(self.name,g.fontRenderContext)
            self.descent=lm.descent
            self.setSize(int(bounds.width+margin*2),int(bounds.height+margin*2))
        
        g.drawString(self.name,margin,self.size.height-margin-self.descent)    
        
    
    def add_component(self,func,location=None,size=(200,200)):
        component=func(self.view,self.name)
        self.view.area.add(component)
        if location is None: location=(self.mouse_pressed_x+self.x,self.mouse_pressed_y+self.y)
        component.setLocation(*location)        
        component.setSize(*size)
                    
    def mouseDragged(self, event):                
        core.DataViewComponent.mouseDragged(self,event)
        self.view.frame.repaint()

