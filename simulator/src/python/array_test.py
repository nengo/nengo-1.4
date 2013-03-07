import nef_theano as nef
import numpy

net=nef.Network('Array Test', seed=5)
net.make_array('A', 100, 3, dimensions=2, neuron_type='lif')

net.make_input('in', [-1,0,0,0,0,1], zero_after=1.0)
net.connect('in', 'A', pstc=0.1)

net.make_array('A2', 100, 2, dimensions=3, neuron_type='lif')
net.connect('A', 'A2',pstc=0.1)


net.make('B', 100, 6, neuron_type='lif')
net.connect('A2', 'B', pstc=0.1)

import time

start=time.time()
for i in range(2000):
    net.run(0.001)
    #print i,net.ensemble['B'].accumulator[0.1].value.get_value()
    #print i,net.node['B'].origin['X'].value.get_value()
print (time.time()-start)/2000
