import nef

net=nef.Network('A Single Neuron (pre-built)')
net.make_input('input',[0.5])
net.make('neuron',1,1,max_rate=(50,100),intercept=(-0.5,0.5),encoders=[[1]],noise=5)
net.connect('input','neuron')
net.add_to_nengo()


