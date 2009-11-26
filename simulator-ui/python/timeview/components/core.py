from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import javax
import java

class RoundedBorder(javax.swing.border.AbstractBorder):
    def __init__(self,color,thickness=1):
        self.color=color
        self.stroke=java.awt.BasicStroke(thickness)
        
    def getBorderInsets(self,component):
        return java.awt.Insets(15,15,15,15)
    def paintBorder(self,c,g,x,y,width,height):
        g.color=self.color
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.stroke=self.stroke
        g.drawRoundRect(x,y,width-1,height-1,10,10)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)



class DataViewComponent(JPanel, MouseListener, MouseWheelListener, MouseMotionListener, ActionListener):
    #hover_border=BorderFactory.createLineBorder(Color.black,2);
    #item_hover_border=BorderFactory.createLineBorder(Color(0.5,0.5,0.5),1);
    
    hover_border=RoundedBorder(Color.black,thickness=2);
    item_hover_border=RoundedBorder(Color(0.7,0.7,0.7))
    
    
    default_border=BorderFactory.createEmptyBorder()
    
    def __init__(self):
        JPanel.__init__(self)
        self.addMouseListener(self)
        self.addMouseWheelListener(self)  
        self.addMouseMotionListener(self)  
        self.hover=False
        self.min_width=20
        self.min_height=20
        self.resize_border=20
        self.popup=JPopupMenu()
        self.popup.add(JMenuItem('hide',actionPerformed=self.actionPerformed))
        self.setSize(100,50)
        self.border=self.default_border
        
    def actionPerformed(self,event):
        if event.actionCommand=='hide':
            parent=self.parent
            self.parent.remove(self) 
            parent.repaint()
    def mouseWheelMoved(self,event):  
        delta=event.wheelRotation
        scale=0.9
        if delta<0:
            scale=1.0/scale
            delta=-delta
        w=self.size.width
        h=self.size.height    
        for i in range(delta):
            w*=scale
            h*=scale
        if w<self.min_width: w=self.min_width
        if h<self.min_height: h=self.min_height
        w=int(w)
        h=int(h)
        self.setLocation(int(self.x-(w-self.size.width)/2),int(self.y-(h-self.size.height)/2))
        self.setSize(w,h)
    def mouseClicked(self, event):     
        if event.button==MouseEvent.BUTTON3:
            self.parent.add(self.popup)
            self.popup.show(self,event.x-5,event.y-5)   
    def mouseEntered(self, event):
        self.border=self.hover_border
        for n in self.view.area.components:
            if n is not self and hasattr(n,'name') and n.name==self.name:
                n.border=n.item_hover_border
        self.repaint()
    def mouseExited(self, event):        
        self.border=self.default_border
        for n in self.view.area.components:
            if n is not self and hasattr(n,'name') and n.name==self.name:
                n.border=n.default_border
        self.repaint()
    def mousePressed(self, event):  
        self.mouse_pressed_x=event.x
        self.mouse_pressed_y=event.y      
        self.mouse_pressed_size=self.size
    def mouseReleased(self, event):        
        pass
    def mouseDragged(self, event):                
        if self.cursor.type==Cursor.HAND_CURSOR:
            self.setLocation(self.x+event.x-self.mouse_pressed_x,self.y+event.y-self.mouse_pressed_y)
        if self.cursor.type in [Cursor.W_RESIZE_CURSOR,Cursor.NW_RESIZE_CURSOR,Cursor.SW_RESIZE_CURSOR]:
            w=self.size.width-event.x+self.mouse_pressed_x
            if w<self.min_width: w=self.min_width
            self.setLocation(self.x-w+self.size.width,self.y)
            self.setSize(w,self.size.height)
        if self.cursor.type in [Cursor.E_RESIZE_CURSOR,Cursor.NE_RESIZE_CURSOR,Cursor.SE_RESIZE_CURSOR]:
            w=self.mouse_pressed_size.width+event.x-self.mouse_pressed_x
            if w<self.min_width: w=self.min_width
            self.setSize(w,self.size.height)
        if self.cursor.type in [Cursor.N_RESIZE_CURSOR,Cursor.NW_RESIZE_CURSOR,Cursor.NE_RESIZE_CURSOR]:
            h=self.size.height-event.y+self.mouse_pressed_y
            if h<self.min_height: h=self.min_height
            self.setLocation(self.x,self.y-h+self.size.height)
            self.setSize(self.size.width,h)
        if self.cursor.type in [Cursor.S_RESIZE_CURSOR,Cursor.SW_RESIZE_CURSOR,Cursor.SE_RESIZE_CURSOR]:
            h=self.mouse_pressed_size.height+event.y-self.mouse_pressed_y
            if h<self.min_height: h=self.min_height
            self.setSize(self.size.width,h)
        self.repaint()    
            
    def mouseMoved(self, event):      
        size=self.resize_border
        if event.x<size:
            if event.y<size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)
            elif event.y>=self.size.height-size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)
            else:    
                self.cursor=Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)
        elif event.x>=self.size.width-size:
            if event.y<size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)
            elif event.y>=self.size.height-size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
            else:    
                self.cursor=Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)
        else:
            if event.y<size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)
            elif event.y>=self.size.height-size:
                self.cursor=Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)
            else:    
                self.cursor=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    def paintComponent(self,g):
        #if self.hover:
        #    g.color=Color(0.9,0.9,0.9)
        #    g.fillRect(0,0,self.size.width,self.size.height)
        #else:
        g.color=Color.white
        g.fillRect(0,0,self.size.width,self.size.height)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

