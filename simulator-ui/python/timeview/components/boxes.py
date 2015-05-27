from java.awt import Color

import core


class Boxes(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.args = args
        self.data = self.view.watcher.watch(name, func, args=args)
        self.margin = 0
        self.setSize(200, 200)
        self.boxes = None

    def tick(self, t):
        try:
            self.boxes = self.data.get(start=self.view.current_tick, count=1)[0]
        except Exception,e:
            print "error in get",e
            return

        self.minx = 1000
        self.maxx = -1000
        self.miny = 1000
        self.maxy = -1000

        self.convert_coords()

        for b in self.boxes:
            x0,y0 = b[1]
            x1,y1 = b[2]

            if x0 < self.minx:
                self.minx = x0
            if x1 > self.maxx:
                self.maxx = x1
            if y0 < self.miny:
                self.miny = y0
            if y1 > self.maxy:
                self.maxy = y1

        self.xscale = self.size.width / (self.maxx - self.minx)
        self.yscale = self.size.height / (self.maxy - self.miny)

#        print (self.maxx-self.minx)*self.xscale,(self.maxy-self.miny)*self.yscale
#        print self.minx,self.maxx,self.xscale
#        print self.miny,self.maxy,self.yscale

    def convert_coords(self):
        #convert euclidian points to drawing points
        minx = min([b[i][0] for b in self.boxes for i in range(1,2)])
        miny = min([-b[i][1] for b in self.boxes for i in range(1,2)])

        def conv(pt):
            x,y = pt
            return (x-minx,-y-miny)

        self.boxes = [[b[0],conv(b[1]),conv(b[2])] for b in self.boxes]

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        if self.boxes is None:
            return

        for b in self.boxes:
            if b[0] == "-":
                g.color = Color.black
            else:
                g.color = b[0]
            x0 = b[1][0]
            y0 = b[1][1]
            x_len = b[2][0] - b[1][0]
            y_len = b[2][1] - b[1][1]
            if b[0] == "-":
                g.drawLine(int(x0*self.xscale), int(y0*self.yscale), int(b[2][0]*self.xscale), int(b[2][1]*self.yscale))
            else:
                g.fillRect(int(x0*self.xscale), int(y0*self.yscale), int(x_len*self.xscale), int(y_len*self.yscale))
