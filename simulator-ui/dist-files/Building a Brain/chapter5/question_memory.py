D=20    #Number of dimensions
N_input=300 #Number of neurons in populations
N_mem=50 # Number of neurons per dimension in memory popoulation
N_conv=70 #Number of neurons per D in bind population

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

#Create input to model
CIRCLE=vocab.parse('CIRCLE').v 
BLUE=vocab.parse('BLUE').v
RED=vocab.parse('RED').v
SQUARE=vocab.parse('SQUARE').v
ZERO=[0]*D

inputA={}
inputA[0]=RED
inputA[0.25]=BLUE
inputA[0.5]=ZERO
net.make_input('inputA',inputA)
net.connect('inputA','A')

inputB={}
inputB[0]=CIRCLE
inputB[0.25]=SQUARE
inputB[0.5]=ZERO
net.make_input('inputB',inputB)
net.connect('inputB','B')

inputC={}
inputC[0]=ZERO
inputC[0.5]=CIRCLE
inputC[0.6]=RED
inputC[0.7]=SQUARE
inputC[0.8]=BLUE
inputC[0.9]=ZERO
inputC[1.0]=CIRCLE
inputC[1.1]=RED
inputC[1.2]=SQUARE
inputC[1.3]=BLUE
inputC[1.4]=ZERO
inputC[1.5]=CIRCLE
inputC[1.6]=RED
inputC[1.7]=SQUARE
inputC[1.8]=BLUE
inputC[1.9]=ZERO
inputC[2.0]=CIRCLE
inputC[2.1]=RED
inputC[2.2]=SQUARE
inputC[2.3]=BLUE
inputC[2.4]=ZERO
inputC[2.5]=CIRCLE
inputC[2.6]=RED
inputC[2.7]=SQUARE
inputC[2.8]=BLUE
inputC[2.9]=ZERO
inputC[3.0]=CIRCLE
inputC[3.1]=RED
inputC[3.2]=SQUARE
inputC[3.3]=BLUE
inputC[3.4]=ZERO
inputC[3.5]=CIRCLE
inputC[3.6]=RED
inputC[3.7]=SQUARE
inputC[3.8]=BLUE
inputC[3.9]=ZERO
inputC[4.0]=CIRCLE
inputC[4.1]=RED
inputC[4.2]=SQUARE
inputC[4.3]=BLUE
inputC[4.4]=ZERO
inputC[4.5]=CIRCLE
inputC[4.6]=RED
inputC[4.7]=SQUARE
inputC[4.8]=BLUE
inputC[4.9]=ZERO

net.make_input('inputC',inputC)
net.connect('inputC','C')

net.connect('D', 'Memory')

net.add_to_nengo()

#net.view(play=1.7)
