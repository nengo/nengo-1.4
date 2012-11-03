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

net=nef.Network('Question Answering with Memory') #Create the network object
net.make('A',1,D,mode='direct') #Make some pseudo populations (so they run 
                                  #well on less powerful machines): 1 neuron, 
                                  #16 dimensions, direct mode
net.make('B',1,D,mode='direct')
net.make_array('C',N,D/subdim,dimensions=subdim,quick=True,radius=1.0/math.sqrt(D)) 
                           #Make a real population, with 100 neurons per 
                           #array element and D/subdim elements in the array
                           #each with subdim dimensions, set the radius as
                           #appropriate for multiplying things of this 
                           #dimension
net.make('E',1,D,mode='direct')
net.make('F',1,D,mode='direct')

conv1=nef.convolution.make_convolution(net,'*','A','B','C',N,
    quick=True) #Make a convolution network using the construct populations
conv2=nef.convolution.make_convolution(net,'/','C','E','F',N,
    invert_second=True,quick=True) #Make a 'correlation' network (by using 
                                #convolution, but inverting the second input)

net.connect('C','C',pstc=0.4) # Recurrently connect C so it acts as a memory

CIRCLE=vocab.parse('CIRCLE')  #Create a vocabulary
BLUE=vocab.parse('BLUE')
RED=vocab.parse('RED')
SQUARE=vocab.parse('SQUARE')
ZERO=[0]*D

class Input(nef.SimpleNode): #Make a simple node to generate interesting 
                             #input for the network
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
  def origin_E(self):
    t=self.t_start
    if t<0.5: return ZERO
    t=t%0.5
    if 0.0<t<0.1: return CIRCLE.v
    if 0.1<t<0.2: return RED.v
    if 0.2<t<0.3: return SQUARE.v
    if 0.3<t<0.4: return BLUE.v
    return ZERO

Input('input')
net.add('input')
net.connect(input.getOrigin('A'),'A') #Connect the origins in the simple node 
                                    #to the populations they are input to
net.connect(input.getOrigin('B'),'B')
net.connect(input.getOrigin('E'),'E')

net.add_to_nengo()

