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
        self.resize_border=10
        self.border_size=5
        self.arc_size=20
        self.label_font=Font.decode('Arial-20') 
        
        self.popup.add(JPopupMenu.Separator())
        for (name,func) in self.view.watcher.list(name):
            self.popup.add(JMenuItem(name,actionPerformed=lambda event,self=self,func=func: self.add_component(func)))
            
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)
        
        #g.color=Color(0.8,0.8,0.8)
        #g.fillRoundRect(self.border_size,self.border_size,self.size.width-self.border_size*2,self.size.height-self.border_size*2,self.arc_size,self.arc_size)
        g.color=Color.black
        g.font=self.label_font
        bounds=g.font.getStringBounds(self.name,g.fontRenderContext)
        g.drawString(self.name,self.size.width/2-bounds.width/2,self.size.height/2+bounds.height/2)    
    
    def add_component(self,func,location=None,size=(200,200)):
        component=func(self.view,self.name)
        self.view.area.add(component)
        if location is None: location=(self.mouse_pressed_x+self.x,self.mouse_pressed_y+self.y)
        component.setLocation(*location)        
        component.setSize(*size)
                    
    def mouseDragged(self, event):                
        core.DataViewComponent.mouseDragged(self,event)
        self.view.frame.repaint()

