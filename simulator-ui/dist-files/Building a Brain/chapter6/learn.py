N=50
D=1

import nef
import nef.templates.learned_termination as learning
import nef.templates.gate as gating
import random

from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units

random.seed(27)

net=nef.Network('Learning (pre-built)')

# Create input and output populations.
net.make('pre',N,D)
net.make('post',N,D)

# Create a random function input.
input=FunctionInput('input',[FourierFunction(.1, 10,.5,1)],Units.UNK)
net.add(input)
net.connect(input,'pre')

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(net,errName='error', N_err=100, preName='pre', postName='post', rate=5e-7)

# Set the modulatory signal.
net.connect('pre', 'error')
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch',[0])
gating.make(net,name='Gate', gated='error', neurons=100 ,pstc=0.01)
net.connect('switch', 'Gate')

# Add another non-gated error population running in direct mode.
actual = net.make('actual error', 1, 1, mode='direct')
net.connect('pre','actual error')
net.connect('post','actual error',weight=-1)

def square(x):
    return x[0]*x[0]
# Make an origin on pre that computes the square, to generate
# a different error signal
net.connect('pre',None,func=square, create_projection=False)

net.add_to_nengo()



