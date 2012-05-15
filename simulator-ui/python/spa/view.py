from timeview.components import TextList
import timeview.view

class RuleWatch:
    def __init__(self):
        self.objs={}
    def check(self,obj):
        return obj in self.objs.keys()
    def measure(self,obj):
        return obj.getNode('rules').getOrigin('X').getValues().getValues()
    def views(self,obj):
        return [('rule activation',TextList,dict(func=self.measure,label="Rules",names=self.objs[obj],show_values=False,ignore_filter=True))]
    def add(self,obj,names):
        self.objs[obj]=names

class UtilityWatch:
    def __init__(self):
        self.objs={}
    def check(self,obj):
        return obj in self.objs.keys()
    def measure(self,obj):
        return obj.getNode('StrD2').getOrigin('X').getValues().getValues()
    def views(self,obj):
        return [('rule utility',TextList,dict(func=self.measure,label="Utility",names=self.objs[obj],show_values=True,ignore_filter=False))]
    def add(self,obj,names):
        self.objs[obj]=names

rule_watch=RuleWatch()
timeview.view.watches.append(rule_watch)
utility_watch=UtilityWatch()
timeview.view.watches.append(utility_watch)



