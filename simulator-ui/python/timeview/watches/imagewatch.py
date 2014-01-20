from timeview.watches.watchtemplate import WatchTemplate
from timeview import components

class ImageWatch(WatchTemplate):
    def check(self, obj):
        return hasattr(obj, "get_image")
    
    def display_image(self, obj):
        return [obj.get_image()]
        
    def views(self, obj):
        r = [("display image", components.DrawImage, dict(func=self.display_image, label=obj.name))]
        return r