Circular Convolution
============================

import nef
import nef.convolution
import hrr

D=10

vocab=hrr.Vocabulary(D, include_pairs=True)
vocab.parse('a+b+c+d+e')

net=nef.Network('Convolution')
A=net.make('A',300,D,quick=True)
B=net.make('B',300,D,quick=True)
C=net.make('C',300,D,quick=True)
conv=nef.convolution.make_convolution(net,'*',A,B,C,100,quick=True)

net.add_to(world)




