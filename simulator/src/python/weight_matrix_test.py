"""This is a test file to test the weight_matrix parameter on addTermination, 
   here we test by creating inhibitory connections."""

import nef_theano as nef
import math
import numpy as np
import matplotlib.pyplot as plt

net=nef.Network('WeightMatrix Test')
net.make_input('in', value=math.sin)
net.make('A', neurons=100, dimensions=1)
net.make('B', neurons=100, dimensions=1)

# setup inhibitory scaling matrix
inhib_scaling_matrix = [[0]*dimensions for i in range(dimensions)]
for i in range(dimensions):
    inhib_scaling_matrix[i][i] = -inhib_scale
# setup inhibitory matrix
inhib_matrix = []
for i in range(dimensions):
    inhib_matrix_part = [[inhib_scaling_matrix[i]] * neurons]
    inhib_matrix.append(inhib_matrix_part[0])

net.nodes['A'].addTermination('bg_input', inhib_matrix, tau_inhib, False)

# define our transform and connect up! 
transform = [[0,1],[1,0],[1,-1]]
net.connect('in', 'A', transform=transform)

timesteps = 500
Fvals = np.zeros((timesteps,2))
Avals = np.zeros((timesteps,3))
for i in range(timesteps):
    net.run(0.01)
    Fvals[i] = net.nodes['in'].decoded_output.get_value() 
    Avals[i] = net.nodes['A'].origin['X'].decoded_output.get_value() 

plt.ion(); plt.clf(); 
plt.subplot(411); plt.title('Input')
plt.plot(Fvals); plt.legend(['In(0)','In(1)'])
plt.subplot(412); plt.title('A(0) = In(1)')
plt.plot(Avals[:,0])
plt.subplot(413); plt.title('A(1) = In(0)')
plt.plot(Avals[:,1])
plt.subplot(414); plt.title('A(2) = In(0) - In(1)')
plt.plot(Avals[:,2])
