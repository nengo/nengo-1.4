import watcher
import components
import timelog
import data
import simulator
import hrr
from timeview.components import hrrgraph, neuronmap
import nef

import java
import javax
from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
from ca.nengo.model.nef import *
from ca.nengo.model.impl import *
from ca.nengo.math.impl import *
from ca.nengo.model import Node, SimulationMode
from ca.nengo.model.plasticity.impl import STDPTermination
from ca.nengo.model.neuron.impl import SpikingNeuron

from java.lang.System.err import println
from java.lang import Exception
import traceback
import math
import shelve
import warnings
from ca.nengo.util import MU
import time
import os

# for save_pdf
import sys
if 'lib/itextpdf-5.3.4.jar' not in sys.path:
    sys.path.append('lib/itextpdf-5.3.4.jar')


class Icon:
    pass


class ShadedIcon:
    pass


for name in 'pause play configure end start backward forward restart arrowup arrowdown save restore refresh data pdf'.split():
    setattr(Icon, name, ImageIcon('python/images/%s.png' % name))
    setattr(ShadedIcon, name, ImageIcon('python/images/%s-pressed.png' % name))


class EnsembleWatch:
    def check(self, obj):
        return isinstance(obj, NEFEnsemble)

    def voltage(self, obj):
        if obj.mode in [SimulationMode.CONSTANT_RATE, SimulationMode.RATE]:
            return [n.getOrigin('AXON').getValues().values[0] * 0.0005 for n in obj.nodes]
        else:
            s = obj.getOrigin('AXON').getValues().values
            try:
                v = [n.generator.voltage for n in obj.nodes]
            except:
                v = [n.generator.dynamics.state[0] for n in obj.nodes]
            for i, ss in enumerate(s):   # check if there's a spike here
                if ss:
                    v[i] = 1.0
            return v

    def spikes(self, obj):
        if obj.mode in [SimulationMode.CONSTANT_RATE, SimulationMode.RATE]:
            return [n.getOrigin('AXON').getValues().values[0] * 0.0005 for n in obj.nodes]
        else:
            return obj.getOrigin('AXON').getValues().values

    def spikes_only(self, obj):
        if obj.mode in [SimulationMode.CONSTANT_RATE, SimulationMode.RATE]:
            return [0] * obj.neurons
        else:
            return obj.getOrigin('AXON').getValues().values

    def encoder(self, obj):
        return [x[0] for x in obj.encoders]

    def views(self, obj):
        r = [
            (None, None, None),
            # Note that the above tuple is to reset popup menu to main popup menu in item.py
            ('voltage grid', components.Grid, dict(func=self.voltage, sfunc=self.spikes_only, label=obj.name, audio=True)),
            ('voltage graph', components.Graph, dict(func=self.voltage, split=True, ylimits=(0, 1), filter=False, neuronmapped=True, label=obj.name)),
            ('firing rate', components.Grid, dict(func=self.spikes, min=0, max=lambda self: 200 * self.view.dt, filter=True, label=obj.name, audio=True)),
            ('spike raster', components.SpikeRaster, dict(func=self.spikes, label=obj.name)),
        ]
        if obj.dimension == 2:
            r += [
                ('preferred directions', components.PreferredDirection, dict(func=self.spikes, min=0, max=lambda self: 500 * self.view.dt, filter=True, label=obj.name)),
            ]
        return r


class NeuronWatch:
    def check(self, obj):
        return isinstance(obj, SpikingNeuron)

    def current(self, obj):
        return obj.getOrigin('current').getValues().getValues()

    def spikes(self, obj):
        return obj.getOrigin('AXON').getValues().getValues()

    def views(self, obj):
        r = [('input', components.Graph, dict(func=self.current, label=obj.name + ":input")),
             ('output', components.Graph, dict(func=self.spikes, label=obj.name + ":output"))]
        return r


