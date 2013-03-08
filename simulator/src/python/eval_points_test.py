"""This test file is for checking the eval_points parameter added to the ensemble and origin constructors.
   An ensemble can be created with a set of default eval_points for every origin to use, or an origin can 
   be called with a specific set of eval_points to use for optimization. 
   
   This tests:
        - creating origin w/ eval_points
        - creating ensemble w/ eval_points
        - creating ensemble w/ eval_points, creating origin w/ eval_points
"""

import nef_theano as nef; reload(nef)
import math
import numpy as np
import matplotlib.pyplot as plt

# create the list of evaluation points
eval_points = np.arange(-1, 0, .002)

net=nef.Network('EvalPoints Test')
net.make_input('in', value=math.sin)

net.make('A', neurons=1000, dimensions=1)
net.make('C', neurons=300, dimensions=1, eval_points=eval_points)
net.make('B', neurons=300, dimensions=1)
# populations with default eval_points
net.make('D', neurons=300, dimensions=1, eval_points=eval_points)

# function for testing evaluation points
def pow(x):
    return [xval**2 for xval in x]

# create origins with eval_points
net.nodes['B'].add_origin('eval_points', func=pow, eval_points=eval_points)
net.nodes['D'].add_origin('eval_points', func=pow, eval_points=eval_points)

net.connect('in', 'A')
net.connect('A', 'B', origin_name='eval_points')
net.connect('A', 'C', func=pow)
net.connect('A', 'D', origin_name='eval_points')

timesteps = 500
# setup arrays to store data gathered from sim
Fvals = np.zeros((timesteps,1))
Avals = np.zeros((timesteps,1))
Bvals = np.zeros((timesteps,1))
Cvals = np.zeros((timesteps,1))
Dvals = np.zeros((timesteps,1))

print "starting simulation"
for i in range(timesteps):
    net.run(0.01)
    Fvals[i] = net.nodes['in'].projected_value.get_value() 
    Avals[i] = net.nodes['A'].origin['X'].projected_value.get_value() 
    Bvals[i] = net.nodes['B'].origin['X'].projected_value.get_value() 
    Cvals[i] = net.nodes['C'].origin['X'].projected_value.get_value() 
    Dvals[i] = net.nodes['D'].origin['X'].projected_value.get_value()

# plot the results
plt.ion(); plt.clf(); plt.hold(1);
plt.plot(Fvals)
plt.plot(Avals)
plt.plot(Bvals)
plt.plot(Cvals)
plt.plot(Dvals)
plt.legend(['Input','A','B','C','D'])
