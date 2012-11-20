from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java

import core
from graph import round

from math import sqrt
from math import log
from math import ceil


class XYPlot(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), filter=None, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.indices = None
        self.data = self.view.watcher.watch(name, func, args=args)
        self.margin = 10

        self.autozoom = True
        self.last_maxy = None
        self.popup_zoom = JCheckBoxMenuItem('auto-zoom', self.autozoom, stateChanged=self.toggle_autozoom)
        self.popup.add(self.popup_zoom)

        self.autohide = True
        self.show_axes_labels = True
        self.popup_hide = JCheckBoxMenuItem('auto-hide axis labels', True, actionPerformed=self.toggle_autohide)
        self.popup.add(self.popup_hide)

        self.axes_label_init_clr = 0.3
        self.axes_label_clr_val = self.axes_label_init_clr
        self.axes_label_clr_step = (1 - self.axes_label_init_clr) / 10

        self.axes_label_hide_dly = 1000
        self.axes_label_hide_tmr = Timer(self.axes_label_hide_dly / 10, None, actionPerformed=self.fade_axes_labels)

        self.filter = filter
        self.setSize(200, 200)

    def save(self):
        save_info = core.DataViewComponent.save(self)
        save_info['sel_dim'] = self.indices
        save_info['autozoom'] = self.autozoom
        save_info['last_maxy'] = self.last_maxy
        save_info['autohide'] = self.autohide
        return save_info

    def restore(self, d):
        core.DataViewComponent.restore(self, d)
        self.indices = d.get('sel_dim', [0, 1])

        self.autozoom = d.get('autozoom', True)
        self.popup_zoom.state = self.autozoom
        self.last_maxy = d.get('last_maxy', None)

        self.autohide = d.get('autohide', True)
        self.popup_hide.state = self.autohide
        self.show_axes_labels = not self.autohide

        self.fix_popup()

    def toggle_autozoom(self, event):
        self.autozoom = event.source.state
        self.repaint()

    def toggle_autohide(self, event):
        self.autohide = event.source.state
        if(self.autohide):
            self.axes_label_hide_tmr.start()
        else:
            self.disp_axes_labels()

    def fix_popup(self):
        self.popup.add(JPopupMenu.Separator())

        # Add submenus for x and y axes
        x_menu = JMenu("X Axis")
        y_menu = JMenu("Y Axis")
        self.popup.add(x_menu)
        self.popup.add(y_menu)

        x_btngrp = ButtonGroup()
        y_btngrp = ButtonGroup()

        # Calculate number of submenu layers needed
        max_ind = len(self.data.get_first())
        num_sub = max(1, int(ceil(log(max_ind) / log(self.max_show_dim))))
        max_sub = [self.max_show_dim ** (num_sub - i) for i in range(num_sub)]

        x_subs = [x_menu] * num_sub
        y_subs = [y_menu] * num_sub

        for i in range(max_ind):
            if(i % self.max_show_dim == 0):
                for n in range(num_sub - 1):
                    if(i % max_sub[n + 1] == 0):
                        new_xmenu = JMenu("%s[%d:%d]" % ('v', i, min(max_ind, i + max_sub[n + 1]) - 1))
                        new_ymenu = JMenu("%s[%d:%d]" % ('v', i, min(max_ind, i + max_sub[n + 1]) - 1))
                        x_subs[n].add(new_xmenu)
                        x_subs[n + 1] = new_xmenu
                        y_subs[n].add(new_ymenu)
                        y_subs[n + 1] = new_ymenu
            x_radio = JRadioButtonMenuItem('%s[%d]' % ('v', i), i == self.indices[0], actionPerformed=lambda x, index=i, self=self: self.indices.__setitem__(0, index))
            y_radio = JRadioButtonMenuItem('%s[%d]' % ('v', i), i == self.indices[1], actionPerformed=lambda x, index=i, self=self: self.indices.__setitem__(1, index))

            x_btngrp.add(x_radio)
            y_btngrp.add(y_radio)

            x_subs[num_sub - 1].add(x_radio)
            y_subs[num_sub - 1].add(y_radio)

    def mouseEntered(self, event):
        if(self.autohide):
            self.disp_axes_labels()
        core.DataViewComponent.mouseEntered(self, event)

    def mouseExited(self, event):
        if(self.autohide):
            self.axes_label_hide_tmr.start()
        core.DataViewComponent.mouseExited(self, event)

    def disp_axes_labels(self):
        self.axes_label_clr_val = self.axes_label_init_clr
        self.show_axes_labels = True
        self.axes_label_hide_tmr.stop()
        self.repaint()

    def fade_axes_labels(self, event):
        if(self.show_axes_labels):
            if(self.axes_label_clr_val >= 1 - self.axes_label_clr_step):
                self.axes_label_hide_tmr.stop()
                self.show_axes_labels = False
            else:
                self.axes_label_clr_val += self.axes_label_clr_step
                self.axes_label_clr_val = min(1, self.axes_label_clr_val)
                self.axes_label_hide_tmr.start()
        self.repaint()

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        xc = self.width / 2
        yc = (self.height - self.label_offset) / 2
        x0 = self.margin / 2.0
        y0 = self.margin / 2.0
        g.color = Color(0.8, 0.8, 0.8)
        g.drawRect(int(x0) - 1, int(y0 + self.label_offset) - 1, int(self.size.width - self.margin) + 1, int(self.size.height - self.label_offset - self.margin) + 1)
        g.drawLine(xc, self.margin + self.label_offset, xc, self.height - self.margin)
        g.drawLine(self.margin, yc + self.label_offset, self.width - self.margin, yc + self.label_offset)

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter

        pts = int(self.view.time_shown / self.view.dt)
        try:
            data = self.data.get(start=self.view.current_tick - pts + 1, count=pts, dt_tau=dt_tau)
        except:
            return

        if data is None:
            return

        if(self.indices is None):
            self.indices = [0, 1]
            self.fix_popup()

        xs = [d[self.indices[0]] for d in data if d is not None]
        ys = [d[self.indices[1]] for d in data if d is not None]
        if len(xs) > 0:
            mx = max(max(xs), max(ys), -min(xs), -min(ys))
            mx = round(mx)[1]
            if mx < 1.0:
                mx = 1.0
        else:
            mx = 1.0

        if(not self.autozoom and self.last_maxy is not None and mx < self.last_maxy):
            mx = self.last_maxy

        self.last_maxy = mx

        g.color = Color.black

        txt = '%g' % mx
        bounds = g.font.getStringBounds(txt, g.fontRenderContext)
        #g.drawString(txt,xc+self.margin,bounds.height)
        g.drawString(txt, xc + x0, bounds.height + self.margin + self.label_offset)
        g.drawString(txt, self.width - self.margin - bounds.width, yc + bounds.height + self.label_offset)

        txt = '%g' % (-mx)
        #g.drawString(txt,xc+self.margin,self.height-self.margin)
        g.drawString(txt, xc + x0, self.height - self.margin)
        g.drawString(txt, self.margin, yc + bounds.height + self.label_offset)

        if(self.show_axes_labels):
            g.color = Color(self.axes_label_clr_val, self.axes_label_clr_val, self.axes_label_clr_val)
            txt = '%s[%d]' % ('v', self.indices[0])
            boundsl = g.font.getStringBounds(txt, g.fontRenderContext)
            g.drawString(txt, self.width - self.margin - boundsl.width, yc - y0 + self.label_offset)

            txt = '%s[%d]' % ('v', self.indices[1])
            boundsl = g.font.getStringBounds(txt, g.fontRenderContext)
            g.drawString(txt, xc - x0 - boundsl.width, bounds.height + self.margin + self.label_offset)

        g.color = Color.black

        pdftemplate = getattr(self.view.area, 'pdftemplate', None)
        if pdftemplate is not None:
            pdf, scale = pdftemplate
            pdf.setLineWidth(0.5)
            last_color = None

        sx = (self.width / 2 - self.margin) / mx
        sy = (yc - self.margin) / mx
        for i in range(pts - 1):
            if data[i] is not None and data[i + 1] is not None:
                x0 = data[i][self.indices[0]]
                y0 = data[i][self.indices[1]]
                x1 = data[i + 1][self.indices[0]]
                y1 = data[i + 1][self.indices[1]]

                c = 1.0 - i / float(pts - 1)
                g.color = Color(c, c, c)

                if pdftemplate is None:
                    g.drawLine(int(xc + x0 * sx), int(yc - y0 * sy + self.label_offset), int(xc + x1 * sx), int(yc - y1 * sy + self.label_offset))
                else:
                    c = int(c * 32) * 8
                    if c > 255:
                        c = 255
                    if c != last_color:
                        if last_color is not None:
                            pdf.setRGBColorStroke(last_color, last_color, last_color)
                            pdf.stroke()
                        pdf.moveTo(((xc + x0 * sx) + self.x) * scale, 800 - (self.y + yc - y0 * sy + self.label_offset) * scale)
                        last_color = c
                    pdf.lineTo(((xc + x1 * sx) + self.x) * scale, 800 - (self.y + yc - y1 * sy + self.label_offset) * scale)

        if pdftemplate is not None:
            pdf.setRGBColorStroke(last_color, last_color, last_color)
            pdf.stroke()
