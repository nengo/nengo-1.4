
import nef
import ccm.lib.actr as actr
import spa2

class DMNode(nef.Node):
    def __init__(self, name, dimensions, vocab):
        nef.Node.__init__(self, name)
        self.vocab = vocab
        self.request = self.make_input('request', dimensions=dimensions, pstc=0.01)
        self.recall = self.make_output('recall', dimensions=dimensions)
        
        self.buffer = actr.Buffer()
        self.dm = actr.Memory(self.buffer)
        self.requesting = None
        
    def tick(self):
        v = self.request.get()
        
        threshold = 0.5
        m = self.vocab.dot_pairs(v)
        
        chunk = []
        for i,k in enumerate(self.vocab.key_pairs):
            if m[i]>threshold:
                chunk.append(k.replace('*',':'))
        chunk = ' '.join(chunk)
        
        if chunk != '':
            if chunk != self.requesting:
                self.dm.request(chunk)
                self.requesting = chunk
    
        self.dm.run(limit=0.001)
        
        if self.buffer.chunk is None:
            items = []
            self.recall.set([0]*self.vocab.dimensions)
        else:
            items = []
            for k in self.buffer.chunk.items():
                items.append('%s*%s'%k)
            self.recall.set(self.vocab.parse('+'.join(items)).v)    
        

class ACTRDM(spa2.Module):
    def __init__(self):
        spa2.Module.__init__(self)
        self.chunks = []
    
    def init(self, dimensions):
        self.net.make('input', 1, dimensions, mode='direct')
        
        self.net.make('output', 1, dimensions, mode='direct')

        self.spa.add_sink(self, 'input')
        self.spa.add_source(self, 'output')
        
        self.dm_node = self.net.add(DMNode('DM', dimensions, self.spa.sources[self.name]))
        for chunk in self.chunks:
            self.dm_node.dm.add(chunk)    
        
        self.net.connect(self.dm_node.getOrigin('recall'), 'output')
        self.net.connect('input', self.dm_node.getTermination('request'))

        
    def add(self, chunk):
        self.chunks.append(chunk)

