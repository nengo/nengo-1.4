import core

class DrawImage(core.DataViewComponent):
    def __init__(self, view, name, func, args=(), label=None):
        core.DataViewComponent.__init__(self, label)
        self.view = view
        self.name = name
        self.func = func
        self.args = args
        self.data = self.view.watcher.watch(name, func, args=args)
        self.margin = 0
        self.setSize(200, 200)
        self.image = None

    def tick(self, t):
        try:
            self.image = self.data.get(start=self.view.current_tick, count=1)[0][0]
        except Exception,e:
            print "error in get",e
            return

    def paintComponent(self, g):
        core.DataViewComponent.paintComponent(self, g)

        if self.image is not None:
            g.drawImage(self.image, 0, 0, self.size.width, self.size.height,
                        0, 0, self.image.getWidth(), self.image.getHeight(), None)
