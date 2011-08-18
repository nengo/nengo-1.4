Combining 1D Representations into a 2D Representation
================================================================

import nef

net=nef.Network('Combining')
inputA=net.make_input('inputA',[0])
inputB=net.make_input('inputB',[0])
A=net.make('A',100,1,quick=True)
B=net.make('B',100,1,quick=True,storage_code='B')
C=net.make('C',100,2,quick=True,radius=1.5)
net.connect(inputA,A)
net.connect(inputB,B)
net.connect(A,C,transform=[1,0])
net.connect(B,C,transform=[0,1])
net.add_to(world)


