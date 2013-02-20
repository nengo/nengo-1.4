from timeview.components import TextList
from timeview.watches.watchtemplate import WatchTemplate
import timeview.view

class UtilityWatch(WatchTemplate):
    def __init__(self):
        pass
    def check(self,obj):
        # Check the object documentation for the special BG string that indicates the network is a BG type network.
        # If it is, retrieve the rule information from the documentation string
        return not obj.documentation is None and obj.documentation.startswith('BG: ')
    def measure(self,obj):
        return obj.getNode('StrD2').getOrigin('X').getValues().getValues()
    def views(self,obj):
        names = obj.documentation[4:].split(',')
        return [('rule utility',TextList,dict(func=self.measure,label="Utility",names=names,show_values=True,ignore_filter=False))]