class NodeWatch:
    def check(self, obj):
        return isinstance(obj, Node)

    def value(self, obj, origin):
        return obj.getOrigin(origin).getValues().values

    def weights(self, obj, termination, include_gain=False):
        v = []
        for n in obj.nodes:
            w = n.getTermination(termination).weights
            if include_gain:
                w = MU.prod(w, n.scale)
            v.extend(w)
        return v

    def in_spikes(self, obj, name):
        term = obj.getTermination(name)
        if isinstance(term, PlasticEnsembleTermination) and term.getInput() is not None:
            return term.getInput().getValues()
        else:
            return [0] * obj.neurons

    def out_spikes(self, obj):
        if obj.mode in [SimulationMode.CONSTANT_RATE, SimulationMode.RATE]:
            return [0] * obj.neurons
        else:
            return obj.getOrigin('AXON').values.values

    def views(self, obj):
        origins = [o.name for o in obj.origins]
        ignored_origins = ['AXON', 'current']

        default = None
        filter = True
        if isinstance(obj, NEFEnsemble):
            default = 'X'
            max_radii = max(obj.radii)
        elif isinstance(obj, FunctionInput):
            default = 'origin'
            max_radii = 1

            filter = False

        else:
            max_radii = 1

        for ignored in ignored_origins:
            if(ignored in origins):
                origins.remove(ignored)

        origins.sort()
        num_origins = len(origins)

        if default in origins:
            origins.remove(default)
            origins = [default] + origins

        r = []
        for name in origins:
            text = 'value'
            text_grid = 'value (grid)'
            label = obj.name
            xy = 'XY plot'

            if(num_origins > 1):
                label = obj.name + ": " + name
                r.append((name, JMenu, JMenu(name)))

            r.append((text + '|' + name, components.Graph, dict(func=self.value, args=(name,), filter=filter, label=label)))

            if len(obj.getOrigin(name).getValues().values) > 8:
                r.append((text_grid + '|' + name, components.VectorGrid, dict(func=self.value, args=(name,), min=-max_radii, max=max_radii, label=label)))

            if len(obj.getOrigin(name).getValues().values) >= 2:
                r.append((xy + '|' + name, components.XYPlot, dict(func=self.value, args=(name,), filter=filter, label=label)))
        if num_origins > 1:
            r.append((None, None, None))  # reset to top level of popup menu

        if isinstance(obj, NEFEnsemble):
            terminations = [t.name for t in obj.nodes[0].terminations]

            if len(terminations) > 0:
                r.append(('connection weights', JMenu, JMenu('connection weights')))

                for name in terminations:
                    label = obj.name + ": " + name
                    w = self.weights(obj, name, True)
                    maxw = max(w)
                    minw = min(w)
                    if -minw > maxw:
                        maxw = -minw
                    if maxw == 0:
                        maxw = 0.01
                    r.append((name, components.Grid, dict(func=self.weights, args=(name, True), rows=len(obj.nodes), label=label, min=-maxw, max=maxw, improvable=False)))

                    if isinstance(name, STDPTermination):
                        r.append((name + ' detail', components.SpikeLineOverlay, dict(
                                  infunc=self.in_spikes, inargs=(name,), outfunc=self.out_spikes,
                                  lfunc=self.weights, largs=(name,), label=label)))

        return r


class FunctionWatch:
    def check(self, obj):
        return isinstance(obj, FunctionInput)

    def funcOrigin(self, obj):
        return obj.getOrigin('origin').getValues().values

    def views(self, obj):
        return [
            ('control', components.FunctionControl, dict(func=self.funcOrigin, label=obj.name)),
        ]


class HRRWatch:
    def check(self, obj):
        if(isinstance(obj, NEFEnsemble) or isinstance(obj, NetworkArrayImpl)):
            return (obj.dimension >= 8)
        elif(isinstance(obj, nef.SimpleNode) or isinstance(obj, NetworkImpl)):
            try:
                return (obj.dimension >= 8 and obj.getOrigin("X"))
            except:
                return False

    def views(self, obj):
        return [
            ('semantic pointer', components.HRRGraph, dict(func=nodeWatch.value, args='X', label=obj.name, nodeid=id(obj))),
        ]


class ArrayWatch:
    def check(self, obj):
        return isinstance(obj, NetworkArrayImpl)

    def spike_array(self, obj):
        r = []
        for n in obj.nodes:
            r.extend(n.getOrigin('AXON').getValues().values)
        return r

    def views(self, obj):
        return [
            ('firing rate', components.Grid, dict(func=self.spike_array, min=0, max=lambda self: 200 * self.view.dt, filter=True, label=obj.name, improvable=False)),
            ('spike raster', components.SpikeRaster, dict(func=self.spike_array, label=obj.name, usemap=False)),
        ]


class FSMWatch:
    def check(self, obj):
        return isinstance(obj, nef.FSMNode)

    def measure(self, obj):
        out = [0.0] * len(obj.states)
        if obj.state is not None:
            out[sorted(obj.states.keys()).index(obj.state.name)] = 1.0
        return out

    def views(self, obj):
        return [('state viewer', components.TextList, dict(func=self.measure, label="States", names=sorted(obj.states.keys()), show_values=False, ignore_filter=True))]

import space
import ccm.nengo


class RoomWatch:
    def check(self, obj):
        if isinstance(obj, ccm.nengo.CCMModelNetwork):
            if isinstance(obj._simulator.model, space.Room):
                return True
        return False

    def physics(self, obj):
        return obj._simulator.model.physics_dump()

    def views(self, obj):
        return [
            ('3D view', components.View3D, dict(func=self.physics)),
        ]

nodeWatch = NodeWatch()
ensembleWatch = EnsembleWatch()
watches = [RoomWatch(), nodeWatch, ensembleWatch, FunctionWatch(), HRRWatch(), ArrayWatch(), NeuronWatch(), FSMWatch()]


import math


