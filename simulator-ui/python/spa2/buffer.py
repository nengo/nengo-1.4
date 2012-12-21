import module

class Buffer(module.Module):
    def init(self, dimensions=16):
        self.net.make('buffer', 200, dimensions)
        self.spa.add_sink(self, 'buffer')
