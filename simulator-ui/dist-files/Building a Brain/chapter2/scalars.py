import nef

net=nef.Network('Scalar Representation (pre-built)')
net.make_input('input',[0])
net.make('neurons',100,1)
net.connect('input','neurons')
net.add_to_nengo()


