import nef

net=nef.Network('Scalar Representation')
input=net.make_input('input',[0])
neuron=net.make('neurons',100,1,encoders=[[1], [-1]]*50,quick=True)
net.connect(input,neuron)
net.add_to(world)


