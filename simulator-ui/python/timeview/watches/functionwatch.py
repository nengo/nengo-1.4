from ca.nengo.model.impl import FunctionInput

from timeview import components
from timeview.watches import watchtemplate

class FunctionWatch(watchtemplate.WatchTemplate):
    def check(self, obj):
        return isinstance(obj, FunctionInput)

    def funcOrigin(self, obj):
        return obj.getOrigin('origin').getValues().values

    def views(self, obj):
        return [
            ('control', components.FunctionControl, dict(func=self.funcOrigin, label=obj.name)),
        ]