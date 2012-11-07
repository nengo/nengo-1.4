D=20
N_input=300
N_mem=50
N_conv=70

import nef
import nef.convolution
import hrr
import math
import random

vocab=hrr.Vocabulary(D,max_similarity=0.1)

net=nef.Network('Question Answering with Memory (pre-built)',seed=11)
net.make('A',N_input,D)
net.make('B',N_input,D)
net.make('C',N_input,D)
net.make('D',N_input,D)
net.make('E',N_input,D)

net.make_array('Memory',N_mem,D,radius=1.0/math.sqrt(D),storage_code='%d')  #This is the same as constructing the memory using the integrator template (400 neurons over 20 dimensions).

conv1=nef.convolution.make_convolution(net,'Bind','A','B','D',N_conv)
conv2=nef.convolution.make_convolution(net,'Unbind','Memory','C','E',N_conv,invert_second=True)

net.connect('Memory','Memory',pstc=0.4)

CIRCLE=vocab.parse('CIRCLE')
BLUE=vocab.parse('BLUE')
RED=vocab.parse('RED')
SQUARE=vocab.parse('SQUARE')
ZERO=[0]*D

class Input(nef.SimpleNode):
  def origin_A(self):
    t=(self.t_start)
    if 0<t<0.25: return RED.v
    if 0.25<t<0.5: return BLUE.v
    return ZERO
  def origin_B(self):
    t=(self.t_start)
    if 0.0<t<0.25: return CIRCLE.v
    if 0.25<t<0.5: return SQUARE.v
    return ZERO
  def origin_C(self):
    t=self.t_start
    if t<0.5: return ZERO
    t=t%0.5
    if 0.0<t<0.1: return CIRCLE.v
    if 0.1<t<0.2: return RED.v
    if 0.2<t<0.3: return SQUARE.v
    if 0.3<t<0.4: return BLUE.v
    return ZERO

input=Input('input')
net.add(input)
net.connect(input.getOrigin('A'),'A')
net.connect(input.getOrigin('B'),'B')
net.connect(input.getOrigin('C'),'C')
net.connect('D', 'Memory')

net.add_to_nengo()

#net.view(play=1.7)
