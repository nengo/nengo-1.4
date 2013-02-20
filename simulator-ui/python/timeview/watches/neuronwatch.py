from ca.nengo.model.neuron.impl import SpikingNeuron

from timeview import components
from timeview.watches import watchtemplate

class NeuronWatch(watchtemplate.WatchTemplate):
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