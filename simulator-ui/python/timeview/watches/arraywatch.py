from ca.nengo.model.impl import NetworkArrayImpl

from timeview import components
from timeview.watches import watchtemplate

class ArrayWatch(watchtemplate.WatchTemplate):
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