class ViewPanel(JPanel):
    def __init__(self, network):
        JPanel.__init__(self)
        self.network = network
        self.nodes = {}

    def paintProjection(self, oname, tname, g):
        if oname in self.nodes and tname in self.nodes:
            c1 = self.nodes[oname]
            c2 = self.nodes[tname]
            if c1.visible and c2.visible:

                arrowsize = 7.0
                sin60 = -math.sqrt(3) / 2
                cos60 = -0.5

                if c1 is c2:
                    scale = 0.1
                    x = c1.x + c1.width / 2
                    y = c1.y + c2.height / 2
                    g.drawOval(int(c1.x - c1.width * scale), int(c1.y - c1.height / 2 - c1.height * scale), int(c1.width * (1 + scale * 2)), int(c2.height * (1 + scale * 2)))
                    xc = x
                    yc = y - c1.height - c1.height * scale
                    xa = -arrowsize
                    ya = 0.0
                else:
                    x1 = c1.x + c1.width / 2
                    x2 = c2.x + c2.width / 2
                    y1 = c1.y + c1.height / 2
                    y2 = c2.y + c2.height / 2
                    g.drawLine(x1, y1, x2, y2)

                    place = 0.4

                    xc = (x1 * place + x2 * (1 - place)) + 0.5
                    yc = (y1 * place + y2 * (1 - place)) + 0.5

                    length = math.sqrt(float((x2 - x1) ** 2 + (y2 - y1) ** 2))
                    if length == 0:
                        xa = arrowsize
                        ya = 0.0
                    else:
                        xa = (x2 - x1) * arrowsize / length
                        ya = (y2 - y1) * arrowsize / length

                g.fillPolygon([int(xc + xa), int(xc + cos60 * xa - sin60 * ya), int(xc + cos60 * xa + sin60 * ya)],
                              [int(yc + ya), int(yc + sin60 * xa + cos60 * ya), int(yc - sin60 * xa + cos60 * ya)],
                              3)

    def paintProjections(self, network, g, prefix=""):
        for p in network.projections:
            origin = p.origin
            termination = p.termination

            oname = prefix + origin.node.name
            tname = prefix + termination.node.name

            self.paintProjection(oname, tname, g)

            # Check to see if the termination or origin is wrapped. If it is, then use that,
            # otherwise, just use the termination / origin itself. 
            # Note: This may or may not have unintended consequences when checking what object
            #       the termination / origin is connected to (see next code block - 
            #       ... isinstance(termination.node, NetworkImpl) ...)
            if hasattr(termination, 'wrappedTermination' ):
                termination = termination.wrappedTermination
            if hasattr(origin, 'wrappedOrigin' ):
                origin = origin.wrappedOrigin
    
            if isinstance(termination.node, NetworkImpl):
                self.paintProjection(oname, tname + ':' + termination.node.name, g)
                if isinstance(origin.node, NetworkImpl):
                    self.paintProjection(oname + ':' + origin.name, tname + ':' + termination.name, g)
            if isinstance(origin.node, NetworkImpl):
                self.paintProjection(oname + ':' + origin.node.name, tname, g)

        for n in network.nodes:
            if isinstance(n, NetworkImpl):
                self.paintProjections(n, g, prefix=prefix + n.name + ':')

    def paintComponent(self, g):
        g.color = Color.white
        g.fillRect(0, 0, self.width, self.height)

        g.color = Color.black
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        self.paintProjections(self.network, g)

    def screenshot(self, filename):
        # TODO: Make this function compatible with both mac and windows systems. The current solution
        #       is kinda hacky.

        #pt=self.getLocationOnScreen()
        #image=java.awt.Robot().createScreenCapture(java.awt.Rectangle(pt.x,pt.y,self.width,self.height))
        #javax.imageio.ImageIO.write(image,'png', java.io.File(filename));
        #return

        #self.visible=False
        image = java.awt.image.BufferedImage(self.width, self.height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        g = image.getGraphics()
        self.paintComponent(g)
        self.paintBorder(g)

        self.paintChildren(g)
        """
        print g

        comps=list(self.components)
        comps.reverse()
        for c in comps:
            g.translate(c.x,c.y)
            g.setClip(0,0,c.width,c.height)
            #g.color=c.foreground
            #g.font=c.font
            c.update(g)
            g.translate(-c.x,-c.y)
        #self.paintComponents(g)
        #self.paintChildren(g)
        g.setClip(0,0,self.width,self.height)
        """

        #self.paint_bubbles(g)

        javax.imageio.ImageIO.write(image, 'png', java.io.File(filename))


class View(MouseListener, MouseMotionListener, ActionListener, java.lang.Runnable):
    def __init__(self, network, size=None, ui=None, play=False):
        self.dt = 0.001
        self.tau_filter = 0.03
        self.delay = 10
        self.current_tick = 0
        self.time_shown = 0.5

        self.autopause_at = None

        self.timelog = timelog.TimeLog()
        self.network = network
        self.watcher = watcher.Watcher(self.timelog)
        for w in watches:
            self.watcher.add_watch(w)

        self.requested_mode = None

        self.frame = JFrame(network.name)
        self.frame.visible = True
        self.frame.layout = BorderLayout()
        self.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)

        self.area = ViewPanel(network)
        self.area.background = Color.white
        self.area.layout = None
        self.area.addMouseListener(self)
        self.area.addMouseMotionListener(self)
        self.frame.add(self.area)

        self.time_control = TimeControl(self)
        self.frame.add(self.time_control, BorderLayout.SOUTH)

        self.forced_origins = {}
        self.forced_origins_prev = {}

        self.tick_queue = []

        self.mapcache = neuronmap.MapCache()

        if size is None:
            size = (950, 600)
            if ui is not None:
                size = (max(int(ui.width), 950), max(int(ui.height), 500))

        self.frame.size = (size[0], size[1] + 100)

        self.popup = JPopupMenu()
        self.area.add(self.popup)

        self.process_nodes(network.nodes, self.popup)

        restored = self.restore()
        if not restored:
            if ui is not None:
                p0 = ui.localToView(java.awt.geom.Point2D.Double(0, 0))
                p1 = ui.localToView(java.awt.geom.Point2D.Double(ui.width, ui.height))

                for n in ui.UINodes:
                    x = (n.offset.x - p0.x) / (p1.x - p0.x) * size[0]
                    y = (n.offset.y - p0.y) / (p1.y - p0.y) * size[1]
                    self.add_item(n.name, location=(int(x), int(y)))

        self.restart = False
        self.paused = True
        self.simulating = False
        self.thread = java.lang.Thread(self)
        self.thread.priority = java.lang.Thread.MIN_PRIORITY
        self.thread.start()

        if play is True or play > 0:
            if isinstance(play, (int, float)):
                self.autopause_at = play
            self.time_control.pause(None)

    def close(self):
        if(self.frame is not None):
            # Close the frame. This will also kill self.thread
            self.frame.visible = False
            self.frame.dispose()
            self.frame = None

    def process_nodes(self, nodes, popup, prefix=""):
        names = [(n.name, n) for n in nodes]
        names.sort()

        for i, (name, n) in enumerate(names):
            self.watcher.add_object(prefix + name, n)

            if isinstance(n, NetworkImpl):
                def click_func(event, self=self, name=prefix + name):
                    self.add_item(name, self.mouse_click_location)
                    self.popup.visible = False
                menu = JMenu(prefix + name, mouseClicked=click_func)
                popup.add(menu)
                self.process_nodes(n.nodes, prefix=prefix + name + ":", popup=menu)
            else:
                popup.add(JMenuItem(prefix + name, actionPerformed=lambda event, self=self, name=prefix + name: self.add_item(name, self.mouse_click_location)))

    def add_item(self, name, location=None):
        g = components.Item(self, name)
        if name in self.area.nodes:
            self.area.nodes[name].do_hide()
        self.area.nodes[name] = g
        if location is not None:
            g.setLocation(*location)
        self.area.add(g)
        self.area.repaint()
        return g

    def refresh_hrrs(self):
        ''' Call refresh methods on all semantic pointer graphs (useful if HRR has just changed).'''
        for item in self.area.components:
            if isinstance(item, hrrgraph.HRRGraph):
                item.redo_indices()

    def clear_all(self):
        self.area.nodes = {}
        for item in self.area.components:
            if isinstance(item, components.core.DataViewComponent):
                self.area.remove(item)

    def mouseClicked(self, event):
        self.mouse_click_location = event.x, event.y
        if event.button == MouseEvent.BUTTON3 or (event.button == MouseEvent.BUTTON1 and event.isControlDown()):
            self.popup.show(self.area, event.x - 5, event.y - 5)

    def mouseEntered(self, event):
        pass

    def mouseExited(self, event):
        pass

    def mousePressed(self, event):
        self.drag_start = event.x, event.y

    def mouseReleased(self, event):
        pass

    def set_target_rate(self, value):
        if value == 'fastest':
            self.delay = 0
        elif value.endswith('x'):
            r = float(value[:-1])
            self.delay = self.dt * 1000 / r

    def mouseDragged(self, event):
        dx = event.x - self.drag_start[0]
        dy = event.y - self.drag_start[1]

        for c in self.area.components:
            c.setLocation(c.x + dx, c.y + dy)
        self.drag_start = event.x, event.y
        self.area.repaint()

    def mouseMoved(self, event):
        pass

    def force_origins(self):
        dt_tau = self.dt / 0.01
        decay = math.exp(-dt_tau)
        for key, value in self.forced_origins.items():
            (name, origin, index) = key
            origin = self.watcher.objects[name].getOrigin(origin)
            if hasattr(origin, 'getWrappedOrigin'):
                origin = origin.getWrappedOrigin()

            ov = origin.getValues()

            v = ov.getValues()

            if index is not None:
                prev = self.forced_origins_prev.get(key, None)
                if prev is None:
                    prev = v[index]

                v[index] = prev * decay + value * (1 - decay)
                self.forced_origins_prev[key] = v[index]
            else:
                v = value

            origin.setValues(RealOutputImpl(v, ov.getUnits(), ov.getTime()))

    def save(self):
        key = self.network.name
        layout = []
        for comp in self.area.components:
            if isinstance(comp, components.core.DataViewComponent):
                layout.append((comp.name, comp.type, comp.save()))

        # Save time control settings
        controls = dict()
        controls['sim_spd'] = self.time_control.rate_combobox.getSelectedIndex()
        controls['dt'] = self.time_control.dt_combobox.getSelectedIndex()
        controls['rcd_time'] = self.time_control.record_time_spinner.getValue()
        controls['filter'] = self.time_control.filter_spinner.getValue()
        controls['show_time'] = self.time_control.time_shown_spinner.getValue()

        view = self.view_save()

        save_layout_file(key, view, layout, controls)

    def restore(self):
        data = load_layout_file(self.network.name)
        if data is None:
            return False
        view, layout, controls = data

        control_keys = controls.keys()
        if('sim_spd' in control_keys):
            self.time_control.rate_combobox.setSelectedIndex(controls['sim_spd'])
        if('dt' in control_keys):
            self.time_control.dt_combobox.setSelectedIndex(controls['dt'])
        if('rcd_time' in control_keys):
            self.time_control.record_time_spinner.setValue(controls['rcd_time'])
        if('filter' in control_keys):
            self.time_control.filter_spinner.setValue(controls['filter'])
        if('show_time' in control_keys):
            self.time_control.time_shown_spinner.setValue(controls['show_time'])

        self.clear_all()
        for name, type, data in layout:
            if name in self.watcher.objects.keys():
                if type is None:
                    c = self.add_item(name)
                    c.restore(data)
                else:
                    for (t, klass, args) in self.watcher.list(name):
                        if t == type:
                            if not isinstance(args, dict):
                                warnings.warn('restoration error in "%s.layout": %s,%s,%s' % (name, t, klass, args), RuntimeWarning)
                            else:
                                c = klass(self, name, **args)
                                c.type = type
                                c.restore(data)
                                self.area.add(c)
                                break

        # Restore time control settings
        self.view_restore(view)
        self.area.repaint()
        return True

    def view_save(self):
        return dict(width=self.frame.width, height=self.frame.height - self.time_control.config_panel_height, state=self.frame.getExtendedState(), x=self.frame.x, y=self.frame.y)

    def view_restore(self, d):
        self.frame.setSize(d['width'], d['height'])
        if 'x' in d and 'y' in d:
            self.frame.setLocation(d['x'], d['y'])
        self.frame.setExtendedState(d.get('state', self.frame.NORMAL))

    screenshot_time = None

    def save_screenshots(self, time=0.01):
        self.screenshot_time = time

    def run(self):
        sim = simulator.Simulator(self.network)

        sim.step(0.0, 0.001)
        sim.reset(False)

        try:
            while self.frame.visible:
                sim.reset(False)
                # run the network for an instant so that FunctionInputs have values at their Origin so they can be read
                for n in self.network.nodes:
                    if isinstance(n, FunctionInput):
                        n.run(0, 0)

                now = 0
                self.watcher.reset()

                if self.screenshot_time is not None:
                    self.screenshot_name = 'screenshot-%s/%s' % (self.network.name, time.strftime('%Y%m%d-%H%M%S'))
                    try:
                        os.makedirs(self.screenshot_name)
                        self.next_record = 0
                        self.next_screenshot_time = 0
                        self.screenshot_index = 0
                    except:
                        print 'Error making directory %s: recording is disabled' % self.screenshot_name
                        self.screenshot_time = None

                self.time_control.set_min_time(max(0, self.timelog.tick_count - self.timelog.tick_limit + 1))
                self.time_control.set_max_time(self.timelog.tick_count)
                self.area.repaint()
                self.forced_origins = {}
                last_frame_time = None
                counter = 0
                while self.frame.visible:
                    while (self.paused or self.timelog.processing or self.time_control.slider.valueIsAdjusting) and not self.restart and self.frame.visible:
                        java.lang.Thread.sleep(10)
                        if self.requested_mode is not None:
                            self.network.mode = self.requested_mode
                            self.requested_mode = None
                    if self.requested_mode is not None:
                        self.network.mode = self.requested_mode
                        self.requested_mode = None
                    if self.restart or not self.frame.visible:
                        self.restart = False
                        break

                    if now == 0:
                        # reset the FunctionInputs so that they don't pass their information too soon after being run previously
                        for n in self.network.nodes:
                            if isinstance(n, FunctionInput):
                                n.reset(False)

                    if self.screenshot_time is not None and (now >= self.next_screenshot_time):
                        self.area.screenshot('%s/%06d.png' % (self.screenshot_name, self.screenshot_index))
                        self.next_screenshot_time += self.screenshot_time
                        self.screenshot_index += 1

                    if self.current_tick >= self.timelog.tick_count - 1:
                        self.simulating = True
                        sim.step(now, now + self.dt)
                        self.simulating = False
                        self.force_origins()
                        now += self.dt
                        for tick in self.tick_queue:
                            tick(now)

                        if self.autopause_at is not None and now > self.autopause_at:
                            self.time_control.pause(None)
                            self.autopause_at = None
                        self.timelog.tick()
                        self.time_control.set_min_time(max(0, self.timelog.tick_count - self.timelog.tick_limit + 1))
                        self.time_control.set_max_time(self.timelog.tick_count)
                        self.time_control.slider.value = self.timelog.tick_count
                    else:
                        self.time_control.slider.value = self.current_tick + 1
                    self.area.repaint()
                    this_frame_time = java.lang.System.currentTimeMillis()
                    if last_frame_time is not None:
                        delta = this_frame_time - last_frame_time
                        sleep = self.delay - delta
                        if sleep < 0:
                            sleep = 0
                        java.lang.Thread.sleep(int(sleep))
                    last_frame_time = this_frame_time

        except AttributeError, error_val:
            if(not self.frame is None):
                raise error_val

        if sim is not None:
            sim.kill()


