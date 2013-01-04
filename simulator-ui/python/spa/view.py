from timeview.components import TextList
import timeview.view

class RuleWatch:
    def __init__(self):
        pass
    def check(self,obj):
        # Check the object documentation for the special THAL string that indicates the network is a BG type network.
        # If it is, retrieve the rule information from the documetation string
        return not obj.documentation is None and obj.documentation.startswith('THAL: ')
    def measure(self,obj):
        return obj.getNode('rules').getOrigin('X').getValues().getValues()
    def views(self,obj):
        names = obj.documentation[6:].split(',')
        return [('rule activation',TextList,dict(func=self.measure,label="Rules",names=names,show_values=False,ignore_filter=True))]


class UtilityWatch:
    def __init__(self):
        pass
    def check(self,obj):
        # Check the object documentation for the special BG string that indicates the network is a BG type network.
        # If it is, retrieve the rule information from the documetation string
        return not obj.documentation is None and obj.documentation.startswith('BG: ')
    def measure(self,obj):
        return obj.getNode('StrD2').getOrigin('X').getValues().getValues()
    def views(self,obj):
        names = obj.documentation[4:].split(',')
        return [('rule utility',TextList,dict(func=self.measure,label="Utility",names=names,show_values=True,ignore_filter=False))]


## Note:
#  We must be careful when using global variables like this. Because these watch objects are global, if items are ADDED to the watch
#  items (i.e. the watch class has an internal variable storing stuff), then it will cause it to grow unbounded when multiple iterations of the
#  same code are executed.
#
#  In this scenario, instead of keeping an association between an object type and the data that needs to be stored (as was the previous implementation)
#  the object type and data to be stored is appended to the object documentation. Thus, instead of need to keep all this data around, all that needs
#  to be done is to check for the special object string in the object documentation - avoids keeping data around, avoids memory leaks.

rule_watch=RuleWatch()
timeview.view.watches.append(rule_watch)
utility_watch=UtilityWatch()
timeview.view.watches.append(utility_watch)



