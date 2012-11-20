import core
from java.awt import Color

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *


class TextList(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), label=None, names=[], show_values=False, ignore_filter=False):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.data = self.view.watcher.watch(name, func, args=args)
        self.names = names
        self.show_values = show_values
        self.ignore_filter = ignore_filter
        self.margin = 10

        self.setSize(150, 300)

        self.popup.add(JPopupMenu.Separator())
        self.popup_vals = JCheckBoxMenuItem('show values', self.show_values, actionPerformed=self.toggle_values)
        self.popup.add(self.popup_vals)
        self.popup_filter = JCheckBoxMenuItem('ignore filter', self.ignore_filter, actionPerformed=self.toggle_filter)
        self.popup.add(self.popup_filter)

    def toggle_values(self, event):
        self.show_values = event.source.state
        self.repaint()

    def toggle_filter(self, event):
        self.ignore_filter = event.source.state
        self.repaint()

    def save(self):
        save_info = core.DataViewComponent.save(self)
        save_info['show_values'] = self.show_values
        save_info['ignore_filter'] = self.ignore_filter
        return save_info

    def restore(self, d):
        core.DataViewComponent.restore(self, d)
        self.show_values = d.get('show_values', False)
        self.ignore_filter = d.get('ignore_filter', False)

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        dt_tau = None
        if not self.ignore_filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter
        try:
            data = self.data.get(start=self.view.current_tick, count=1, dt_tau=dt_tau)[0]
        except:
            return

        x0 = self.margin / 2.0
        y0 = self.margin / 2.0 + self.label_offset
        g.color = Color.black
        g.drawRect(int(x0) - 1, int(y0) - 1,
                   int(self.size.width - self.margin),
                   int(self.size.height - self.label_offset - self.margin) + 1)

        dy = float(self.size.height - self.label_offset - self.margin) / len(self.names)

        for i, n in enumerate(self.names):
            c = 1 - data[i]
            if c < 0:
                c = 0.0
            if c > 1:
                c = 1.0
            g.color = Color(c, c, c)
            g.fillRect(int(x0), int(y0 + dy * i), int(self.size.width - self.margin - 1), int(dy + 1))

            if c < 0.5:
                g.color = Color.white
            else:
                g.color = Color.black

            bounds = g.font.getStringBounds(n, g.fontRenderContext)

            if self.show_values:
                textx = self.size.width / 4 - bounds.width / 2
            else:
                textx = self.size.width / 2 - bounds.width / 2
            g.drawString(n, textx, int(y0 + dy * i + dy / 2 + bounds.height / 2))

            if self.show_values:
                v = '%4.2f' % data[i]
                bounds = g.font.getStringBounds(v, g.fontRenderContext)
                g.drawString(v, self.size.width * 3 / 4 - bounds.width / 2, int(y0 + dy * i + dy / 2 + bounds.height / 2))
