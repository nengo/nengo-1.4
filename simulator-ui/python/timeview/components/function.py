import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from java.util import Hashtable

import random

from math import floor
from math import ceil

import java


class FunctionControl(core.DataViewComponent, ComponentListener):
    def __init__(self, view, name, func, label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.label_height = 18
        self.resize_border = 2

        self.popup_hide_limit = JCheckBoxMenuItem('auto-hide limits', True, actionPerformed=self.toggle_autohide)
        self.popup.add(self.popup_hide_limit)
        self.show_limits = False
        self.auto_hide_limits = True
        self.limits_font = Font("Dialog", Font.PLAIN, 10)
        self.limit_width = 0
        self.limit_hide_delay = 1000
        self.limit_color_def = 0.3
        self.limit_color_val = self.limit_color_def
        self.limit_color_step = (1 - self.limit_color_def) / 10
        self.limit_hide_timer = Timer(self.limit_hide_delay / 10, None, actionPerformed=self.hide_limits)
        self.limit_hide_timer.setRepeats(False)

        self.popup.add(JPopupMenu.Separator())
        self.popup.add(JMenuItem('zero', actionPerformed=self.zero))
        self.popup.add(JMenuItem('set value', actionPerformed=self.set_value))

        self.filename = None
        self.popup.add(JMenuItem('set from file...', actionPerformed=self.set_from_file))

        self.popup.add(JPopupMenu.Separator())
        self.popup.add(JMenuItem('increase range', actionPerformed=self.increase_range))
        self.popup.add(JMenuItem('decrease range', actionPerformed=self.decrease_range))
        self.scale_factor = 0.01
        self.range = 1.0

        self.data = self.view.watcher.watch(name, func)

        values = self.data.get_first()
        self.sliders = []
        self.labels = []
        for i, v in enumerate(values):
            vv = int(v * 100 / self.range)
            if vv > 100:
                vv = 100
            if vv < -100:
                vv = -100
            slider = JSlider(JSlider.VERTICAL, -100, 100, vv, stateChanged=lambda event, index=i: self.slider_moved(index))
            slider.background = Color.white
            self.add(slider)
            self.sliders.append(slider)
            label = JLabel('0.00')
            self.add(label)
            self.labels.append(label)
            slider.addMouseListener(self)

        self.setSize(len(values) * 40 + 40, 200)
        self.addComponentListener(self)
        self.componentResized(None)

    def increase_range(self, event):
        self.range *= 2.0
        self.check_label_size()
        self.repaint()

    def decrease_range(self, event):
        self.range *= 0.5
        self.check_label_size()
        self.repaint()

    def check_label_size(self):
        if(self.show_limits):
            limit_label = JLabel(("-%1.2f" % (self.range)))
            limit_width = limit_label.getPreferredSize().width - self.sliders[0].width / 2

            if(limit_width != self.limit_width):
                self.setSize(self.size.width + limit_width - self.limit_width, self.size.height)
                self.setLocation(self.x - limit_width + self.limit_width, self.y)
                self.limit_width = limit_width

    def zero(self, event):
        for i in range(len(self.sliders)):
            self.set_slider_value(i, 0)

    def set_value(self, event):
        try:
            example = ','.join(['%1.1f' % random.uniform(-5, 5) for i in range(len(self.sliders))])
            text = JOptionPane.showInputDialog(self.view.frame, 'Enter input value. \nExample: %s' % example, "Set value", JOptionPane.PLAIN_MESSAGE, None, None, None)
            v = eval(text)
            if isinstance(v, (int, float)):
                v = [v]
            if len(v) > len(self.sliders):
                v = v[:len(self.sliders)]
            for i, vv in enumerate(v):
                self.set_slider_value(i, vv)
        except:
            self.release_value(event)

    def set_from_file(self, event):
        fileChooser = JFileChooser()
        if self.filename is not None:
            fileChooser.setSelectedFile(java.io.File(self.filename))

        if fileChooser.showOpenDialog(self) == JFileChooser.APPROVE_OPTION:
            self.filename = fileChooser.selectedFile.absolutePath

            #TODO: this doesn't for for nested FunctionInputs
            input = self.view.network.getNode(self.name)

            from nef.functions import Interpolator
            interp = Interpolator(self.filename)
            interp.load_into_function(input)

    def set_slider_value(self, index, value):
        sv = value / (self.scale_factor * self.range)
        self.sliders[index].value = int(sv)
        self.labels[index].text = '%1.2f' % value
        self.check_label_size()
        self.repaint()
        if self.view.paused:  # change immediately, bypassing filter
            self.data.data[-1][index] = value
            self.view.forced_origins_prev[(self.name, 'origin', index)] = value
        self.view.forced_origins[(self.name, 'origin', index)] = value

    def slider_moved(self, index):
        if self.sliders[index].valueIsAdjusting:   # if I moved it
            v = self.sliders[index].value * self.scale_factor * self.range
            self.labels[index].text = '%1.2f' % v
            if self.view.paused:  # change immediately, bypassing filter
                self.data.data[-1][index] = v
                self.view.forced_origins_prev[(self.name, 'origin', index)] = v

            self.view.forced_origins[(self.name, 'origin', index)] = v

    def paintComponent(self, g):
        temp = self.show_label
        self.show_label = False
        core.DataViewComponent.paintComponent(self, g)
        self.show_label = temp

        if self.show_label:
            g.color = Color(0.3, 0.3, 0.3)
            bounds = g.font.getStringBounds(self.label, g.fontRenderContext)
            g.drawString(self.label, (self.size.width - self.limit_width) / 2 - bounds.width / 2 + self.limit_width, bounds.height)

        self.active = self.view.current_tick >= self.view.timelog.tick_count - 1

        data = self.data.get(start=self.view.current_tick, count=1)[0]
        if data is None:
            data = self.data.get_first()

        if(self.show_limits):
            g.color = Color(self.limit_color_val, self.limit_color_val, self.limit_color_val)
            txt_min = "%1.2f" % (-self.range)
            txt_max = "%1.2f" % (self.range)

            temp_font = g.font
            g.font = self.limits_font

            bounds_min = g.font.getStringBounds(txt_min, g.fontRenderContext)
            bounds_max = g.font.getStringBounds(txt_max, g.fontRenderContext)
            g.drawString(txt_max, 10 + bounds_min.width - bounds_max.width, self.resize_border + self.label_offset + bounds_max.height)
            g.drawString(txt_min, 10, self.height - self.resize_border - self.labels[0].getPreferredSize().height - bounds_min.height)

            g.font = temp_font

        for i, v in enumerate(data):
            while v > self.range * 1.1:
                self.range *= 2
            while v < -self.range * 1.1:
                self.range *= 2

        for i, v in enumerate(data):
            sv = int(v * 100.0 / self.range)
            if sv > 100:
                sv = 100
            if sv < -100:
                sv = -100
            if not self.sliders[i].valueIsAdjusting:
                self.sliders[i].value = sv
            self.labels[i].text = '%1.2f' % v
            self.sliders[i].enabled = self.active

        self.componentResized(None)

    def componentResized(self, e):
        w = self.width - self.resize_border * 2 - self.limit_width
        dw = w / len(self.sliders)
        x = (dw - self.sliders[0].minimumSize.width) / 2
        for i, slider in enumerate(self.sliders):
            slider.setLocation(self.limit_width + self.resize_border + x + i * dw, self.resize_border + self.label_offset)
            slider.setSize(slider.minimumSize.width, self.height - self.resize_border * 2 - 20 - self.label_offset)
            self.labels[i].setLocation(slider.x + slider.width / 2 - self.labels[i].width / 2, slider.y + slider.height)

    def componentHidden(self, e):
        pass

    def componentMoved(self, e):
        pass

    def componentShown(self, e):
        pass

    def save(self):
        info = core.DataViewComponent.save(self)

        if(self.auto_hide_limits):
            self.hide_limits(None)

        info['x'] = self.x            # Overwrite x and width to account for removed limits
        info['width'] = self.width
        info['range'] = self.range
        info['limits'] = self.auto_hide_limits
        info['limits_w'] = self.limit_width
        return info

    def restore(self, d):
        core.DataViewComponent.restore(self, d)
        self.range = d.get('range', 1.0)
        self.auto_hide_limits = d.get('limits', True)
        self.limit_width = d.get('limits_w', 0)
        self.popup_hide_limit.state = self.auto_hide_limits
        self.show_limits = not self.auto_hide_limits

    def mouseEntered(self, event):
        if(self.auto_hide_limits):
            self.disp_limits()
        core.DataViewComponent.mouseEntered(self, event)

    def mouseExited(self, event):
        if(self.auto_hide_limits):
            self.limit_hide_timer.start()
        core.DataViewComponent.mouseExited(self, event)

    def toggle_autohide(self, event):
        self.auto_hide_limits = event.source.state
        if(self.auto_hide_limits):
            self.limit_hide_timer.start()
        else:
            self.disp_limits()

    def disp_limits(self):
        if(not self.show_limits):
            limit_label = JLabel(("-%1.2f" % (self.range)))
            self.limit_width = limit_label.getPreferredSize().width - self.sliders[0].width / 2
            self.setSize(self.size.width + self.limit_width, self.size.height)
            self.setLocation(self.x - self.limit_width, self.y)

        self.limit_hide_timer.stop()
        self.limit_color_val = self.limit_color_def
        self.show_limits = True
        self.repaint()

    def hide_limits(self, event):
        if(self.show_limits):
            if(self.limit_color_val >= 1 - self.limit_color_step):
                self.limit_hide_timer.stop()
                self.setSize(self.size.width - self.limit_width, self.size.height)
                self.setLocation(self.x + self.limit_width, self.y)
                self.limit_width = 0
                self.show_limits = False
            else:
                self.limit_color_val += self.limit_color_step
                self.limit_color_val = min(self.limit_color_val, 1.0)
                self.limit_hide_timer.start()
        self.repaint()
