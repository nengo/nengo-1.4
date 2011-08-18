2D Representation
============================

This demonstrates 2D representation, the first extension of scalar representation.  The code is::

import nef

net=nef.Network('2D Representation')
input=net.make_input('input',[0,0])
neuron=net.make('neurons',100,2,quick=True)
net.connect(input,neuron)
net.add_to(world)

To run the demo, 

