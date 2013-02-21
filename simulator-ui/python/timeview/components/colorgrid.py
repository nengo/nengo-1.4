from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import core

from math import sqrt


class ColorGrid(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), rows=None, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.args = args
        self.data = self.view.watcher.watch(name, func, args=args)
        self.rows = rows
        self.margin = 10
        self.setSize(200, 200)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)
        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset
        g.color = Color.black
        g.drawRect(int(x0) - 1, int(y0) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.label_offset - self.margin) + 1)

        dt_tau = None

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

        dx = float(self.size.width - self.margin) / cols
        dy = float(self.size.height - self.label_offset - self.margin) / rows
        for y in range(rows):
            for x in range(cols):
                if x + y * cols < len(data):
                    index = x + y * cols
                    dat = data[index]
                    if isinstance(dat, Color):
                        g.color = dat
                    elif isinstance(dat, (list,tuple)):
                        g.color = Color(dat[0], dat[1], dat[2])
                    elif isinstance(dat, float):
                        g.color = Color(dat, dat, dat) 
                    g.fillRect(int(x0 + dx * x), int(y0 + dy * y), int(dx + 1), int(dy + 1))
