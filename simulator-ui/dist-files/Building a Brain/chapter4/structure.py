import nef
import nef.convolution
import hrr

D=20

vocab=hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

# Binding
net=nef.Network('Structured Representation (pre-built)',seed=1)
net.make('A',300,D)
net.make('B',300,D)
net.make('C',300,D)
conv=nef.convolution.make_convolution(net,'Bind','A','B','C',70)

# Adding
net.make('Sum', 300,D)
net.connect('A', 'Sum')
net.connect('B', 'Sum')

net.add_to_nengo()


