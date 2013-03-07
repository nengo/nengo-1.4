import nef_theano as nef
import math

net=nef.Network('Encoder Test')
net.make_input('in', math.sin)
net.make('A', 100, 1)
net.make('B', 100, 1, encoders=[[1]], intercept=(0,1.0))
net.make('C', 100, 1)

net.connect('in', 'A')
net.connect('A', 'B')
net.connect('B', 'C')

for i in range(1000):
    net.run(0.1)
    #print net.node['A'].origin['X'].value.get_value(),net.node['B'].origin['X'].value.get_value(),net.node['C'].origin['X'].value.get_value()
    print net.nodes['B'].accumulator[0.01].projected_value.get_value(), net.nodes['C'].accumulator[0.01].projected_value.get_value()
