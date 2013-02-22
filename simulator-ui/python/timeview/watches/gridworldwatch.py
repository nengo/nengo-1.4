from java.awt import Color

from timeview.watches.watchtemplate import WatchTemplate
from timeview import components

from environment import gridworldenvironment

class GridWorldWatch(WatchTemplate):
    def check(self, obj):
        return isinstance(obj, gridworldenvironment.GridWorldEnvironment)
    
    def display_grid(self, obj):
        return [self.color_translation(c) for c in str(obj) if c != "\n"] 
    
    def color_translation(self, data):
        if data == ".":
            return Color.black
        if data == " ":
            return Color.white
        if data == "x" or data == "X":
            return Color.yellow
        if data == "a":
            return Color.orange
        return Color.black
    
    def display_Qs(self, obj):
        """This function assumes that there are four actions (generally, representing
        movement in the four cardinal directions."""
        Qs = obj.getQs()
        data = [[Qs.get((j,i)) for j in range(len(obj.grid[i]))] for i in range(len(obj.grid))]
        
#        print len(Qs.values())
        if(len(Qs.values()) == 0):
            maxval = 0
            minval = 0
        else:
            qlist = [max(vs) for vs in Qs.values()]
            maxval = max(qlist) #the largest of the max action values for each states
            minval = min(qlist) #the smallest of the max action values for each state
            
#        print minval,maxval
        
        result = []
        for y in range(len(obj.grid)):
            for x in range(len(obj.grid[y])):
                try:
                    qvals = Qs[(x,y)]
                except:
                    result += [(0.0,0.0,0.0)]
                    continue
                
                val = max(qvals)
                a = qvals.index(val)
                
                #shift the value into the range 0--1
                val -= minval
                if maxval != minval:
                    val /= maxval-minval
                else:
                    val = 0.0
                
#                print val
        
                if a == 0:
                    result += [(val,val,val)]
                elif a == 1:
                    result += [(0.0,0.0,val)]
                elif a == 2:
                    result += [(val,0.0,0.0)]
                elif a == 3:
                    result += [(0.0,val,0.0)]
        return result
        
    def views(self, obj):
        r = [
             ("display grid", components.ColorGrid, dict(func=self.display_grid, rows=len(obj.grid), label=obj.name))
             ]
        if obj.num_actions == 4:
            r += [
                 ("display Qs", components.ColorGrid, dict(func=self.display_Qs, rows=len(obj.grid), label=obj.name+" Qs"))
                 ]
        return r