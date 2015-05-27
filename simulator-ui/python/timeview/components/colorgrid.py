from java.awt import Color

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

    def tick(self, t):
        try:
            self.grid = self.data.get(start=self.view.current_tick, count=1)[0]
        except:
            return

        if self.rows is None:
            self.rows = int(sqrt(len(self.grid)))

        self.cols = len(self.grid) / self.rows
        if self.rows * self.cols < len(self.grid):
            self.cols += 1



    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)
        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset
        g.color = Color.black
        g.drawRect(int(x0) - 1, int(y0) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.label_offset - self.margin) + 1)

        dt_tau = None

        if self.grid is None:
            return

        dx = float(self.size.width - self.margin) / self.cols
        dy = float(self.size.height - self.label_offset - self.margin) / self.rows

        for y in range(self.rows):
            for x in range(self.cols):
                if x + y * self.cols < len(self.grid):
                    index = x + y * self.cols
                    dat = self.grid[index]
                    if isinstance(dat, Color):
                        g.color = dat
                    elif isinstance(dat, (list,tuple)):
                        g.color = Color(dat[0], dat[1], dat[2])
                    elif isinstance(dat, float):
                        g.color = Color(dat, dat, dat)
                    g.fillRect(int(x0 + dx * x), int(y0 + dy * y), int(dx + 1), int(dy + 1))
