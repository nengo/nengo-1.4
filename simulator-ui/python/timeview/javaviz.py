import nef

import struct
import math

class ProbeNode(nef.Node):
    def __init__(self, receiver, name):
        nef.Node.__init__(self, name)
        self.termination_count = 0
        self.probes = {}
        self.receiver = receiver
        self.data = []

        # make a dummy connection for drawing arrow in the gui.
        #  we use "current" since that is ignored by the
        #  interactive plots visualizer
        self.make_output('current', 1)

    def add_probe(self, id, dimensions, origin_name):
        self.probes[id] = self.make_output(origin_name, dimensions)
        self.receiver.register(id, self, dimensions)

    def add_spike_probe(self, id, num_neurons):
        # Assuming it will only have 1 spike probe
        self.spike_probe = lambda: None
        self.spike_probe._value = [0] * num_neurons
        self.receiver.register(id, self.spike_probe, num_neurons)

    def set_encoders(self, n_neurons, dimensions, encoder_data):
        self.encoders = []
        for i in range(n_neurons):
            self.encoders.append(list(encoder_data[i*dimensions:(i+1)*dimensions]))

    def create_new_dummy_termination(self, dimensions):
        name = 'term%d'%self.termination_count
        self.make_input(name, dimensions)
        self.termination_count += 1
        return self.getTermination(name)

    def add_data(self, id, time, data):
        # if we've been reset
        try:
            if len(self.data) > 0 and self.data[0][1] > time:
                del self.data[:]
        except IndexError:
            # this can happen (rarely) due to threads updating
            # at unlucky times
            pass
        self.data.append((id, time, data))

    def run(self, start, end):
        for p in self.probes.values():
            for i in range(len(p._value)):
                p._value[i] = 0
        while len(self.data) > 0:
            if self.data[0][1] >= start:
                break
            id, time, data = self.data.pop(0)
            for i in range(len(data)):
                # we add these inputs since we may receive more than one
                # packet of data per timestep as the simulator and the
                # visualizer are not kept locked together in time
                self.probes[id]._value[i] += data[i]

        nef.Node.run(self, start, end)



import java
import jarray
from java.awt.event import KeyEvent
class ValueReceiver(java.lang.Thread):
    def __init__(self, port):
        self.socket = java.net.DatagramSocket(port)
        self.socket.setSoTimeout(200) # 200ms
        self.should_close = False
        maxLength = 65535
        self.buffer = jarray.zeros(maxLength,'b')
        self.packet = java.net.DatagramPacket(self.buffer, maxLength)
        self.probes = {}
        self.probe_lengths = {}
        self.sim_time = 0.0

    def register(self, id, probe, length):
        self.probes[id] = probe
        self.probe_lengths[id] = length

    def run(self):
        while True:
            try:
                self.socket.receive(self.packet)
            except java.net.SocketTimeoutException:
                if self.should_close:
                    break
                else:
                    continue
            d = java.io.DataInputStream(
                    java.io.ByteArrayInputStream(self.packet.getData()))

            id = d.readInt()
            if id == -1:
                # dummy probe
                time = d.readFloat()
                self.sim_time = time
                continue

            probe = self.probes[id]


            if callable(probe):
                num_spikes = d.readUnsignedShort()
                for i in range(num_spikes):
                    spike_index = d.readUnsignedShort()
                    probe._value[spike_index] += 1.0
            else:
                time = d.readFloat()
                self.sim_time = time

                length = self.probe_lengths[id]

                probe.add_data(id, time, [d.readFloat() for i in range(length)])

        self.socket.close()
        print 'finished running JavaViz'

class ControlNode(nef.Node, java.awt.event.WindowListener, java.awt.event.KeyListener):

    def __init__(self, name, address, port, receiver, dt=0.001):
        nef.Node.__init__(self, name)
        self.view = None
        self.uses_keyboard = False
        self.receiver = receiver
        self.socket = java.net.DatagramSocket()
        self.address = java.net.InetAddress.getByName(address)
        self.port = port
        self.inputs = {}
        self.dt = dt
        self.formats = {}
        self.ids = {}

    def set_view(self, view):
        self.view = view
        self.view.frame.addWindowListener(self)
        #self.view.frame.addKeyListener(self)
        self.view.area.addKeyListener(self)
        #self.view.time_control.addKeyListener(self)

    def register(self, id, input):
        if input.name == 'keyboard':
            try:
                self.uses_keyboard = input.getOrigin('origin').dimensions
                print 'Using keyboard: dimensions=', self.uses_keyboard
            except:
                pass
        self.inputs[id] = input
        self.ids[input.name] = id

    def register_semantic(self, id, probe_node):
        self.ids[probe_node.name] = id

    def start(self):
        cache = {}

        while True:
            if self.view is not None:
                msg = struct.pack('>f', self.t)
                for key, value in self.view.forced_origins.items():
                    (name, origin, index) = key
                    id = self.ids.get(name, None)
                    if id is not None:
                        if index is None:
                            for i in range(len(value)):
                                msg += struct.pack('>LLf', id, i, value[i])
                        else:
                            msg += struct.pack('>LLf', id, index, value)
                    else:
                        print 'No registered id for ', key
                packet = java.net.DatagramPacket(msg, len(msg), self.address, self.port)
                self.socket.send(packet)

            # don't let the visualizer get too far ahead of the simulation
            while self.t > self.receiver.sim_time + 0.002:
                pass
            if self.view is not None and self.uses_keyboard:
                self.view.area.requestFocusInWindow()
            yield self.dt
    def windowActivated(self, event):
        pass
    def windowClosed(self, event):
        print 'window closed!'
        self.receiver.should_close = True
    def windowClosing(self, event):
        pass
    def windowDeactivated(self, event):
        pass
    def windowDeiconified(self, event):
        pass
    def windowIconified(self, event):
        pass
    def windowOpened(self, event):
        pass
        
    def set_key(self, index, value):
        print 'set_key', index, value
        if self.uses_keyboard > 0 and index < self.uses_keyboard:
            self.view.forced_origins[('keyboard', 'origin', index)] = value
        
    def keyPressed(self,event):
        if event.keyChar=='w': self.set_key(0, 1)
        if event.keyChar=='s': self.set_key(0, -1)
        if event.keyChar=='a': self.set_key(1, -1)
        if event.keyChar=='d': self.set_key(1, 1)
        if event.keyCode==KeyEvent.VK_LEFT: self.set_key(2, -1)
        if event.keyCode==KeyEvent.VK_RIGHT: self.set_key(2, 1)
        if event.keyCode==KeyEvent.VK_UP: self.set_key(3, 1)
        if event.keyCode==KeyEvent.VK_DOWN: self.set_key(3, -1)
    def keyReleased(self,event):
        if event.keyChar=='w': self.set_key(0, 0)
        if event.keyChar=='s': self.set_key(0, 0)
        if event.keyChar=='a': self.set_key(1, 0)
        if event.keyChar=='d': self.set_key(1, 0)
        if event.keyCode==KeyEvent.VK_LEFT: self.set_key(2, 0)
        if event.keyCode==KeyEvent.VK_RIGHT: self.set_key(2, 0)
        if event.keyCode==KeyEvent.VK_UP: self.set_key(3, 0)
        if event.keyCode==KeyEvent.VK_DOWN: self.set_key(3, 0)
    def keyTyped(self,event):
        pass        

