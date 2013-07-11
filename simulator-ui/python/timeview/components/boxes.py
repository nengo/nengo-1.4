from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import core

from math import sqrt


class Boxes(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), rows=None, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.args = args
        self.data = self.view.watcher.watch(name, func, args=args)
        self.margin = 10
        self.setSize(200, 200)
        self.boxes = None
        
    def tick(self, t):
        try:
            self.boxes = self.data.get(start=self.view.current_tick, count=1)[0]
        except Exception,e:
            print "error in get",e
            return
        
        self.minx = 1000
        self.maxx = -1
        self.miny = 1000
        self.maxy = -1
        
        for b in self.boxes:
            x0 = b[1][0]
            x1 = b[2][0]
            y0 = b[1][1]
            y1 = b[2][1]
            if x0 < self.minx:
                self.minx = x0
            if x1 > self.maxx:
                self.maxx = x1
            if y0 < self.miny:
                self.miny = y0
            if y1 > self.maxy:
                self.maxy = y1
                
        self.xscale = self.size.width / (self.maxx - self.minx)
        self.yscale = self.size.height / (self.maxy - self.miny)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        if self.boxes is None:
            return
        
        for b in self.boxes:
            g.color = b[0]
            x0 = b[1][0]
            y0 = b[1][1]
            x = b[2][0] - x0
            y = b[2][1] - y0
            g.fillRect(int(x0*self.xscale), int(y0*self.yscale), int(x*self.xscale), int(y*self.yscale))
