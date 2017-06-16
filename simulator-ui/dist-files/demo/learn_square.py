import random

import nef
import nef.templates.gate as gating
import nef.templates.learned_termination as learning

N = 60
D = 2

random.seed(27)

# Create the network object
net = nef.Network('Learn square')

# Create input and output populations.
net.make('pre', N, D)
net.make('post', N, D)

# Create a random white noise input function with
# .1 base freq, max freq 8 rad/s, RMS of .4, and random number seed 0
net.make_fourier_input(
    'input', dimensions=D, base=0.1, high=8, power=0.4, seed=0)

net.connect('input', 'pre')

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(
    net, errName='error', N_err=100, preName='pre', postName='post', rate=5e-4)

# Set the modulatory signal to compute the desired function
def square(x):
    return [xx * xx for xx in x]

net.connect('pre', 'error', func=square)
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch', [0])
gating.make(net, name='Gate', gated='error', neurons=40, pstc=0.01)
net.connect('switch', 'Gate')

net.add_to_nengo()