class RoundedBorder(javax.swing.border.AbstractBorder):
    def __init__(self):
        self.color = Color(0.7, 0.7, 0.7)

    def getBorderInsets(self, component):
        return java.awt.Insets(5, 5, 5, 5)

    def paintBorder(self, c, g, x, y, width, height):
        g.color = self.color
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.drawRoundRect(x, y, width - 1, height - 1, 10, 10)


class TimeControl(JPanel, ChangeListener, ActionListener):
    def __init__(self, view):
        JPanel.__init__(self)
        self.view = view
        self.background = Color.white
        self.config_panel_height = 60

        mainPanel = JPanel(background=self.background, layout=BorderLayout())
        mainPanel.border = RoundedBorder()
        configPanel = JPanel(background=self.background, visible=False)

        self.layout = BorderLayout()
        self.add(mainPanel, BorderLayout.NORTH)
        self.add(configPanel, BorderLayout.SOUTH)

        self.config_button = JButton(Icon.arrowdown, rolloverIcon=ShadedIcon.arrowdown, toolTipText='configure', actionPerformed=self.configure, borderPainted=False, focusPainted=False, contentAreaFilled=False)
        self.add(self.config_button)

        self.configPanel = configPanel

        self.slider = JSlider(0, 1, 0, background=self.background)
        self.slider.snapToTicks = True
        mainPanel.add(self.slider)
        self.slider.addChangeListener(self)

        self.min_time = JLabel(' 0.0000 ', opaque=True, background=self.background)
        self.max_time = JLabel(' 0.0000 ', opaque=True, background=self.background)

        self.left_panel = JPanel(background=self.background)
        self.left_panel.add(JButton(Icon.restart, rolloverIcon=ShadedIcon.restart, toolTipText='restart', actionPerformed=self.start, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        self.left_panel.add(self.min_time)
        self.left_panel.add(JButton(icon=Icon.start, rolloverIcon=ShadedIcon.start, toolTipText='jump to beginning', actionPerformed=lambda x: self.slider.setValue(self.slider.minimum), borderPainted=False, focusPainted=False, contentAreaFilled=False))

        self.right_panel = JPanel(background=self.background)
        self.right_panel.add(JButton(icon=Icon.end, rolloverIcon=ShadedIcon.end, toolTipText='jump to end', actionPerformed=lambda x: self.slider.setValue(self.slider.maximum), borderPainted=False, focusPainted=False, contentAreaFilled=False))
        self.right_panel.add(self.max_time)
        self.playpause_button = JButton(Icon.play, actionPerformed=self.pause, rolloverIcon=ShadedIcon.play, toolTipText='continue', borderPainted=False, focusPainted=False, contentAreaFilled=False)
        self.right_panel.add(self.playpause_button)

        mainPanel.add(self.left_panel, BorderLayout.WEST)
        mainPanel.add(self.right_panel, BorderLayout.EAST)

        pdf = JPanel(layout=BorderLayout(), opaque=False)
        pdf.add(JButton(Icon.pdf, rolloverIcon=ShadedIcon.pdf, toolTipText='save pdf', actionPerformed=self.save_pdf, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        pdf.add(JLabel('pdf', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        pdf.maximumSize = pdf.preferredSize
        configPanel.add(pdf)

        data = JPanel(layout=BorderLayout(), opaque=False)
        data.add(JButton(Icon.data, rolloverIcon=ShadedIcon.data, toolTipText='examine data', actionPerformed=self.show_data, borderPainted=False, focusPainted=False, contentAreaFilled=False))
        data.add(JLabel('data', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        data.maximumSize = data.preferredSize
        configPanel.add(data)

        mode = JPanel(layout=BorderLayout(), opaque=False)
        cb = JComboBox(['default', 'rate', 'direct'])
        if self.view.network.mode in [SimulationMode.DEFAULT, SimulationMode.PRECISE]:
            cb.setSelectedIndex(0)
        elif self.view.network.mode in [SimulationMode.RATE]:
            cb.setSelectedIndex(1)
        elif self.view.network.mode in [SimulationMode.DIRECT, SimulationMode.APPROXIMATE]:
            cb.setSelectedIndex(2)
        cb.addActionListener(self)
        self.mode_combobox = cb
        mode.add(cb)
        mode.add(JLabel('mode'), BorderLayout.NORTH)
        mode.maximumSize = mode.preferredSize
        configPanel.add(mode)

        dt = JPanel(layout=BorderLayout(), opaque=False)
        cb = JComboBox(['0.001', '0.0005', '0.0002', '0.0001'])
        cb.setSelectedIndex(0)
        self.view.dt = float(cb.getSelectedItem())
        cb.addActionListener(self)
        self.dt_combobox = cb
        dt.add(cb)
        dt.add(JLabel('time step'), BorderLayout.NORTH)
        dt.maximumSize = dt.preferredSize
        configPanel.add(dt)

        rate = JPanel(layout=BorderLayout(), opaque=False)
        self.rate_combobox = JComboBox(['fastest', '1x', '0.5x', '0.2x', '0.1x', '0.05x', '0.02x', '0.01x', '0.005x', '0.002x', '0.001x'])
        self.rate_combobox.setSelectedIndex(4)
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())
        self.rate_combobox.addActionListener(self)
        rate.add(self.rate_combobox)
        rate.add(JLabel('speed'), BorderLayout.NORTH)
        rate.maximumSize = rate.preferredSize
        configPanel.add(rate)

        spin1 = JPanel(layout=BorderLayout(), opaque=False)
        self.record_time_spinner = JSpinner(SpinnerNumberModel((self.view.timelog.tick_limit - 1) * self.view.dt, 0.1, 100, 1), stateChanged=self.tick_limit)
        spin1.add(self.record_time_spinner)
        spin1.add(JLabel('recording time'), BorderLayout.NORTH)
        spin1.maximumSize = spin1.preferredSize
        configPanel.add(spin1)

        spin2 = JPanel(layout=BorderLayout(), opaque=False)
        self.filter_spinner = JSpinner(SpinnerNumberModel(self.view.tau_filter, 0, 0.5, 0.01), stateChanged=self.tau_filter)
        spin2.add(self.filter_spinner)
        spin2.add(JLabel('filter'), BorderLayout.NORTH)
        spin2.maximumSize = spin2.preferredSize
        configPanel.add(spin2)

        spin3 = JPanel(layout=BorderLayout(), opaque=False)
        self.time_shown_spinner = JSpinner(SpinnerNumberModel(self.view.time_shown, 0.01, 50, 0.1), stateChanged=self.time_shown)
        spin3.add(self.time_shown_spinner)
        spin3.add(JLabel('time shown'), BorderLayout.NORTH)
        spin3.maximumSize = spin3.preferredSize
        configPanel.add(spin3)

        layout = JPanel(layout=BorderLayout(), opaque=False)
        layout.add(JButton(icon=Icon.save, rolloverIcon=ShadedIcon.save, actionPerformed=self.save, borderPainted=False, focusPainted=False, contentAreaFilled=False, margin=java.awt.Insets(0, 0, 0, 0), toolTipText='save layout'), BorderLayout.WEST)
        layout.add(JButton(icon=Icon.restore, rolloverIcon=ShadedIcon.restore, actionPerformed=self.restore, borderPainted=False, focusPainted=False, contentAreaFilled=False, margin=java.awt.Insets(0, 0, 0, 0), toolTipText='restore layout'), BorderLayout.EAST)

        layout.add(JLabel('layout', horizontalAlignment=javax.swing.SwingConstants.CENTER), BorderLayout.NORTH)
        layout.maximumSize = layout.preferredSize
        configPanel.add(layout)

        configPanel.setPreferredSize(java.awt.Dimension(20, self.config_panel_height))
        configPanel.visible = False

        for c in [dt, rate, spin1, spin2, spin3]:
            c.border = javax.swing.border.EmptyBorder(0, 10, 0, 10)

    def show_data(self, event):
        frame = JFrame('%s Data' % self.view.network.name)
        frame.visible = True
        frame.add(data.DataPanel(self.view))
        frame.size = (500, 600)

    def forward_one_frame(self, event):
        self.slider.setValue(self.slider.value + 1)

    def backward_one_frame(self, event):
        self.slider.setValue(self.slider.value - 1)

    def set_max_time(self, maximum):
        self.slider.maximum = maximum
        self.max_time.text = ' %1.4f ' % (self.view.dt * maximum)

    def set_min_time(self, minimum):
        self.slider.minimum = minimum
        self.min_time.text = ' %1.4f ' % (self.view.dt * minimum)

    def stateChanged(self, event):
        self.view.current_tick = self.slider.value
        self.view.area.repaint()

    def start(self, event):
        self.view.restart = True

    def configure(self, event):
        view_state = self.view.frame.getExtendedState()
        if self.configPanel.visible:
            self.view.frame.setSize(self.view.frame.width, self.view.frame.height - self.config_panel_height)
            self.configPanel.visible = False
            self.config_button.icon = Icon.arrowdown
            self.config_button.rolloverIcon = ShadedIcon.arrowdown
            self.config_button.toolTipText = 'configure'
        else:
            if(view_state & self.view.frame.MAXIMIZED_BOTH == self.view.frame.MAXIMIZED_BOTH):
                self.view.frame.setSize(self.view.frame.width, self.view.frame.height)
            else:
                self.view.frame.setSize(self.view.frame.width, self.view.frame.height + self.config_panel_height)
            self.configPanel.visible = True
            self.config_button.icon = Icon.arrowup
            self.config_button.rolloverIcon = ShadedIcon.arrowup
            self.config_button.toolTipText = 'hide configuration'
        self.view.frame.setExtendedState(view_state)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.layout.layoutContainer(self)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.layout.layoutContainer(self)
        self.view.frame.layout.layoutContainer(self.view.frame)
        self.view.frame.repaint()

    def pause(self, event):
        self.view.paused = not self.view.paused
        if self.view.paused:
            self.playpause_button.icon = Icon.play
            self.playpause_button.rolloverIcon = ShadedIcon.play
            self.playpause_button.toolTipText = 'continue'
        else:
            self.playpause_button.icon = Icon.pause
            self.playpause_button.rolloverIcon = ShadedIcon.pause
            self.playpause_button.toolTipText = 'pause'

    def tau_filter(self, event):
        self.view.tau_filter = float(event.source.value)
        self.view.area.repaint()

    def time_shown(self, event):
        self.view.time_shown = float(event.source.value)
        self.view.area.repaint()

    def actionPerformed(self, event):
        dt = float(self.dt_combobox.getSelectedItem())
        if dt != self.view.dt:
            self.view.dt = dt
            self.record_time_spinner.value = (self.view.timelog.tick_limit - 1) * self.view.dt
            self.dt_combobox.repaint()
            self.view.restart = True
        self.view.set_target_rate(self.rate_combobox.getSelectedItem())

        if self.mode_combobox is not None:
            mode = self.mode_combobox.getSelectedItem()
            if mode == 'default':
                requested = SimulationMode.DEFAULT
            elif mode == 'rate':
                requested = SimulationMode.RATE
            elif mode == 'direct':
                requested = SimulationMode.DIRECT
            if requested != self.view.network.mode:
                self.view.requested_mode = requested

    def tick_limit(self, event):
        self.view.timelog.tick_limit = int(event.source.value / self.view.dt) + 1

    def save(self, event):
        self.view.save()

    def restore(self, event):
        self.view.restore()

    def save_pdf(self, event):
        from com.itextpdf.text.pdf import PdfWriter
        from com.itextpdf.text import Document

        fileChooser = JFileChooser()
        fileChooser.setSelectedFile(java.io.File('%s.pdf' % self.view.network.name))
        if fileChooser.showSaveDialog(self) == JFileChooser.APPROVE_OPTION:
            f = fileChooser.getSelectedFile()

            doc = Document()
            writer = PdfWriter.getInstance(doc, java.io.FileOutputStream(f))
            doc.open()
            cb = writer.getDirectContent()
            w = self.view.area.size.width
            h = self.view.area.size.height
            pw = 550
            ph = 800
            tp = cb.createTemplate(pw, ph)
            g2 = tp.createGraphicsShapes(pw, ph)
            at = java.awt.geom.AffineTransform()
            s = min(float(pw) / w, float(ph) / h)
            at.scale(s, s)
            g2.transform(at)
            self.view.area.pdftemplate = tp, s
            self.view.area.paint(g2)
            self.view.area.pdftemplate = None
            g2.dispose()

            cb.addTemplate(tp, 20, 0)
            doc.close()


def make_layout_dir(dir):
    if not dir.exists():
        dir.mkdirs()
        devdir = java.io.File('dist-files/layouts')
        if devdir.exists():
            devlayouts = devdir.listFiles()
            for layout in devlayouts:
                newlayout = dir.getPath() + '/' + layout.getName()
                copyfile(layout.getCanonicalPath(), newlayout)


def save_layout_file(name, view, layout, controls):
    dir = java.io.File('layouts')
    make_layout_dir(dir)

    fn = 'layouts/%s.layout' % name
    # Check if file exists
    # - If it does, extract java layout information, otherwise just make a new file
    java_layout = ""
    if java.io.File(fn).exists():
        # Save a backup of the layout file
        fp = java.io.File(fn).getCanonicalPath()
        copyfile(fp, fp + '.bak')

        f = file(fn, 'r')
        data = f.read()
        for line in data.split('\n'):
            if line.startswith('#'):
                java_layout += '\n' + line
        f.close()

    f = file(fn, 'w')

    layout_text = ',\n  '.join([repr(x) for x in layout])

    f.write('(%s,\n [%s],\n %s) %s' % (view, layout_text, controls, java_layout))
    f.close()


def load_layout_file(name, try_backup = True):
    # Support for loading a file using just the name, and a file extension 
    # (useful for loading backup files)
    if(isinstance(name, list)):
        fn = '.'.join(name)
    else:
        fn = '%s.layout' % name

    read_fail = False 
    data = []
    if not java.io.File(fn).exists():
        make_layout_dir(java.io.File('layouts'))
        fn = 'layouts/' + fn
        bckup_fn = fn + '.bak'
        if not java.io.File(fn).exists():
            if not java.io.File(bckup_fn).exists():
                return None
            else:
                warnings.warn('Could not load layout file "%s"' % fn, RuntimeWarning)
                read_fail = True
    
    if not read_fail:
        try:
            f = file(fn, 'r')
            text = f.read()
            f.close()
            if text[0] == '#':
                return None
            data = eval(text)
        except SyntaxError, e:
            warnings.warn('Could not parse layout file "%s"' % fn, RuntimeWarning)
            warnings.warn(e, RuntimeWarning)
            read_fail = True       
        except Exception, e:
            warnings.warn('Could not parse layout file "%s"' % fn, RuntimeWarning)
            warnings.warn(e, RuntimeWarning)
            read_fail = True
        except IndexError, e:
            warnings.warn('Could not parse layout file "%s"' % fn, RuntimeWarning)
            warnings.warn(e, RuntimeWarning)
            read_fail = True

    if len(data) != 3 and not read_fail:
        warnings.warn('Could not parse layout file "%s"' % fn, RuntimeWarning)
        read_fail = True

    # Read fail logic -
    # If try_backup, then try loading backup file, otherwise, just return None
    if read_fail:
        if try_backup:
            warnings.warn('Attempting to load backup file "%s.bak"' %fn, RuntimeWarning)
            result = load_layout_file([fn,'bak'], try_backup = False)
            if result is None:
                warnings.warn('Backup load failed. Using default layout', RuntimeWarning)
            return result
        else:
            return None
    
    # Save a backup of the layout file (but only if we are going to use it)
    if(not read_fail and try_backup):
        fp = java.io.File(fn).getCanonicalPath()
        try:
            copyfile(fp, fp + '.bak')
        except IOError:
            pass    

    return data


def copyfile(file1, file2):
    f = file(file1, 'r')
    text = f.read()
    f.close()
    f = file(file2, 'w')
    f.write(text)
    f.close()
