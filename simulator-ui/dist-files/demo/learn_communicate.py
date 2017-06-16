import random

import nef
import nef.templates.gate as gating
import nef.templates.learned_termination as learning

N = 60
D = 1

random.seed(27)

# Create the network object
net = nef.Network('Learn communication')

# Create input and output populations.
net.make('pre', N, D)
net.make('post', N, D)

# Create a random white noise input function with
# .1 base freq, max freq 10 rad/s, RMS of .5, and random number seed 12
net.make_fourier_input('input', base=0.1, high=10, power=0.5, seed=12)

net.connect('input', 'pre')

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(
    net, errName='error', N_err=100, preName='pre', postName='post', rate=5e-4)


# Set the modulatory signal.
net.connect('pre', 'error')
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch', [0])
gating.make(net, name='Gate', gated='error', neurons=40, pstc=0.01)
net.connect('switch', 'Gate')

# Add another non-gated error population running in direct mode.
net.make('actual error', 1, 1, mode='direct')
net.connect('pre', 'actual error')
net.connect('post', 'actual error', weight=-1)

net.add_to_nengo()
