import core
import graph
import hrr

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import math
import numeric


class HRRGraph(graph.Graph):
    def __init__(self, view, name, func, args=(), filter=True, ylimits=(-1.0, 1.0), label=None, nodeid=None):
        graph.Graph.__init__(self, view, name, func, args=args, filter=filter, ylimits=ylimits, split=False, neuronmapped=False, label=label)

        self.border_top = 30

        self.vocab = hrr.Vocabulary.registered.get(nodeid, None)
        if self.vocab is None:
            dim = len(self.data.get_first())
            if dim in hrr.Vocabulary.defaults.keys():
                self.vocab = hrr.Vocabulary.defaults[dim]
            else:
                self.vocab = hrr.Vocabulary(dim)

        self.hidden_pairs = None
        self.normalize = True
        self.popup_normalize = JCheckBoxMenuItem('normalize', self.normalize, stateChanged=self.toggle_normalize)
        self.popup.add(self.popup_normalize)
        self.smooth_normalize = False
        self.popup_smooth = JCheckBoxMenuItem('smooth normalize', self.smooth_normalize, stateChanged=self.toggle_smooth_normalize)
        self.smooth_normalize_threshold = 0.4
        self.popup.add(self.popup_smooth)

        self.show_pairs = False
        self.popup_pairs = JCheckBoxMenuItem('show pairs', self.show_pairs, stateChanged=self.toggle_show_pairs)
        self.popup.add(self.popup_pairs)

        self.show_graph = True
        self.popup_graph = JCheckBoxMenuItem('show graph', self.show_graph, stateChanged=self.toggle_show_graph)
        self.popup.add(self.popup_graph)
        self.font_height_50 = None

        self.popup_set = JMenuItem('set value', actionPerformed=self.set_value)
        self.popup.add(self.popup_set)
        self.popup_release = JMenuItem('release value', actionPerformed=self.release_value)
        self.popup_release.setEnabled(False)
        self.popup.add(self.popup_release)

        self.fixed_value = None
        self.cache = {}
        self.cache_dt_tau = None
        self.cache_tick_count = 0

    def toggle_normalize(self, event):
        if event.source.state == self.normalize:
            return
        self.normalize = event.source.state
        if self.normalize and self.smooth_normalize:
            self.popup_smooth.state = False
        self.clear_cache()
        self.repaint()

    def toggle_smooth_normalize(self, event):
        if event.source.state == self.smooth_normalize:
            return
        self.smooth_normalize = event.source.state
        if self.smooth_normalize and self.normalize:
            self.popup_normalize.state = False
        self.clear_cache()
        self.repaint()

    def toggle_show_pairs(self, event):
        if event.source.state == self.show_pairs:
            return
        self.show_pairs = event.source.state

        self.redo_indices()

    def toggle_show_graph(self, event):
        self.show_graph = event.source.state

    def redo_indices(self):
        if self.indices is None:
            return
        self.clear_cache()

        if self.show_pairs:
            if self.vocab.vector_pairs is None:
                self.vocab.generate_pairs()

        pairs = self.indices[len(self.vocab.keys):]
        if self.hidden_pairs is not None:
            pairs.extend(self.hidden_pairs)
        pairs = pairs + [False] * (len(self.vocab.key_pairs) - len(pairs))
        self.indices = self.indices[:len(self.vocab.keys)]
        if len(self.indices) < len(self.vocab.keys):
            self.indices.extend([True] * (len(self.vocab.keys) - len(self.indices)))

        if self.show_pairs:
            self.indices = self.indices + pairs
            self.hidden_pairs = None
        else:
            self.hidden_pairs = pairs
        self.refix_popup()
        self.repaint()

    def save(self):
        info = graph.Graph.save(self)
        info['show_pairs'] = self.show_pairs
        info['normalize'] = self.normalize
        info['smooth_normalize'] = self.smooth_normalize
        info['show_graph'] = self.show_graph
        return info

    def restore(self, d):
        self.show_pairs = d.get('show_pairs', self.vocab.include_pairs)
        self.popup_pairs.state = self.show_pairs
        self.normalize = d.get('normalize', False)
        self.popup_normalize.state = self.normalize
        self.smooth_normalize = d.get('smooth_normalize', True)
        self.popup_smooth.state = self.smooth_normalize
        self.show_graph = d.get('show_graph', True)
        self.popup_graph.state = self.show_graph

        graph.Graph.restore(self, d)

    def label_for_index(self, index):
        if index < len(self.vocab.keys):
            text = self.vocab.keys[index]
        else:
            text = self.vocab.key_pairs[index - len(self.vocab.keys)]
        return text

    def set_value(self, event):
        key_count = len(self.vocab.keys)
        try:
            text = JOptionPane.showInputDialog(self.view.frame, 'Enter the symbolic value to represent.\nFor example: "a*b+0.3*(c*d+e)*~f"', "Set semantic pointer", JOptionPane.PLAIN_MESSAGE, None, None, None)
            v = self.vocab.parse(text)
        except:
            self.release_value(event)
            return
        if isinstance(v, (int, float)):
            v = [v] * self.vocab.dimensions
        else:
            v = v.v

        self.fixed_value = v

        self.popup_release.setEnabled(True)
        self.view.forced_origins[(self.name, 'X', None)] = self.fixed_value

        if key_count != len(self.vocab.keys):
            self.view.refresh_hrrs()

    def release_value(self, event):
        self.fixed_value = None
        self.popup_release.setEnabled(False)

        key = (self.name, 'X', None)
        if key in self.view.forced_origins:
            del self.view.forced_origins[key]

    def paintComponent(self, g):
        if self.show_graph:
            graph.Graph.paintComponent(self, g)
        else:
            core.DataViewComponent.paintComponent(self, g)

        dt_tau = None
        if self.filter and self.view.tau_filter > 0:
            dt_tau = self.view.dt / self.view.tau_filter
        try:
            data = self.data.get(start=self.view.current_tick, count=1, dt_tau=dt_tau)[0]
        except:
            return

        if data is None:
            return

        if self.normalize or self.smooth_normalize:
            data = self.calc_normal(data)

        if self.show_graph:
            text = self.vocab.text(data, threshold=0.3, join=',', include_pairs=self.show_pairs)
            g.color = Color.black
            bounds = g.font.getStringBounds(text, g.fontRenderContext)
            g.drawString(text, self.size.width / 2 - bounds.width / 2, 25 + self.label_offset)
        else:
            if self.font_height_50 is None:
                self.font_height_50 = self.getFontMetrics(Font('SansSerif', Font.BOLD, 50)).getHeight()
            items = []
            total = 0
            for i, dp in enumerate(self.vocab.dot(data)):
                if dp > 0.1:
                    items.append((self.vocab.keys[i], dp))
                    total += dp
            if self.show_pairs:
                d = self.vocab.dot_pairs(data)
                if d is not None:
                    for i, dp in enumerate(d):
                        if dp > 0.1:
                            items.append((self.vocab.key_pairs[i], dp))
                            total += dp

            if total > 0:
                scale = (self.size.height - self.label_offset - 25) / total
                y = self.label_offset
                for name, dp in items:
                    g.color = Color(0.0, 0.0, 0.0, min(dp, 1.0))
                    font_size = int(dp * scale * 50 / self.font_height_50)
                    g.font = Font('SansSerif', Font.BOLD, font_size)
                    bounds = g.font.getStringBounds(name, g.fontRenderContext)
                    g.drawString(name, self.size.width / 2 - bounds.width / 2, y + bounds.height)
                    y += bounds.height

    def calc_normal(self, v):
        length = 0
        for i in range(len(v)):
            length += v[i] * v[i]
        length = math.sqrt(length)
        if length > 0:
            scale = 1.0 / length
        else:
            scale = 0
        if self.smooth_normalize and length < self.smooth_normalize_threshold:
            scale = 1.0 / self.smooth_normalize_threshold
        v = [x * scale for x in v]
        return v

    def clear_cache(self):
        self.cache.clear()
        self.cache_tick_count = 0

    def post_process(self, data, start, dt_tau):
        if dt_tau != self.cache_dt_tau or self.cache_tick_count > self.view.watcher.timelog.tick_count:
            self.cache_dt_tau = dt_tau
            self.clear_cache()

        self.cache_tick_count = self.view.watcher.timelog.tick_count

        forget = self.cache_tick_count - self.view.watcher.timelog.tick_limit
        if forget in self.cache:
            del self.cache[forget]

        if len(self.vocab.keys) == 0:
            return []

        d = []
        for i, dd in enumerate(data):
            if dd is None:
                d.append(None)
            else:
                index = i + start
                v = self.cache.get((index, dt_tau), None)
                if v is None:
                    if self.normalize or self.smooth_normalize:
                        dd = self.calc_normal(dd)
                    v = self.vocab.dot(dd)
                    if self.show_pairs:
                        v2 = self.vocab.dot_pairs(dd)
                        if v2 is not None:
                            v = numeric.concatenate((v, v2), 0)
                    self.cache[(index, dt_tau)] = v
                d.append(v)
        return d
