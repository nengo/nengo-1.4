import plotlib.pyplot as plt
import math
import numeric as np

class Simulator:
    def __init__(self, probes, simulator, dt, t_str):
        self.probes = probes
        self.simulator = simulator
        self.dt = dt
        self.t_str = t_str

    def run(self, runtime):
        plt.reset()
        self.simulator.run(0, runtime, self.dt, False)

    def data(self, name):
        if( name == self.t_str ):
            d = self.probes[name].getData().getTimes()
        else:
            probe = self.probes[name]["probe"]
            tau   = self.probes[name]["filter"]
            d = self.filter(probe.getData().getValues(), tau)
        return d
    
    def filter(self, data, tau):
        if tau is None or tau < self.dt:
            return data
        else:
            d = []
            decay = math.exp(-self.dt/tau)
            v = [0] * (len(data[0]))
            for dd in data:
                v = [v[j] * decay + dd[j] * (1-decay) for j in range(len(dd))]
                d.append(v)
            return d
