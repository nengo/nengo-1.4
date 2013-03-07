import nef_theano as nef
import math
import numpy as np
import matplotlib.pyplot as plt

net=nef.Network('Encoder Test')
net.make_input('in', math.sin)
net.make('A', 100, 1)
net.make('B', 100, 1, encoders=[[1]], intercept=(0,1.0))
net.make('C', 100, 1)

net.connect('in', 'A')
net.connect('A', 'B')
net.connect('B', 'C')

Avals = np.zeros((1001,1))
Bvals = np.zeros((1001,1))
for i in range(1000):
    net.run(0.1)
    #print net.nodes['A'].origin['X'].projected_value.get_value(), net.nodes['B'].origin['X'].projected_value.get_value(), net.nodes['C'].origin['X'].projected_value.get_value()
     #net.nodes['B'].accumulator[0.01].projected_value.get_value(), net.nodes['C'].accumulator[0.01].projected_value.get_value()
    Avals[i] = net.nodes['B'].accumulator[0.01].projected_value.get_value() # get the post-synaptic values because they're already filtered
    Bvals[i] = net.nodes['C'].accumulator[0.01].projected_value.get_value()

plt.ion(); plt.hold(1)
plt.plot(Avals)
plt.plot(Bvals)
