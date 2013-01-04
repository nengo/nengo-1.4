import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

from math import sqrt
from math import log
from math import ceil


def safe_get_index(x, i):
    if x is None:
        return None
    return x[i]


def round(x):
    sign = 1
    if x < 0:
        sign = -1
        x = -x
    v = 1e-30
    if v > x:
        mn, mx = 0, v
    else:
        while v * 10 < x:
            v *= 10
        if v * 2 > x:
            mn, mx = v, 2 * v
        elif v * 5 > x:
            mn, mx = 2 * v, 5 * v
        else:
            mn, mx = 5 * v, 10 * v
    if sign < 0:
        mn, mx = -mx, -mn
    return mn, mx


class SpikeLineOverlay(core.DataViewComponent):
    def __init__(self, view, name, infunc, outfunc, lfunc, inargs=(), outargs=(), largs=(), label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.infunc = infunc
        self.outfunc = outfunc
        self.lfunc = lfunc
        self.in_neuron = 0
        self.out_neuron = 0
        self.indata = self.view.watcher.watch(name, infunc, args=inargs)
        self.outdata = self.view.watcher.watch(name, outfunc, args=outargs)
        self.ldata = self.view.watcher.watch(name, lfunc, args=largs)
        self.label_height = 10
        self.border_top = 10
        self.border_left = 30
        self.border_right = 30
        self.border_bottom = 20
        self.setSize(300, 200)
        self.last_maxy = None
        self.neurons = len(self.outdata.get_first())

        self.popup.add(JPopupMenu.Separator())

        in_menu = JMenu('in neuron')
        self.in_group = ButtonGroup()
        for i in range(self.neurons):
            checked = (i == self.in_neuron)
            button = JRadioButtonMenuItem('%s[%d]' % ('in', i), checked,
                                          actionPerformed=lambda x, i=i: self.set_in_neuron(i))
            in_menu.add(button)
            self.in_group.add(button)
        self.popup.add(in_menu)

        out_menu = JMenu('out neuron')
        self.in_group = ButtonGroup()
        for i in range(self.neurons):
            checked = (i == self.out_neuron)
            button = JRadioButtonMenuItem('%s[%d]' % ('out', i), checked,
                                          actionPerformed=lambda x, i=i: self.set_out_neuron(i))
            out_menu.add(button)
            self.in_group.add(button)
        self.popup.add(out_menu)

    def set_in_neuron(self, x):
        self.in_neuron = x
        self.repaint()

    def set_out_neuron(self, x):
        self.out_neuron = x
        self.repaint()

    def save(self):
        d = core.DataViewComponent.save(self)

        return d

    def restore(self, d):
        core.DataViewComponent.restore(self, d)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        border_top = self.border_top + self.label_offset

        g.color = Color(0.8, 0.8, 0.8)
        g.drawLine(self.border_left, self.height - self.border_bottom, self.size.width - self.border_right, self.size.height - self.border_bottom)

        pts = int(self.view.time_shown / self.view.dt)

        start = self.view.current_tick - pts + 1
        if start < 0:
            start = 0
        if start <= self.view.timelog.tick_count - self.view.timelog.tick_limit:
            start = self.view.timelog.tick_count - self.view.timelog.tick_limit + 1

        g.color = Color.black
        txt = '%4g' % ((start + pts) * self.view.dt)
        bounds = g.font.getStringBounds(txt, g.fontRenderContext)
        g.drawString(txt, self.size.width - self.border_right - bounds.width / 2, self.size.height - self.border_bottom + bounds.height)

        txt = '%4g' % ((start) * self.view.dt)
        bounds = g.font.getStringBounds(txt, g.fontRenderContext)
        g.drawString(txt, self.border_left - bounds.width / 2, self.size.height - self.border_bottom + bounds.height)

        indata = self.indata.get(start=start, count=pts)
        outdata = self.outdata.get(start=start, count=pts)
        ldata = self.ldata.get(start=start, count=pts)
        now = self.view.current_tick - start
        for i in range(now + 1, len(indata)):
            indata[i] = None
            outdata[i] = None
            ldata[i] = None

        maxy = None
        miny = None
        dx = float(self.size.width - self.border_left - self.border_right - 1) / (pts - 1)

        index = (self.neurons * self.out_neuron) + self.in_neuron
        fdata = [safe_get_index(x, index) for x in ldata]
        trimmed = [x for x in fdata if x is not None]
        if len(trimmed) > 0:
            fmaxy = max(trimmed)
            fminy = min(trimmed)
            fmaxy = round(fmaxy)[1]
            fminy = round(fminy)[0]
            if maxy is None or fmaxy > maxy:
                maxy = fmaxy
            if miny is None or fminy < miny:
                miny = fminy

        if maxy is None:
            maxy = 1.0
        if miny is None:
            miny = -1.0
        if maxy > -miny:
            miny = -maxy
        if miny < -maxy:
            maxy = -miny

        self.last_maxy = maxy

        if maxy == miny:
            yscale = 0
        else:
            yscale = float(self.size.height - self.border_bottom - border_top) / (maxy - miny)

        # draw zero line
        g.color = Color(0.8, 0.8, 0.8)
        y0 = int(self.size.height - (0 - miny) * yscale - self.border_bottom)
        g.drawLine(self.border_left, y0, self.width - self.border_right, y0)

        # draw ticks
        tick_count = 10
        if ('%f' % maxy)[0] == '2':
            tick_count = 8

        tick_span = maxy * 2.0 / tick_count
        tick_size = 3
        for i in range(1, tick_count):
            yt = int(self.size.height - (i * tick_span) * yscale - self.border_bottom)
            g.drawLine(self.border_left, yt, self.border_left + tick_size, yt)
            g.drawLine(self.width - self.border_right, yt, self.width - self.border_right - tick_size, yt)

        g.color = Color.black
        if (self.size.width - self.border_left - self.border_right) > 0:
            skip = (len(fdata) / (self.size.width - self.border_left - self.border_right)) - 1
            if skip < 0:
                skip = 0

            offset = start % (skip + 1)
            for i in range(len(fdata) - 1 - skip):
                if skip == 0 or (i + offset) % (skip + 1) == 0:
                    if fdata[i] is not None and fdata[i + 1 + skip] is not None:
                        y1 = self.size.height - (fdata[i] - miny) * yscale - self.border_bottom
                        y2 = self.size.height - (fdata[i + 1 + skip] - miny) * yscale - self.border_bottom
                        g.drawLine(int(i * dx + self.border_left), int(y1), int((i + 1 + skip) * dx + self.border_left), int(y2))

        if maxy is not None:
            g.drawString('%6g' % maxy, 0, 20 + border_top)
        if miny is not None:
            g.drawString('%6g' % miny, 0, self.size.height - self.border_bottom - 5)

        dy = float(self.size.height - self.border_bottom - border_top) / 2.0  # div by 2 if you put in on top, out on bottom

        # Mark in and out spike
        txt = 'in'
        bounds = g.font.getStringBounds(txt, g.fontRenderContext)
        g.drawString(txt, 5, 10 + border_top + dy * .5)

        txt = 'out'
        bounds = g.font.getStringBounds(txt, g.fontRenderContext)
        g.drawString(txt, 5, 10 + border_top + dy * 1.5)

        g.color = Color.blue
        for i, d in enumerate(indata):
            if d is None:
                continue
            spike = d[self.in_neuron]
            if spike:
                x = int(i * dx + self.border_left)
                y = int(border_top)

                w = int(dx) - 1
                h = int(dy) - 1

                if w < 1:
                    w = 1
                if h < 1:
                    h = 1

                if w <= 1 and h <= 1:
                    g.drawLine(x, y, x, y)
                else:
                    g.fillRect(x, y, w, h)

        g.color = Color.red
        for i, d in enumerate(outdata):
            if d is None:
                continue
            spike = d[self.out_neuron]
            if spike:
                x = int(i * dx + self.border_left)
                y = int(border_top + dy)

                w = int(dx) - 1
                h = int(dy) - 1

                if w < 1:
                    w = 1
                if h < 1:
                    h = 1

                if w <= 1 and h <= 1:
                    g.drawLine(x, y, x, y)
                else:
                    g.fillRect(x, y, w, h)
