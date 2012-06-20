import nef

class TextNode(nef.SimpleNode):
    def __init__(self,name,text):
        nef.SimpleNode.__init__(self,name)
        self.text=text
    def current_text(self):
        if callable(self.text): return self.text(self.t)
        else: return self.text

from javax.swing import *
from javax.swing.event import *
from java.awt import *
import timeview.components.core as core
import timeview.view
import java
import javax

class TextView(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),label=None):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.show_label=False
        self.update_label()
        self.data=self.view.watcher.watch(name,func,args=args)        
        self.text_label=JLabel('',verticalAlignment=JLabel.CENTER,horizontalAlignment=JLabel.CENTER)
        self.text_label.font=Font('SansSerif',Font.BOLD,12)
        self.font_size=12
        self.add(self.text_label)

        self.popup.add(JPopupMenu.Separator())        
        self.popup.add(JMenuItem('larger',actionPerformed=self.increase_font))
        self.popup.add(JMenuItem('smaller',actionPerformed=self.decrease_font))
        
    def increase_font(self,event):
        self.font_size+=2
        self.text_label.font=Font('SansSerif',Font.BOLD,self.font_size)
    def decrease_font(self,event):
        if self.font_size<7: return
        self.font_size-=2
        self.text_label.font=Font('SansSerif',Font.BOLD,self.font_size)
            
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)        
        data=self.data.get(start=self.view.current_tick,count=1)[0]
        self.text_label.setSize(self.width,self.height-self.label_offset)
        self.text_label.setLocation(0,self.label_offset)
        self.text_label.text=data

        
    def save(self):
        info = core.DataViewComponent.save(self)
        info['font_size']=self.font_size
        return info
    
    def restore(self,d):
        core.DataViewComponent.restore(self,d)
        self.font_size=d.get('font_size',12)
        self.text_label.font=Font('SansSerif',Font.BOLD,self.font_size)
        


class TextWatch:
    def check(self,obj):
        return isinstance(obj,TextNode)
    def text(self,obj):
        return obj.current_text()
    def views(self,obj):
        return [('text',TextView,dict(func=self.text,label=obj.name)),
               ]    
import timeview        
timeview.view.watches.append(TextWatch())        


