import nef

net=nef.Network('A Single Neuron')
input=net.make_input('input',[0.5])
neuron=net.make('neuron',1,1,max_rate=(100,100),intercept=(-0.5,-0.5),encoders=[[1]],noise=0.1)
net.connect(input,neuron)
net.add_to(world)


