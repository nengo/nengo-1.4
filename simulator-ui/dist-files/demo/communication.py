import nef

net=nef.Network('Communications Channel')
input=net.make_input('input',[0.5])
A=net.make('A',100,1,quick=True)
B=net.make('B',100,1,quick=True,storage_code='B')
net.connect(input,A)
net.connect(A,B)
net.add_to(world)
