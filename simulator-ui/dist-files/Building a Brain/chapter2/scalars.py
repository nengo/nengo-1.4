import nef

net=nef.Network('Scalar Representation (pre-built)')
input=net.make_input('input',[0])
neuron=net.make('neurons',100,1,quick=True)
net.connect(input,neuron)
net.add_to(world)


