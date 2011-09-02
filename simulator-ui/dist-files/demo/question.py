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

net=nef.Network('Question Answering') #Create the network object
A=net.make('A',1,D,mode='direct') #Make some pseudo populations (so they 
                                  #run well on less powerful machines): 
                                  #1 neuron, 16 dimensions, direct mode
B=net.make('B',1,D,mode='direct')
C=net.make_array('C',N,D/subdim,dimensions=subdim,quick=True,radius=1.0/math.sqrt(D),storage_code='%d') #Make a real population, with 100 neurons per 
                           #array element and D/subdim elements in the array
                           #each with subdim dimensions, set the radius as
                           #appropriate for multiplying things of this 
                           #dimension
E=net.make('E',1,D,mode='direct')
F=net.make('F',1,D,mode='direct')

conv1=nef.convolution.make_convolution(net,'*',A,B,C,N,
    quick=True) #Make a convolution network using the construct populations
conv2=nef.convolution.make_convolution(net,'/',C,E,F,N,
    invert_second=True,quick=True) #Make a 'correlation' network (by using
                                   #convolution, but inverting the second 
                                   #input)

CIRCLE=vocab.parse('CIRCLE') #Add elements to the vocabulary to use
BLUE=vocab.parse('BLUE')
RED=vocab.parse('RED')
SQUARE=vocab.parse('SQUARE')
ZERO=[0]*D

class Input(nef.SimpleNode): #Make a simple node to generate 
                             #interesting input for the network
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
net.connect(input.getOrigin('A'),A) #Connect the origins in the simple node 
                                    #to the populations they are input to
net.connect(input.getOrigin('B'),B)
net.connect(input.getOrigin('E'),E)


net.add_to_nengo()

