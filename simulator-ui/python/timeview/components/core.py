from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import javax
import java

################################################################################


class RoundedBorder(javax.swing.border.AbstractBorder):
    def __init__(self, color, thickness=1):
        self.color = color
        self.stroke = java.awt.BasicStroke(thickness)

    def getBorderInsets(self, component):
        return java.awt.Insets(15, 15, 15, 15)

    def paintBorder(self, c, g, x, y, width, height):
        g.color = self.color
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.stroke = self.stroke
        g.drawRoundRect(x, y, width - 1, height - 1, 10, 10)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)

################################################################################


class DataViewComponent(JPanel, MouseListener, MouseWheelListener, MouseMotionListener, ActionListener):
    #hover_border=BorderFactory.createLineBorder(Color.black,2);
    #item_hover_border=BorderFactory.createLineBorder(Color(0.5,0.5,0.5),1);

    hover_border = RoundedBorder(Color.black, thickness=2)
    item_hover_border = RoundedBorder(Color(0.7, 0.7, 0.7))

    default_border = BorderFactory.createEmptyBorder()

    def __init__(self, label=None):
        JPanel.__init__(self)
        self.addMouseListener(self)
        self.addMouseWheelListener(self)
        self.addMouseMotionListener(self)
        self.hover = False
        self.min_width = 20
        self.min_height = 20
        self.resize_border = 20
        self.max_show_dim = 30        # The maximum number of display dimensions to show in the popup menu
        self.setSize(100, 50)
        self.border = self.default_border

        self.popup = JPopupMenu()
        self.popup.add(JMenuItem('hide', actionPerformed=self.hideme))

        self.show_label = False
        self.label = label
        self.label_offset = 0
        if self.label is not None:
            self.show_label = True
            self.popup.add(JPopupMenu.Separator())
            self.popup_label = JCheckBoxMenuItem('label', self.show_label, actionPerformed=self.toggle_label)
            self.popup.add(self.popup_label)
            self.label_height = 15
            self.update_label()
        else:
            self.label_height = 0

    def save(self):
        return dict(x=self.x, y=self.y, width=self.width, height=self.height, label=self.show_label)

    def restore(self, d):
        self.setLocation(d['x'], d['y'])
        self.setSize(d['width'], d['height'])
        self.show_label = d.get('label', False)
        if self.label is not None:
            self.popup_label.state = self.show_label
        self.label_offset = self.label_height * self.show_label

    def do_hide(self):
        parent = self.parent
        self.visible = False
        if self.parent is not None:
            self.parent.remove(self)
            parent.repaint()

    def hideme(self, event):
        if event.actionCommand == 'hide':
            self.do_hide()

    def toggle_label(self, event):
        self.show_label = event.source.state
        self.update_label()

    def update_label(self):
        if(self.show_label):
            self.setLocation(self.x, self.y - self.label_height)
            self.setSize(self.size.width, self.size.height + self.label_height)
            self.label_offset = self.label_height
        else:
            self.setLocation(self.x, self.y + self.label_height)
            self.setSize(self.size.width, self.size.height - self.label_height)
            self.label_offset = 0

        self.repaint()

    def mouseWheelMoved(self, event):
        delta = event.wheelRotation
        scale = 0.9
        if delta < 0:
            scale = 1.0 / scale
            delta = -delta
        w = self.size.width
        h = self.size.height
        for i in range(delta):
            w *= scale
            h *= scale
        if w < self.min_width:
            w = self.min_width
        if h < self.min_height:
            h = self.min_height
        w = int(w)
        h = int(h)
        self.setLocation(int(self.x - (w - self.size.width) / 2), int(self.y - (h - self.size.height) / 2))
        self.setSize(w, h)

    def mouseClicked(self, event):
        if event.button == MouseEvent.BUTTON3 or (event.button == MouseEvent.BUTTON1 and event.isControlDown()):
            self.parent.add(self.popup)
            self.popup.show(self, event.x - 5, event.y - 5)

    def mouseEntered(self, event):
        self.border = self.hover_border
        for n in self.view.area.components:
            if n is not self and hasattr(n, 'name') and n.name == self.name:
                n.border = n.item_hover_border
        self.repaint()

    def mouseExited(self, event):
        self.border = self.default_border
        for n in self.view.area.components:
            if n is not self and hasattr(n, 'name') and n.name == self.name:
                n.border = n.default_border
        self.repaint()

    def mousePressed(self, event):
        self.mouse_pressed_x = event.x
        self.mouse_pressed_y = event.y
        self.mouse_pressed_size = self.size

    def mouseReleased(self, event):
        pass

    def mouseDragged(self, event):
        if self.cursor.type == Cursor.HAND_CURSOR:
            self.setLocation(self.x + event.x - self.mouse_pressed_x, self.y + event.y - self.mouse_pressed_y)
        if self.cursor.type in [Cursor.W_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR]:
            w = self.size.width - event.x + self.mouse_pressed_x
            if w < self.min_width:
                w = self.min_width
            self.setLocation(self.x - w + self.size.width, self.y)
            self.setSize(w, self.size.height)
        if self.cursor.type in [Cursor.E_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR]:
            w = self.mouse_pressed_size.width + event.x - self.mouse_pressed_x
            if w < self.min_width:
                w = self.min_width
            self.setSize(w, self.size.height)
        if self.cursor.type in [Cursor.N_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR]:
            h = self.size.height - event.y + self.mouse_pressed_y
            if h < self.min_height:
                h = self.min_height
            self.setLocation(self.x, self.y - h + self.size.height)
            self.setSize(self.size.width, h)
        if self.cursor.type in [Cursor.S_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR]:
            h = self.mouse_pressed_size.height + event.y - self.mouse_pressed_y
            if h < self.min_height:
                h = self.min_height
            self.setSize(self.size.width, h)
        self.view.area.repaint()

    def mouseMoved(self, event):
        size = self.resize_border
        if event.x < size:
            if event.y < size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)
            elif event.y >= self.size.height - size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)
            else:
                self.cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)
        elif event.x >= self.size.width - size:
            if event.y < size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)
            elif event.y >= self.size.height - size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
            else:
                self.cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)
        else:
            if event.y < size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)
            elif event.y >= self.size.height - size:
                self.cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)
            else:
                self.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

    def paintComponent(self, g):
        #if self.hover:
        #    g.color=Color(0.9,0.9,0.9)
        #    g.fillRect(0,0,self.size.width,self.size.height)
        #else:
        g.color = Color.white
        g.fillRect(0, 0, self.size.width, self.size.height)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        if self.show_label:
            g.color = Color(0.3, 0.3, 0.3)
            bounds = g.font.getStringBounds(self.label, g.fontRenderContext)
            g.drawString(self.label, self.size.width / 2 - bounds.width / 2, bounds.height)
