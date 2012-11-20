from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import core
import neuronmap
import clicker

from math import sqrt


class Grid(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), sfunc=None, sargs=(), min=0, max=1, rows=None, cols=None, filter=False, label=None, improvable=True, audio=False):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.sfunc = sfunc
        self.data = self.view.watcher.watch(name, func, args=args)
        if sfunc is not None:
            self.sdata = self.view.watcher.watch(name, sfunc, args=sargs)
        self.rows = rows
        self.cols = cols
        self.margin = 10
        self.min = min
        self.max = max
        self.map = None
        self.requested_improvements = 0
        self.auto_improve = False
        self.improvable = improvable

        if not clicker.ClickerEnabled.enabled:
            audio = False
        self.audio = audio
        if audio:
            if sfunc is not None:
                cdata = self.sdata
            else:
                cdata = self.data
            self.clicker = clicker.Clicker(self, cdata)
            self.audio_follow_mouse = True

        if self.improvable:
            self.popup.add(JPopupMenu.Separator())
            self.popup.add(JMenuItem('improve layout', actionPerformed=self.improve_layout))
            self.popup_auto = JCheckBoxMenuItem('auto-improve', self.auto_improve, stateChanged=self.toggle_auto_improve)
            self.popup.add(self.popup_auto)

        self.popup.add(JMenuItem('set # of rows', actionPerformed=self.setRows))
        self.popup.add(JMenuItem('set # of cols', actionPerformed=self.setCols))

        self.filter = filter
        self.setSize(200, 200)

    def toggle_auto_improve(self, event):
        if self.improvable:
            self.auto_improve = event.source.state
            if self.auto_improve and self.requested_improvements < 20:
                self.requested_improvements = 20

    def setRows(self, event):
        try:
            text = JOptionPane.showInputDialog(self.view.frame, 'Specify the number of rows in the grid:', "Set row count", JOptionPane.PLAIN_MESSAGE, None, None, None)
            self.rows = int(text)
            if self.rows < 1:
                self.rows = 1
        except:
            pass

    def setCols(self, event):
        try:
            text = JOptionPane.showInputDialog(self.view.frame, 'Specify the number of columns in the grid:', "Set column count", JOptionPane.PLAIN_MESSAGE, None, None, None)
            self.cols = int(text)
            if self.cols < 1:
                self.cols = 1
        except:
            self.cols = None

    def save(self):
        d = core.DataViewComponent.save(self)
        if self.improvable:
            d['auto_improve'] = self.auto_improve
        d['rows'] = self.rows
        d['cols'] = self.cols
        if not callable(self.max):
            d['max'] = self.max
        return d

    def restore(self, d):
        core.DataViewComponent.restore(self, d)
        self.rows = d.get('rows', self.rows)
        self.cols = d.get('cols', self.cols)
        self.max = d.get('max', self.max)
        if not callable(self.max) and self.min != 0:
            self.min = -self.max
        if self.improvable:
            self.auto_improve = d.get('auto_improve', False)
            self.popup_auto.state = self.auto_improve
            if self.auto_improve:
                self.requested_improvements = 20

    def improve_layout(self, event):
        if self.improvable:
            self.requested_improvements = self.map.improvements + 20

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)
        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter
        try:
            data = self.data.get(start=self.view.current_tick, count=1, dt_tau=dt_tau)[0]
        except:
            return
        if self.sfunc is not None:
            sdata = self.sdata.get(start=self.view.current_tick, count=1)[0]
        else:
            sdata = None

        if data is None:
            return

        if self.rows is None:
            rows = int(sqrt(len(data)))
        else:
            rows = self.rows
        if self.cols is not None:
            cols = self.cols
        else:
            cols = len(data) / rows
            if rows * cols < len(data):
                cols += 1

        if self.map is None and self.improvable:
            self.map = self.view.mapcache.get(self.view.watcher.objects[self.name], rows, cols)

        max = self.max
        if callable(max):
            max = max(self)
        min = self.min
        if callable(min):
            min = min(self)

        dx = float(self.size.width - self.margin) / cols
        dy = float(self.size.height - self.label_offset - self.margin) / rows
        for y in range(rows):
            for x in range(cols):
                if x + y * cols < len(data):

                    if self.improvable:
                        index = self.map.map[x + y * cols]
                    else:
                        index = x + y * cols
                    if sdata is not None and self.view.current_tick > 0 and sdata[index]:
                        g.color = Color.yellow
                    else:
                        if min >= 0.0:
                            c = (float(data[index]) - min) / (max - min)
                            if c < 0:
                                c = 0.0
                            if c > 1:
                                c = 1.0
                            g.color = Color(c, c, c)
                        else:
                            c = (float(data[index]) - min) / (max - min)
                            c = (c - 0.5) * 2
                            if c < -1:
                                c = -1.0
                            if c > 1:
                                c = 1.0
                            if c < 0.0:
                                g.color = Color(1.0 + c, 1.0 + c, 1.0)
                            else:
                                g.color = Color(1.0, 1.0 - c, 1.0 - c)

                    g.fillRect(int(x0 + dx * x), int(y0 + dy * y), int(dx + 1), int(dy + 1))
                    if self.audio and index == self.clicker.selected:
                        g.color = Color.blue
                        g.drawRect(int(x0 + dx * x) + 1, int(y0 + dy * y) + 1, int(dx) - 2, int(dy) - 2)

        if self.audio and self.audio_follow_mouse:
            x = int((self.mouse_x - x0) / dx)
            y = int((self.mouse_y - y0) / dy)
            index = x + y * cols
            if x > cols:
                index = -1
            if 0 <= index < len(data):
                if self.improvable:
                    index = self.map.map[index]
                self.clicker.select(index)
            else:
                self.clicker.select(None)

        g.color = Color.black
        g.drawRect(int(x0) - 1, int(y0) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.label_offset - self.margin) + 1)

        if self.improvable and self.requested_improvements > self.map.improvements:
            self.map.improve()
            self.parent.repaint()

    mouse_x = 0
    mouse_y = 0

    def mouseMoved(self, event):
        core.DataViewComponent.mouseMoved(self, event)
        self.mouse_x = event.x
        self.mouse_y = event.y

    def mousePressed(self, event):
        core.DataViewComponent.mousePressed(self, event)
        if self.audio:
            self.audio_follow_mouse = not self.audio_follow_mouse

    def mouseExited(self, event):
        core.DataViewComponent.mouseExited(self, event)
        self.mouse_x = -1
        self.mouse_y = -1
