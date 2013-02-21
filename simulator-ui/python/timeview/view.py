import watcher
import components
import timelog
import data
import hrr
from timeview.components import hrrgraph, neuronmap, timecontrol, viewpanel
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
from ca.nengo.model import Node, SimulationMode, SimulationException
from ca.nengo.model.plasticity.impl import STDPTermination
from ca.nengo.model.neuron.impl import SpikingNeuron
from ca.nengo.util.impl import NodeThreadPool, NEFGPUInterface
from ca.nengo.util import MU

from java.lang.System.err import println
from java.lang import Exception
import traceback
import math
import shelve
import warnings
import time
import os

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
        self.load_watches()

        self.requested_mode = None

        self.frame = JFrame(network.name)
        self.frame.visible = True
        self.frame.layout = BorderLayout()
        self.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)

        self.area = viewpanel.ViewPanel(network)
        self.area.background = Color.white
        self.area.layout = None
        self.area.addMouseListener(self)
        self.area.addMouseMotionListener(self)
        self.frame.add(self.area)

        self.time_control = timecontrol.TimeControl(self)
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
    
    def load_watches(self):
        watch_dir = os.path.join("python","timeview","watches")
        
        names = [s[:-3] for s in os.listdir(watch_dir) if (s.endswith(".py") and not s.startswith("__"))]
        
        for n in names:
            module = __import__("timeview.watches." + n, fromlist=[n])
            for k in module.__dict__.keys():
                if k.upper() == n.upper():
                    self.watcher.add_watch(module.__dict__[k]())
                    
    def add_watch(self, watch):
        self.watcher.add_watch(watch)

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
        sim = self.network.getSimulator()
        sim.makeNodeThreadPool()

        sim.step(0.0, 0.001)
        sim.resetNetwork(False, False)

        try:
            while self.frame.visible:
                sim.resetNetwork(False, False)
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
                        try:
                            sim.step(now, now + self.dt)
                        except SimulationException,se:
                            self.close()
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

        if sim is not None and NodeThreadPool.isMultithreading():
            NEFGPUInterface.requireAllOutputsOnCPU(False);
            sim.getNodeThreadPool().kill()
        
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
