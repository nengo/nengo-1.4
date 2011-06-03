import nef
import nef.convolution
import hrr

D=30

vocab=hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

# Binding
net=nef.Network('Structured Representation')
A=net.make('A',300,D, quick=True)
B=net.make('B',300,D, quick=True)
C=net.make('C',300,D, quick=True)
conv=nef.convolution.make_convolution(net,'Bind',A,B,C,300, quick=True)

# Adding
add = net.make('Add', 300, D, quick=True)
D=net.make('D',300,D, quick=True)
net.connect(A, add)
net.connect(B, add)
net.connect(add, D)

net.add_to(world)


