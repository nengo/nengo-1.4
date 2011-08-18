Constructing a Population with Many Neurons
==============================================

import nef

net=nef.Network('Many Neurons')
input=net.make_input('input',[-0.45])
neuron=net.make('neurons',100,1,noise=1,quick=True)
net.connect(input,neuron)
net.add_to(world)

