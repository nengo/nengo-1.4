import nef
import nps

D=5
net=nef.Network('Basal Ganglia')
input=net.make_input('input',[0]*D)
output=net.make('output',1,D,mode='direct',quick=True)
nps.basalganglia.make_basal_ganglia(net,input,output,D,same_neurons=False,N=50)
net.add_to(world)

