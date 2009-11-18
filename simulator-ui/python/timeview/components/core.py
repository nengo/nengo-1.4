from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *


class DataViewComponent(JPanel, MouseListener, MouseWheelListener, MouseMotionListener, ActionListener):
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
        self.popup.add(JMenuItem('delete',actionPerformed=self.actionPerformed))
        self.add(self.popup)
        self.setSize(100,50)
    def actionPerformed(self,event):
        if event.actionCommand=='delete':
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
            self.popup.show(self,event.x-5,event.y-5)   
    def mouseEntered(self, event):
        self.hover=True
        self.repaint()
    def mouseExited(self, event):        
        self.hover=False
        self.repaint()
    def mousePressed(self, event):  
        self.mouse_pressed_x=event.x
        self.mouse_pressed_y=event.y      
        self.mouse_pressed_size=self.size
    def mouseReleased(self, event):        
        pass
    def mouseDragged(self, event):                
        self.hover=True
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
        if self.hover:
            g.color=Color(0.9,0.9,0.9)
        else:
            g.color=Color(1.0,1.0,1.0)    
        g.fillRect(0,0,self.size.width,self.size.height)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

