"""This is a file to test the network array function, both with make_array, 
   and by using the array_size parameter in the network.make command"""

import nef_theano as nef
import numpy as np
import matplotlib.pyplot as plt

net=nef.Network('Array Test', seed=5)
net.make_input('in', [-1,0,0,0,0,1], zero_after=1.0)
net.make_array('A', neurons=100, array_size=3, dimensions=2, neuron_type='lif')
net.make('A2', neurons=100, array_size=2, dimensions=3, neuron_type='lif')
net.make('B', 100, 6, neuron_type='lif')

net.connect('in', 'A', pstc=0.1)
net.connect('A', 'A2', pstc=0.1)
net.connect('A2', 'B', pstc=0.1)

timesteps = 500
# setup arrays to store data gathered from sim
Fvals = np.zeros((timesteps, 6))
Avals = np.zeros((timesteps, 6))
A2vals = np.zeros((timesteps, 6))
Bvals = np.zeros((timesteps, 6))

print "starting simulation"
for i in range(timesteps):
    net.run(0.001)
    Fvals[i] = net.nodes['in'].projected_value.get_value() 
    Avals[i] = net.nodes['A'].origin['X'].projected_value.get_value() 
    A2vals[i] = net.nodes['A2'].origin['X'].projected_value.get_value() 
    Bvals[i] = net.nodes['B'].origin['X'].projected_value.get_value() 

# plot the results
plt.ion(); plt.close(); 
plt.subplot(4,1,1)
plt.plot(Fvals,'x'); plt.title('Input')
plt.subplot(4,1,2)
plt.plot(Avals); plt.title('A')
plt.subplot(4,1,3)
plt.plot(A2vals); plt.title('A2')
plt.subplot(4,1,4)
plt.plot(Bvals); plt.title('B')
