D=20
N_input=300
N_conv=70
mySeed=7

import nef
import nef.convolution
import hrr
import math
import random

vocab=hrr.Vocabulary(D,max_similarity=0.1)

net=nef.Network('Question Answering (pre-built)')
net.make('A',N_input,D)
net.make('B',N_input,D)
net.make('C',N_input,D)
net.make('ens_D',N_input,D)
net.make('E',N_input,D)

nef.convolution.make_convolution(net, 'Bind', 'A', 'B', 'ens_D', N_conv, invert_first=False, invert_second=False)
nef.convolution.make_convolution(net, 'Unbind', 'C', 'ens_D', 'E', N_conv, invert_first=True, invert_second=False)

CIRCLE=vocab.parse('CIRCLE')
BLUE=vocab.parse('BLUE')
RED=vocab.parse('RED')
SQUARE=vocab.parse('SQUARE')
ZERO=[0]*D

class Input(nef.SimpleNode):
  def origin_A(self):
    t=(self.t_start)%1.0
    if 0<t<0.5: return RED.v
    if 0.5<t<1: return BLUE.v
    return ZERO
  def origin_B(self):
    t=(self.t_start)%1.0
    if 0.0<t<0.5: return CIRCLE.v
    if 0.5<t<1: return SQUARE.v
    return ZERO
  def origin_C(self):
    t=(self.t_start)%1.0
    if 0.2<t<0.35: return CIRCLE.v
    if 0.35<t<0.5: return RED.v
    if 0.7<t<0.85: return SQUARE.v
    if 0.85<t<1: return BLUE.v
    return ZERO



input=Input('input')
net.add(input)
net.connect(input.getOrigin('A'),'A')
net.connect(input.getOrigin('B'),'B')
net.connect(input.getOrigin('C'),'C')


net.add_to_nengo()

