N=60
D=1

import nef
import nef.templates.learned_termination as learning
import nef.templates.gate as gating

from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units


net=nef.Network('Learning')

# Create input and output populations.
A=net.make('pre',N,D)
B=net.make('post',N,D)

# Create a random function input.
input=FunctionInput('input',[FourierFunction(.1, 10,.5,12)],Units.UNK)
net.add(input)
net.connect(input,A)

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(net,errName='error', N_err=100, preName='pre', postName='post', rate=5e-7)

# Set the modulatory signal.
net.connect('pre', 'error')
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch',[0])
gating.make(net,name='Gate', gated='error', neurons=40 ,pstc=0.01)
net.connect('switch', 'Gate')

# Add another non-gated error population running in direct mode.
actual = net.make('actual error', 1, 1, mode='direct')
net.connect(A,actual)
net.connect(B,actual,weight=-1)

net.add_to(world)



