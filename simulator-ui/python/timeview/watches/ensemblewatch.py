from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model import SimulationMode

from timeview import components
from timeview.watches import watchtemplate

class EnsembleWatch(watchtemplate.WatchTemplate):
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
    
    def priority(self):
        return 1