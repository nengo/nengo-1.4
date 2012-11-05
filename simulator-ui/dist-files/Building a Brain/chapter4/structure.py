import nef
import nef.convolution
import hrr

D=30

vocab=hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

# Binding
net=nef.Network('Structured Representation (pre-built)',seed=1)
net.make('A',300,D)
net.make('B',300,D)
net.make('C',300,D)
conv=nef.convolution.make_convolution(net,'Bind','A','B','C',300)

# Adding
net.make('add', 300,D)
net.make('D',300,D)
net.connect('A', 'add')
net.connect('B', 'add')
net.connect('add', 'D')

net.add_to_nengo()


