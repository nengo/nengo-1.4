N=60
D=2

import nef
import nef.templates.learned_termination as learning
import nef.templates.gate as gating
import random

random.seed(27)

net=nef.Network('Learn Square') #Create the network object

# Create input and output populations.
net.make('pre',N,D) #Make a population with 60 neurons, 1 dimensions
net.make('post',N,D) #Make a population with 60 neurons, 1 dimensions

# Create a random function input.
net.make_fourier_input('input', dimensions = D, base=0.1, high=8, power=0.4, seed=0)
               #Create a white noise input function .1 base freq, max 
               #freq 10 rad/s, and RMS of .4; 0 is a seed  

net.connect('input','pre')

# Create a modulated connection between the 'pre' and 'post' ensembles.
learning.make(net,errName='error', N_err=100, preName='pre', postName='post',
    rate=5e-7) #Make an error population with 100 neurons, and a learning 
            #rate of 5e-7

# Set the modulatory signal to compute the desired function
def square(x):
    return [xx*xx for xx in x]

net.connect('pre', 'error', func=square)
net.connect('post', 'error', weight=-1)

# Add a gate to turn learning on and off.
net.make_input('switch',[0]) #Create a controllable input function with 
                             #a starting value of 0 and 0 in the two 
                             #dimensions
gating.make(net,name='Gate', gated='error', neurons=40,
    pstc=0.01) #Make a gate population with 40 neurons, and a postsynaptic 
               #time constant of 10ms
net.connect('switch', 'Gate')

net.add_to_nengo()

