import copy
import nef
from .simulator import Simulator
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ca.nengo.math.impl import PostfixFunction

class probeSimpleNode(nef.SimpleNode):
    def __init__(self, name, num_dim, radius = 1, pstc = 0.03):
        self.pstc = pstc
        self.dimension = num_dim
        self.radius = radius
        self.input_data = [0] * num_dim
        self.data = [[0] * num_dim]
        nef.SimpleNode.__init__(self, name)

    def reset(self, randomize = False):
        self.data = [[0] * self.dimension]

    def termination_Input(self, x):
        self.input_data = x

    def origin_X(self):
        return self.input_data

    def tick(self):
        self.data.append(copy.deepcopy(self.input_data))


class probeTimeNode(nef.SimpleNode):
    def __init__(self, name = "@t"):
        self.data = [0]
        nef.SimpleNode.__init__(self, name)

    def reset(self, randomize = False):
        self.data = [0]

    def tick(self):
        self.data.append(self.t_start)


class Model(nef.Network):
    def __init__(self, name):
        nef.Network.__init__(self, name)
        self.t = "@t"
        self.probes = {}
        self.add_to_nengo()

    def make_ensemble(self, name, num_neurons, num_dim, max_rate):
        return self.make(name, num_neurons, num_dim, max_rate = max_rate)

    def make_node(self, name, func):
        return self.make_input(name, func)

    def probe(self, name, filter = 0.001):
        node = self.get(name)
        sim = self.network.simulator
        
        if( isinstance(node, FunctionInput) ):
            self.probes[name] = {'probe': sim.addProbe(name, "input", True), 'filter': filter}
        else:
            self.probes[name] = {'probe': sim.addProbe(name, "X", True), 'filter': filter}

    def simulator(self, dt, **kwargs):
        time_function = FunctionInput(self.t, [PostfixFunction("x0",1)], Units.UNK)
        self.add(time_function)
        self.probes[self.t] = self.network.simulator.addProbe(self.t, "input", True)
        return Simulator(self.probes, self.network.simulator, dt, self.t)
    
    def connect(self, pre, post, **kwargs):
        if 'filter' in kwargs.keys():
            kwargs['pstc'] = kwargs['filter']
            del kwargs['filter']
        return nef.Network.connect(self, pre, post, **kwargs)
