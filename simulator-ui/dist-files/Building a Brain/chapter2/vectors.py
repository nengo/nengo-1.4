import nef

net=nef.Network('2D Representation (pre-built)')
net.make_input('input',[0,0])
net.make('neurons',100,2,quick=True)
net.connect('input','neurons')
net.add_to_nengo()


