from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.impl import NetworkArrayImpl, NetworkImpl

import nef

from timeview import components
from timeview.watches import watchtemplate

class HRRWatch(watchtemplate.WatchTemplate):
    def check(self, obj):
        if(isinstance(obj, NEFEnsemble) or isinstance(obj, NetworkArrayImpl)):
            return (obj.dimension >= 8)
        elif(isinstance(obj, nef.SimpleNode) or isinstance(obj, NetworkImpl)):
            try:
                return (obj.dimension >= 8 and obj.getOrigin("X"))
            except:
                return False

    def views(self, obj):
        return [
            ('semantic pointer', components.HRRGraph, dict(func=nodeWatch.value, args='X', label=obj.name, nodeid=id(obj))),
        ]