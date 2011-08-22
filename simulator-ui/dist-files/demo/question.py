D=16
subdim=4
N=100
seed=7

import nef
import nef.convolution
import hrr
import math
import random

random.seed(seed)

vocab=hrr.Vocabulary(D,max_similarity=0.1)

net=nef.Network('Question Answering')
A=net.make('A',1,D,mode='direct')
B=net.make('B',1,D,mode='direct')
C=net.make_array('C',N,D/subdim,dimensions=subdim,quick=True,radius=1.0/math.sqrt(D),storage_code='%d')
E=net.make('E',1,D,mode='direct')
F=net.make('F',1,D,mode='direct')

conv1=nef.convolution.make_convolution(net,'*',A,B,C,N,quick=True)
conv2=nef.convolution.make_convolution(net,'/',C,E,F,N,invert_second=True,quick=True)

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
  def origin_E(self):
    t=(self.t_start)%1.0
    if 0.2<t<0.35: return CIRCLE.v
    if 0.35<t<0.5: return RED.v
    if 0.7<t<0.85: return SQUARE.v
    if 0.85<t<1: return BLUE.v
    return ZERO



input=Input('input')
net.add(input)
net.connect(input.getOrigin('A'),A)
net.connect(input.getOrigin('B'),B)
net.connect(input.getOrigin('E'),E)


net.add_to(world)

