Addition
============================

import nef

net=nef.Network('Addition')
inputA=net.make_input('inputA',[0])
inputB=net.make_input('inputB',[0])
A=net.make('A',100,1,quick=True)
B=net.make('B',100,1,quick=True,storage_code='B')
C=net.make('C',100,1,quick=True,storage_code='C')
net.connect(inputA,A)
net.connect(inputB,B)
net.connect(A,C)
net.connect(B,C)
net.add_to(world)


