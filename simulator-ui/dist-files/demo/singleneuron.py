import nef

net=nef.Network('Single Neuron')
input=net.make_input('input',[-0.45])
neuron=net.make('neuron',1,1,max_rate=(100,100),intercept=(-0.5,-0.5),encoders=[[1]],noise=3)
net.connect(input,neuron)
#Add everything to the world
net.add_to(world)


