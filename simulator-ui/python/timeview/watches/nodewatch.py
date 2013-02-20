from javax.swing import JMenu

from ca.nengo.model import Node, SimulationMode
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.util import MU
from ca.nengo.model.plasticity.impl import PlasticEnsembleTermination

from timeview import components
from timeview.watches import watchtemplate

class NodeWatch(watchtemplate.WatchTemplate):
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