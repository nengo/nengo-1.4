import core
import neuronmap

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


class Graph(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), filter=True, ylimits=(-1.0, 1.0), split=False, neuronmapped=False, label=None, data=None, x_labels=None, show_negative=True, fixed_y=None, use_colors_when_split=False):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.indices = None
        self.sel_all = False
        if func is not None:
            self.data = self.view.watcher.watch(name, func, args=args)
        else:
            if callable(data):
                self.rawdata = data()
            else:
                self.rawdata = data
            self.data = None
        self.border_top = 10
        self.border_left = 30
        self.border_right = 30
        self.border_bottom = 20
        self.default_selected = 5     # The default number of selected display dimensions
        self.filter = filter
        self.label_height = 10
        self.setSize(300, 200)
        self.ylimits = ylimits
        self.split = split
        self.autozoom = False
        self.last_maxy = None
        self.neuronmapped = neuronmapped
        self.x_labels = x_labels
        self.show_negative = show_negative
        self.mouse_location = None
        self.fixed_y = fixed_y
        self.use_colors_when_split = use_colors_when_split

        self.map = None
        self.popup_zoom = JCheckBoxMenuItem('auto-zoom', self.autozoom, stateChanged=self.toggle_autozoom)
        self.popup.add(self.popup_zoom)
        self.popup.add(JMenuItem('set y range', actionPerformed=self.set_fixed_y))

        self.added_popup_separator = False

        self.popup_dim_menus = []

    def initialize_map(self):
        data = self.data.get_first()
        data = self.post_process([data], 0, None)[0]
        rows = int(sqrt(len(data)))
        cols = len(data) / rows
        if rows * cols < len(data):
            cols += 1

        self.map = self.view.mapcache.get(self.view.watcher.objects[self.name], rows, cols)

    def save(self):
        save_info = core.DataViewComponent.save(self)

        sel_dim = []
        for n in range(len(self.indices)):          # Get the checkbox states
            if(self.indices[n]):
                sel_dim += [n]

        save_info['sel_dim'] = sel_dim              # Save the checkbox states
        save_info['sel_all'] = self.sel_all         # Save select all status
        save_info['autozoom'] = self.autozoom
        save_info['last_maxy'] = self.last_maxy
        save_info['fixed_y'] = self.fixed_y

        return save_info

    def restore(self, d):
        core.DataViewComponent.restore(self, d)

        self.sel_all = d.get('sel_all', False)

        if self.data is not None:
            dd = self.data.get_first()
            post_result = self.post_process([dd], 0, None)
            if len(post_result) > 0:
                dd = post_result[0]
                data_dim = len(dd)       # Get dimensionality of data
            else:
                data_dim = 0
        else:
            data_dim = len(self.rawdata[0])

        if data_dim > 0:
            if self.sel_all:
                # If select all is selected, select all indicies
                self.indices = [True] * data_dim
            else:
                self.indices = [False] * data_dim

                sel_dim = d.get('sel_dim', range(min(data_dim, self.default_selected)))
                
                for dim in sel_dim:                         # Iterate and restore the saved state
                    if(dim < data_dim):
                        self.indices[dim] = True
            
                # Check if all of the indices are selected
                # Just in case someone altered the layout file and didn't set things right
                self.sel_all = all(self.indices)

            self.fix_popup()                            # Update the pop-up box

        self.autozoom = d.get('autozoom', True)
        self.popup_zoom.state = self.autozoom
        self.last_maxy = d.get('last_maxy', None)
        self.fixed_y = d.get('fixed_y', None)

    def set_fixed_y(self, event):
        example = '%s,%s' % (-self.last_maxy, self.last_maxy)
        text = JOptionPane.showInputDialog(self.view.frame, 'Enter y-axis range. \nExample: %s' % example, "Set Y-Axis Range", JOptionPane.PLAIN_MESSAGE, None, None, None)
        if text == '':
            self.fixed_y = None
        else:
            try:
                v = eval(text)
                if isinstance(v, (int, float)):
                    v = [v, v]
                if len(v) > 2:
                    v = v[:2]
                self.fixed_y = v
            except:
                self.fixed_y = None

    def toggle_autozoom(self, event):
        self.autozoom = event.source.state
        self.repaint()

    def label_for_index(self, index):
        return 'v[%d]' % index

    def refix_popup(self):
        for p in self.selection_menu_items:
            self.popup.remove(p)
        for p in self.popup_dim_menus:
            self.popup.remove(p)
        self.popup_dim_menus = []
        self.fix_popup()

    def fix_popup(self):

        if not self.added_popup_separator:
            self.popup.add(JPopupMenu.Separator())
            self.sel_all_menu_item = self.popup.add(JCheckBoxMenuItem('select all', self.sel_all, actionPerformed=self.select_all))
            self.popup.add(JMenuItem('select none', actionPerformed=self.select_none))
            self.added_popup_separator = True

        # Calculate number of submenu layers needed
        max_ind = len(self.indices)
        num_sub = max(1, int(ceil(log(max_ind) / log(self.max_show_dim))))
        max_sub = [self.max_show_dim ** (num_sub - i) for i in range(num_sub)]
        sub_menus = [self.popup] * num_sub

        self.selection_menu_items = []

        for i, draw in enumerate(self.indices):
            if(i % self.max_show_dim == 0):
                for n in range(num_sub - 1):
                    if(i % max_sub[n + 1] == 0):
                        new_menu = JMenu("%s[%d:%d]" % ('v', i, min(max_ind, i + max_sub[n + 1]) - 1))
                        sub_menus[n].add(new_menu)
                        self.popup_dim_menus.append(new_menu)
                        sub_menus[n + 1] = new_menu
            menu_item = JCheckBoxMenuItem(self.label_for_index(i), draw, actionPerformed=lambda x, index=i, self=self: self.select_index(index, x))
            self.selection_menu_items.append(menu_item)
            sub_menus[num_sub - 1].add(menu_item)

    def select_all(self, event):
        for index,item in enumerate(self.selection_menu_items):
            item.setState(True)
            self.indices[index] = True
        self.sel_all = True
        self.sel_all_menu_item.setState(self.sel_all)

    def select_index(self, index, x):
        self.indices[index] = x.source.state
        self.sel_all = all(self.indices)
        self.sel_all_menu_item.setState(self.sel_all)

    def select_none(self, event):
        for index,item in enumerate(self.selection_menu_items):
            item.setState(False)
            self.indices[index] = False
        self.sel_all = False
        self.sel_all_menu_item.setState(self.sel_all)

    def post_process(self, data, start, dt_tau):
        return data

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        if self.neuronmapped and self.map is None:
            self.initialize_map()

        border_top = self.border_top + self.label_offset

        g.color = Color(0.8, 0.8, 0.8)
        g.drawRect(self.border_left, border_top, self.width - self.border_left - self.border_right, self.size.height - border_top - self.border_bottom)

        g.color = Color(0.8, 0.8, 0.8)
        g.drawLine(self.border_left, border_top, self.border_left, self.size.height - self.border_bottom)

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter

        pts = int(self.view.time_shown / self.view.dt)

        start = self.view.current_tick - pts + 1
        if start < 0:
            start = 0
        if start <= self.view.timelog.tick_count - self.view.timelog.tick_limit:
            start = self.view.timelog.tick_count - self.view.timelog.tick_limit + 1

        if self.data is not None:
            data = self.data.get(start=start, count=pts, dt_tau=dt_tau)
            data = self.post_process(data, start, dt_tau)
            now = self.view.current_tick - start
            for i in range(now + 1, len(data)):
                data[i] = None
        else:
            data = self.rawdata
            pts = len(self.rawdata)

        maxy = None
        miny = None
        dx = float(self.size.width - self.border_left - self.border_right - 1) / (pts - 1)

        if self.x_labels is None:
            x_labels = dict()
            x_labels[0] = '%4g' % ((start) * self.view.dt)
            x_labels[pts] = '%4g' % ((start + pts) * self.view.dt)
        else:
            x_labels = self.x_labels

        g.color = Color.black
        for i, txt in x_labels.items():
            bounds = g.font.getStringBounds(txt, g.fontRenderContext)
            g.drawString(txt, self.border_left - bounds.width / 2 + int(i * dx), self.size.height - self.border_bottom + bounds.height)

        if self.indices is None:
            for x in data:
                if x is not None:
                    self.indices = [False] * len(x)
                    for i in range(self.default_selected):
                        if i < len(x):
                            self.indices[i] = True
                    self.fix_popup()
                    break
            else:
                return

        self.sel_all = all(self.indices)
        self.sel_all_menu_item.setState(self.sel_all)

        filtered = []
        for i, draw in enumerate(self.indices):
            if draw:
                if self.neuronmapped:
                    fdata = [safe_get_index(x, self.map.map[i]) for x in data]
                else:
                    fdata = [safe_get_index(x, i) for x in data]
                trimmed = [x for x in fdata if x is not None]
                if len(trimmed) == 0:
                    continue
                fmaxy = max(trimmed)
                fminy = min(trimmed)
                fmaxy = round(fmaxy)[1]
                fminy = round(fminy)[0]
                if maxy is None or fmaxy > maxy:
                    maxy = fmaxy
                if miny is None or fminy < miny:
                    miny = fminy
                filtered.append(fdata)

        if maxy is None:
            maxy = 1.0
        if miny is None:
            miny = -1.0
        if maxy < self.ylimits[1]:
            maxy = float(self.ylimits[1])
        if miny > self.ylimits[0]:
            miny = float(self.ylimits[0])
        if maxy > -miny:
            miny = -maxy
        if miny < -maxy:
            maxy = -miny

        if not self.autozoom and self.last_maxy is not None and maxy < self.last_maxy:
            maxy = self.last_maxy
            miny = -maxy

        self.last_maxy = maxy

        if not self.show_negative:
            miny = 0

        if self.fixed_y is not None:
            miny, maxy = self.fixed_y

        if maxy == miny:
            yscale = 0
        else:
            yscale = float(self.size.height - self.border_bottom - border_top) / (maxy - miny)
        if self.split and len(filtered) > 0:
            yscale = yscale / len(filtered)
            split_step = float(self.size.height - self.border_bottom - border_top) / len(filtered)

        if(not self.neuronmapped):
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

        colors = [Color.black, Color.blue, Color.red, Color.green, Color.magenta, Color.cyan, Color.yellow]
        g.color = Color.blue

        pdftemplate = getattr(self.view.area, 'pdftemplate', None)
        if pdftemplate is not None:
            pdf, scale = pdftemplate
            pdf.setLineWidth(0.5)
            pdf_line_started = False

        for j, fdata in enumerate(filtered):
            if (self.size.width - self.border_left - self.border_right) <= 0:
                break
            skip = (len(fdata) / (self.size.width - self.border_left - self.border_right)) - 1
            if self.filter and self.view.tau_filter == 0:
                skip -= 1     # special case to make unfiltered recoded value graphs look as expected
            if skip < 0:
                skip = 0
            if pdftemplate is not None:   # draw every point for pdf rendering
                skip = 0

            offset = start % (skip + 1)
            if not self.split or self.use_colors_when_split:
                g.color = colors[j % len(colors)]
            for i in range(len(fdata) - 1 - skip):
                if skip == 0 or (i + offset) % (skip + 1) == 0:
                    if fdata[i] is not None and fdata[i + 1 + skip] is not None:
                        y1 = self.size.height - (fdata[i] - miny) * yscale - self.border_bottom
                        y2 = self.size.height - (fdata[i + 1 + skip] - miny) * yscale - self.border_bottom
                        if self.split:
                            y1 -= (len(filtered) - 1 - j) * split_step
                            y2 -= (len(filtered) - 1 - j) * split_step

                        if pdftemplate is None:
                            try:
                                g.drawLine(int(i * dx + self.border_left), int(y1), int((i + 1 + skip) * dx + self.border_left), int(y2))
                            except OverflowError:
                                pass
                        else:
                            x = (self.x + i * dx + self.border_left) * scale
                            y = 800 - (self.y + y1) * scale
                            if not pdf_line_started:
                                pdf.moveTo(x, y)
                                pdf_line_started = True
                            else:
                                pdf.lineTo(x, y)
            if pdftemplate is not None and pdf_line_started:
                pdf.setRGBColorStroke(g.color.red, g.color.green, g.color.blue)
                pdf.stroke()
                pdf_line_started = False
        if not self.split:
            g.color = Color.black
            if maxy is not None:
                g.drawString('%6g' % maxy, 0, 10 + border_top)
            if miny is not None:
                g.drawString('%6g' % miny, 0, self.size.height - self.border_bottom)

        if self.mouse_location is not None:
            x, y = self.mouse_location
            if x >= self.border_left and x <= self.width - self.border_right:
                g.color = Color(0.8, 0.8, 0.8)
                g.drawLine(x, border_top, x, self.height - self.border_bottom)

                pt = int((x - self.border_left) * pts / (self.size.width - self.border_right - self.border_left))

                g.color = Color.black
                txt = '%4g' % ((start + pt) * self.view.dt)
                bounds = g.font.getStringBounds(txt, g.fontRenderContext)
                g.drawString(txt, x - bounds.width / 2, self.size.height - self.border_bottom + bounds.height)

            if border_top < y < self.height - self.border_bottom:
                g.color = Color(0.8, 0.8, 0.8)
                g.drawLine(self.border_left, y, self.width - self.border_right, y)

                pt = int((y - self.border_left) * pts / (self.size.width - self.border_right - self.border_left))

                value = (self.size.height - y - self.border_bottom) / yscale + miny

                g.color = Color.black
                txt = '%4g' % value
                bounds = g.font.getStringBounds(txt, g.fontRenderContext)
                g.drawString(txt, self.size.width - self.border_right, y + bounds.height / 2)

    def mouseMoved(self, event):
        self.mouse_location = event.x, event.y
        core.DataViewComponent.mouseMoved(self, event)
        self.repaint()

    def mouseExited(self, event):
        self.mouse_location = None
        self.repaint()
        core.DataViewComponent.mouseExited(self, event)
