Squaring the Input
================================================


import nef

net=nef.Network('Squaring')
input=net.make_input('input',[0])
A=net.make('A',100,1,quick=True)
B=net.make('B',100,1,quick=True,storage_code='B')
net.connect(input,A)
net.connect(A,B,func=lambda x: x[0]*x[0])
net.add_to(world)


