import plotlib.pyplot as plt

class Simulator:
    def __init__(self, probes, simulator, dt):
        self.probes = probes
        self.simulator = simulator
        self.dt = dt

    def run(self, runtime):
        plt.reset()
        self.simulator.run(0, runtime, self.dt, False)

    def data(self, name):
        return self.probes[name].data


