import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *


class Item(core.DataViewComponent):
    def __init__(self, view, name):
        core.DataViewComponent.__init__(self)
        self.view = view
        self.name = name
        self.resize_border = 0
        self.min_width = 5
        self.min_height = 5
        self.border_size = 5
        self.arc_size = 20
        self.label_font = Font.decode('Arial-20')
        self.type = None

        self.descent = None

        if(self.view.watcher.contains(name)):
            self.popup.add(JPopupMenu.Separator())

        popup_menu = self.popup
        for (type, klass, args) in self.view.watcher.list(name):
            if(klass is JMenu):
                popup_menu = args
                self.popup.add(popup_menu)
            elif(klass is None):
                popup_menu = self.popup
            else:
                if '|' in type:
                    text = type.split('|', 1)[0]
                else:
                    text = type
                popup_menu.add(
                    JMenuItem(text, actionPerformed=lambda event, self=self, klass=klass, args=args,
                              type=type: self.add_component(type, klass, args)))

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        g.color = Color.black
        g.font = self.label_font

        margin = 5

        if self.descent is None:
            bounds = g.font.getStringBounds(self.name, g.fontRenderContext)
            lm = g.font.getLineMetrics(self.name, g.fontRenderContext)
            self.descent = lm.descent
            self.setSize(int(bounds.width + margin * 2), int(bounds.height + margin * 2))
            self.view.area.repaint()

        g.drawString(self.name, margin, self.size.height - margin - self.descent)

    def add_component(self, type, klass, args, location=None, size=(200, 200)):
        component = klass(self.view, self.name, **args)
        component.type = type
        self.view.area.add(component)
        if location is None:
            location = (self.mouse_pressed_x + self.x, self.mouse_pressed_y + self.y)
        component.setLocation(*location)
        #component.setSize(*size)

    def mouseDragged(self, event):
        core.DataViewComponent.mouseDragged(self, event)
        self.view.frame.repaint()
