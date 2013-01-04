from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java

import core

from math import sqrt


class PreferredDirection(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), min=0, max=1, filter=None, decoders=False, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.data = self.view.watcher.watch(name, func, args=args)
        self.min = min
        self.max = max
        self.margin = 10
        self.decoders = decoders

        self.filter = filter
        self.setSize(200, 200)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset
        g.color = Color(0.8, 0.8, 0.8)
        g.drawOval(int(x0) - 1, int(y0) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.margin - self.label_offset) + 1)

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter
        try:
            data = self.data.get(start=self.view.current_tick, count=1, dt_tau=dt_tau)[0]
        except:
            return

        if data is None:
            return

        g.color = Color.black
        max = self.max
        min = self.min
        if callable(max):
            max = max(self)
        xc = self.width / 2
        yc = (self.height - self.label_offset) / 2 + self.label_offset
        for i, v in enumerate(data):
            v = (float(v) - min) / (max - min)
            if v < 0:
                c = 0.0
            if v > 1:
                c = 1.0

            if self.decoders:
                ex, ey = self.view.watcher.objects[self.name].getOrigin('X').decoders[i]
            else:
                ex, ey = self.view.watcher.objects[self.name].encoders[i]

            g.drawLine(xc, yc, int(xc + ex * v * self.width / 2), int(yc - ey * v * (self.height - self.label_offset) / 2))
