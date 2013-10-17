import copy
import nef
from .simulator import Simulator
from ca.nengo.model.impl import FunctionInput

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
        if( isinstance(node, FunctionInput) ):
            dim = len(node.functions)
        else:
            dim = node.dimension
        self.probes[name] = probeSimpleNode(name+"_probe", dim, pstc = filter)
        self.add(self.probes[name])
        self.connect(name, self.probes[name].getTermination("Input"))

    def simulator(self, dt):
        self.probes[self.t] = probeTimeNode(self.t)
        self.add(self.probes[self.t])
        return Simulator(self.probes, self.network.simulator, dt)
    
    def connect(self, pre, post, **kwargs):
        if 'filter' in kwargs.keys():
            kwargs['pstc'] = kwargs['filter']
            del kwargs['filter']
        return nef.Network.connect(self, pre, post, **kwargs)
