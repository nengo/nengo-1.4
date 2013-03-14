D=20        #Number of dimensions
N_input=300 #Number of neurons in populations
N_conv=70   #Number of neurons per D in bind population

import nef
import nef.convolution
import hrr
import math
import random

vocab=hrr.Vocabulary(D,max_similarity=0.1)

net=nef.Network('Question Answering (pre-built)',seed=7)
net.make('A',N_input,D)
net.make('B',N_input,D)
net.make('C',N_input,D)
net.make('D',N_input,D)
net.make('E',N_input,D)

nef.convolution.make_convolution(net, 'Bind', 'A', 'B', 'D', N_conv, invert_first=False, invert_second=False)
nef.convolution.make_convolution(net, 'Unbind', 'C', 'D', 'E', N_conv, invert_first=True, invert_second=False)

CIRCLE=vocab.parse('CIRCLE').v
BLUE=vocab.parse('BLUE').v
RED=vocab.parse('RED').v
SQUARE=vocab.parse('SQUARE').v
ZERO=[0]*D


# Create the inputs
inputA={}
inputA[0.0]=RED
inputA[0.5]=BLUE
inputA[1.0]=RED
inputA[1.5]=BLUE
inputA[2.0]=RED
inputA[2.5]=BLUE
inputA[3.0]=RED
inputA[3.5]=BLUE
inputA[4.0]=RED
inputA[4.5]=BLUE
net.make_input('inputA',inputA)
net.connect('inputA','A')

inputB={}
inputB[0.0]=CIRCLE
inputB[0.5]=SQUARE
inputB[1.0]=CIRCLE
inputB[1.5]=SQUARE
inputB[2.0]=CIRCLE
inputB[2.5]=SQUARE
inputB[3.0]=CIRCLE
inputB[3.5]=SQUARE
inputB[4.0]=CIRCLE
inputB[4.5]=SQUARE
net.make_input('inputB',inputB)
net.connect('inputB','B')


inputC={}
inputC[0.0]=ZERO
inputC[0.2]=CIRCLE
inputC[0.35]=RED
inputC[0.5]=ZERO
inputC[0.7]=SQUARE
inputC[0.85]=BLUE
inputC[1.0]=ZERO
inputC[1.2]=CIRCLE
inputC[1.35]=RED
inputC[1.5]=ZERO
inputC[1.7]=SQUARE
inputC[1.85]=BLUE
inputC[2.0]=ZERO
inputC[2.2]=CIRCLE
inputC[2.35]=RED
inputC[2.5]=ZERO
inputC[2.7]=SQUARE
inputC[2.85]=BLUE
inputC[3.0]=ZERO
inputC[3.2]=CIRCLE
inputC[3.35]=RED
inputC[3.5]=ZERO
inputC[3.7]=SQUARE
inputC[3.85]=BLUE
inputC[4.0]=ZERO
inputC[4.2]=CIRCLE
inputC[4.35]=RED
inputC[4.5]=ZERO
inputC[4.7]=SQUARE
inputC[4.85]=BLUE

net.make_input('inputC',inputC)
net.connect('inputC','C')

net.add_to_nengo()

