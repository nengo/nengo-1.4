import nef

net=nef.Network('Scalar Addition (pre-built)')
inputA=net.make_input('inputA',[0])
inputB=net.make_input('inputB',[0])
A=net.make('A',100,1,quick=True)
B=net.make('B',100,1,quick=True)
C=net.make('Sum',100,1,quick=True, radius=2)
net.connect(inputA,A)
net.connect(inputB,B)
net.connect(A,C)
net.connect(B,C)
net.add_to(world)


