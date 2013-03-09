"""This is a test file to test the weight, index_pre, and index_post parameters 
   on the connect function. 
"""

import nef_theano as nef
import math
import numpy as np
import matplotlib.pyplot as plt

net=nef.Network('Encoder Test')
net.make_input('in', value=math.sin)
net.make('A', 500, 1)
net.make('B', 500, 1)
net.make('C', 1000, 2)
net.make('D', 1000, 3)

net.connect('in', 'A', weight=.5)
net.connect('A', 'B', weight=2)
net.connect('A', 'C', index_post=1)
net.connect('A', 'D')

timesteps = 500
Fvals = np.zeros((timesteps,1))
Avals = np.zeros((timesteps,1))
Bvals = np.zeros((timesteps,1))
Cvals = np.zeros((timesteps,2))
Dvals = np.zeros((timesteps,3))
for i in range(timesteps):
    net.run(0.01)
    Fvals[i] = net.nodes['in'].projected_value.get_value() 
    Avals[i] = net.nodes['A'].origin['X'].projected_value.get_value() 
    Bvals[i] = net.nodes['B'].origin['X'].projected_value.get_value()
    Cvals[i] = net.nodes['C'].origin['X'].projected_value.get_value()
    Dvals[i] = net.nodes['D'].origin['X'].projected_value.get_value()

plt.ion(); plt.clf(); 
plt.subplot(511); plt.title('Input')
plt.plot(Fvals)
plt.subplot(512); plt.title('A = Input * .5')
plt.plot(Avals)
plt.subplot(513); plt.title('B = A * 2')
plt.plot(Bvals)
plt.subplot(514); plt.title('C(1) = A, C(2) 0')
plt.plot(Cvals)
plt.subplot(515); plt.title('D(1:3) = A')
plt.plot(Dvals)
