from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import core

from math import sqrt


class VectorGrid(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), min=-1, max=1, rows=None, filter=True, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.args = args
        self.data = self.view.watcher.watch(name, func, args=args)
        self.rows = rows
        self.margin = 10
        self.min = min
        self.max = max
        self.filter = filter
        self.setSize(200, 200)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)
        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset
        g.color = Color.black
        g.drawRect(int(x0) - 1, int(y0) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.label_offset - self.margin) + 1)

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter
        try:
            data = self.data.get(start=self.view.current_tick, count=1, dt_tau=dt_tau)[0]
        except:
            return

        if data is None:
            return

        if self.rows is None:
            rows = int(sqrt(len(data)))
        else:
            rows = self.rows
        cols = len(data) / rows
        if rows * cols < len(data):
            cols += 1

        max = self.max
        if callable(max):
            max = max()
        min = self.min
        if callable(min):
            min = min()

        dx = float(self.size.width - self.margin) / cols
        dy = float(self.size.height - self.label_offset - self.margin) / rows
        for y in range(rows):
            for x in range(cols):
                if x + y * cols < len(data):
                    index = x + y * cols
                    c = (float(data[index]) - min) / (max - min)
                    if c < 0:
                        c = 0.0
                    if c > 1:
                        c = 1.0
                    g.color = Color(c, c, c)
                    g.fillRect(int(x0 + dx * x), int(y0 + dy * y), int(dx + 1), int(dy + 1))
        #g.color=Color.black
        #g.drawRect(int(x0)-1,int(y0)-1,int(self.size.width-self.margin)+1,int(self.size.height-self.label_offset-self.margin)+1)
