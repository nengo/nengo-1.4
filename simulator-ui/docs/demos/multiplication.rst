Performing Multiplication
============================

import nef

net=nef.Network('Multiply')
inputA=net.make_input('inputA',[8])
inputB=net.make_input('inputB',[5])
A=net.make('A',100,1,radius=10,quick=True)
B=net.make('B',100,1,radius=10,quick=True,storage_code='B')
C=net.make('Combined',225,2,radius=15,quick=True)
D=net.make('D',100,1,radius=100,quick=True,storage_code='D')
net.connect(inputA,A)
net.connect(inputB,B)
net.connect(A,C,transform=[1,0])
net.connect(B,C,transform=[0,1])
def product(x):
    return x[0]*x[1]
net.connect(C,D,func=product)
net.add_to(world)


