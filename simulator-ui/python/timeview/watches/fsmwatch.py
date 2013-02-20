import nef

from timeview import components
from timeview.watches import watchtemplate

class FSMWatch(watchtemplate.WatchTemplate):
    def check(self, obj):
        return isinstance(obj, nef.FSMNode)

    def measure(self, obj):
        out = [0.0] * len(obj.states)
        if obj.state is not None:
            out[sorted(obj.states.keys()).index(obj.state.name)] = 1.0
        return out

    def views(self, obj):
        return [('state viewer', components.TextList, dict(func=self.measure, label="States", names=sorted(obj.states.keys()), show_values=False, ignore_filter=True))]