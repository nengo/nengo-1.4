from timeview.components import TextList
from timeview.watches.watchtemplate import WatchTemplate
import timeview.view


class RuleWatch(WatchTemplate):
    def __init__(self):
        pass
    def check(self,obj):
        # Check the object documentation for the special THAL string that indicates the network is a BG type network.
        # If it is, retrieve the rule information from the documentation string
        return not obj.documentation is None and obj.documentation.startswith('THAL: ')
    def measure(self,obj):
        return obj.getNode('rules').getOrigin('X').getValues().getValues()
    def views(self,obj):
        names = obj.documentation[6:].split(',')
        return [('rule activation',TextList,dict(func=self.measure,label="Rules",names=names,show_values=False,ignore_filter=True))]