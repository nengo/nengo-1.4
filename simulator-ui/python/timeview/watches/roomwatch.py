import space
import ccm.nengo

from timeview import components
from timeview.watches import watchtemplate

class RoomWatch(watchtemplate.WatchTemplate):
    def check(self, obj):
        if isinstance(obj, ccm.nengo.CCMModelNetwork):
            if isinstance(obj._simulator.model, space.Room):
                return True
        return False

    def physics(self, obj):
        return obj._simulator.model.physics_dump()

    def views(self, obj):
        return [
            ('3D view', components.View3D, dict(func=self.physics)),
        ]