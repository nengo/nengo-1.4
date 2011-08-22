N=60
D=2

import nef
import nef.templates.learned_termination as learning
import nef.templates.gate as gating
import random

from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units

random.seed(27)

net=nef.Network('Learn Square')

# Create input and output populations.
A=net.make('pre',N,D)
B=net.make('post',N,D)

# Create a random function input.
input=FunctionInput('input',[FourierFunction(.1, 8,.4,i,0) for i in range(D)],Units.UNK)
net.add(input)
net.connect(input,A)

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(net,errName='error', N_err=100, preName='pre', postName='post', rate=5e-7)

# Set the modulatory signal.
def square(x):
    return [xx*xx for xx in x]

net.connect('pre', 'error', func=square)
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch',[0])
gating.make(net,name='Gate', gated='error', neurons=40 ,pstc=0.01)
net.connect('switch', 'Gate')

net.add_to_nengo()

