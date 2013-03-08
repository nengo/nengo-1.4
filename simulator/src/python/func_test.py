"""This is a test file to test the func parameter on the connect method"""

import nef_theano as nef
import numpy as np
import matplotlib.pyplot as plt

net=nef.Network('Function Test', seed=3)
net.make_input('in', 0.5)
net.make('A', neurons=500, dimensions=1)
net.make('B', neurons=500, dimensions=3)

# function example for testing
def square(x):
    return x[0]*x[0], -x[0], x[0]

net.connect('in', 'A')
net.connect('A','B',func=square,pstc=0.1)

timesteps = 500
# setup arrays to store data gathered from sim
Fvals = np.zeros((timesteps, 1))
Avals = np.zeros((timesteps, 1))
Bvals = np.zeros((timesteps, 3))

print "starting simulation"
for i in range(timesteps):
    net.run(0.001)
    Fvals[i] = net.nodes['in'].projected_value.get_value() 
    Avals[i] = net.nodes['A'].origin['X'].projected_value.get_value() 
    Bvals[i] = net.nodes['B'].origin['X'].projected_value.get_value() 

# plot the results
plt.ion(); plt.clf(); plt.hold(1);
plt.plot(Fvals)
plt.plot(Avals)
plt.plot(Bvals)
plt.legend(['Input','A','B'])
